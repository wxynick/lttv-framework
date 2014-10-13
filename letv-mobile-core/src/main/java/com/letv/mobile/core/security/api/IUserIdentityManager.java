/**
 * 
 */
package com.letv.mobile.core.security.api;

/**
 * @author  
 *
 */
public interface IUserIdentityManager {
	String UNAUTHENTICATED_USER_ID = "anonymouse";
	
	boolean isUserAuthenticated();
	String getUserId();
	boolean usrHasRoles(String... roles);
}
