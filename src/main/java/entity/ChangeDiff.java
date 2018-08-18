package entity;

/**
 * 	
 *  @author Rach (Racheal Chen)
 *
 */
public class ChangeDiff {
	
	/**
	 * the differences in which text, e.g. 2
	 */
	private int fileNumber;
	
	/**
	 * the different string
	 */
	private String text;
	
	/**
	 * location of the string's first character 
	 */
	private int startRow, startCol;
	
	/**
	 * location of the string's last character
	 * equals to start location if the string has only one character
	 */
	private int endRow, endCol;
	
	public void setFileNumber(int fileNumber) {
		this.fileNumber = fileNumber;
	}
	public int getFileNumber() {
		return fileNumber;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}
	public int getStartRow() {
		return startRow;
	}
	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}
	public int getEndRow() {
		return endRow;
	}
	public void setStartCol(int startCol) {
		this.startCol = startCol;
	}
	public int getStartCol() {
		return startCol;
	}
	public void setEndCol(int endCol) {
		this.endCol = endCol;
	}
	public int getEndCol() {
		return endCol;
	}
	
}
