package libs;

import javax.ws.rs.core.Response.*;

/**
 * Base exception.
 * @author Rach
 *
 */

public class BaseException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public Status statusCode = Status.INTERNAL_SERVER_ERROR;
	public int errorCode = 999;
	public String msg = "sorry, there's a mistake.";
	
	public BaseException(Status statusCode, int errorCode, String msg) {
		
		this.statusCode = statusCode;
		this.errorCode = errorCode;
		this.msg = msg;
		
	}
	
	public BaseException() {
		
	}

}
