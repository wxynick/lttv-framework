/*
 * @(#)AbstractMicroKernel.java	 Oct 22, 2010
 *
 * Copyright 2004-2010 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */

package com.letv.mobile.core.microkernel.api;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.letv.mobile.core.async.api.ICancellable;
import com.letv.mobile.core.async.api.IProgressMonitor;
import com.letv.mobile.core.common.EventRouterImpl;
import com.letv.mobile.core.event.api.ApplicationShutdownEvent;
import com.letv.mobile.core.event.api.ApplicationStartedEvent;
import com.letv.mobile.core.event.api.IEventRouter;
import com.letv.mobile.core.log.api.Trace;
import com.letv.mobile.core.util.StringUtils;


/**
 * @class desc A AbstractMicroKernel.
 * 
 * @author  
 * @version v1.0 
 * @created time Oct 22, 2010  10:06:58 AM
 */
public abstract class AbstractMicroKernel<C extends IKernelContext, M extends IKernelModule<C>> implements IMicroKernel<C,M>{
	private static Trace log = Trace.register(AbstractMicroKernel.class);
	protected IProgressMonitor[] startMonitorHolder = new IProgressMonitor[1];
	protected IProgressMonitor[] stopMonitorHolder = new IProgressMonitor[1];
	
	protected abstract class AbstractContext implements IKernelContext {

		@Override
		public Object getAttribute(String key) {
			return attributes.get(key);
		}

		@Override
		public <T> T getService(Class<T> interfaceClazz) {
			return getLocalService(interfaceClazz);
		}



		@Override
		public <T> IServiceFuture<T> getServiceAsync(Class<T> interfaceClazz) {
			return getServiceFuture(interfaceClazz);
		}

		@Override
		public <T> void registerService(Class<T> interfaceClazz, T handler) {
			if(log.isInfoEnabled()){
				log.info("Register service :%s, handler : %s",interfaceClazz.getCanonicalName(),handler);
			}
			if(!interfaceClazz.isInterface()){
				throw new IllegalArgumentException("pass in interface class is not a interface :"+interfaceClazz.getCanonicalName());
			}
			List<Class<?>> exportingServices = getExportingInterfaces(interfaceClazz);
			if(exportingServices.isEmpty()){
				exportingServices.add(interfaceClazz);
			}
			for (Class<?> clazz : exportingServices) {
				registerLocalService(clazz, handler);
			}
		}

		@Override
		public void addKernelServiceListener(IKernelServiceListener listener) {
			addLocalKernelServiceListener(listener);
		}


		@Override
		public boolean removeKernelServiceListener(IKernelServiceListener listener){
			return removeLocalKernelServiceListener(listener);
		}


		@Override
		public Object removeAttribute(String key) {
			return removeLocalAttribute(key);
		}

		@Override
		public void setAttribute(String key, Object value) {
			setLocalAttribute(key, value);
		}

		@Override
		public <T> void unregisterService(Class<T> interfaceClazz, T handler) {
			if(log.isInfoEnabled()){
				log.info("Unregister service :%s, handler : %s",interfaceClazz.getCanonicalName(),handler);
			}
			List<Class<?>> exportingServices = getExportingInterfaces(interfaceClazz);
			if(exportingServices.isEmpty()){
				exportingServices.add(interfaceClazz);
			}
			for (Class<?> clazz : exportingServices) {
				unregisterLocalService(clazz, handler);
			}
		}

		@Override
		public <T> void checkServiceAvailable(Class<T> interfaceClazz,
				IServiceAvailableCallback<T> callback) {
			addServiceAvailableCallback(interfaceClazz, callback);
		}

//		@Override
//		public ExecutorService getExecutor() {
//			return getExecutorService();
//		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public IMicroKernel getKernel() {
			return AbstractMicroKernel.this;
		}

		@Override
		public ICancellable invokeLater(final Runnable task, long delay, TimeUnit unit) {
			if(delay == 0L){
				invokeLater(task);
				return null;
			}else{
				final TimerTask t = new TimerTask() {
					
					@Override
					public void run() {
						try {
							invokeLater(task);
						}catch(Throwable t){
							log.warn("Caught throwable when execute scheduled task, task discarded :"+task, t);
						}
					}
				};
				getTimer().schedule(t, TimeUnit.MILLISECONDS.convert(delay, unit));
				return new ICancellable() {
					private boolean cancelled = false;
					@Override
					public boolean isCancelled() {
						return cancelled;
					}
					
					@Override
					public void cancel() {
						cancelled = t.cancel();
					}
				};
			}
		}
		
		public abstract void invokeLater(final Runnable task);
	};
	
	private class PluginChain implements IServicePluginChain {
		private int idx = 0;
		@Override
		public <T> T invokeNext(Class<T> serviceInterface, Object serviceHandler) {
			synchronized(plugins){
				if(idx >= plugins.size()){
					return serviceInterface.cast(serviceHandler);
				}else{
					return plugins.get(idx++).buildServiceHandler(serviceInterface, serviceHandler, this);
				}
			}
		}
	}
	
//	private Element moduleConfigure;

	private LinkedList<M> modules = new LinkedList<M>();

	private LinkedList<IServiceFeaturePlugin> plugins = new LinkedList<IServiceFeaturePlugin>();

//	private boolean started = false;
	
	private MStatus status = MStatus.INIT;

	private Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();

	private HashMap<Class<?>, IServiceFuture<?>> serviceHandlers = new HashMap<Class<?>, IServiceFuture<?>>();
	private HashMap<Class<?>, List<WeakReference<IServiceAvailableCallback<?>>>> callbacks = new HashMap<Class<?>, List<WeakReference<IServiceAvailableCallback<?>>>>();

	private LinkedList<IKernelServiceListener> serviceListeners = new LinkedList<IKernelServiceListener>();
	private LinkedList<IModuleListener> moduleListeners = new LinkedList<IModuleListener>();
	
	private Timer timer;

	private IEventRouter eventRouter;
	
	public AbstractMicroKernel() {
		initInternalServices();
	}
	
	public void attachStartMonitor(IProgressMonitor monitor){
		monitor.setTaskName("Startup Kernel");
		switch(this.status){
		case FAILED:
		case STARTED:
		case STOPED:
		case STOPPING:
				monitor.done(null);
				break;
		case INIT:
		case START_PENDING:
			synchronized(this.startMonitorHolder){
				this.startMonitorHolder[0] = monitor;
				this.startMonitorHolder.notifyAll();
			}
			break;
		case STARTING:
			synchronized(this.startMonitorHolder){
				this.startMonitorHolder[0] = monitor;
				if(getAllModules() != null){
					monitor.beginTask(getAllModules().length);
				}
			}
		}
	}
	
	public void attachStopMonitor(IProgressMonitor monitor){
		monitor.setTaskName("Shutdown Kernel");
		switch(this.status){
		case STOPED:
				monitor.done(null);
				break;
		case STOPPING:
			synchronized(this.stopMonitorHolder){
				this.stopMonitorHolder[0] = monitor;
				if(getAllModules() != null){
					monitor.beginTask(getAllModules().length);
				}
			}
			break;
		default:
			synchronized(this.stopMonitorHolder){
				this.stopMonitorHolder[0] = monitor;
				this.stopMonitorHolder.notifyAll();
			}
			break;
		}
	}

	protected IProgressMonitor getStartMonitor() {
		synchronized(this.startMonitorHolder){
			return this.startMonitorHolder[0];
		}
	}
	
	protected IProgressMonitor getStopMonitor() {
		synchronized(this.stopMonitorHolder){
			return this.stopMonitorHolder[0];
		}
	}


	protected Timer getTimer() {
		if(this.timer == null){
			timer = new Timer("MicroKernel Timer Thread");
		}
		return this.timer;
	}
	@SuppressWarnings("unchecked")
	public void start() throws Exception{
		initServicePlugins();
		IProgressMonitor monitor = null;
		setStatus(MStatus.STARTING);
		try {
			fireKernelStarting();
			initModules();
			IKernelModule<C>[] mods = getAllModules();
			if((monitor = getStartMonitor()) != null){
				monitor.beginTask(mods.length);
			}
			int cnt = 1;
			for (IKernelModule<C> mod : mods) {
				if((monitor = getStartMonitor()) != null){
					monitor.updateProgress(cnt, "启动模块 :["+getModuleName(mod)+"] ...");
				}
				startModule(((M)mod));
				cnt++;
			}
			for (IKernelModule<C> mod : mods) {
				if(mod instanceof AbstractModule){
					((AbstractModule<?>)mod).startServiceWithoutOptional();
				}
			}		
			setStatus(MStatus.STARTED);
			fireKernelStarted();
			if((monitor = getStartMonitor()) != null){
				monitor.done(this);
			}
		}catch(Exception e){
			setStatus(MStatus.FAILED);
			if((monitor = getStartMonitor()) != null){
				monitor.taskFailed(e,"Failed to start kernel");
			}
			throw e;
		}catch(Error e){
			setStatus(MStatus.FAILED);
			if((monitor = getStartMonitor()) != null){
				monitor.taskFailed(e,"Failed to start kernel");
			}
			throw e;
		}
	}

	/**
	 * @param mod
	 * @return
	 */
	protected String getModuleName(IKernelModule<C> mod) {
		String name = mod.getModuleName();
		if(StringUtils.isBlank(name)){
			return mod.getClass().getSimpleName();
		}else{
			return name;
		}
	}


	@SuppressWarnings("unchecked")
	public void stop(){
		IProgressMonitor monitor = null;
		setStatus(MStatus.STOPPING);
		try {
			fireKernelStopping();
	//		destroyModules();
			IKernelModule<C>[] mods = getAllModules();
			if((monitor = getStartMonitor()) != null){
				monitor.beginTask(mods.length);
			}
			int cnt = 1;
			for (IKernelModule<C> mod : getAllModules()) {
				if((monitor = getStartMonitor()) != null){
					monitor.updateProgress(cnt, "停止模块 :["+getModuleName(mod)+"] ...");
				}
				stopModule(((M)mod));
				cnt++;
			}
			if(timer != null){
				timer.purge();
				timer.cancel();
				timer = null;
			}
		}catch(Throwable t){
			log.error("Failed to stop kernel", t);
		}
		setStatus(MStatus.STOPED);
		fireKernelStopped();
		clearServiceFeaturePlugins();
		if((monitor = getStartMonitor()) != null){
			monitor.done(this);
		}
	}

	public void startLater(final int maxDelayTime, final TimeUnit unit) {
		new Thread("Kernel start Thread"){
			public void run() {
				synchronized(startMonitorHolder){
					try {
						startMonitorHolder.wait(TimeUnit.MILLISECONDS.convert(maxDelayTime, unit));
					} catch (InterruptedException e) {
						log.fatal("Kernel start thread was interrupted, abort kernel starting !");
					}
					try {
						AbstractMicroKernel.this.start();
					} catch (Throwable e) {
						log.fatal("Failed to start kernel :",e);
					}
				}
			}
		}.start();
	}


	protected <T>  void fireServiceRegistered(Class<T> clazz, T handler){
		IKernelServiceListener[] list = null;
		synchronized(this.serviceListeners){
			list = this.serviceListeners.toArray(new IKernelServiceListener[this.serviceListeners.size()]);
		}
		if(list != null){
			for (IKernelServiceListener l : list) {
				if(l.accepts(clazz)){
					l.serviceRegistered(clazz, handler);
				}
			}
		}
		List<WeakReference<IServiceAvailableCallback<?>>> cbs = null;
		synchronized(callbacks){
			cbs = callbacks.remove(clazz);
		}
		if(cbs != null){
			for (WeakReference<IServiceAvailableCallback<?>> ref : cbs) {
				@SuppressWarnings("unchecked")
				IServiceAvailableCallback<T> cb = (IServiceAvailableCallback<T>)ref.get();
				if(cb != null){
					cb.serviceAvailable(handler);
				}
			}
		}
	}

	protected <T>  void fireServiceUnregistered(Class<T> clazz, T handler){
		IKernelServiceListener[] list = null;
		synchronized(this.serviceListeners){
			list = this.serviceListeners.toArray(new IKernelServiceListener[this.serviceListeners.size()]);
		}
		if(list != null){
			for (IKernelServiceListener l : list) {
				if(l.accepts(clazz)){
					l.serviceUnregistered(clazz, handler);
				}
			}
		}

	}
	
	protected void fireModuleRegistered(M module){
		IModuleListener[] list = null;
		synchronized(this.moduleListeners){
			list = this.moduleListeners.toArray(new IModuleListener[this.moduleListeners.size()]);
		}
		if(list != null){
			for (IModuleListener l : list) {
					l.moduleRegistered(module);
			}
		}
	}

	
	protected void fireModuleUnregistered(M module){
		IModuleListener[] list = null;
		synchronized(this.moduleListeners){
			list = this.moduleListeners.toArray(new IModuleListener[this.moduleListeners.size()]);
		}
		if(list != null){
			for (IModuleListener l : list) {
					l.moduleUnregistered(module);
			}
		}
	}

	protected void fireKernelStarting(){
		IModuleListener[] list = null;
		synchronized(this.moduleListeners){
			list = this.moduleListeners.toArray(new IModuleListener[this.moduleListeners.size()]);
		}
		if(list != null){
			for (IModuleListener l : list) {
					l.kernelStarting();
			}
		}
	}

	protected void fireKernelStarted(){
		IModuleListener[] list = null;
		synchronized(this.moduleListeners){
			list = this.moduleListeners.toArray(new IModuleListener[this.moduleListeners.size()]);
		}
		if(list != null){
			for (IModuleListener l : list) {
					l.kernelStarted();
			}
		}
		synchronized(modules){
			for (M m : this.modules) {
				if(m instanceof AbstractModule){
					((AbstractModule<?>)m).notifyKernelStarted();
				}
			}
		}
		
		IEventRouter router = getService(IEventRouter.class);
		if(router != null){
			router.routeEvent(new ApplicationStartedEvent());
		}

	}

	protected void fireKernelStopping(){
		IEventRouter router = getService(IEventRouter.class);
		if(router != null){
			router.routeEvent(new ApplicationShutdownEvent());
		}
		IModuleListener[] list = null;
		synchronized(this.moduleListeners){
			list = this.moduleListeners.toArray(new IModuleListener[this.moduleListeners.size()]);
		}
		if(list != null){
			for (IModuleListener l : list) {
					l.kernelStopping();
			}
		}
	}

	
	protected void fireKernelStopped(){
		IModuleListener[] list = null;
		synchronized(this.moduleListeners){
			list = this.moduleListeners.toArray(new IModuleListener[this.moduleListeners.size()]);
		}
		if(list != null){
			for (IModuleListener l : list) {
					l.kernelStopped();
			}
		}
	}

	protected <T> void addServiceAvailableCallback(Class<T> clazz, IServiceAvailableCallback<T> cb){
		if((clazz == null)||(cb == null)){
			throw new IllegalArgumentException("Service class and callback cannot be NULL !");
		}
		T service = getLocalService(clazz);
		if(service != null){
			cb.serviceAvailable(service);
		}else{
			synchronized(this.callbacks){
				List<WeakReference<IServiceAvailableCallback<?>>> list = callbacks.get(clazz);
				if(list == null){
					list = new ArrayList<WeakReference<IServiceAvailableCallback<?>>>();
					callbacks.put(clazz, list);
				}
				list.add(new WeakReference<IServiceAvailableCallback<?>>(cb));
			}
		}
	}

	protected <T> T getLocalService(Class<T> interfaceClazz) {
		IServiceFuture<T> handlers = getServiceFuture(interfaceClazz);
		return handlers.getService();
	}

	protected <T> void registerLocalService(Class<T> interfaceClazz, Object handler) {
		if(getServiceFuture(interfaceClazz).addService(interfaceClazz.cast(handler))){
			fireServiceRegistered(interfaceClazz, interfaceClazz.cast(handler));
		}
	}

	protected List<Class<?>> getExportingInterfaces(Class<?> clazz){
		ArrayList<Class<?>> list = new ArrayList<Class<?>>();
		getExportingInterfaces(clazz, list);
		return list;
	}
	
	protected boolean isValidServiceInterface(Class<?> clazz){
		if(!clazz.isInterface()){
			return false;
		}
		String pkgName = clazz.getCanonicalName();
		if(pkgName.startsWith("java.")||pkgName.startsWith("javax.")||pkgName.startsWith("sun.")||pkgName.startsWith("com.sun.")){
			return false;
		}
		return true;
	}
	
	protected void getExportingInterfaces(Class<?> clazz,List<Class<?>> list){
		if(!isValidServiceInterface(clazz)){
			return;
		}
		Class<?>[] interfaces = clazz.getInterfaces();
		if(interfaces != null){
			for (Class<?> class1 : interfaces) {
				getExportingInterfaces(class1,list);
			}
		}
		list.add(clazz);
	}

	protected IServicePluginChain createServicePluginChain() {
		return new PluginChain();
	}
	
	@SuppressWarnings("unchecked")
	protected <T> IServiceFuture<T> getServiceFuture(final Class<T> interfaceClazz) {
		IServiceFuture<T> future = null;
		synchronized(serviceHandlers){
			future = (IServiceFuture<T>)serviceHandlers.get(interfaceClazz);
			if(future == null){
				future = new ServiceFuture<T>(this,interfaceClazz);
				serviceHandlers.put(interfaceClazz, future);
			}
		}
		return future;
	}
	
	protected boolean hasDecorators() {
		synchronized(this.plugins){
			return this.plugins.size() > 0;
		}
	}

	protected <T> void unregisterLocalService(Class<T> interfaceClazz, Object handler) {
		if(getServiceFuture(interfaceClazz).removeService(interfaceClazz.cast(handler))){
			fireServiceUnregistered(interfaceClazz, interfaceClazz.cast(handler));
		}
	}
	
	protected void setLocalAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	protected Object removeLocalAttribute(String key) {
		return attributes.remove(key);
	}

	protected void destroyInternalServices() {
		if(this.eventRouter instanceof IKernelComponent){
			((IKernelComponent)this.eventRouter).destroy();
		}
		this.eventRouter = null;
	}
	
	
	protected void initInternalServices() {
		this.serviceHandlers.put(IEventRouter.class, new IServiceFuture<IEventRouter>() {

			@Override
			public IEventRouter[] getServices(IEventRouter[] vals) {
				return new IEventRouter[] {  getService() };
			}

			@Override
			public IEventRouter getService() {
				if(eventRouter == null){
					eventRouter = createEventRouter();
					if(eventRouter instanceof IKernelComponent){
						((IKernelComponent)eventRouter).init(getContext());
					}
				}
				return eventRouter;
			}

			@Override
			public IEventRouter get(long timeout, TimeUnit unit)
					throws InterruptedException, TimeoutException {
				return getService();
			}

			@Override
			public IEventRouter get() throws InterruptedException {
				return getService();
			}

			@Override
			public boolean removeService(IEventRouter object) {
				throw new IllegalStateException("IEventRouter service is internal kernel serice !!!");
			}

			@Override
			public boolean addService(IEventRouter object) {
				throw new IllegalStateException("IEventRouter service is internal kernel serice !!!");
			}
		});
	}
	
	protected IEventRouter createEventRouter() {
		return new EventRouterImpl();
	}
	protected void addLocalKernelServiceListener(IKernelServiceListener listener) {
		synchronized(serviceListeners){
			if(!serviceListeners.contains(listener)){
				serviceListeners.add(listener);
			}
		}
	}


	protected boolean removeLocalKernelServiceListener(IKernelServiceListener listener){
		synchronized(serviceListeners){
			return serviceListeners.remove(listener);
		}
	}

	public void registerKernelModule(M module) {
		boolean added = false;
		synchronized(modules){
			if(!this.modules.contains(module)){
				this.modules.add(module);
				added = true;
			}
		}
		if(added){
			if(isStarted()){
				startModule(module);
			}
			fireModuleRegistered(module);
		}
	}

	/**
	 * @param module
	 */
	protected void startModule(M module) {
		if(log.isInfoEnabled()){
			log.info("Starting module :["+module+"] ...");
		}
		module.start(getContext());
		if(log.isInfoEnabled()){
			log.info("Module :["+module+"] started !");
		}
	}

	public void unregisterKernelModule(M module) {
		boolean removed = false;
		synchronized(modules){
			removed = this.modules.remove(module);
		}
		if(removed){
			if(isStarted()){
				stopModule(module);
			}   
			fireModuleUnregistered(module);
		}
	}

	/**
	 * @param module
	 */
	protected void stopModule(M module) {
		if(log.isInfoEnabled()){
			log.info("Stopping module :["+module+"] ...");
		}
		module.stop();
		if(log.isInfoEnabled()){
			log.info("Module :["+module+"] stopped !");
		}
	}


	abstract protected C getContext();

	protected abstract void initModules();
	
	@SuppressWarnings("unchecked")
	public IKernelModule<C>[] getAllModules() {
		synchronized(modules){
			if(modules.isEmpty()){
				return new IKernelModule[0];
			}else{
				return modules.toArray(new IKernelModule[modules.size()]);
			}
		}
	}
	

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.microkernel.api.IMicroKernel#getAllRegisteredModules()
	 */
	@Override
	public ModuleStatus[] getAllRegisteredModules() {
		ModuleStatus[] mods = null;
		synchronized(modules){
			int len = modules.size();
			if(len > 0){
				mods = new ModuleStatus[len];
				for(int i=0 ; i < len; i++){
					mods[i] = modules.get(i).getStatus();
				}
			}
		}
		return mods;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.microkernel.api.IMicroKernel#getService(java.lang.Class)
	 */
	@Override
	public <S> S getService(Class<S> interfaceClazz) {
		return getContext().getService(interfaceClazz);
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.microkernel.api.IMicroKernel#getServiceAsync(java.lang.Class)
	 */
	@Override
	public <S> IServiceFuture<S> getServiceAsync(Class<S> interfaceClazz) {
		return getContext().getServiceAsync(interfaceClazz);
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.microkernel.api.IMicroKernel#checkServiceAvailable(java.lang.Class, com.wxxr.mobile.core.microkernel.api.IServiceAvailableCallback)
	 */
	@Override
	public <S> void checkServiceAvailable(Class<S> interfaceClazz,
			IServiceAvailableCallback<S> callback) {
		getContext().checkServiceAvailable(interfaceClazz, callback);
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.microkernel.api.IMicroKernel#addModuleListener(com.wxxr.mobile.core.microkernel.api.IModuleListener)
	 */
	@Override
	public void addModuleListener(IModuleListener listener) {
		synchronized(moduleListeners){
			if(!moduleListeners.contains(listener)){
				moduleListeners.add(listener);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.microkernel.api.IMicroKernel#removeModuleListener(com.wxxr.mobile.core.microkernel.api.IModuleListener)
	 */
	@Override
	public boolean removeModuleListener(IModuleListener listener) {
		synchronized(moduleListeners){
			return moduleListeners.remove(listener);
		}
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.microkernel.api.IMicroKernel#invokeLater(java.lang.Runnable, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public ICancellable invokeLater(Runnable task, long delay, TimeUnit unit) {
		return getContext().invokeLater(task, delay, unit);
	}
	
	public synchronized boolean isStarted() {
		return this.status == MStatus.STARTED;
	}
	
	protected synchronized void setStatus(MStatus stat) {
		this.status  = stat;
	}
	
	protected void initServicePlugins() {
		
	}
	
	public AbstractMicroKernel<C, M> addServiceFeaturePlugin(IServiceFeaturePlugin decorator){
		if(this.status != MStatus.INIT){
			throw new IllegalStateException("Plugin could be only be added before kernel starting !");
		}
		if(decorator == null){
			throw new IllegalArgumentException("decorator cannot be NULL!");
		}
		synchronized(this.plugins){
			if(!this.plugins.contains(decorator)){
				this.plugins.add(decorator);
				if(decorator instanceof IKernelComponent){
					((IKernelComponent)decorator).init(getContext());
				}
			}
		}
		return this;
	}
	
		
	protected void clearServiceFeaturePlugins() {
		synchronized(this.plugins){
			if(this.plugins.size() > 0){
				for (IServiceFeaturePlugin decor : this.plugins) {
					if(decor instanceof IKernelComponent){
						((IKernelComponent)decor).destroy();
					}
				}
				this.plugins.clear();
			}
		}
	}
}
