package com.letv.mobile.core.rpc.rest;
import java.security.KeyStore;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import com.letv.javax.ws.rs.client.ClientBuilder;
import com.letv.javax.ws.rs.core.Configuration;
import com.letv.mobile.core.log.api.Trace;
import com.letv.mobile.core.microkernel.api.IKernelContext;
import com.letv.mobile.core.rpc.api.IServerUrlLocator;
import com.letv.mobile.core.rpc.http.api.HttpRpcService;
import com.letv.mobile.core.rpc.http.api.IRestProxyService;
import com.letv.mobile.core.rpc.rest.provider.ByteArrayProvider;
import com.letv.mobile.core.rpc.rest.provider.DocumentProvider;
import com.letv.mobile.core.rpc.rest.provider.FileProvider;
import com.letv.mobile.core.rpc.rest.provider.FormUrlEncodedProvider;
import com.letv.mobile.core.rpc.rest.provider.InputStreamProvider;
import com.letv.mobile.core.rpc.rest.provider.JaxrsFormProvider;
import com.letv.mobile.core.rpc.rest.provider.SerializableProvider;
import com.letv.mobile.core.rpc.rest.provider.StreamingOutputProvider;
import com.letv.mobile.core.util.StringUtils;
import com.letv.mobile.preference.api.IPreferenceManager;

/**
 * Abstraction for creating Clients.  Allows SSL configuration.  Currently defaults to using Apache Http Client under
 * the covers.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public class ResteasyRestClientService extends ClientBuilder implements IRestProxyService
{
   private static final Trace log = Trace.register(ResteasyRestClientService.class);
   private Map<String, Map<Class<?>, Object>> proxyCache = new HashMap<String, Map<Class<?>,Object>>();
   protected ResteasyProviderFactory providerFactory;
   protected Map<String, Object> properties = new HashMap<String, Object>();
   protected ClientHttpEngine httpEngine;
   protected IKernelContext application;
   protected String defautTarget, systemRestUrl;
   /**
    * Changing the providerFactory will wipe clean any registered components or properties.
    *
    * @param providerFactory
    * @return
    */
   public ResteasyRestClientService providerFactory(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
      return this;
   }


   /**
    * Negates all ssl and connection specific configuration
    *
    * @param httpEngine
    * @return
    */
   public ResteasyRestClientService httpEngine(ClientHttpEngine httpEngine)
   {
      this.httpEngine = httpEngine;
      return this;
   }


   @Override
   public ResteasyRestClientService property(String name, Object value)
   {
      getProviderFactory().property(name, value);
      return this;
   }

   protected ResteasyProviderFactory getProviderFactory()
   {
      if (providerFactory == null)
      {
         // create a new one
         providerFactory = new ResteasyProviderFactory();
         ResteasyProviderFactory.setInstance(providerFactory);
         RegisterBuiltin.register(providerFactory);
      }
      return providerFactory;
   }

   @Override
   public ResteasyClient build()
   {
      ClientConfiguration config = new ClientConfiguration(getProviderFactory());
      for (Map.Entry<String, Object> entry : properties.entrySet())
      {
         config.property(entry.getKey(), entry.getValue());
      }
      return new ResteasyClient(this.application, httpEngine, config);
   }

 
   @Override
   public Configuration getConfiguration()
   {
      return getProviderFactory().getConfiguration();
   }

   @Override
   public ResteasyRestClientService register(Class<?> componentClass)
   {
      getProviderFactory().register(componentClass);
      return this;
   }

   @Override
   public ResteasyRestClientService register(Class<?> componentClass, int priority)
   {
      getProviderFactory().register(componentClass, priority);
      return this;
   }

   @Override
   public ResteasyRestClientService register(Class<?> componentClass, Class<?>... contracts)
   {
      getProviderFactory().register(componentClass, contracts);
      return this;
   }

   @Override
   public ResteasyRestClientService register(Class<?> componentClass, Map<Class<?>, Integer> contracts)
   {
      getProviderFactory().register(componentClass, contracts);
      return this;
   }

   @Override
   public ResteasyRestClientService register(Object component)
   {
      getProviderFactory().register(component);
      return this;
   }

   @Override
   public ResteasyRestClientService register(Object component, int priority)
   {
      getProviderFactory().register(component, priority);
      return this;
   }

   @Override
   public ResteasyRestClientService register(Object component, Class<?>... contracts)
   {
      getProviderFactory().register(component, contracts);
      return this;
   }

   @Override
   public ResteasyRestClientService register(Object component, Map<Class<?>, Integer> contracts)
   {
      getProviderFactory().register(component, contracts);
      return this;
   }

   @Override
   public ResteasyRestClientService withConfig(Configuration config)
   {
      providerFactory = new ResteasyProviderFactory();
      providerFactory.setProperties(config.getProperties());
      for (Class clazz : config.getClasses())
      {
         Map<Class<?>, Integer> contracts = config.getContracts(clazz);
         try {
            register(clazz, contracts);
         }
         catch (RuntimeException e) {
            throw new RuntimeException("failed on registering class: " + clazz.getName(), e);
         }
      }
      for (Object obj : config.getInstances())
      {
         Map<Class<?>, Integer> contracts = config.getContracts(obj.getClass());
         register(obj, contracts);
      }
      return this;
   }

	@Override
	public ClientBuilder sslContext(SSLContext sslContext) {
		return this;
	}
	
	@Override
	public ClientBuilder keyStore(KeyStore keyStore, char[] password) {
		return this;
	}
	
	@Override
	public ClientBuilder trustStore(KeyStore trustStore) {
		return this;
	}
	
	@Override
	public ClientBuilder hostnameVerifier(HostnameVerifier verifier) {
		return this;
	}
	
	protected Map<Class<?>, Object> getProxyCache(String target, boolean create){
		synchronized(this.proxyCache){
			Map<Class<?>, Object> map = this.proxyCache.get(target);
			if(create&&(map == null)){
				map = new HashMap<Class<?>, Object>();
				this.proxyCache.put(target, map);
			}
			return map;
		}
	}
	
	protected void startup(IKernelContext ctx) {
		this.application = ctx;
		ResteasyProviderFactory factory = getProviderFactory();
		factory.register(ByteArrayProvider.class).
			register(DocumentProvider.class).
			register(FileProvider.class).
			register(FormUrlEncodedProvider.class).
			register(InputStreamProvider.class).
			register(JaxrsFormProvider.class).
			register(SerializableProvider.class).
			register(StreamingOutputProvider.class);
		try {
			httpEngine = new HttpRpcClientEngine(this.application.getService(HttpRpcService.class));
			application.registerService(IRestProxyService.class, ResteasyRestClientService.this);
		} catch (Throwable e) {
			log.error("Failed to initialize http client",e);
		}
	}



	public void shutdown() {
		this.application.unregisterService(IRestProxyService.class, this);
		if(this.httpEngine != null){
			this.httpEngine.close();
			this.httpEngine = null;
		}
	}


	@Override
	public <T> T getRestService(Class<T> clazz,Class<?> ifaceRest, String target) {
		Map<Class<?>, Object> map = getProxyCache(target, true);
		synchronized(map){
			T obj = clazz.cast(map.get(clazz));
			if(obj == null){
				obj =  build().target(target).proxy(clazz,ifaceRest);
				map.put(clazz, obj);
			}
			return obj;
		}
	}


	protected String getDefaultServerUrl(){
		if(StringUtils.isNotBlank(this.defautTarget)) {
			return this.defautTarget;
		}
		IServerUrlLocator locator = application.getService(IServerUrlLocator.class);
		if(locator != null){
			return locator.getServerURL();
		}
		if(this.systemRestUrl != null){
			return this.systemRestUrl;
		}
		IPreferenceManager prefManager = application.getService(IPreferenceManager.class);
		if(prefManager == null){
			return null;
		}
		Dictionary<String, String> d = prefManager.getPreference(IPreferenceManager.SYSTEM_PREFERENCE_NAME);
		this.systemRestUrl = d != null ? d.get(IPreferenceManager.SYSTEM_PREFERENCE_KEY_DEFAULT_SERVER_URL) : null;
		return this.systemRestUrl;
	}

	@Override
	public <T> T getRestService(Class<T> clazz,Class<?> ifRest) {
		String url = getDefaultServerUrl();
		if(url == null){
			throw new IllegalArgumentException("There is not default server url setup, you should specified server target url !!!");
		}
		return getRestService(clazz, ifRest, url);
	}


   @Override
   public void setDefautTarget(String target) {   
      if (StringUtils.isNotBlank(target)) {
         this.defautTarget = target;
      }
   }

}
