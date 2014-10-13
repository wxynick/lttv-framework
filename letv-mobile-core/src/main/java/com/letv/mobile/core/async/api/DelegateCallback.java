package com.letv.mobile.core.async.api;
/**
 * 
 * @author  
 *
 * @param <S> source return type
 * @param <T> target return type
 */
public abstract class DelegateCallback<S,T> implements IAsyncCallback<S> {
	
		private final IAsyncCallback<T> delegate;
		
		public DelegateCallback(IAsyncCallback<T> cb){
			this.delegate = cb;
		}

		/**
		 * @param result
		 * @see com.letv.mobile.core.async.api.IAsyncCallback#success(java.lang.Object)
		 */
		public void success(S result) {
			delegate.success(getTargetValue(result));
		}
		
		protected abstract T getTargetValue(S value);

		/**
		 * @param cause
		 * @see com.letv.mobile.core.async.api.IAsyncCallback#failed(java.lang.Throwable)
		 */
		public void failed(Throwable cause) {
			delegate.failed(cause);
		}

		/**
		 * 
		 * @see com.letv.mobile.core.async.api.IAsyncCallback#cancelled()
		 */
		public void cancelled() {
			delegate.cancelled();
		}

		/**
		 * @param cancellable
		 * @see com.letv.mobile.core.async.api.IAsyncCallback#setCancellable(com.letv.mobile.core.async.api.ICancellable)
		 */
		public void setCancellable(ICancellable cancellable) {
			delegate.setCancellable(cancellable);
		}
	}