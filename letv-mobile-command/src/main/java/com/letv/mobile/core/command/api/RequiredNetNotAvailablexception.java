/**
 * 
 */
package com.letv.mobile.core.command.api;

import com.letv.mobile.core.command.annotation.ConstraintLiteral;

/**
 * @author  
 *
 */
public class RequiredNetNotAvailablexception extends
		CommandConstraintViolatedException {

	private static final long serialVersionUID = -4803874472598447075L;

	public RequiredNetNotAvailablexception(ConstraintLiteral ann) {
		super(ann);
	}

}
