package com.letv.mobile.core.microkernel.api;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ServiceFuture<T> implements Future<T>, IServiceFuture<T> {

	private final AbstractMicroKernel<?, ?> kernel;
	private final Class<T> clazz;
	private CountDownLatch latch = new CountDownLatch(1);
	private Map<String, T> services = new HashMap<String, T>();

	public ServiceFuture(AbstractMicroKernel<?,?> k,Class<T> klass){
		this.kernel = k;
		this.clazz = klass;
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return this.latch.getCount() == 0;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.microkernel.api.IServiceFuture#getServices(T[])
	 */
	@Override
	public T[] getServices(T[] vals) {
		synchronized(this.services){
			return this.services.values().toArray(vals);
		}
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.microkernel.api.IServiceFuture#getService()
	 */
	@Override
	public T getService() {
		synchronized(this.services){
			return this.services.isEmpty() ? null : this.services.values().iterator().next();
		}
	}

	@Override
	public T get() throws InterruptedException {
		try {
			return get(-1,null);
		} catch (TimeoutException e) {	// should not happen
			return null;
		}	
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException,TimeoutException {
		synchronized(this.services){
			if(this.services.size() > 0){
				return this.services.values().iterator().next();
			}else if(this.latch == null){
				this.latch = new CountDownLatch(1);
			}
		}
		if(timeout == -1){
			this.latch.await();
		}else{
			if (!this.latch.await(timeout,unit)) {
				throw new TimeoutException("Timeout when waiting for service!!!");
			}

		}
		synchronized(this.services){
			return this.services.values().iterator().next();
		}	
	}

	@Override
	public boolean addService(T object) {
		String id = getObjectKey(object);		
		synchronized(this.services){
			object = this.kernel.createServicePluginChain().invokeNext(this.clazz, object);
			this.services.put(id,object);
			if((this.latch != null)&&(this.latch.getCount() > 0)){
				this.latch.countDown();
				this.latch = null;
			}
			return true;
		}
	}

	/**
	 * @param object
	 * @return
	 */
	protected String getObjectKey(Object object) {
		return object.getClass().getSimpleName()+System.identityHashCode(object); 	// hopefully the return string is unique in this service
	}

	@Override
	public boolean removeService(T object) {
		String id = getObjectKey(object);
		synchronized(this.services){
			return this.services.remove(id) != null;
		}
	}
}
