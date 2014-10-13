/**
 * 
 */
package com.letv.mobile.core.async.api;

/**
 * @author  
 *
 */
public class NestedRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 310419493916078442L;

	/**
	 * @param throwable
	 */
	public NestedRuntimeException(Throwable throwable) {
		super(throwable);
	}

}
