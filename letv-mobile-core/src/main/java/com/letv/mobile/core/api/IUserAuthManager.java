/**
 * 
 */
package com.letv.mobile.core.api;

/**
 * @author  
 *
 */
public interface IUserAuthManager {
	IUserAuthCredential getAuthCredential(String host, String realm);
}
