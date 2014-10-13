/**
 * 
 */
package com.letv.mobile.core.security.api;

import com.letv.mobile.core.event.api.IBroadcastEvent;

/**
 * @author  
 *
 */
public interface IUserIdentityChangedEvent extends IBroadcastEvent {
	IUserIdentityManager getIdentityManager();
}
