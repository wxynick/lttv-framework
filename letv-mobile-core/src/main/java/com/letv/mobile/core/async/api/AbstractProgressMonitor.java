/**
 * 
 */
package com.letv.mobile.core.async.api;


/**
 * @author  
 *
 */
public abstract class AbstractProgressMonitor implements IProgressMonitor {

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.api.IProgressMonitor#beginTask(int)
	 */
	@Override
	public void beginTask(int arg0) {
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.api.IProgressMonitor#done(java.lang.Object)
	 */
	@Override
	public void done(Object arg0) {
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.api.IProgressMonitor#setTaskName(java.lang.String)
	 */
	@Override
	public void setTaskName(String arg0) {
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.api.IProgressMonitor#taskCanceled(boolean)
	 */
	@Override
	public void taskCanceled(boolean arg0) {
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.api.IProgressMonitor#taskFailed(java.lang.Throwable)
	 */
	@Override
	public void taskFailed(Throwable arg0, String message) {

	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.api.IProgressMonitor#updateProgress(int)
	 */
	@Override
	public void updateProgress(int arg0,String message) {

	}

}
