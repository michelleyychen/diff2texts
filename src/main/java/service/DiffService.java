package service;

import java.util.*;

import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import entity.ChangeDiff;
import entity.Diff;
import entity.FFile;
import entity.FText;
import libs.OutOfTimeException;
import libs.UploadException;

/**
 * Computes the difference between two texts, allowing for errors.
 * 	
 * @author Rach (Racheal Chen)
 * 
 */
public class DiffService {
	
	/**
	 * Set a deadline of seconds by which time the diff must be complete,
	 * otherwise give up.
	 */
	public double Diff_Timeout = 1000.0;
	
	private static final Logger logger = Logger.getLogger(DiffService.class); 
	
	/**
	 * Find the differences between two texts.
	 * 
	 * @param text1
	 *            one string to be diffed
	 * @param text2
	 *            another string to be diffed
	 * @param checklines
	 *            speedup or not
	 * @return Linked List of Diff objects
	 */
	public List<ChangeDiff> diff_main(FFile f1, FFile f2, boolean checklines) {

		long deadline;
		if (Diff_Timeout <= 0) {
			deadline = Long.MAX_VALUE;
		} else {
			deadline = System.currentTimeMillis() + (long) (Diff_Timeout * 1000);
		}
		
		FText t1 = new FText();
		t1.setFFile(f1);
		t1.setText(f1.getContent());
		t1.setFirstCharNo(f1.getCharCount() > 0 ? 1 : 0);
		t1.setLastCharNo(f1.getCharCount());
		
		FText t2 = new FText();
		t2.setFFile(f2);
		t2.setText(f2.getContent());
		t2.setFirstCharNo(f2.getCharCount() > 0 ? 1 : 0);
		t2.setLastCharNo(f2.getCharCount());

		return diff_cleanupMerge(diff_main(t1, t2, checklines, deadline));

	}

		
	/**
	 * Find the differences between two texts. 
	 * Simplifies the problem by stripping 
	 * any common prefix or suffix off the texts before diffing.
	 * 
	 * @param text1
	 *            one string to be diffed
	 * @param text2
	 *            another string to be diffed
	 * @param checklines
	 *             speedup or not.
	 * @return Linked List of Diff objects
	 */
	private LinkedList<Diff> diff_main(FText t1, FText t2, boolean checklines, long deadline) {
		
		String text1 = t1.getText();
		String text2 = t2.getText();
		
		// Check for null inputs.
		if (text1 == null || text2 == null) {
			throw new UploadException(Status.BAD_REQUEST, 1009, "Null input file.");
		}
		
		// Check for equality (speedup).
		LinkedList<Diff> diffs;
		if (text1.equals(text2)) {
			diffs = new LinkedList<Diff>();
			if (text1.length() != 0) {
				diffs.add(
						new Diff(Operation.EQUAL, text1)
				);
			}
			return diffs;
		}
				
		// Trim off common prefix (speedup).
		int commonlength = diff_commonPrefix(text1, text2);
		String commonprefix = text1.substring(0, commonlength);		
		text1 = text1.substring(commonlength);
		text2 = text2.substring(commonlength);
		t1.setFirstCharNo(commonlength + t1.getFirstCharNo());
		t2.setFirstCharNo(commonlength + t2.getFirstCharNo());

		// Trim off common suffix (speedup).
		commonlength = diff_commonSuffix(text1, text2);
		String commonsuffix = text1.substring(text1.length() - commonlength);	
		text1 = text1.substring(0, text1.length() - commonlength);
		text2 = text2.substring(0, text2.length() - commonlength);
		t1.setLastCharNo(t1.getLastCharNo() - commonlength);
		t2.setLastCharNo(t2.getLastCharNo() - commonlength);
				
		t1.setText(text1);
		t2.setText(text2);
		
		// Compute the middle block.
		diffs = diff_compute(t1, t2, checklines, deadline);

		// Restore the prefix and suffix.
		if (commonprefix.length() != 0) {
			diffs.addFirst(new Diff(Operation.EQUAL, commonprefix));
		}
		if (commonsuffix.length() != 0) {
			diffs.addLast(new Diff(Operation.EQUAL, commonsuffix));
		}

		return diffs;
		
	}

	/**
	 * Find the differences between two texts.
	 * Assumes that the texts do not have any common prefix or suffix.
	 * 
	 * @param text1
	 *            one string to be diffed
	 * @param text2
	 *            another string to be diffed
	 * @param checklines
	 *            speedup or not
	 * @param deadline
	 *            time when the diff should be complete by
	 * @return Linked List of Diff objects
	 */
	private LinkedList<Diff> diff_compute(FText t1, FText t2, boolean checklines, long deadline) {
		
		LinkedList<Diff> diffs = new LinkedList<Diff>();
		String text1 = t1.getText();
		String text2 = t2.getText();
		FFile f1 = t1.getFFile();
		FFile f2 = t2.getFFile();
		
		if (text1.length() == 0) {
			logger.debug("\n---- One of the two strings is prefix or suffix of another. ----");
			logger.debug(text1 + "  |  " + text2);
			// Just add some text (speedup).
			diffs.add(
					new Diff(
							Operation.INSERT, text2,
							t2.getPos(t2.getFirstCharNo()),
							t2.getPos(t2.getLastCharNo())
			));
			return diffs;
		}
		
		if (text2.length() == 0) {
			logger.debug("\n---- One of the two strings is prefix or suffix of another. ----");
			logger.debug(text1 + "  |  " + text2);
			// Just delete some text (speedup).
			diffs.add(
					new Diff(
							Operation.DELETE, text1,
							t1.getPos(t1.getFirstCharNo()),
							t1.getPos(t1.getLastCharNo())
			));
			return diffs;
		}			
		
		boolean tag = text1.length() > text2.length();
		FText longT = tag ? t1 : t2;
		FText shortT = tag ? t2 : t1;
		
		String longtext = longT.getText();
		String shorttext = shortT.getText();
		
		int i = longtext.indexOf(shorttext);
	
		if (i != -1) {
			logger.debug("\n---- One of the two strings is inside another. ----");
			logger.debug(text1 + "  |  " + text2);
			// Shorter text is inside the longer text (speedup).
			Operation op = (text1.length() > text2.length()) ? Operation.DELETE : Operation.INSERT;
			
			diffs.add(new Diff( op, longtext.substring(0, i), longT.getPos(longT.getFirstCharNo()), 
					longT.getPos(longT.getFirstCharNo() + i - 1)));
			diffs.add(new Diff(Operation.EQUAL, shorttext));
			diffs.add(new Diff(op, longtext.substring(i + shorttext.length()), 
					longT.getPos(longT.getFirstCharNo() + i + shorttext.length()), longT.getPos(longT.getLastCharNo())));	
			
			return diffs;
		}

		if (shorttext.length() == 1) {
			logger.debug("\n---- One of the two strings has only one character. ----");
			logger.debug(text1 + "  |  " + text2);
			// Single character string.
			// After the previous speedup, the character can't be an equality.			
			diffs.add(new Diff(Operation.DELETE, text1, t1.getPos(t1.getFirstCharNo()), t1.getPos(t1.getLastCharNo())));
			diffs.add(new Diff(Operation.INSERT, text2, t2.getPos(t2.getFirstCharNo()), t2.getPos(t2.getLastCharNo())));		
			return diffs;
		}

		
		// Check to see if the problem can be split in two.
		String[] hm = diff_halfMatch(text1, text2);
		
		if (hm != null) {
			// A half-match was found, sort out the return data.
			String text1_a = hm[0];
			String text1_b = hm[1];
			String text2_a = hm[2];
			String text2_b = hm[3];
			String mid_common = hm[4];
			
			logger.debug("\n---- Two strings share a substring which is at least half the length of the longer string. ----");		
			for(int j = 0; j < hm.length; j++) {
				logger.debug(hm[j] + "  |  ");
			}
			
			FText t1_x = new FText();
			t1_x.setFFile(f1);
			t1_x.setText(text1_a);
			t1_x.setFirstCharNo(t1.getFirstCharNo());
			t1_x.setLastCharNo(t1.getFirstCharNo() + text1_a.length() - 1);		
			FText t2_x = new FText();
			t2_x.setFFile(f2);
			t2_x.setText(text2_a);
			t2_x.setFirstCharNo(t2.getFirstCharNo());
			t2_x.setLastCharNo(t2.getFirstCharNo() + text2_a.length() - 1);	

			int t1_lastCharNo = t1_x.getLastCharNo();
			int t2_lastCharNo = t2_x.getLastCharNo();
			
			// Send both pairs off for separate processing.
			LinkedList<Diff> diffs_a = diff_main(t1_x, t2_x, checklines, deadline);

			t1_x.setText(text1_b);
			t1_x.setFirstCharNo(t1_lastCharNo + mid_common.length() + 1);
			t1_x.setLastCharNo(t1.getLastCharNo());
			t2_x.setText(text2_b);
			t2_x.setFirstCharNo(t2_lastCharNo + mid_common.length() + 1);
			t2_x.setLastCharNo(t2.getLastCharNo());
			
			LinkedList<Diff> diffs_b = diff_main(t1_x, t2_x, checklines, deadline);
		
			// Merge the results.
			diffs = diffs_a;
			diffs.add(new Diff(Operation.EQUAL, mid_common));
			diffs.addAll(diffs_b);
			return diffs;
			
		}

		logger.debug("\n---- No specifics to speed up. ----");
		logger.debug(text1 + "  |  " + text2);
	
		return diff_bisect(t1, t2, deadline);
		
	}

	/**
	 * Find the common prefix of two strings
	 * 
	 * @param text1
	 *            one string
	 * @param text2
	 *            another string
	 * @return the number of common characters at the start of each string
	 */
	public int diff_commonPrefix(String text1, String text2) {

		int n = Math.min(text1.length(), text2.length());
		for (int i = 0; i < n; i++) {
			if (text1.charAt(i) != text2.charAt(i)) {
				return i;
			}
		}
		return n;
		
	}

	/**
	 * Find the common suffix of two strings
	 * 
	 * @param text1
	 *            one string
	 * @param text2
	 *            another string
	 * @return the number of common characters at the end of each string
	 */
	public int diff_commonSuffix(String text1, String text2) {
		
		int text1_length = text1.length();
		int text2_length = text2.length();
		int n = Math.min(text1_length, text2_length);
		for (int i = 1; i <= n; i++) {
			if (text1.charAt(text1_length - i) != text2.charAt(text2_length - i)) {
				return i - 1;
			}
		}
		return n;
		
	}

	/**
	 * Merge certain substrings that has same operation into one string
	 * 
	 * @param diffs
	 *            LinkedList of Diff objects
	 */
	public List<ChangeDiff> diff_cleanupMerge(LinkedList<Diff> diffs) {	
		
		ListIterator<Diff> pointer = diffs.listIterator();
		LinkedList<ChangeDiff> changeDiffs = new LinkedList<>();
		Diff curDiff = pointer.next();
		Diff preDiff = null;

		while (pointer.hasNext()) {
			boolean tag = true;
			preDiff = curDiff;
			curDiff = pointer.next();
			if (preDiff.operation == curDiff.operation && preDiff.endRow == curDiff.startRow
					&& preDiff.endCol == curDiff.startCol - 1) {
				preDiff.text += curDiff.text;
				preDiff.endRow = curDiff.endRow;
				preDiff.endCol = curDiff.endCol;
				pointer.remove();
				curDiff = preDiff;
				tag = false;
			}

			if (tag && (preDiff.operation == Operation.INSERT || preDiff.operation == Operation.DELETE)) {
				ChangeDiff change = new ChangeDiff();
				change.setFileNumber(preDiff.operation == Operation.DELETE ? 1 : 2);
				change.setText(preDiff.text);
				change.setStartRow(preDiff.startRow);
				change.setStartCol(preDiff.startCol);
				change.setEndRow(preDiff.endRow);
				change.setEndCol(preDiff.endCol);
				changeDiffs.add(change);				
				logger.debug("\nFrom CleanupMerge：" + preDiff);
			}
			if (pointer.hasNext() == false && curDiff != preDiff && (curDiff.operation == Operation.INSERT
					|| curDiff.operation == Operation.DELETE)) {
				ChangeDiff change = new ChangeDiff();
				change.setFileNumber(curDiff.operation == Operation.DELETE ? 1 : 2);
				change.setText(curDiff.text);
				change.setStartRow(curDiff.startRow);
				change.setStartCol(curDiff.startCol);
				change.setEndRow(curDiff.endRow);
				change.setEndCol(curDiff.endCol);
				changeDiffs.add(change);
				logger.debug("\nFrom CleanupMerge：" + curDiff);
			}

		}
		
		return changeDiffs;
		
	}

	/**
	 * Do the two texts share a substring which is at least half the length of the
	 * longer text? This speedup can produce non-minimal diffs.
	 * 
	 * @param text1
	 *            one string
	 * @param text2
	 *            other string
	 * @return Five element String array, containing the prefix of text1, the suffix
	 *         of text1, the prefix of text2, the suffix of text2 and the common
	 *         middle block. Or null if there was no match.
	 */
	protected String[] diff_halfMatch(String text1, String text2) {
		
		if (Diff_Timeout <= 0) {
			throw new OutOfTimeException(Status.INTERNAL_SERVER_ERROR, 1008, "Time out.");
		}
		String longtext = text1.length() > text2.length() ? text1 : text2;
		String shorttext = text1.length() > text2.length() ? text2 : text1;
		if (longtext.length() < 4 || shorttext.length() * 2 < longtext.length()) {
			return null; // Pointless.
		}

		// First check if the second quarter is the seed for a half-match.
		String[] hm1 = diff_halfMatchI(longtext, shorttext, (longtext.length() + 3) / 4);
		// Check again based on the third quarter.
		String[] hm2 = diff_halfMatchI(longtext, shorttext, (longtext.length() + 1) / 2);		
		String[] hm;
		
		if (hm1 == null && hm2 == null) {
			return null;
		} else if (hm2 == null) {
			hm = hm1;
		} else if (hm1 == null) {
			hm = hm2;
		} else {
			hm = hm1[4].length() > hm2[4].length() ? hm1 : hm2;
		}

		// A half-match was found, sort out the return data.
		if (text1.length() > text2.length()) {
			return hm;
		} else {
			return new String[] { hm[2], hm[3], hm[0], hm[1], hm[4] };
		}
		
	}

	/**
	 * Does a substring of shorttext exist within longtext such that the substring
	 * is at least half the length of longtext?
	 * 
	 * @param longtext
	 *            longer string
	 * @param shorttext
	 *            shorter string
	 * @param i
	 *            start index of quarter length substring within longtext
	 * @return Five element String array, containing the prefix of longtext, the
	 *         suffix of longtext, the prefix of shorttext, the suffix of shorttext
	 *         and the common middle block. Or null if there was no match.
	 */
	private String[] diff_halfMatchI(String longtext, String shorttext, int i) {
		
		// Start with a 1/4 length substring at position i as a seed.
		String seed = longtext.substring(i, i + longtext.length() / 4);
		int j = -1;
		String best_common = "";
		String best_longtext_a = "", best_longtext_b = "";
		String best_shorttext_a = "", best_shorttext_b = "";
		while ((j = shorttext.indexOf(seed, j + 1)) != -1) {
			int prefixLength = diff_commonPrefix(longtext.substring(i), shorttext.substring(j));
			int suffixLength = diff_commonSuffix(longtext.substring(0, i), shorttext.substring(0, j));
			if (best_common.length() < suffixLength + prefixLength) {
				best_common = shorttext.substring(j - suffixLength, j) + shorttext.substring(j, j + prefixLength);
				best_longtext_a = longtext.substring(0, i - suffixLength);
				best_longtext_b = longtext.substring(i + prefixLength);
				best_shorttext_a = shorttext.substring(0, j - suffixLength);
				best_shorttext_b = shorttext.substring(j + prefixLength);
			}
		}
		if (best_common.length() * 2 >= longtext.length()) {
			return new String[] { best_longtext_a, best_longtext_b, best_shorttext_a, best_shorttext_b, best_common };
		} else {
			return null;
		}
		
	}


	/**
	 * Find the 'middle snake', split the problem in two and return the
	 * recursively constructed diff. 
	 * See Myers 1986 paper: An O(ND) Difference Algorithm and Its Variations.
	 * 
	 * @param text1
	 *            one string to be diffed
	 * @param text2
	 *            another string to be diffed
	 * @param deadline
	 *            time at which to bail if not yet complete
	 * @return LinkedList of Diff objects
	 */
	protected LinkedList<Diff> diff_bisect(FText t1, FText t2, long deadline) {
		
		String text1 = t1.getText();
		String text2 = t2.getText(); 
 		
		// Cache the text lengths to prevent multiple calls.
		int text1_length = text1.length();
		int text2_length = text2.length();
		int max_d = (text1_length + text2_length + 1) / 2;
		int v_offset = max_d;
		int v_length = 2 * max_d;
		int[] v1 = new int[v_length];
		int[] v2 = new int[v_length];
		for (int x = 0; x < v_length; x++) {
			v1[x] = -1;
			v2[x] = -1;
		}
		v1[v_offset + 1] = 0;
		v2[v_offset + 1] = 0;
		int delta = text1_length - text2_length;
		
		// If the total number of characters is odd, then the front path will
		// collide with the reverse path.
		boolean front = (delta % 2 != 0);
		
		// Offsets for start and end of k loop.
		// Prevents mapping of space beyond the grid.
		int k1start = 0;
		int k1end = 0;
		int k2start = 0;
		int k2end = 0;
		
		for (int d = 0; d < max_d; d++) {
			
			// Bail out if deadline is reached.
			if (System.currentTimeMillis() > deadline) {
				break;
			}
			
			// Walk the front path one step.
			for (int k1 = -d + k1start; k1 <= d - k1end; k1 += 2) {
				int k1_offset = v_offset + k1;
				int x1;
				if (k1 == -d || (k1 != d && v1[k1_offset - 1] < v1[k1_offset + 1])) {
					x1 = v1[k1_offset + 1];
				} else {
					x1 = v1[k1_offset - 1] + 1;		
				}
				int y1 = x1 - k1;
				while (x1 < text1_length && y1 < text2_length && text1.charAt(x1) == text2.charAt(y1)) {
					x1++;
					y1++;
				}
				v1[k1_offset] = x1;
				if (x1 > text1_length) {
					// Ran off the right of the graph.
					k1end += 2;
				} else if (y1 > text2_length) {
					// Ran off the bottom of the graph.
					k1start += 2;
				} else if (front) {
					int k2_offset = v_offset + delta - k1;
					if (k2_offset >= 0 && k2_offset < v_length && v2[k2_offset] != -1) {
						// Mirror x2 onto top-left coordinate system.
						int x2 = text1_length - v2[k2_offset];
						if (x1 >= x2) {
							// Overlap detected.
							return diff_bisectSplit(t1, t2, x1, y1, deadline);
						}
					}
				}
			}
			
			// Walk the reverse path one step.
			for (int k2 = -d + k2start; k2 <= d - k2end; k2 += 2) {
				int k2_offset = v_offset + k2;
				int x2;
				if (k2 == -d || (k2 != d && v2[k2_offset - 1] < v2[k2_offset + 1])) {
					x2 = v2[k2_offset + 1];
				} else {
					x2 = v2[k2_offset - 1] + 1;
				}
				int y2 = x2 - k2;
				while (x2 < text1_length && y2 < text2_length
						&& text1.charAt(text1_length - x2 - 1) == text2.charAt(text2_length - y2 - 1)) {
					x2++;
					y2++;
				}
				v2[k2_offset] = x2;
				if (x2 > text1_length) {
					// Ran off the left of the graph.
					k2end += 2;
				} else if (y2 > text2_length) {
					// Ran off the top of the graph.
					k2start += 2;
				} else if (!front) {
					int k1_offset = v_offset + delta - k2;
					if (k1_offset >= 0 && k1_offset < v_length && v1[k1_offset] != -1) {
						int x1 = v1[k1_offset];
						int y1 = v_offset + x1 - k1_offset;
						// Mirror x2 onto top-left coordinate system.
						x2 = text1_length - x2;
						if (x1 >= x2) {
							// Overlap detected.
							return diff_bisectSplit(t1, t2, x1, y1, deadline);
						}
					}
				}
			}
			
		}
	
		// Diff took too long and hit the deadline or
		// number of diffs equals number of characters, no commonality at all.
		LinkedList<Diff> diffs = new LinkedList<Diff>();
		diffs.add(new Diff(Operation.DELETE, text1, t1.getPos(t1.getFirstCharNo()), t1.getPos(t1.getLastCharNo())));
		diffs.add(new Diff(Operation.INSERT, text2, t2.getPos(t2.getFirstCharNo()), t2.getPos(t2.getLastCharNo())));
		
		return diffs;
		
	}

	/**
	 * Given the location of the 'middle snake', split the diff in two parts and recurse.
	 * 
	 * @param text1
	 *            one string to be diffed
	 * @param text2
	 *            another string to be diffed
	 * @param x
	 *            index of split point in text1
	 * @param y
	 *            index of split point in text2
	 * @param deadline
	 *            time at which to bail if not yet complete
	 * @return LinkedList of Diff objects
	 */
	private LinkedList<Diff> diff_bisectSplit(FText t1, FText t2, int x, int y, long deadline) {
		
		String text1 = t1.getText();
		String text2 = t2.getText();
		String text1a = text1.substring(0, x);
		String text2a = text2.substring(0, y);
		String text1b = text1.substring(x);
		String text2b = text2.substring(y);
		
		FText t1_x = new FText();
		FText t2_x = new FText();
		
		t1_x.setText(text1a);
		t1_x.setFFile(t1.getFFile());
		t1_x.setFirstCharNo(t1.getFirstCharNo());
		t1_x.setLastCharNo(t1.getFirstCharNo() + x - 1);
		t2_x.setText(text2a);
		t2_x.setFFile(t2.getFFile());
		t2_x.setFirstCharNo(t2.getFirstCharNo());
		t2_x.setLastCharNo(t2.getFirstCharNo() + y - 1);
	
		// Compute both diffs serially.
		LinkedList<Diff> diffs = diff_main(t1_x, t2_x, false, deadline);
		
		t1_x.setText(text1b);
		t1_x.setFirstCharNo(t1.getFirstCharNo() + x);
		t1_x.setLastCharNo(t1.getLastCharNo());
		t2_x.setText(text2b);
		t2_x.setFirstCharNo(t2.getFirstCharNo() + y);
		t2_x.setLastCharNo(t2.getLastCharNo());		
		
		LinkedList<Diff> diffsb = diff_main(t1_x, t2_x, false, deadline);

		diffs.addAll(diffsb);
		return diffs;
		
	}

}
