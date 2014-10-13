/**
 * 
 */
package com.letv.mobile.core.command.api;

import com.letv.mobile.core.command.annotation.ConstraintLiteral;

/**
 * @author  
 *
 */
public class UserNotAuthorizedException extends
		CommandConstraintViolatedException {

	private static final long serialVersionUID = -8042836859630341295L;

	public UserNotAuthorizedException(ConstraintLiteral ann) {
		super(ann);
	}

}
