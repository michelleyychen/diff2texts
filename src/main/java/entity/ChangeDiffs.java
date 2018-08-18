package entity;

import java.util.LinkedList;
import java.util.List;

/**
 * 	A custom class which contains a list of ChangeDiff. 
 *  @author Rach (Racheal Chen)
 *
 */
public class ChangeDiffs {
	
	private List<ChangeDiff> changeDiffList = new LinkedList<>();
	
	public void setChangeDiffList(List<ChangeDiff> changeDiffList) {
		
		this.changeDiffList = changeDiffList;
		
	}
	
	public List<ChangeDiff> getChangeDiffList(){
		
		return changeDiffList;
		
	}
	
}
