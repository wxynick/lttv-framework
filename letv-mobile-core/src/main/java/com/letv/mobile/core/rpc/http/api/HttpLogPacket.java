/**
 * 
 */
package com.letv.mobile.core.rpc.http.api;

import java.util.HashMap;
import java.util.Map;

/**
 * @author  
 *
 */
public class HttpLogPacket {
	private final static  ThreadLocal<HttpLogPacket> local = new ThreadLocal<HttpLogPacket>();
	
	public static HttpLogPacket init() {
		HttpLogPacket p = new HttpLogPacket();
		local.set(p);
		return p;
	}
	
	public static HttpLogPacket destroy() {
		HttpLogPacket p = local.get();
		local.set(null);
		return p;
	}
	
	
	public static HttpLogPacket currentHttpLog() {
		return local.get();
	}
	
	
	private Map<String, String> requestHeaders, responseHeaders;
	private String requestLine, requestBody, responseBody;

	/**
	 * @return the requestLine
	 */
	public String getRequestLine() {
		return requestLine;
	}

	/**
	 * @return the requestBody
	 */
	public String getRequestBody() {
		return requestBody;
	}

	/**
	 * @return the responseBody
	 */
	public String getResponseBody() {
		return responseBody;
	}

	/**
	 * @param requestLine the requestLine to set
	 */
	public void setRequestLine(String requestLine) {
		this.requestLine = requestLine;
	}

	/**
	 * @param requestBody the requestBody to set
	 */
	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	/**
	 * @param responseBody the responseBody to set
	 */
	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}
	
	public void addRequestHeader(String name, String val){
		if(this.requestHeaders == null){
			this.requestHeaders = new HashMap<String, String>();
		}
		this.requestHeaders.put(name, val);
	}
	
	public void addResponseHeader(String name, String val){
		if(this.responseHeaders == null){
			this.responseHeaders = new HashMap<String, String>();
		}
		this.responseHeaders.put(name, val);
	}

	/**
	 * @return the requestHeaders
	 */
	public Map<String, String> getRequestHeaders() {
		return requestHeaders;
	}

	/**
	 * @return the responseHeaders
	 */
	public Map<String, String> getResponseHeaders() {
		return responseHeaders;
	}
	
	
	
}
