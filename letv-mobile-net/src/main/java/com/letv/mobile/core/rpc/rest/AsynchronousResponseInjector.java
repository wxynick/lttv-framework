package com.letv.mobile.core.rpc.rest;
import java.util.concurrent.TimeUnit;

import com.letv.javax.ws.rs.container.AsyncResponse;
import com.letv.mobile.core.rpc.rest.spi.HttpRequest;
import com.letv.mobile.core.rpc.rest.spi.HttpResponse;
import com.letv.mobile.core.rpc.rest.spi.ValueInjector;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public class AsynchronousResponseInjector implements ValueInjector
{
   long timeout = -1;
   TimeUnit unit = null;

   public AsynchronousResponseInjector()
   {
   }

   @Override
   public Object inject()
   {
      throw new IllegalStateException("You cannot inject AsynchronousResponse outside the scope of an HTTP request");
   }

   @Override
   public Object inject(HttpRequest request, HttpResponse response)
   {
      AsyncResponse asynchronousResponse = null;
//      if (timeout == -1)
//      {
//         asynchronousResponse = request.getAsyncContext().suspend();
//      }
//      else
//      {
//         asynchronousResponse = request.getAsyncContext().suspend(timeout, unit);
//      }
      return asynchronousResponse;
   }
}