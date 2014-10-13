/**
 * 
 */
package com.letv.mobile.core.common;

import java.util.HashMap;
import java.util.Map;

import com.letv.mobile.core.event.api.IBroadcastEvent;
import com.letv.mobile.core.event.api.IEventListener;
import com.letv.mobile.core.event.api.IEventRouter;
import com.letv.mobile.core.microkernel.api.IKernelComponent;
import com.letv.mobile.core.microkernel.api.IKernelContext;
import com.letv.mobile.core.microkernel.api.IServiceDelegateHolder;
import com.letv.mobile.core.microkernel.api.IServiceFeaturePlugin;
import com.letv.mobile.core.microkernel.api.IServicePluginChain;
import com.letv.mobile.core.microkernel.api.IStatefulService;
import com.letv.mobile.core.session.api.ISession;
import com.letv.mobile.core.session.api.ISessionEvent;
import com.letv.mobile.core.session.api.ISessionManager;
import com.letv.mobile.core.session.api.SessionAction;

/**
 * @author  
 *
 */
public class StatefullServicePlugin implements IServiceFeaturePlugin,IEventListener,IKernelComponent {

	private String ATTRIBUTE_KEY = "_statefull_service_";
	
	private static class SessionHandlerHolder {
		IStatefulService prototype;
		Object handler;
	}
	
	private class ServiceHandlerHolder<T> implements IServiceDelegateHolder<T> {

		private final IStatefulService prototype;
		private final Class<T> clazz;
		
		public ServiceHandlerHolder(Class<T> klass, IStatefulService service){
			this.clazz = klass;
			this.prototype = service;
		}
		
		@Override
		public T getDelegate() {
			return this.clazz.cast(getStatefulServiceHandler(this.prototype));
		}
		
		public T createService() {
			return this.prototype.getDecoratorBuilder().createServiceDecorator(clazz, this);
		}
		
	}
	
	
	private ISessionManager sessionMgr;
	private IKernelContext kCtx;
	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.microkernel.api.IServiceDecorator#decorateServiceHandler(java.lang.Class, java.lang.Object, com.wxxr.mobile.core.microkernel.api.IServiceDecoratorChain)
	 */
	@Override
	public <T> T buildServiceHandler(Class<T> serviceInterface,
			Object handler, IServicePluginChain chain) {
		if(handler instanceof IStatefulService){
			handler = new ServiceHandlerHolder<T>(serviceInterface,(IStatefulService)handler).createService();
		}
		return chain.invokeNext(serviceInterface, handler);
	}

	/**
	 * @param handler
	 * @return
	 */
	protected Object getStatefulServiceHandler(Object handler) {
		Map<String, SessionHandlerHolder> map = getHandlerMap();
		String key = String.valueOf(System.identityHashCode(handler));
		SessionHandlerHolder val = map.get(key);
		if(val == null){
			val = new SessionHandlerHolder();
			val.handler = ((IStatefulService)handler).clone();
			val.prototype = (IStatefulService)handler;
			map.put(key, val);
		}
		handler = val.handler;
		return handler;
	}

	@Override
	public void onEvent(IBroadcastEvent event) {
		if(event instanceof ISessionEvent){
			ISessionEvent sessionEvent = (ISessionEvent)event;
			if(sessionEvent.getAction() == SessionAction.DESTROY){
				ISession session = sessionEvent.getSession();
				@SuppressWarnings("unchecked")
				Map<String, SessionHandlerHolder> map = (Map<String, SessionHandlerHolder>)session.removeValue(ATTRIBUTE_KEY);
				if((map != null)&&(map.size() > 0)){
					for (SessionHandlerHolder holder : map.values()) {
						holder.prototype.destroy(holder.handler);
					}
				}
				map.clear();
			}
		}
		
	}
	
	protected void registerListener() {
		IEventRouter router = kCtx.getService(IEventRouter.class);
		router.registerEventListener(ISessionEvent.class, this);
	}
	
	protected void unregisterListener() {
		IEventRouter router = kCtx.getService(IEventRouter.class);
		if(router != null){
			router.unregisterEventListener(ISessionEvent.class, this);
		}
	}

	@SuppressWarnings("unchecked")
	protected Map<String, SessionHandlerHolder> getHandlerMap(){
		ISession session = getSessionManager().getCurrentSession(true);
		Map<String, SessionHandlerHolder> map = (Map<String, SessionHandlerHolder>)session.getValue(ATTRIBUTE_KEY);
		if(map == null){
			map = new HashMap<String, SessionHandlerHolder>();
			session.setValue(ATTRIBUTE_KEY, map);
		}
		return map;
	}

	protected ISessionManager getSessionManager() {
		if(this.sessionMgr == null){
			this.sessionMgr = kCtx.getService(ISessionManager.class);
		}
		return this.sessionMgr;
	}
	
	@Override
	public void init(IKernelContext context) {
		this.kCtx = context;
		registerListener();
	}

	@Override
	public void destroy() {
		unregisterListener();
		this.kCtx = null;
	}

}
