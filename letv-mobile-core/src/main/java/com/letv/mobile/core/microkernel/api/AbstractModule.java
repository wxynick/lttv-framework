/**
 * 
 */
package com.letv.mobile.core.microkernel.api;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.letv.mobile.core.log.api.Trace;
import com.letv.mobile.core.util.StringUtils;

/**
 * @author  
 *
 */
public abstract class AbstractModule<T extends IKernelContext> implements IKernelModule<T> {

	private static final Trace log = Trace.register(AbstractModule.class);
	
	protected T context;
	private Set<Class<?>> requiredServices;
	private Set<Class<?>> optionalServices;
	private Set<Class<?>> pendingServices;
	private AtomicBoolean started = new AtomicBoolean(false);
	private AtomicBoolean initted = new AtomicBoolean(false);
	private ModuleStatus moduleStatus = new ModuleStatus();
	private IKernelServiceListener serviceListener = new IKernelServiceListener() {
		
		@Override
		public <S> void serviceUnregistered(Class<S> clazz, S handler) {
			if(log.isTraceEnabled()){
				log.trace("Service %s was unregistered !",clazz);
			}
			synchronized(AbstractModule.this){
				if(pendingServices == null) {
					pendingServices = new HashSet<Class<?>>();
				}
				pendingServices.add(clazz);
			}
			doStopService();
		}
		
		@Override
		public <S> void serviceRegistered(Class<S> clazz, S handler) {
			if(log.isTraceEnabled()){
				log.trace("Service %s was registered !",clazz);
			}
			boolean start = false;
			synchronized(AbstractModule.this){
				if(pendingServices != null) {
					pendingServices.remove(clazz);
					if(pendingServices.size() == 0){
						if(log.isDebugEnabled()){
							log.debug("No more pending services,going to start service ...");
						}
						start = true;
					}
				}
			}
			if(start &&(started.get() == false)){
				Long time = moduleStatus.getStartTime();
				if(time != null){
					moduleStatus.setPendingTimeSpent(System.currentTimeMillis() - time.longValue());
				}
				doStartService();
			}
		}
		
		@Override
		public boolean accepts(Class<?> clazz) {
			synchronized(AbstractModule.this){
				return (requiredServices != null && requiredServices.contains(clazz))||((optionalServices != null)&&(optionalServices.contains(clazz)));
			}
		}
	};
	
	public AbstractModule() {
		super();
		if(getModuleName() != null){
			this.moduleStatus.setModuleName(getModuleName());
		}else{
			this.moduleStatus.setModuleName(getClass().getName());
		}
		this.moduleStatus.setCreatedTime(System.currentTimeMillis());
		this.moduleStatus.setStatus(MStatus.INIT);
	}

	protected void doStopService() {
		if(started.compareAndSet(true,false)){
			if(log.isDebugEnabled()){
				log.debug("Going to stop service in module %s ...",this);
			}
			try {
				stopService();
			}catch(Throwable t){
				log.warn("Caught throwable when try to stop service in module %s", this, t);
			}
		}
	}
	
	protected void doStartService() {
		if(started.compareAndSet(false,true)){
			long time = System.currentTimeMillis();
			this.moduleStatus.setStartTime(time);
			this.moduleStatus.setStatus(MStatus.STARTING);
			if(log.isDebugEnabled()){
				log.debug("Going to start module %s ...",this);
			}
			try {
				startService();
				this.moduleStatus.setStatus(MStatus.STARTED);
				this.moduleStatus.setStartTimeSpent(System.currentTimeMillis()-time);
				if(log.isDebugEnabled()){
					log.debug("Module : %s started !",this);
				}
			}catch(Throwable t){
				log.error("Caught throwable when try to start service in module %s", this, t);
				this.moduleStatus.setStatus(MStatus.FAILED);
				this.moduleStatus.setRecentError(t);
				this.moduleStatus.setRecentErrorTime(System.currentTimeMillis());
			}
		}
	}
	
	protected synchronized void initPendingServices() {
		this.context.addKernelServiceListener(serviceListener);
		if(this.requiredServices != null){
			for (Class<?> clazz : this.requiredServices) {
				Object h = this.context.getService(clazz);
				if(h == null){
					if(this.pendingServices == null){
						this.pendingServices = new HashSet<Class<?>>();
					}
					this.pendingServices.add(clazz);
				}
			}
		}
		if(this.optionalServices != null){
			for (Class<?> clazz : this.optionalServices) {
				Object h = this.context.getService(clazz);
				if(h == null){
					if(this.pendingServices == null){
						this.pendingServices = new HashSet<Class<?>>();
					}
					this.pendingServices.add(clazz);
				}
			}
		}
	}
	
	protected synchronized void clearPendingServices() {
		this.context.removeKernelServiceListener(serviceListener);
		if(this.pendingServices != null){
			this.pendingServices.clear();
			this.pendingServices = null;
		}
		if(this.requiredServices != null){
			this.requiredServices.clear();
			this.requiredServices = null;
		}
		if(this.optionalServices != null){
			this.optionalServices.clear();
			this.optionalServices = null;
		}
	}

	protected <S> S getService(Class<S> clazz) {
		return this.context.getService(clazz);
	}
	
	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.microkernel.api.IKernelModule#start(com.wxxr.mobile.core.microkernel.api.IKernelContext)
	 */
	@Override
	public final void start(T ctx) {
		this.context = ctx;
		this.moduleStatus.setStatus(MStatus.STARTING);
		if(!initted.get()){
			initServiceDependency();
			initPendingServices();
			initted.set(true);
		}
		boolean start = false;
		synchronized(this){
			start = this.pendingServices == null || this.pendingServices.isEmpty();
		}
		if(start){
			doStartService();
		}else{
			this.moduleStatus.setStatus(MStatus.START_PENDING);
			this.moduleStatus.setStartTime(System.currentTimeMillis());
		}
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.microkernel.api.IKernelModule#stop()
	 */
	@Override
	public final void stop() {
		this.moduleStatus.setStatus(MStatus.STOPPING);
		doStopService();
		clearPendingServices();
		initted.set(false);
		this.moduleStatus.setStatus(MStatus.STOPED);
		this.moduleStatus.setStopTime(System.currentTimeMillis());
	}
	
	protected abstract void initServiceDependency();
	
	protected abstract void startService();
	
	protected abstract void stopService();

	
	protected synchronized void addRequiredService(Class<?> serviceInterface){
		if(serviceInterface == null){
			throw new IllegalArgumentException("Invalid service interface : NULL");
		}
		if(initted.get()){
			throw new IllegalStateException("module has been started, it's illegal to add new service dependency !");
		}
		if(this.requiredServices == null){
			this.requiredServices = new HashSet<Class<?>>();
		}
		this.requiredServices.add(serviceInterface);
	}
	
	protected synchronized void addOptionalService(Class<?> serviceInterface){
		if(serviceInterface == null){
			throw new IllegalArgumentException("Invalid service interface : NULL");
		}
		if(initted.get()){
			throw new IllegalStateException("module has been started, it's illegal to add new service dependency !");
		}
		if(this.optionalServices == null){
			this.optionalServices = new HashSet<Class<?>>();
		}
		this.optionalServices.add(serviceInterface);
	}

	
	protected synchronized boolean removeRequiredService(Class<?> serviceInterface){
		if(initted.get()){
			throw new IllegalStateException("module has been started, it's illegal to remove service dependency !");
		}
		if(this.requiredServices != null){
			return this.requiredServices.remove(serviceInterface);
		}
		return false;
	}

	
	protected void updateError(Throwable t){
		if(t == null){
			return;
		}
		this.moduleStatus.setRecentError(t);
		this.moduleStatus.setRecentErrorTime(System.currentTimeMillis());
	}
	
	public ModuleStatus getStatus() {
		synchronized(this){
			if((this.pendingServices != null)&&(this.pendingServices.size() > 0)){
				int len = this.pendingServices.size();
				String[] vals = new String[len];
				Iterator<Class<?>> itr = this.pendingServices.iterator();
				for (int i = 0; i < vals.length; i++) {
					vals[i] = itr.next().getCanonicalName();					
				}
				this.moduleStatus.setPendingServices(vals);
			}
		}
		return this.moduleStatus;
	}
	
	protected void startServiceWithoutOptional() {
		if((this.optionalServices == null)||(this.optionalServices.size() == 0)){
			return;
		}
		if(this.started.get()){
			return;
		}
		for (Class<?> clazz : this.optionalServices) {
			this.pendingServices.remove(clazz);
		}
		if(this.pendingServices.isEmpty()){
			doStartService();
		}
	}
	
	protected final void notifyKernelStarted() {
		if(started.get() == false){
			log.warn(String.format("Service was not started in module : %s due to missing required services : %s ", this,StringUtils.join(this.pendingServices.iterator(), ',')));
			return;		// no started module will not received kernel started event
		}else{
			onKernelStarted();
		}
	}
	
	protected void onKernelStarted() {
		
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.microkernel.api.IKernelModule#getModuleName()
	 */
	@Override
	public String getModuleName() {
		return null;
	}

}
