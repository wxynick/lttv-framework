/**
 * 
 */
package com.letv.mobile.core.command.annotation;

import com.letv.mobile.core.util.StringUtils;

/**
 * @author  
 *
 */
public class SecurityConstraintLiteral extends ConstraintLiteral{
	private String[] allowRoles;
	
	public SecurityConstraintLiteral(){
		
	}
	
	public SecurityConstraintLiteral(String[] roles){
		this.allowRoles = roles;
	}
	
	public SecurityConstraintLiteral(String roleString){
		String[] roles = StringUtils.split(roleString, ',');
		this.allowRoles = roles;
	}


	/**
	 * @return the allowRoles
	 */
	public String[] getAllowRoles() {
		return allowRoles;
	}

	/**
	 * @param allowRoles the allowRoles to set
	 */
	public void setAllowRoles(String[] allowRoles) {
		this.allowRoles = allowRoles;
	}
	
	public String getAllowRoleString() {
		if((this.allowRoles == null)||(this.allowRoles.length == 0)){
			return null;
		}
		return StringUtils.join(this.allowRoles,',');
	}
	
	public static SecurityConstraintLiteral fromAnnotation(SecurityConstraint constraint){
		return new SecurityConstraintLiteral(constraint.allowRoles());
	}
	
}
