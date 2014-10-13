/*
 * @(#)IKernelContext.java	 Sep 21, 2010
 *
 * Copyright 2004-2010 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */

package com.letv.mobile.core.microkernel.api;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.letv.mobile.core.async.api.ICancellable;


public interface IKernelContext {
	@SuppressWarnings("rawtypes")
	IMicroKernel getKernel();
	
	<T> void registerService(Class<T> interfaceClazz,T handler);
	<T> void unregisterService(Class<T> interfaceClazz,T handler);
	
	<T> T getService(Class<T> interfaceClazz);
	<T> IServiceFuture<T> getServiceAsync(Class<T> interfaceClazz);
	<T> void checkServiceAvailable(Class<T> interfaceClazz, IServiceAvailableCallback<T> callback);

	void addKernelServiceListener(IKernelServiceListener listener);
	boolean removeKernelServiceListener(IKernelServiceListener listener);

	void setAttribute(String key, Object value);
	Object removeAttribute(String key);
	Object getAttribute(String key);

//	ExecutorService getExecutor();

	ICancellable invokeLater(Runnable task, long delay, TimeUnit unit);

}
