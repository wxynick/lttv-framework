/**
 * 
 */
package com.letv.mobile.core.microkernel.api;

/**
 * @author  
 *
 */
public interface IKernelServiceListener {
	<T> void serviceRegistered(Class<T> clazz, T handler);
	<T> void serviceUnregistered(Class<T> clazz, T handler);
	boolean accepts(Class<?> clazz);
}
