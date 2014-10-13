/**
 * 
 */
package com.letv.mobile.core.command.api;

import com.letv.mobile.core.command.annotation.ConstraintLiteral;

/**
 * @author  
 *
 */
public interface ICommandValidator {
	void init(ICommandExecutionContext ctx);
	void checkCommandConstraints(ICommand<?> command) throws CommandConstraintViolatedException;
	void validationConstraints(ConstraintLiteral... constraints);
	void destroy();
}
