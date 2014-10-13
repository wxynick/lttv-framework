/**
 * 
 */
package com.letv.mobile.core.command.common;

import com.letv.mobile.core.api.IDataExchangeCoordinator;
import com.letv.mobile.core.command.annotation.ConstraintLiteral;
import com.letv.mobile.core.command.annotation.NetworkConnectionType;
import com.letv.mobile.core.command.annotation.NetworkConstraint;
import com.letv.mobile.core.command.annotation.NetworkConstraintLiteral;
import com.letv.mobile.core.command.api.CommandConstraintViolatedException;
import com.letv.mobile.core.command.api.ICommand;
import com.letv.mobile.core.command.api.ICommandExecutionContext;
import com.letv.mobile.core.command.api.ICommandValidator;
import com.letv.mobile.core.command.api.RequiredNetNotAvailablexception;

/**
 * @author  
 *
 */
public class NetworkConstaintValidator implements ICommandValidator {

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
		NetworkConstraint constraint = command.getClass().getAnnotation(NetworkConstraint.class);
		if(constraint == null){
			return;
		}
		doValidation(NetworkConstraintLiteral.fromAnnotation(constraint));
	}

	protected NetworkConstraintLiteral getNetworkConstaint(ConstraintLiteral... constraints){
		if((constraints == null)||(constraints.length == 0)){
			return null;
		}
		for (ConstraintLiteral con : constraints) {
			if(con instanceof NetworkConstraintLiteral){
				return (NetworkConstraintLiteral)con;
			}
		}
		return null;
	}
	/**
	 * @param constraint
	 * @param allowedTypes
	 */
	protected void doValidation(NetworkConstraintLiteral constraint) {
		IDataExchangeCoordinator coordinator = context.getKernelContext().getService(IDataExchangeCoordinator.class);
		if(coordinator == null){
			return;
		}
		int availableNetwork = coordinator.checkAvailableNetwork();
		NetworkConnectionType[] allowedTypes = constraint.getAllowConnectionTypes();
		if(((allowedTypes == null)||(allowedTypes.length == 0))&&(availableNetwork > 0)){
			return;
		}
		NetworkConnectionType availableNetworkType = NetworkConnectionType.byId(availableNetwork);
		for (NetworkConnectionType type : allowedTypes) {
			if(type == availableNetworkType){
				return;
			}
		}
		throw new RequiredNetNotAvailablexception(constraint);
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.command.api.ICommandValidator#destroy()
	 */
	public void destroy() {
		this.context = null;
	}

	public void validationConstraints(ConstraintLiteral... constraints) {
		NetworkConstraintLiteral constraint = getNetworkConstaint(constraints);
		if(constraint != null){
			doValidation(constraint);
		}
		
	}

}
