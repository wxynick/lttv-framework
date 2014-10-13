/**
 * 
 */
package com.letv.mobile.core.command.api;

/**
 * @author  
 *
 */
public class UnsupportedCommandException extends CommandException {

	private static final long serialVersionUID = 2700028276883254084L;

	public UnsupportedCommandException() {
		super();
	}

	public UnsupportedCommandException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedCommandException(String message) {
		super(message);
	}

	public UnsupportedCommandException(Throwable cause) {
		super(cause);
	}

}
