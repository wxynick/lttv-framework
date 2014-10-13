/**
 * 
 */
package com.letv.mobile.core.api;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.letv.mobile.core.microkernel.api.IKernelContext;
import com.letv.mobile.core.microkernel.api.IKernelModule;
import com.letv.mobile.core.microkernel.api.IMicroKernel;

/**
 * @author  
 *
 */
public interface IApplication<C extends IKernelContext, M extends IKernelModule<C>> extends IMicroKernel<C, M> {
	
	//ExecutorService getExecutor();
	
	void runOnUIThread(Runnable task, long delay, TimeUnit unit);
	
	void runOnUIThread(Runnable task);
	
	boolean isInDebugMode();
	
	boolean isCurrentUIThread();
	
	
}
