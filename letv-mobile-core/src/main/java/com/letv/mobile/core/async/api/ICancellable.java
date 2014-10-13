/**
 * 
 */
package com.letv.mobile.core.async.api;

/**
 * @author  
 *
 */
public interface ICancellable {
	void cancel();
	boolean isCancelled();
}
