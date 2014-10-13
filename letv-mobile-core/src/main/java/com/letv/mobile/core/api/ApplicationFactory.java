package com.letv.mobile.core.api;
/**
 * 
 * @author wangxuyang1
 *
 */

@SuppressWarnings("rawtypes")
public class ApplicationFactory {
	private static ApplicationFactory INSTANCE = new ApplicationFactory();
	
	public static ApplicationFactory getInstance() {
		return INSTANCE;
	}
	
	private ApplicationFactory(){}
	
	private IApplication application;
	
	public IApplication getApplication() {
		return this.application;
	}
	
	public void setApplication(IApplication app){
		if(this.application != null){
			throw new IllegalStateException("Application had been initialized !!!");
		}
		if(app == null){
			throw new IllegalArgumentException("Application cannot be NULL !");
		}
		this.application = app;
	}
}
