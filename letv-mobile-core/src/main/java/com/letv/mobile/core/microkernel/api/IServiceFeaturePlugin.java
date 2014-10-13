/**
 * 
 */
package com.letv.mobile.core.microkernel.api;

/**
 * @author  
 *
 */
public interface IServiceFeaturePlugin {
	<T> T buildServiceHandler(Class<T> serviceInterface, Object handler,IServicePluginChain chain);
}
