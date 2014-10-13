/**
 * 
 */
package com.letv.mobile.core.event.api;

/**
 * @author  
 *
 */
public interface IStreamEventListener {
	void onEvent(IStreamEvent event,IListenerChain chain);
}
