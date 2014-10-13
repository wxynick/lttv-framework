/**
 * 
 */
package com.letv.mobile.core.rpc.http.api;

import java.util.Map;

import com.letv.mobile.core.async.api.ICancellable;
import com.letv.mobile.core.rpc.api.Request;

/**
 * @author  
 *
 */
public interface HttpRequest extends Request<HttpResponse>,ICancellable {

	Map<String, String> getHeaders();
	
	String getHeader(String key);
	
	void setHeader(String key, String value);
	
	String getURI();
}
