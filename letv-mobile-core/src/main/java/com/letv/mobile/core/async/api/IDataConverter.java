/**
 * 
 */
package com.letv.mobile.core.async.api;

/**
 * @author  
 *
 */
public interface IDataConverter<S,T> {
	T convert(S value) throws NestedRuntimeException;
}
