package com.letv.mobile.core.microkernel.api;

public interface IAdaptable {
	<T> T getAdaptor(Class<T> clazz);
}
