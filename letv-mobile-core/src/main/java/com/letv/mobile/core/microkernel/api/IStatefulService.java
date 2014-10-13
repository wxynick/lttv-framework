/**
 * 
 */
package com.letv.mobile.core.microkernel.api;

/**
 * @author  
 *
 */
public interface IStatefulService extends Cloneable {
	Object clone();
	void destroy(Object serviceHandler);
	IServiceDecoratorBuilder getDecoratorBuilder();
}
