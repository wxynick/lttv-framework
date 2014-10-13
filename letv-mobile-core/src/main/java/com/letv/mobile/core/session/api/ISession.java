/**
 * 
 */
package com.letv.mobile.core.session.api;

/**
 * @author  
 *
 */
public interface ISession {
	Object getValue(String name);
	Object removeValue(String name);
	void setValue(String name, Object val);
	String[] getValueNames();
	boolean hasValue(String name);
	String getSessionId();
}
