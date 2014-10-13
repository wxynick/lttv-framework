/**
 * 
 */
package com.letv.mobile.core.async.api;

/**
 * @author  
 *
 */
public interface InProgressTask {
	void registerProgressMonitor(IProgressMonitor monitor);
	boolean unregisterProgressMonitor(IProgressMonitor monitor);
}
