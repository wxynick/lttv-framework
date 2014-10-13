/**
 * 
 */
package com.letv.mobile.core.session.api;

/**
 * @author  
 *
 */
public interface ISessionManager {
	ISession getCurrentSession(boolean createIfNotExisting);
	void destroySession(ISession session);
}
