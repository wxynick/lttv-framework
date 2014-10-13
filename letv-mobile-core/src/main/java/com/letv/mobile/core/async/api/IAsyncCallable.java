/**
 * 
 */
package com.letv.mobile.core.async.api;

/**
 * @author  
 *
 */
public interface IAsyncCallable<V> {
	void call(IAsyncCallback<V> callback);
}
