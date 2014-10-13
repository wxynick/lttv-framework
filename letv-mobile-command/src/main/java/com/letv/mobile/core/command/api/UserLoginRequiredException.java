/**
 * 
 */
package com.letv.mobile.core.command.api;

import com.letv.mobile.core.command.annotation.ConstraintLiteral;

/**
 * @author  
 *
 */
public class UserLoginRequiredException extends
		CommandConstraintViolatedException {

	private static final long serialVersionUID = -7748662050296439546L;

	public UserLoginRequiredException(ConstraintLiteral ann) {
		super(ann);
	}

}
