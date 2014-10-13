/**
 * 
 */
package com.letv.mobile.core.rpc.rest;

import com.letv.mobile.core.async.api.IAsyncCallback;
import com.letv.mobile.core.async.api.ICancellable;

/**
 * @author  
 *
 */
public class InstantCallback<T> implements IAsyncCallback<T> {

	private final IAsyncCallback<T> delegate;
	/**
	 * 
	 */
	public InstantCallback(IAsyncCallback<T> nested) {
		this.delegate = nested;
		if(this.delegate == null){
			throw new IllegalArgumentException("Delegate callback cannot be NULL !");
		}
	}
	/**
	 * @param result
	 * @see com.wxxr.mobile.core.async.api.IAsyncCallback#success(java.lang.Object)
	 */
	public void success(T result) {
		delegate.success(result);
	}
	/**
	 * @param cause
	 * @see com.wxxr.mobile.core.async.api.IAsyncCallback#failed(java.lang.Throwable)
	 */
	public void failed(Throwable cause) {
		delegate.failed(cause);
	}
	/**
	 * 
	 * @see com.wxxr.mobile.core.async.api.IAsyncCallback#cancelled()
	 */
	public void cancelled() {
		delegate.cancelled();
	}
	/**
	 * @param cancellable
	 * @see com.wxxr.mobile.core.async.api.IAsyncCallback#setCancellable(com.wxxr.mobile.core.async.api.ICancellable)
	 */
	public void setCancellable(ICancellable cancellable) {
		delegate.setCancellable(cancellable);
	}

}
