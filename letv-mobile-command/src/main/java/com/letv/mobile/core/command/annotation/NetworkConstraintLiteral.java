/**
 * 
 */
package com.letv.mobile.core.command.annotation;

import com.letv.mobile.core.util.StringUtils;


/**
 * @author  
 *
 */
public class NetworkConstraintLiteral extends ConstraintLiteral{
	private NetworkConnectionType[] allowConnectionTypes;
	
	public NetworkConstraintLiteral(){
		
	}
	
	public NetworkConstraintLiteral(NetworkConnectionType[] types){
		if(types == null){
			throw new IllegalArgumentException("Invalid connection types : NULL");
		}
		this.allowConnectionTypes = types;
	}
	
	public NetworkConstraintLiteral(String[] types){
		if(types == null){
			throw new IllegalArgumentException("Invalid connection types : NULL");
		}
		this.allowConnectionTypes = new NetworkConnectionType[types.length];
		for (int i=0 ; i < types.length; i++) {
			this.allowConnectionTypes[i] = NetworkConnectionType.valueOf(types[i]);
		}
	}
	
	public NetworkConstraintLiteral(String typeString){
		if((typeString == null)||StringUtils.isBlank(typeString)){
			throw new IllegalArgumentException("Invalid connection types : NULL");
		}
		String[] types = StringUtils.split(typeString, ',');
		this.allowConnectionTypes = new NetworkConnectionType[types.length];
		for (int i=0 ; i < types.length; i++) {
			this.allowConnectionTypes[i] = NetworkConnectionType.valueOf(types[i]);
		}
	}


	/**
	 * @return the allowConnectionTypes
	 */
	public NetworkConnectionType[] getAllowConnectionTypes() {
		return allowConnectionTypes;
	}

	/**
	 * @param allowConnectionTypes the allowConnectionTypes to set
	 */
	public void setAllowConnectionTypes(NetworkConnectionType[] allowConnectionTypes) {
		this.allowConnectionTypes = allowConnectionTypes;
	}
	
	public static NetworkConstraintLiteral fromAnnotation(NetworkConstraint ann){
		return new NetworkConstraintLiteral(ann.allowConnectionTypes());
	}
	
	public String getAllowConnectionTypeString() {
		if((this.allowConnectionTypes == null)||(this.allowConnectionTypes.length == 0)){
			return null;
		}
		StringBuffer buf = new StringBuffer();
		int cnt = 0;
		for (NetworkConnectionType type : this.allowConnectionTypes) {
			if(cnt > 0){
				buf.append(',');
			}
			buf.append(type.name());
			cnt++;
		}
		return buf.toString();
	}
	
}
