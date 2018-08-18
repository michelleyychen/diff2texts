package entity;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.Response.Status;

import libs.ServerError;

/**
 * An instance of this class represents a string in one text.
 *
 * @author Rach (Racheal Chen)
 * 
 */

public class FText {
	
	private String text = null;
	
	/**
	 * character number of first character
	 */
	private int firstCharNo = -1;
	
	/**
	 * character number of last character
	 */
	private int lastCharNo = -1;
	
	/**
	 * reference to the text it belongs to 
	 */
	private FFile file = null;
	
	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	public void setFirstCharNo(int firstCharNo) {
		this.firstCharNo = firstCharNo;
	}
	public int getFirstCharNo() {
		return firstCharNo;
	}
	public void setLastCharNo(int lastCharNo) {
		this.lastCharNo = lastCharNo;
	}
	public int getLastCharNo() {
		return lastCharNo;
	}
	public void setFFile(FFile file) {
		this.file = file;
	}
	public FFile getFFile() {
		return file;
	}
	
	/**
	 * @param charNo
	 * @return location array
	 * 			row and col
	 */
	public int[] getPos(int charNo) {
		
		int row = -1;
		
		Iterator<Entry<Integer, Integer>> it = file.getMap().entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Integer, Integer> entry = it.next();
			if(charNo < entry.getValue()) {
				row = entry.getKey() - 1;
				break;
			}			
		}
	
		if(row == -1) {
			row = file.getLineList().size();
		}
		
		int[] pos = new int[2];
		pos[0] = row;
	
		if(file.getMap().get(row) == null) {
			throw new ServerError(Status.INTERNAL_SERVER_ERROR, 999, "Uncorrect array parameters.");
		}
		
		pos[1] = charNo - file.getMap().get(row) + 1;
		
		return pos;
		
	}
	
}
