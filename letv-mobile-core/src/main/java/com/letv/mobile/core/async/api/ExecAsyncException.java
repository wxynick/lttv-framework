/**
 * 
 */
package com.letv.mobile.core.async.api;


/**
 * @author  
 *
 */
public class ExecAsyncException extends RuntimeException {
	
	private static final long serialVersionUID = 1482973930520876263L;
	
	private final AsyncFuture<?> taskControl;
	private final String title;
	
	
	public ExecAsyncException(AsyncFuture<?> control){
		this.taskControl = control;
		this.title = null;
	}

	public ExecAsyncException(AsyncFuture<?> control,String title,String message){
		super(message);
		this.taskControl = control;
		this.title = title;
	}


	/**
	 * @return the taskControl
	 */
	public AsyncFuture<?> getTaskControl() {
		return taskControl;
	}


	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	
	
}
