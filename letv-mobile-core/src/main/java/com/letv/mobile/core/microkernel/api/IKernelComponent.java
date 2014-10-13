/**
 * 
 */
package com.letv.mobile.core.microkernel.api;

/**
 * @author  
 *
 */
public interface IKernelComponent {
	void init(IKernelContext context);
	void destroy();
}
