package com.letv.mobile.core.event.api;
public interface IEventRouter {
	void registerEventListener(Class<? extends IEventObject> eventType,IEventListener listener);
	boolean unregisterEventListener(Class<? extends IEventObject> eventType,IEventListener listener);
	void registerEventListener(IEventSelector selector,IEventListener listener);
	boolean unregisterEventListener(IEventSelector selector,IEventListener listener);
	
	void addListenerFirst(IStreamEventListener listener);
	void addListenerLast(IStreamEventListener listener);
	boolean removeListener(IStreamEventListener listener);
	
	/**
	 * 
	 * @param event
	 * @return 同步事件返回Null，异步事件返回Future<Object>
	 */
	Object routeEvent(IEventObject event);
}
