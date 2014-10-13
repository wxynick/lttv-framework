/**
 * 
 */
package com.letv.mobile.core.async.api;


/**
 * @author  
 *
 */
public interface IAsyncCallback<T> {
	void success(T result);
	void failed(Throwable cause);
	void cancelled();
	void setCancellable(ICancellable cancellable);
}
