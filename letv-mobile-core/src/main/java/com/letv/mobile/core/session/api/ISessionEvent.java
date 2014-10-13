/**
 * 
 */
package com.letv.mobile.core.session.api;

import com.letv.mobile.core.event.api.IBroadcastEvent;

/**
 * @author  
 *
 */
public interface ISessionEvent extends IBroadcastEvent {
	ISession getSession();
	SessionAction getAction();
}
