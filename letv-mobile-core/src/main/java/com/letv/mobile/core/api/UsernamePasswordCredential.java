/**
 * 
 */
package com.letv.mobile.core.api;

/**
 * @author  
 *
 */
public class UsernamePasswordCredential implements IUserAuthCredential {

	private String userName,password;
	
	
	
	public UsernamePasswordCredential() {
		super();
	}


	public UsernamePasswordCredential(String userName) {
		super();
		this.userName = userName;
	}

	
	public UsernamePasswordCredential(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
	}


	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.api.IUserAuthCredential#getUserName()
	 */
	@Override
	public String getUserName() {
		return this.userName;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.api.IUserAuthCredential#getAuthPassword()
	 */
	@Override
	public String getAuthPassword() {
		return this.password;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @param password the password to set
	 */
	public void setAuthPassword(String password) {
		this.password = password;
	}

}
