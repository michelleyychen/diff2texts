package entity;

import java.io.Serializable;

/**
 *	
 * @author Rach (Racheal Chen)
 * 
 */

public class ResponseBody implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int errorCode = 999;
	private String msg = "sorry, there's a mistake.";
	private String requestUrl = null;
	private Object data = null;
	
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public Object getData() {
		return data;
	}
	
}
