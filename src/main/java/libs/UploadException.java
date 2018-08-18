package libs;

import javax.ws.rs.core.Response.Status;

/**
 *	
 * @author Rach (Racheal Chen)
 * 
 */

public class UploadException extends BaseException{

	private static final long serialVersionUID = 1L;
	
	public UploadException(Status statusCode, int errorCode, String msg) {
		
		super(statusCode, errorCode, msg);
		
	}
	
	public UploadException() {
		
	}
	
}
