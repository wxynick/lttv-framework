/*
 * @(#)IMicroKernal.java May 18, 2011 Copyright 2004-2011 WXXR Network
 * Technology Co. Ltd. All rights reserved. WXXR PROPRIETARY/CONFIDENTIAL.
 */

package com.letv.mobile.core.microkernel.api;

import java.util.concurrent.TimeUnit;

import com.letv.mobile.core.async.api.ICancellable;

public interface IMicroKernel<C extends IKernelContext, T extends IKernelModule<C>> {

   public void registerKernelModule(T module);

   public void unregisterKernelModule(T module);
   
   ModuleStatus[] getAllRegisteredModules();
   
   <S> S getService(Class<S> interfaceClazz);
   
   <S> IServiceFuture<S> getServiceAsync(Class<S> interfaceClazz);
   
   <S> void checkServiceAvailable(Class<S> interfaceClazz, IServiceAvailableCallback<S> callback);

   Object[] getAllModules();
   
   void addModuleListener(IModuleListener listener);
   
   boolean removeModuleListener(IModuleListener listener);

	ICancellable invokeLater(Runnable task, long delay, TimeUnit unit);


}