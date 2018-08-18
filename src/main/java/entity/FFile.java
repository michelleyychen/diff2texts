package entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import libs.NotFoundError;
import libs.ServerError;

/**
 * An instance of this class is prepared for one text to be 
 * diffed, it can be considered as a "map" for every character
 * and it's location in the text.
 * 
 * The main data structure is map which maintains a line number
 * and the character number of line's first character.  
 * 
 * Find a difference of two texts, then look up the difference's 
 * location from "map" in each text.
 * 
 *  @author Rach (Racheal Chen)
 *
 */
	
public class FFile {
	
	/**
	 * lines of a text
	 */
	private List<String> lineList = new ArrayList<String>();
	
	private String content = null;
	
	/**
	 * total number of characters, including space character
	 */
	private int charCount = 0;
	
	/**
	 * "map" of a text
	 */
	private Map<Integer, Integer> map = new HashMap<>();
	
	private static final Logger logger = Logger.getLogger(FFile.class); 
	
	public void setLineListContent(String saveLocation) {
		
		String str = "";
		File file = new File(saveLocation);
		
		if(!file.exists() || !file.isFile()) {
			throw new NotFoundError(Status.NOT_FOUND, 1001, "File not found.");
		}
		
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(saveLocation)))) {
			
			String line = null;
			while((line = br.readLine()) != null) {
				lineList.add(line);
				str += line;
			}
			
		} catch (IOException e) {
			throw new ServerError(Status.INTERNAL_SERVER_ERROR, 999, "IOException.");
		} 
		
		content = str;
		
	}
	
	public List<String> getLineList(){
		
		return lineList;
		
	}
	
	public String getContent() {
		
		return content;
		
	}
	
	/**
	 * Compute the character numbers.
	 * Characters are numbered from 1 to charCount.  
	 */
	public void setCharCountMap() {
		
		for(int i = 0; i < lineList.size(); i++) {
        	String curLine = lineList.get(i); 
      
        	if(charCount == 0 && !curLine.equals("")) {
        		charCount = 1;
        	} 
        	
        	map.put(i + 1, charCount);
        	charCount += curLine.length();        	
        }
		
		if(charCount > 0) {
			charCount--;
		}

		logger.debug("\n----Text INFO----\n" + "Total Number of Characters：" + charCount + "\nText MAP：");

		Iterator<Map.Entry<Integer, Integer>> it = map.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<Integer, Integer> entry = it.next();
			logger.debug(entry.getKey() + " " + entry.getValue());
		}

	}
	
	public int getCharCount() {
		
		return charCount;
		
	}
	
	public Map<Integer, Integer> getMap(){
		
		return map;
		
	}
	

}
