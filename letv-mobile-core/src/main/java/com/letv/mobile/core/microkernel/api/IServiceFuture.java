package com.letv.mobile.core.microkernel.api;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public interface IServiceFuture<T> {

	T[] getServices(T[] vals);

	T getService();

	T get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException;

	T get() throws InterruptedException;

	boolean removeService(T object);

	boolean addService(T object);

}