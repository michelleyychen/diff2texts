package api;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

import entity.ResponseBody;
import libs.BaseException;

/**
 * An exception AOP handler supported by Jersey AOP Provider
 *
 * @author Rach (Racheal Chen)
 * 
 */

@Provider
public class ExceptionMapperSupport implements ExceptionMapper<Exception>{
	
	private static final Logger logger = Logger.getLogger(ExceptionMapperSupport.class); 
	
	@Context Request request;
	@Context UriInfo uriInfo;
	
	/**
	 * Return additional information in response to HTTP request.
	 * The additional information contains the request URL, 
	 * an error code with error message.
	 * This can be achieved by building an ResponseBody entity
	 * as part of the response.  
	 */
	@Override
	public Response toResponse(Exception e) {
		
		ResponseBody body = new ResponseBody();
		body.setRequestUrl(request.getMethod() + " " + uriInfo.getAbsolutePath().toString());
		
		Status statusCode = Status.INTERNAL_SERVER_ERROR;
		
		if(e instanceof BaseException) {
			BaseException be = (BaseException) e;
			statusCode = be.statusCode;
			body.setErrorCode(be.errorCode);
			body.setMsg(be.msg);
		} else{
			logger.error(body, e);
		}
		
		return Response.status(statusCode).header("Content-type", "application/json;charset=UTF-8")
				.entity(body).type("application/json;charset=UTF-8").build();
	
	}

}
