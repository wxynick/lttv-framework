/**
 * 
 */
package com.letv.mobile.core.async.api;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.letv.mobile.core.microkernel.api.KUtils;

/**
 * @author  
 *
 */
public class AsyncFuture<V> implements Future<V>,IAsyncTaskControl<V> {
	
	private Async<V> async;
	private final CountDownLatch latch;
	private V result;
	private Throwable throwable;
	private ICancellable cancellable;
	private CallbackLink<V> cbLink = new CallbackLink<V>();
	private boolean cancelled;
	
	public AsyncFuture(){
		this.latch = new CountDownLatch(1);
	}
	
	
	public AsyncFuture(Async<V> async){
		this();
		this.async = async;
		startProcess();
	}

	public AsyncFuture(final Callable<V> call){
		this();
		if(call == null){
			throw new IllegalArgumentException("Invald callable object : NULL!");
		}
		this.async = new Async<V>() {

			@Override
			public void onResult(final IAsyncCallback<V> cb) {
				if(call instanceof ICancellable){
					cb.setCancellable((ICancellable)call);
				}
				KUtils.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						try {
							V result = call.call();
							cb.success(result);
						} catch (Throwable e) {
							cb.failed(e);
						}
					}
				});
			}
		};
		startProcess();
	}

	protected void startProcess() {
		if(this.async != null){
			this.async.onResult(getInternalCallback());
		}
	}
	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#cancel(boolean)
	 */
	@Override
	public boolean cancel(boolean arg0) {
		if(this.cancellable == null){
			return false;
		}else{
			this.cancellable.cancel();
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#get()
	 */
	@Override
	public V get() throws InterruptedException, ExecutionException {
		this.latch.await();
		if(this.throwable != null){
			throw new ExecutionException(throwable);
		}
		return this.result;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		this.latch.await(timeout, unit);
		if(this.throwable != null){
			throw new ExecutionException(throwable);
		}
		return this.result;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#isCancelled()
	 */
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Future#isDone()
	 */
	@Override
	public boolean isDone() {
		return this.latch.getCount() == 0L;
	}
	
	public InProgressTask getProgress() {
		if(this.cancellable instanceof InProgressTask){
			return (InProgressTask)this.cancellable;
		}else{
			return null;
		}
	}

	/**
	 * @return the callback
	 */
	public IAsyncCallback<V> getInternalCallback() {
		return new IAsyncCallback<V>() {

				@Override
				public void success(V res) {
					result = res;
					if(cbLink != null){
						cbLink.success(result);
					}
					latch.countDown();
				}

				@Override
				public void failed(Throwable cause) {
					throwable = cause;
					if(cbLink != null){
						cbLink.failed(cause);
					}
					latch.countDown();
				}

				@Override
				public void setCancellable(ICancellable can) {
					cancellable = can;
					if(cbLink != null){
						cbLink.setCancellable(can);
					}
				}

				@Override
				public void cancelled() {
					cancelled = true;
					if(cbLink != null){
						cbLink.cancelled();
					}
					latch.countDown();
				}
			};
	}

	public static <S,V> AsyncFuture<V> newAsyncFuture(final Async<S> async, final IDataConverter<S, V> dataConverter) {
		
		return new AsyncFuture<V>(new Async<V>() {

			@Override
			public void onResult(final IAsyncCallback<V> callback) {
				 async.onResult(new IAsyncCallback<S>() {

					@Override
					public void success(S result) {
						try {
							callback.success(dataConverter.convert(result));
						}catch(NestedRuntimeException e){
							callback.failed(e.getCause());
						}
					}

					@Override
					public void failed(Throwable cause) {
						callback.failed(cause);
					}

					@Override
					public void cancelled() {
						callback.cancelled();
					}

					@Override
					public void setCancellable(ICancellable cancellable) {
						callback.setCancellable(cancellable);
					}
				});
			}
		});
	}


	/**
	 * @param linkCallback the linkCallback to set
	 */
	@Override
	public void onResult(IAsyncCallback<V> cb) {
		if(cb != null){
			addCallback(cb);
		}
//		if(this.async != null){
//			this.async.onResult(getInternalCallback());
//		}
	}


	@Override
	public ICancellable getCancellable() {
		return this.cancellable;
	}


	/**
	 * @param cb
	 * @see com.letv.mobile.core.async.api.CallbackLink#addCallback(com.letv.mobile.core.async.api.IAsyncCallback)
	 */
	public void addCallback(IAsyncCallback<V> cb) {
		if(this.cbLink == null){
			this.cbLink = new CallbackLink<V>();
		}
		cbLink.addCallback(cb);
	}


	/**
	 * @param cb
	 * @return
	 * @see com.letv.mobile.core.async.api.CallbackLink#removeCallback(com.letv.mobile.core.async.api.IAsyncCallback)
	 */
	public boolean removeCallback(IAsyncCallback<V> cb) {
		return cbLink != null ? cbLink.removeCallback(cb) : false;
	}

}
