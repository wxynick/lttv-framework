/**
 * 
 */
package com.letv.mobile.core.async.api;

import java.util.ArrayList;
import java.util.List;

/**
 * @author  
 *
 */
public class CallbackLink<T> implements IAsyncCallback<T> {
	
	private static final int STATUS_FAILED = 1;
	private static final int STATUS_SUCCESS = 2;
	private static final int STATUS_CANCELLED = 3;
	private static final int STATUS_WAITING = 0;
	
	private List<IAsyncCallback<T>> link;
	private int status;
	private Throwable error;
	private T result;
	private ICancellable cancellable;
	
	public synchronized void addCallback(IAsyncCallback<T> cb){
		if(cb == null){
			throw new IllegalArgumentException("Invalid callback NULL !");
		}
		if(this.link == null){
			this.link = new ArrayList<IAsyncCallback<T>>();
		}
		if(!this.link.contains(cb)){
			this.link.add(cb);
			switch(this.status){
			case STATUS_CANCELLED:
				cb.cancelled();
				break;
			case STATUS_FAILED:
				cb.failed(error);
				break;
			case STATUS_SUCCESS:
				cb.success(result);
				break;
			case STATUS_WAITING:
				cb.setCancellable(this.cancellable);
				break;
			}
		}
	}
	
	public synchronized boolean removeCallback(IAsyncCallback<T> cb){
		if(this.link != null){
			return this.link.remove(cb);
		}
		return false;
	}
	
	private synchronized IAsyncCallback<T>[] getCallbacks() {
		return this.link !=null ? this.link.toArray(new IAsyncCallback[0]) : null;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.async.api.IAsyncCallback#success(java.lang.Object)
	 */
	@Override
	public synchronized void success(T result) {
		IAsyncCallback<T>[] cbs = getCallbacks();
		if(cbs != null){
			for (IAsyncCallback<T> iAsyncCallback : cbs) {
				iAsyncCallback.success(result);
			}
		}
		this.status = STATUS_SUCCESS;
		this.result = result;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.async.api.IAsyncCallback#failed(java.lang.Throwable)
	 */
	@Override
	public synchronized void failed(Throwable cause) {
		IAsyncCallback<T>[] cbs = getCallbacks();
		if(cbs != null){
			for (IAsyncCallback<T> iAsyncCallback : cbs) {
				iAsyncCallback.failed(cause);
			}
		}
		this.status = STATUS_FAILED;
		this.error = cause;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.async.api.IAsyncCallback#cancelled()
	 */
	@Override
	public synchronized void cancelled() {
		IAsyncCallback<T>[] cbs = getCallbacks();
		if(cbs != null){
			for (IAsyncCallback<T> iAsyncCallback : cbs) {
				iAsyncCallback.cancelled();
			}
		}
		this.status = STATUS_CANCELLED;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.async.api.IAsyncCallback#setCancellable(com.wxxr.mobile.core.async.api.ICancellable)
	 */
	@Override
	public void setCancellable(ICancellable cancellable) {
		IAsyncCallback<T>[] cbs = getCallbacks();
		if(cbs != null){
			for (IAsyncCallback<T> iAsyncCallback : cbs) {
				iAsyncCallback.setCancellable(cancellable);
			}
		}
		this.cancellable = cancellable;
	}

}
