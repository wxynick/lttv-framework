/**
 * 
 */
package com.letv.mobile.core.rpc.api;

/**
 * @author  
 *
 */
public interface Request<T extends Response> {
	void invokeAsync(RequestCallback<T,Request<T>> callback);
	
	void setRequestEntity(DataEntity entity);
	
	DataEntity getRequestEntity();

}
