package libs;

import javax.ws.rs.core.Response.Status;

/**
 *	
 * @author Rach (Racheal Chen)
 * 
 */

public class NotFoundError extends BaseException{

	private static final long serialVersionUID = 1L;
	
	public NotFoundError(Status statusCode, int errorCode, String msg) {
		
		super(statusCode, errorCode, msg);
		
	}
	
	public NotFoundError() {
		
	}
}
