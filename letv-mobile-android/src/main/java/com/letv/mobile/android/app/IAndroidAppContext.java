/**
 * 
 */
package com.letv.mobile.android.app;

import com.letv.mobile.core.microkernel.api.IKernelContext;

/**
 * @author  
 *
 */
public interface IAndroidAppContext extends IKernelContext {
	@SuppressWarnings("rawtypes")
	IAndroidFramework getApplication();
}
