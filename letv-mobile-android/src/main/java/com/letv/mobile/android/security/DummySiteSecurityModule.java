package com.letv.mobile.android.security;

import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;

import com.letv.mobile.android.app.IAndroidAppContext;
import com.letv.mobile.core.microkernel.api.AbstractModule;
import com.letv.mobile.core.security.api.ISiteSecurityService;

public class DummySiteSecurityModule<T extends IAndroidAppContext> extends
		AbstractModule<T> implements ISiteSecurityService{

	@Override
	protected void initServiceDependency() {
	}

	@Override
	protected void startService() {
		this.context.registerService(ISiteSecurityService.class, this);
	}

	@Override
	protected void stopService() {
		this.context.unregisterService(ISiteSecurityService.class, this);
	}

	@Override
	public HostnameVerifier getHostnameVerifier() {
		return null;
	}

	@Override
	public KeyStore getTrustKeyStore() {
		return null;
	}

	@Override
	public KeyStore getSiteKeyStore() {
		return null;
	}

}
