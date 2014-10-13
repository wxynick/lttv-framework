package com.letv.mobile.core.rpc.rest;
import java.net.URI;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import com.letv.javax.ws.rs.client.Client;
import com.letv.javax.ws.rs.client.Invocation;
import com.letv.javax.ws.rs.client.WebTarget;
import com.letv.javax.ws.rs.core.Configuration;
import com.letv.javax.ws.rs.core.Link;
import com.letv.javax.ws.rs.core.UriBuilder;
import com.letv.mobile.core.command.api.ICommandExecutor;
import com.letv.mobile.core.microkernel.api.IKernelContext;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public class ResteasyClient implements Client
{
   protected volatile ClientHttpEngine httpEngine;
//   protected volatile ExecutorService asyncInvocationExecutor;
   protected ClientConfiguration configuration;
   protected boolean closed;
   protected IKernelContext context;


   ResteasyClient(IKernelContext ctx,ClientHttpEngine httpEngine, ClientConfiguration configuration)
   {
      this.httpEngine = httpEngine;
//      this.asyncInvocationExecutor = asyncInvocationExecutor;
      this.configuration = configuration;
      this.context = ctx;
   }

   public ClientHttpEngine httpEngine()
   {
      abortIfClosed();
      return httpEngine;
   }


//   public ExecutorService asyncInvocationExecutor()
//   {
//      return asyncInvocationExecutor;
//   }
   
   public ICommandExecutor getExecutor() {
	   return this.context.getService(ICommandExecutor.class);
   }

   public void abortIfClosed()
   {
      if (isClosed()) throw new IllegalStateException("Client is closed.");
   }

   public boolean isClosed()
   {
      return closed;
   }

   @Override
   public void close()
   {
      closed = true;
      try
      {
         httpEngine.close();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public Configuration getConfiguration()
   {
      abortIfClosed();
      return configuration;
   }

   @Override
   public SSLContext getSslContext()
   {
      abortIfClosed();
      return null; //httpEngine().getSslContext();
   }

   @Override
   public HostnameVerifier getHostnameVerifier()
   {
      abortIfClosed();
      return null; //httpEngine().getHostnameVerifier();
   }

   @Override
   public ResteasyClient property(String name, Object value)
   {
      abortIfClosed();
      configuration.property(name, value);
      return this;
   }

   @Override
   public ResteasyClient register(Class<?> componentClass)
   {
      abortIfClosed();
      configuration.register(componentClass);
      return this;
   }

   @Override
   public ResteasyClient register(Class<?> componentClass, int priority)
   {
      abortIfClosed();
      configuration.register(componentClass, priority);
      return this;
   }

   @Override
   public ResteasyClient register(Class<?> componentClass, Class<?>... contracts)
   {
      abortIfClosed();
      configuration.register(componentClass, contracts);
      return this;
   }

   @Override
   public ResteasyClient register(Class<?> componentClass, Map<Class<?>, Integer> contracts)
   {
      abortIfClosed();
      configuration.register(componentClass, contracts);
      return this;
   }

   @Override
   public ResteasyClient register(Object component)
   {
      abortIfClosed();
      configuration.register(component);
      return this;
   }

   @Override
   public ResteasyClient register(Object component, int priority)
   {
      abortIfClosed();
      configuration.register(component, priority);
      return this;
   }

   @Override
   public ResteasyClient register(Object component, Class<?>... contracts)
   {
      abortIfClosed();
      configuration.register(component, contracts);
      return this;
   }

   @Override
   public ResteasyClient register(Object component, Map<Class<?>, Integer> contracts)
   {
      abortIfClosed();
      configuration.register(component, contracts);
      return this;
   }

   @Override
   public ResteasyWebTarget target(String uri) throws IllegalArgumentException, NullPointerException
   {
      abortIfClosed();
      if (uri == null) throw new NullPointerException("uri was null");
      return new ClientWebTarget(this, uri, configuration);
   }

   @Override
   public ResteasyWebTarget target(URI uri) throws NullPointerException
   {
      abortIfClosed();
      if (uri == null) throw new NullPointerException("uri was null");
      return new ClientWebTarget(this, uri, configuration);
   }

   @Override
   public ResteasyWebTarget target(UriBuilder uriBuilder) throws NullPointerException
   {
      abortIfClosed();
      if (uriBuilder == null) throw new NullPointerException("uriBuilder was null");
      return new ClientWebTarget(this, uriBuilder, configuration);
   }

   @Override
   public ResteasyWebTarget target(Link link) throws NullPointerException
   {
      abortIfClosed();
      if (link == null) throw new NullPointerException("link was null");
      URI uri = link.getUri();
      return new ClientWebTarget(this, uri, configuration);
   }

   @Override
   public Invocation.Builder invocation(Link link) throws NullPointerException, IllegalArgumentException
   {
      abortIfClosed();
      if (link == null) throw new NullPointerException("link was null");
      WebTarget target = target(link);
      if (link.getType() != null) return target.request(link.getType());
      else return target.request();
   }

}
