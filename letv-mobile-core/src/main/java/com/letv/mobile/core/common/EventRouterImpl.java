/*
 * @(#)SimpleEventRouter.java	 May 18, 2011
 *
 * Copyright 2004-2011 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */

package com.letv.mobile.core.common;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.letv.mobile.core.async.api.AsyncFuture;
import com.letv.mobile.core.event.api.IBroadcastEvent;
import com.letv.mobile.core.event.api.IEventListener;
import com.letv.mobile.core.event.api.IEventObject;
import com.letv.mobile.core.event.api.IEventRouter;
import com.letv.mobile.core.event.api.IEventSelector;
import com.letv.mobile.core.event.api.IListenerChain;
import com.letv.mobile.core.event.api.IStreamEvent;
import com.letv.mobile.core.event.api.IStreamEventListener;
import com.letv.mobile.core.log.api.Trace;
import com.letv.mobile.core.microkernel.api.IKernelComponent;
import com.letv.mobile.core.microkernel.api.IKernelContext;

/**
 * @class desc A EventRouterModule.
 * 
 * @author wxy
 * @version v1.0 
 * @created time May 18, 2011  3:49:19 PM
 */
public class EventRouterImpl implements IEventRouter,IKernelComponent {

	private static final Trace log = Trace.register(EventRouterImpl.class);
	
	private LinkedList<ListenerHolder> listeners = new LinkedList<ListenerHolder>();
	private LinkedList<WeakReference<IStreamEventListener>> streamListeners = new LinkedList<WeakReference<IStreamEventListener>>();
	private IKernelContext context;
	
	private static class ListenerHolder {
		WeakReference<IEventSelector> selector;
		WeakReference<IEventListener> listener;
		EventTypeSelector ref;
	}

	private static class EventTypeSelector implements IEventSelector {

		private Class<? extends IEventObject> type;

		public EventTypeSelector(Class<? extends IEventObject> eventType){
			this.type = eventType;
		}

		public boolean isEventApply(IEventObject event) {
			if(this.type == null){
				return true;
			}
			return this.type.isAssignableFrom(event.getClass());
		}     
	}

	private class EventBroadcaster implements Callable<Object> {

		private final IBroadcastEvent event;
		private final ListenerHolder[] allParties;

		public EventBroadcaster(IBroadcastEvent evt, ListenerHolder[] parties){
			this.event = evt;
			this.allParties = parties;
		}

		public Object call() {
			for (ListenerHolder h : allParties) {
				if(h != null){
					if((h.listener.get() == null)||((h.selector != null)&&(h.selector.get() == null))){
						continue;
					}
					
					if((h.selector == null)||h.selector.get().isEventApply(event)){
						handleEvent(h.listener.get(),event);
					}
				}
			}
			return null;
		}

	}
	
	private class EventPipeLine implements Callable<Object>,IListenerChain {

		private final IStreamEvent event;
		private final WeakReference<IStreamEventListener>[] allParties;
		private Map<String, Object> attrs;
		private int idx = 0;

		public EventPipeLine(IStreamEvent evt, WeakReference<IStreamEventListener>[] parties){
			this.event = evt;
			this.allParties = parties;
		}

		public Object call() {
			invokeNext(event);
			return null;
		}

		@Override
		public void invokeNext(IStreamEvent event) {
			IStreamEventListener l = null;
			for(;idx < allParties.length;idx++){
				WeakReference<IStreamEventListener> ref = allParties[idx];
				if((ref != null)&&(ref.get() != null)){
					l = ref.get();
					break;
				}
			}
			if(l != null){
				idx++;
				l.onEvent(event, this);
			}
			return;
			
		}

		@Override
		public void setAttribute(String name, Object val) {
			if(this.attrs == null){
				this.attrs = new HashMap<String, Object>();
			}
			this.attrs.put(name, val);
		}

		@Override
		public Object getAttribute(String name) {
			return this.attrs != null ? this.attrs.get(name) : null;
		}

		@Override
		public List<String> getAttributeNames() {
			return this.attrs != null ? new ArrayList<String>(this.attrs.keySet()) : null;
		}

	}


	private synchronized ListenerHolder[] getAllListeners() {
		return this.listeners.toArray(new ListenerHolder[this.listeners.size()]);
	}
	
	@SuppressWarnings("unchecked")
	private synchronized WeakReference<IStreamEventListener>[] getAllStreamListeners() {
		return this.streamListeners.toArray(new WeakReference[this.streamListeners.size()]);
	}


	private synchronized ListenerHolder findListener(Class<? extends IEventObject> eventType, IEventListener listener) {
		for (ListenerHolder l : listeners) {
			if(l == null){
				continue;
			}
			if(l.listener.get() != listener){
				continue;
			}
			if(!(l.selector.get() instanceof EventTypeSelector)){
				continue;
			}
			if((eventType == null)&&(((EventTypeSelector)l.selector.get()).type != null)){
				continue;
			}
			if((eventType != null)&&(eventType.equals(((EventTypeSelector)l.selector.get()).type) != true)){
				continue;
			}
			return l;
		}
		return null;
	}

	private synchronized ListenerHolder findListener(IEventSelector selector, IEventListener listener) {
		for (ListenerHolder l : listeners) {
			if(l == null){
				continue;
			}
			if(l.listener.get() != listener){
				continue;
			}
			if(((selector == null)&&(l.selector== null))||(selector == l.selector.get())){
				return l;
			}
		}
		return null;
	}


	public synchronized void registerEventListener(Class<? extends IEventObject> eventType, IEventListener listener) {
		ListenerHolder l = findListener(eventType, listener);
		if(l == null){
			l = new ListenerHolder();
			if(eventType != null){
				l.ref = new EventTypeSelector(eventType);
				l.selector = new WeakReference<IEventSelector>(l.ref);
			}
			l.listener = new WeakReference<IEventListener>(listener);
			listeners.add(l);
		}
	}

	public synchronized boolean unregisterEventListener(Class<? extends IEventObject> eventType, IEventListener listener) {
		ListenerHolder l = findListener(eventType, listener);
		if(l != null){
			listeners.remove(l);
			return true;
		}
		return false;

	}

	public synchronized void registerEventListener(IEventSelector selector,
			IEventListener listener) {
		ListenerHolder l = findListener(selector, listener);
		if(l == null){
			l = new ListenerHolder();
			if(selector != null){
				l.selector = new WeakReference<IEventSelector>(selector);
			}
			l.listener = new WeakReference<IEventListener>(listener);
			listeners.add(l);
		}

	}

	public synchronized boolean unregisterEventListener(IEventSelector selector,
			IEventListener listener) {
		ListenerHolder l = findListener(selector, listener);
		if(l != null){
			listeners.remove(l);
			return true;
		}
		return false;

	}
	
	protected void handleEvent(IEventListener listener,IBroadcastEvent event) {
		listener.onEvent(event);
	}



	public Object routeEvent(IEventObject event) {
	
		Callable<Object> call = null;
		if(event instanceof IBroadcastEvent){
			call = new EventBroadcaster((IBroadcastEvent)event, getAllListeners());
		}else{
			call = new EventPipeLine((IStreamEvent)event, getAllStreamListeners());
		}
		if(log.isDebugEnabled()){
			log.debug("Going to route event :"+event);
		}
		if(event.needSyncProcessed()){
			try {
				return call.call();
			} catch (Exception e) {
				return null;
			}
		}else{
			return new AsyncFuture<Object>(call);
		}
	}

	
	private WeakReference<IStreamEventListener> findListener(IStreamEventListener listener){
		for (WeakReference<IStreamEventListener> l : this.streamListeners) {
			if(l.get() == listener){
				return l;
			}
		}
		return null;
	}


	private boolean hasListener(IStreamEventListener listener){
		return findListener(listener) != null;
	}
	@Override
	public synchronized void addListenerFirst(IStreamEventListener listener) {
		if(!hasListener(listener)){
			this.streamListeners.addFirst(new WeakReference<IStreamEventListener>(listener));
		}
	}

	@Override
	public synchronized void addListenerLast(IStreamEventListener listener) {
		if(!hasListener(listener)){
			this.streamListeners.addLast(new WeakReference<IStreamEventListener>(listener));
		}
	}

	@Override
	public synchronized boolean removeListener(IStreamEventListener listener) {
		WeakReference<IStreamEventListener> l = findListener(listener);
		return l != null ? this.streamListeners.remove(l) : false;
	}

	@Override
	public void init(IKernelContext context) {
		this.context = context;
	}

	@Override
	public void destroy() {
		this.listeners.clear();
		this.listeners.clear();
		this.context = null;
	}

}
