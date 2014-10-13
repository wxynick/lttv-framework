/**
 * 
 */
package com.letv.mobile.core.async.api;



/**
 * @author  
 *
 */
public interface IAsyncTaskControl<T> extends Async<T> {
	/**
	 * 
	 * @return ICancellable instance if the task could be cancelled 
	 */
	ICancellable getCancellable();
	
	InProgressTask getProgress();
}
