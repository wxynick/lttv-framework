/**
 * 
 */
package com.letv.mobile.core.rpc.http.api;

import java.util.Map;

import com.letv.mobile.core.rpc.api.Response;

/**
 * @author  
 *
 */
public interface HttpResponse extends Response {
	
	Map<String, String> getHeaders();
	
	String getHeader(String key);
	
	int getStatusCode();
  /**
   * Returns the status message text.
   * 
   * @return the status message text
   */
	String getStatusText();
		

}
