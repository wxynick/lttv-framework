package com.letv.mobile.core.rpc.rest;

import com.letv.mobile.core.async.api.IAsyncCallback;

public interface MethodInvoker<T>
{
	void invoke(Object[] args, IAsyncCallback<T> callback);
}
