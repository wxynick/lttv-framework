package com.letv.mobile.core.rpc.rest;


import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Set;

import com.letv.javax.ws.rs.Path;
import com.letv.javax.ws.rs.core.MediaType;
import com.letv.mobile.core.util.MethodHashing;

public class ProxyBuilder<T>
{
	private final Class<T> ifaceClient;
	private final Class<?> ifaceRest;
	private final ResteasyWebTarget webTarget;
	private ClassLoader loader = Thread.currentThread().getContextClassLoader();
	private MediaType serverConsumes;
	private MediaType serverProduces;

   public static <T> ProxyBuilder<T> builder(Class<T> iface, Class<?> ifaceRest,ResteasyWebTarget webTarget)
   {
      return new ProxyBuilder<T>(iface, ifaceRest, webTarget);
   }

	@SuppressWarnings("unchecked")
	public static <T> T proxy(final Class<T> ifaceClient, Class<?> ifaceRest, ResteasyWebTarget base, final ProxyConfig config)
	{
		if(ifaceRest == null){
			@SuppressWarnings("rawtypes")
			Class clazz = ifaceClient;
			ifaceRest = clazz;
		}
      if (ifaceRest.isAnnotationPresent(Path.class))
      {
         Path path = ifaceRest.getAnnotation(Path.class);
         if (!path.value().equals("") && !path.value().equals("/"))
         {
            base = (ResteasyWebTarget)base.path(path.value());
         }
      }
		HashMap<Long, MethodInvoker<?>> methodMap = new HashMap<Long, MethodInvoker<?>>();
		for (Method method : ifaceRest.getMethods())
		{
         MethodInvoker<?> invoker;
//         Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
//         if ((httpMethods == null || httpMethods.size() == 0) && method.isAnnotationPresent(Path.class) && method.getReturnType().isInterface())
//         {
//            invoker = new SubResourceInvoker(base, method, config);
//         }
//         else
//         {
            invoker = createClientInvoker(ifaceRest, method, base, config);
//         }
         methodMap.put(MethodHashing.methodHash0(method), invoker);
		}

		Class<?>[] intfs =
		{
				ifaceClient
		};

		ClientProxy clientProxy = new ClientProxy(methodMap);
		// this is done so that equals and hashCode work ok. Adding the proxy to a
		// Collection will cause equals and hashCode to be invoked. The Spring
		// infrastructure had some problems without this.
		clientProxy.setClazz(ifaceClient);

		return (T) Proxy.newProxyInstance(config.getLoader(), intfs, clientProxy);
	}

   private static <T> ClientInvoker<T> createClientInvoker(Class<T> clazz, Method method, ResteasyWebTarget base, ProxyConfig config)
   {
      Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
      if (httpMethods == null || httpMethods.size() != 1)
      {
         throw new RuntimeException("You must use at least one, but no more than one http method annotation on: " + method.toString());
      }
      ClientInvoker<T> invoker = new ClientInvoker<T>(base, clazz, method, config);
      invoker.setHttpMethod(httpMethods.iterator().next());
      return invoker;
   }

   private ProxyBuilder(Class<T> ifaceClient,Class<?> iface, ResteasyWebTarget webTarget)
   {
      this.ifaceClient = ifaceClient;
      this.ifaceRest = iface;
      this.webTarget = webTarget;
   }

   public ProxyBuilder<T> classloader(ClassLoader cl)
	{
		this.loader = cl;
		return this;
	}

	public ProxyBuilder<T> defaultProduces(MediaType type)
	{
		this.serverProduces = type;
		return this;
	}

	public ProxyBuilder<T> defaultConsumes(MediaType type)
	{
		this.serverConsumes = type;
		return this;
	}

   public ProxyBuilder<T> defaultProduces(String type)
   {
      this.serverProduces = MediaType.valueOf(type);
      return this;
   }

   public ProxyBuilder<T> defaultConsumes(String type)
   {
      this.serverConsumes = MediaType.valueOf(type);
      return this;
   }
	public T build()
	{
      return proxy(ifaceClient,ifaceRest, webTarget, new ProxyConfig(loader, serverConsumes, serverProduces));
	}



}
