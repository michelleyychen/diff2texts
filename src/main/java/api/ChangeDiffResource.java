package api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import entity.ChangeDiff;
import entity.ChangeDiffs;
import entity.FFile;
import libs.UploadException;
import service.DiffService;

/**
 * A REST resource which provides the different characters and 
 * their locations between two texts, allowing for errors.  
 * Location of a character is solved as row and column, which
 * can determine the character uniquely in the text.
 * 
 * @author Rach (Racheal Chen)
 * 
 */

/**
 * The resource class only defines POST resource method,
 * and the method work with an entity that is an instance of ChangeDiffs.
 */
@Path("/diffs")
public class ChangeDiffResource {
	
	private String[] getFiles(HttpServletRequest request) throws FileUploadException, IOException {
		
		String[] saveLocations = new String[2];
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload fileUpload = new ServletFileUpload(factory);
		
		List<FileItem> items = new ArrayList<>();
		
		try {
			items = fileUpload.parseRequest(request);
		} catch (FileUploadException e) {
			throw e;
		}
		
		if(items.size() != 2 || items.get(0).getName().equals("") || items.get(0).getName().equals("")) {
			throw new UploadException(Status.BAD_REQUEST, 1009, "Uncorrect uploaded files.");
		}
						
		for (int i = 0; i < items.size(); i++) {
			FileItem item = items.get(i);				
			if (!item.isFormField()) {
				String saveLocation = "/uploadFile-" + i;  
				saveLocations[i] = saveLocation;				
				try(InputStream is = item.getInputStream()){
					org.apache.commons.io.FileUtils.copyInputStreamToFile(is, new File(saveLocation));
				} catch(IOException e) {
					throw e;
				}
			}
		}

		return saveLocations;
		
	}
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({"application/json;charset=UTF-8"})
	public ChangeDiffs getDiffs(@Context HttpServletRequest request) throws FileUploadException, IOException {
		
		String[] saveLocations = getFiles(request);
		String f1_saveLocation = saveLocations[0];
		String f2_saveLocation = saveLocations[1];
		FFile f1 = new FFile();
		FFile f2 = new FFile();
		
		f1.setLineListContent(f1_saveLocation);
		f1.setCharCountMap();
		f2.setLineListContent(f2_saveLocation);
		f2.setCharCountMap();

		DiffService ds = new DiffService();	
		List<ChangeDiff> list = ds.diff_main(f1, f2, true);
		
		final ChangeDiffs changeDiffs = new ChangeDiffs();
		changeDiffs.setChangeDiffList(list);
		
		File file = new File(f1_saveLocation);
		file.delete();
		file = new File(f2_saveLocation);
		file.delete();
		
		return changeDiffs;
		
	}
	
}
