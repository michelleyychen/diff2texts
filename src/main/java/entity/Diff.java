package entity;

import java.util.Objects;

import service.Operation;

/**
 * The data structure representing a diff is a Linked list of Diff objects:
 * { Diff ( Operation.DELETE, "Hello" ), Diff ( Operation.INSERT, "Goodbye" ),
 * Diff ( Operation.EQUAL, " world." ) }, which means delete "Hello", add "Goodbye"
 * and keep " world."
 * 	
 *  @author Rach (Racheal Chen)
 *
 */
	
public class Diff {
		
		/**
		 * one of INSERT, DELETE or EQUAL
		 */
		public Operation operation;		
		
		/**
		 * the text applied
		 */
		public String text;	
		
		/**
		 * location of the first character 
		 */
		public int startRow, startCol;
		
		/**
		 * location of the last character
		 */
		public int endRow, endCol;
		
		/**
		 * Constructors.
		 */
		public Diff(Operation operation, String text, int[] startPos, int[] endPos) {			
			this.operation = operation;
			this.text = text;
			startRow = startPos[0];
			startCol = startPos[1];
			endRow = endPos[0];
			endCol = endPos[1];
		}
		
		public Diff(Operation operation, String text) {
			this.operation = operation;
			this.text = text;
		}
		
		public Diff() {
			
		}
		
		/**
		 * Display a human-readable version of Diff for DEBUG.
		 * 
		 * @return text version.
		 */
		public String toString() {
			String prettyText = this.text.replace('\n', '\u00b6');
			return "Diff(" + this.operation + ",\"" + prettyText + "\", " + startRow + ", " + startCol +", "+ endRow +", " + endCol + ")";
		}

		/**
		 * Create a numeric hash value for a Diff. 
		 * 
		 * @return Hash value.
		 */
		@Override
		public int hashCode() {
			return Objects.hash(operation, text, startRow, startCol, endRow, endCol);
		}

		/**
		 * Is this Diff equivalent to another Diff?
		 * 
		 * @param obj
		 *            Another Diff to compare against.
		 *            
		 * @return true or false.
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if(obj != null && Diff.class == obj.getClass()) {
				Diff target = (Diff) obj;
				if(operation == target.operation && text.equals(target.text) &&
						startRow == target.startRow && startCol == target.startCol &&
						endRow == target.endRow && endCol == target.endCol) {
					return true;
				}			
			}
			return false;
		}
		
	}
