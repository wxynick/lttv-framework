/**
 * 
 */
package com.letv.mobile.core.common;

import java.util.HashMap;
import java.util.Map;

import com.letv.mobile.core.event.api.GenericEventObject;
import com.letv.mobile.core.event.api.IBroadcastEvent;
import com.letv.mobile.core.event.api.IEventListener;
import com.letv.mobile.core.event.api.IEventRouter;
import com.letv.mobile.core.microkernel.api.AbstractModule;
import com.letv.mobile.core.microkernel.api.IKernelContext;
import com.letv.mobile.core.security.api.IUserIdentityChangedEvent;
import com.letv.mobile.core.security.api.IUserIdentityManager;
import com.letv.mobile.core.session.api.ISession;
import com.letv.mobile.core.session.api.ISessionEvent;
import com.letv.mobile.core.session.api.ISessionManager;
import com.letv.mobile.core.session.api.SessionAction;

/**
 * @author  
 *
 */
public class UserBasedSessionManagerModule<C extends IKernelContext> extends AbstractModule<C> implements
		ISessionManager,IEventListener {
	
	private static class SessionEvent extends GenericEventObject implements ISessionEvent {
		
		private final SessionAction action;
		
		public SessionEvent(ISession session, SessionAction action) {
			setSource(session);
			this.action = action;
		}

		@Override
		public ISession getSession() {
			return (ISession)getSource();
		}

		@Override
		public SessionAction getAction() {
			return this.action;
		}
		
	}
	private static class UserSession implements ISession {
		
		private final String userId;
		
		private Map<String, Object> attrs = new HashMap<String, Object>();
		
		public UserSession(String id){
			if(id == null){
				throw new IllegalArgumentException("Invalid user id : NULL !");
			}
			this.userId = id;
		}

		@Override
		public Object getValue(String name) {
			return this.attrs.get(name);
		}

		@Override
		public Object removeValue(String name) {
			return this.attrs.remove(name);
		}

		@Override
		public void setValue(String name, Object val) {
			this.attrs.put(name, val);
		}

		@Override
		public String[] getValueNames() {
			return this.attrs.isEmpty() ? null : this.attrs.keySet().toArray(new String[0]);
		}

		@Override
		public String getSessionId() {
			return userId;
		}
		
		public void destroy() {
			this.attrs.clear();
			this.attrs = null;
		}

		@Override
		public boolean hasValue(String name) {
			return this.attrs.containsKey(name);
		}
		
	}
	
	private IUserIdentityManager userMgr;
	private IEventRouter eventRouter;
	private UserSession current;
	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.session.api.ISessionManager#getCurrentSession(boolean)
	 */
	@Override
	public ISession getCurrentSession(boolean createIfNotExisting) {
		String userId = validateCurrentSession();
		if((this.current == null)&&createIfNotExisting){
			this.current = new UserSession(userId);
			this.eventRouter.routeEvent(new SessionEvent(this.current, SessionAction.CREATE));
		}
		return this.current;
	}

	/**
	 * @return
	 */
	protected String validateCurrentSession() {
		String userId = this.userMgr.getUserId();
		String currentId = this.current != null ? this.current.getSessionId() : null;
		if((currentId != null)&&(!currentId.equals(userId))){
			this.eventRouter.routeEvent(new SessionEvent(this.current, SessionAction.DESTROY));
			this.current.destroy();
			this.current = null;
		}
		return userId;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.session.api.ISessionManager#destroySession(com.wxxr.mobile.core.session.api.ISession)
	 */
	@Override
	public void destroySession(ISession session) {
		this.eventRouter.routeEvent(new SessionEvent(session, SessionAction.DESTROY));
		if(session instanceof UserSession){
			((UserSession)session).destroy();
		}
		if(this.current == session){
			this.current = null;
		}

	}

	@Override
	protected void initServiceDependency() {
		addRequiredService(IEventRouter.class);
		addRequiredService(IUserIdentityManager.class);
	}

	@Override
	protected void startService() {
		this.eventRouter = context.getService(IEventRouter.class);
		this.userMgr = context.getService(IUserIdentityManager.class);
		this.eventRouter.registerEventListener(IUserIdentityChangedEvent.class, this);
		context.registerService(ISessionManager.class, this);
	}

	@Override
	protected void stopService() {
		context.unregisterService(ISessionManager.class, this);
		IEventRouter router = context.getService(IEventRouter.class);
		if(router != null){
			router.unregisterEventListener(IUserIdentityChangedEvent.class, this);
		}
		this.eventRouter = null;
		this.userMgr = null;
	}

	@Override
	public void onEvent(IBroadcastEvent event) {
		if(event instanceof IUserIdentityChangedEvent){
			validateCurrentSession();
		}
		
	}

}
