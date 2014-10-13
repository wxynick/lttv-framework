/**
 * 
 */
package com.letv.mobile.core.async.api;

/**
 * @author  
 *
 */
public interface Async<T> {
	void onResult(IAsyncCallback<T> callback);
}
