/**
 * 
 */
package com.letv.mobile.core.command.api;

import com.letv.mobile.core.microkernel.api.IKernelContext;

/**
 * @author  
 *
 */
public interface ICommandExecutionContext {
	IKernelContext getKernelContext();
}
