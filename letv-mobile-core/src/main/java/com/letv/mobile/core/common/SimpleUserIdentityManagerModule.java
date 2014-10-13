/**
 * 
 */
package com.letv.mobile.core.common;

import com.letv.mobile.core.event.api.GenericEventObject;
import com.letv.mobile.core.event.api.IBroadcastEvent;
import com.letv.mobile.core.event.api.IEventListener;
import com.letv.mobile.core.event.api.IEventRouter;
import com.letv.mobile.core.microkernel.api.AbstractModule;
import com.letv.mobile.core.microkernel.api.IKernelContext;
import com.letv.mobile.core.security.api.IUserIdentityChangedEvent;
import com.letv.mobile.core.security.api.IUserIdentityManager;
import com.letv.mobile.core.security.api.IUserLoginEvent;

/**
 * @author  
 *
 */
public class SimpleUserIdentityManagerModule<C extends IKernelContext> extends AbstractModule<C> implements
		IUserIdentityManager,IEventListener {

	private static class UserIdentityChangedEvent extends GenericEventObject implements IUserIdentityChangedEvent {

		public UserIdentityChangedEvent(IUserIdentityManager src){
			setSource(src);
			setNeedSyncProcessed(false);
		}
		
		@Override
		public IUserIdentityManager getIdentityManager() {
			return (IUserIdentityManager)getSource();
		}	
	}
	private String userId;
	private IEventRouter router;
	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.security.api.IUserIdentityManager#isUserAuthenticated()
	 */
	@Override
	public boolean isUserAuthenticated() {
		return (this.userId != null) && (this.userId.equals(UNAUTHENTICATED_USER_ID) == false);
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.security.api.IUserIdentityManager#getUserId()
	 */
	@Override
	public String getUserId() {
		return this.userId != null ? this.userId : UNAUTHENTICATED_USER_ID;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.security.api.IUserIdentityManager#usrHasRoles(java.lang.String[])
	 */
	@Override
	public boolean usrHasRoles(String... roles) {
		return false;
	}

	@Override
	protected void initServiceDependency() {
		addRequiredService(IEventRouter.class);
	}

	@Override
	protected void startService() {
		this.router = context.getService(IEventRouter.class);
		this.router.registerEventListener(IUserLoginEvent.class, this);
		context.registerService(IUserIdentityManager.class, this);
	}

	@Override
	protected void stopService() {
		context.unregisterService(IUserIdentityManager.class, this);
		this.router.unregisterEventListener(IUserLoginEvent.class, this);
		this.router = null;
	}


	@Override
	public void onEvent(IBroadcastEvent event) {
		if(event instanceof IUserLoginEvent){
			IUserLoginEvent evt = (IUserLoginEvent)event;
			switch(evt.getAction()){
			case LOGIN:
				this.userId = evt.getUserId();
				this.router.routeEvent(new UserIdentityChangedEvent(this));
				break;
			case LOGOUT:
				if(evt.getUserId().equals(this.userId)){
					this.userId = null;
					this.router.routeEvent(new UserIdentityChangedEvent(this));
				}
				break;
			}
		}
		
	}

}
