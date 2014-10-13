/**
 * 
 */
package com.letv.mobile.core.microkernel.api;

/**
 * @author  
 *
 */
public interface IServiceDecoratorBuilder {
	<T> T createServiceDecorator(Class<T> clazz, IServiceDelegateHolder<T> holder);
}
