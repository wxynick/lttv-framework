package com.letv.mobile.core.bean.api;

public interface IBindableBean {

	void addPropertyChangeListener(IPropertyChangeListener listener);

	void removePropertyChangeListener(IPropertyChangeListener listener);
	
	boolean hasPropertyChangeListener(IPropertyChangeListener listener);

}