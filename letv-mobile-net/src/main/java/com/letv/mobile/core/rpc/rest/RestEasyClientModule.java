/**
 * 
 */
package com.letv.mobile.core.rpc.rest;

import com.letv.mobile.core.microkernel.api.AbstractModule;
import com.letv.mobile.core.microkernel.api.IKernelContext;
import com.letv.mobile.core.rpc.api.IServerUrlLocator;
import com.letv.mobile.core.rpc.http.api.HttpRpcService;

/**
 * @author  
 *
 */
public class RestEasyClientModule<T extends IKernelContext> extends
		AbstractModule<T> {

	private ResteasyRestClientService service = new ResteasyRestClientService();
	
	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.microkernel.api.AbstractModule#initServiceDependency()
	 */
	@Override
	protected void initServiceDependency() {
		addRequiredService(HttpRpcService.class);
		addOptionalService(IServerUrlLocator.class);
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.microkernel.api.AbstractModule#startService()
	 */
	@Override
	protected void startService() {
		this.service.startup(this.context);
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.microkernel.api.AbstractModule#stopService()
	 */
	@Override
	protected void stopService() {
		service.shutdown();
	}
	
	public ResteasyRestClientService getClient() {
		return this.service;
	}

}
