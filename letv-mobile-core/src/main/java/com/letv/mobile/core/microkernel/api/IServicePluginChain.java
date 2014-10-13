/**
 * 
 */
package com.letv.mobile.core.microkernel.api;

/**
 * @author  
 *
 */
public interface IServicePluginChain {
	<T> T invokeNext(Class<T> serviceInterface,Object serviceHandler);
}
