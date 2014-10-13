/**
 * 
 */
package com.letv.mobile.core.microkernel.api;

/**
 * @author  
 *
 */
public interface IModuleListener {
	void moduleRegistered(IKernelModule<?> module);
	void moduleUnregistered(IKernelModule<?> module);
	void kernelStarting();
	void kernelStarted();
	void kernelStopping();
	void kernelStopped();
}
