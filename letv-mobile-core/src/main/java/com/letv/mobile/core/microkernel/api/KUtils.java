/**
 * 
 */
package com.letv.mobile.core.microkernel.api;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.letv.mobile.core.api.ApplicationFactory;
import com.letv.mobile.core.api.IApplication;

/**
 * @author  
 *
 */
public abstract class KUtils {
	public static IApplication<?, ?> getApplication() {
		return ApplicationFactory.getInstance().getApplication();
	}
	
	public static <T> T getService(Class<T> clazz) {
		return getApplication().getService(clazz);
	}
	
	public static void runOnUIThread(Runnable task, long delay, TimeUnit unit) {
		getApplication().runOnUIThread(task, delay, unit);
	}
	
	public static void runOnUIThread(Runnable task) {
		getApplication().runOnUIThread(task);
	}
	
	public static void invokeLater(Runnable task,long delay, TimeUnit unit){
		getApplication().invokeLater(task, delay, unit);
	}
	
	public static void invokeLater(Runnable task){
		getApplication().invokeLater(task,0L,null);
	}

//	public static Future<?> executeTask(Runnable task){
//		return getApplication().getExecutor().submit(task);
//	}
//	
//	public static <V> Future<V> executeTask(Callable<V> task){
//		return getApplication().getExecutor().submit(task);
//	}
	
	
	public static boolean isCurrentUIThread() {
		return getApplication().isCurrentUIThread();
	}


}
