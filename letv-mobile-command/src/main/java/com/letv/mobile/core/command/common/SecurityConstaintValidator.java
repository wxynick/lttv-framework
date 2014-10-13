/**
 * 
 */
package com.letv.mobile.core.command.common;

import com.letv.mobile.core.command.annotation.ConstraintLiteral;
import com.letv.mobile.core.command.annotation.SecurityConstraint;
import com.letv.mobile.core.command.annotation.SecurityConstraintLiteral;
import com.letv.mobile.core.command.api.CommandConstraintViolatedException;
import com.letv.mobile.core.command.api.ICommand;
import com.letv.mobile.core.command.api.ICommandExecutionContext;
import com.letv.mobile.core.command.api.ICommandValidator;
import com.letv.mobile.core.command.api.UserLoginRequiredException;
import com.letv.mobile.core.command.api.UserNotAuthorizedException;
import com.letv.mobile.core.security.api.IUserIdentityManager;

/**
 * @author  
 *
 */
public class SecurityConstaintValidator implements ICommandValidator {

	private ICommandExecutionContext context;
	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.command.api.ICommandValidator#init(com.wxxr.mobile.core.command.api.ICommandExecutionContext)
	 */
	public void init(ICommandExecutionContext ctx) {
		this.context = ctx;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.command.api.ICommandValidator#checkCommandConstraints(com.wxxr.mobile.core.command.api.ICommand)
	 */
	public void checkCommandConstraints(ICommand<?> command)
			throws CommandConstraintViolatedException {
		SecurityConstraint constraint = command.getClass().getAnnotation(SecurityConstraint.class);
		if(constraint == null){
			return;
		}
		doValidation(SecurityConstraintLiteral.fromAnnotation(constraint));
	}

	/**
	 * @param constraint
	 */
	protected void doValidation(SecurityConstraintLiteral constraint) {
		String[] allowedRoles= constraint.getAllowRoles();
		IUserIdentityManager mgr = context.getKernelContext().getService(IUserIdentityManager.class);
		if(mgr == null){
			return;
		}
		if(!mgr.isUserAuthenticated()){
			throw new UserLoginRequiredException(constraint);
		}
		if((allowedRoles == null)||(allowedRoles.length == 0)){
			return;
		}
		if(!mgr.usrHasRoles(allowedRoles)){
			throw new UserNotAuthorizedException(constraint);
		}
	}

	protected SecurityConstraintLiteral getSecurityConstraint(ConstraintLiteral... constraints){
		if((constraints == null)||(constraints.length == 0)){
			return null;
		}
		for (ConstraintLiteral con : constraints) {
			if(con instanceof SecurityConstraintLiteral){
				return (SecurityConstraintLiteral)con;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.command.api.ICommandValidator#destroy()
	 */
	public void destroy() {
		this.context = null;
	}

	public void validationConstraints(ConstraintLiteral... constraints) {
		SecurityConstraintLiteral constraint = getSecurityConstraint(constraints);
		if(constraint != null){
			doValidation(constraint);
		}
		
	}

}
