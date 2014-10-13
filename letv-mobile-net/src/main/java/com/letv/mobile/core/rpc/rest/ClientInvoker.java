package com.letv.mobile.core.rpc.rest;

import java.lang.reflect.Method;

import com.letv.javax.ws.rs.Path;
import com.letv.javax.ws.rs.client.WebTarget;
import com.letv.javax.ws.rs.core.Configuration;
import com.letv.javax.ws.rs.core.MediaType;
import com.letv.javax.ws.rs.core.Response;
import com.letv.mobile.core.async.api.IAsyncCallback;
import com.letv.mobile.core.async.api.ICancellable;
import com.letv.mobile.core.command.api.ICommandExecutor;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
@SuppressWarnings("unchecked")
public class ClientInvoker<T> implements MethodInvoker<T>
{
   protected String httpMethod;
   protected Method method;
   protected Class<?> declaring;
   protected MediaType accepts;
   protected Object[] processors;
   protected WebTarget webTarget;
   protected boolean followRedirects;
   protected EntityExtractor<T> extractor;
   protected DefaultEntityExtractorFactory entityExtractorFactory;
   protected Configuration invokerConfig;
   protected ICommandExecutor executor;


   public ClientInvoker(ResteasyWebTarget parent, Class<?> declaring, Method method, ProxyConfig config)
   {
      // webTarget must be a clone so that it has a cloned ClientConfiguration so we can apply DynamicFeature
      if (method.isAnnotationPresent(Path.class))
      {
         this.webTarget = parent.path(method);
      }
      else
      {
         this.webTarget = parent.clone();
      }
      this.declaring = declaring;
      this.method = method;
      invokerConfig = this.webTarget.getConfiguration();
      this.executor = parent.getResteasyClient().getExecutor();
//      ResourceInfo info = new ResourceInfo()
//      {
//         @Override
//         public Method getResourceMethod()
//         {
//            return ClientInvoker.this.method;
//         }
//
//         @Override
//         public Class<?> getResourceClass()
//         {
//            return ClientInvoker.this.declaring;
//         }
//      };
//      for (DynamicFeature feature : invokerConfig.getDynamicFeatures())
//      {
//         feature.configure(info, new FeatureContextDelegate(invokerConfig));
//      }
//

      this.processors = ProcessorFactory.createProcessors(declaring, method, invokerConfig, config.getDefaultConsumes());
      accepts = MediaTypeHelper.getProduces(declaring, method, config.getDefaultProduces());
      entityExtractorFactory = new DefaultEntityExtractorFactory();
      this.extractor = entityExtractorFactory.createExtractor(method);
   }

   public MediaType getAccepts()
   {
      return accepts;
   }

   public Method getMethod()
   {
      return method;
   }

   public Class<?> getDeclaring()
   {
      return declaring;
   }

   public void invoke(Object[] args, final IAsyncCallback<T> callback)
   {
	   final ClientInvocation request = createRequest(args);
	   request.invoke(new IAsyncCallback<Response>() {

		   @Override
		   public void success(Response result) {
			   ClientContext context = new ClientContext(request, result, entityExtractorFactory);
			   final T obj = extractor.extractEntity(context);
			   if((executor != null)&&(!(callback instanceof InstantCallback))){
				   executor.submit(new Runnable() {
					
					@Override
					public void run() {
						   callback.success(obj);
					}
				}, null);
			   }else{
				   callback.success(obj);
			   }
		   }

		   @Override
		   public void failed(final Throwable cause) {
			   if((executor != null)&&(!(callback instanceof InstantCallback))){
				   executor.submit(new Runnable() {
					
					@Override
					public void run() {
						   callback.failed(cause);
					}
				}, null);
			   }else{
				   callback.failed(cause);
			   }
		   }

		   @Override
		   public void setCancellable(ICancellable cancellable) {
			   callback.setCancellable(cancellable);
		   }

		   @Override
		   public void cancelled() {
			   if((executor != null)&&(!(callback instanceof InstantCallback))){
				   executor.submit(new Runnable() {
					
					@Override
					public void run() {
						   callback.cancelled();
					}
				}, null);
			   }else{
				   callback.cancelled();
			   }
		   }
	   });
   }

   protected ClientInvocation createRequest(Object[] args)
   {
      WebTarget target = this.webTarget;
      for (int i = 0; i < processors.length; i++)
      {
         if (processors != null && processors[i] instanceof WebTargetProcessor)
         {
            WebTargetProcessor processor = (WebTargetProcessor)processors[i];
            target = processor.build(target, args[i]);

         }
      }

      ClientInvocationBuilder builder = null;
      if (accepts != null)
      {
         builder = (ClientInvocationBuilder)target.request(accepts);
      }
      else
      {
         builder = (ClientInvocationBuilder)target.request();
      }

      for (int i = 0; i < processors.length; i++)
      {
         if (processors != null && processors[i] instanceof InvocationProcessor)
         {
            InvocationProcessor processor = (InvocationProcessor)processors[i];
            processor.process(builder, args[i]);

         }
      }
      return (ClientInvocation)builder.build(httpMethod);
   }

   public String getHttpMethod()
   {
      return httpMethod;
   }

   public void setHttpMethod(String httpMethod)
   {
      this.httpMethod = httpMethod;
   }

   public boolean isFollowRedirects()
   {
      return followRedirects;
   }

   public void setFollowRedirects(boolean followRedirects)
   {
      this.followRedirects = followRedirects;
   }

   public void followRedirects()
   {
      setFollowRedirects(true);
   }
}