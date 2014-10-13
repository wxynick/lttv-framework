/**
 * 
 */
package com.letv.mobile.android.app;

import java.lang.Thread.UncaughtExceptionHandler;

import com.letv.mobile.core.log.api.Trace;

/**
 * @author  
 *
 */
public class UnexpectingExceptionHandler implements UncaughtExceptionHandler {
	private static final Trace log = Trace.register(UnexpectingExceptionHandler.class);
		
	private Thread uiThread;
	private UncaughtExceptionHandler defaultHandler;

	
	public UnexpectingExceptionHandler(){
		this.uiThread = Thread.currentThread();
		this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
	}
	
	public Thread getUiThread() {
		return this.uiThread;
	}
	/* (non-Javadoc)
	 * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
	 */
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		if(t == uiThread){
			log.fatal("Caught unexpecting exception on ui thread, that's critical!",e);
			this.defaultHandler.uncaughtException(t, e);
		}else{
			log.error("Caught unexpecting exception on thread :"+t+", uiThread :"+uiThread,e);
		}

	}

	/**
	 * @return the defaultHandler
	 */
	public UncaughtExceptionHandler getDefaultHandler() {
		return defaultHandler;
	}

}
