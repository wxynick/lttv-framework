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
public class HttpParamsBean {
	private Map<String, Object> map;
	
	protected Map<String, Object> getMap() {
		if(this.map == null){
			this.map = new HashMap<String, Object>();
		}
		return this.map;
	}
	public HttpParamsBean setHttpMethod(HttpMethod method){
		getMap().put(ParamConstants.PARAMETER_KEY_HTTP_METHOD, method);
		return this;
	}
	
	public Map<String, Object> toMap() {
		return this.map;
	}
}
