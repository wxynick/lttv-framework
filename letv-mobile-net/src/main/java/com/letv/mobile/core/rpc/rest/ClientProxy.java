package com.letv.mobile.core.rpc.rest;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.letv.mobile.core.async.api.Async;
import com.letv.mobile.core.async.api.AsyncFuture;
import com.letv.mobile.core.async.api.IAsyncCallback;
import com.letv.mobile.core.util.MethodHashing;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public class ClientProxy implements InvocationHandler
{
	private Map<Long, MethodInvoker<?>> methodMap;
	private Class<?> clazz;

	public ClientProxy(Map<Long, MethodInvoker<?>> methodMap)
	{
		super();
		this.methodMap = methodMap;
	}

	public Class<?> getClazz()
	{
		return clazz;
	}

	public void setClazz(Class<?> clazz)
	{
		this.clazz = clazz;
	}

	public Object invoke(Object o, Method method, final Object[] args)
           throws Throwable
   {
      // equals and hashCode were added for cases where the proxy is added to
      // collections. The Spring transaction management, for example, adds
      // transactional Resources to a Collection, and it calls equals and
      // hashCode.
	Long hashId = MethodHashing.methodHash0(method);
    @SuppressWarnings("rawtypes")
	final MethodInvoker clientInvoker = methodMap.get(hashId);
      if (clientInvoker == null)
      {
         if (method.getName().equals("equals"))
         {
            return this.equals(o);
         }
         else if (method.getName().equals("hashCode"))
         {
            return this.hashCode();
         }
         else if (method.getName().equals("toString") && (args == null || args.length == 0))
         {
            return this.toString();
         }
      }

      if (clientInvoker == null)
      {
         throw new RuntimeException("Could not find a method for: " + method);
      }
	  Async<Object> async = new Async<Object>() {
			
		@SuppressWarnings("unchecked")
		@Override
		public void onResult(IAsyncCallback<Object> callback) {
			clientInvoker.invoke(args, callback);
		}
      };
      Class<?> returnType = method.getReturnType();
      if(Async.class.isAssignableFrom(returnType)){
    	  return async;
      }else{
    	  try {
    		  return new AsyncFuture<Object>(async){
				@Override
				public IAsyncCallback<Object> getInternalCallback() {
					return new InstantCallback<Object>(super.getInternalCallback());
				}
    			  
    		  }.get();
    	  }catch(ExecutionException e){
    		  throw e.getCause();
    	  }
      }
   }

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null || !(obj instanceof ClientProxy))
			return false;
		ClientProxy other = (ClientProxy) obj;
		if (other == this)
			return true;
		if (other.clazz != this.clazz)
			return false;
		return super.equals(obj);
	}

	@Override
	public int hashCode()
	{
		return clazz.hashCode();
	}

	public String toString()
	{
		return "Resteasy Client Proxy for :" + clazz.getName();
	}
}
