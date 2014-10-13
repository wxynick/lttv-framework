package com.letv.mobile.core.rpc.rest;


import java.net.URI;
import java.util.Locale;

import com.letv.javax.ws.rs.HttpMethod;
import com.letv.javax.ws.rs.client.Entity;
import com.letv.javax.ws.rs.client.Invocation;
import com.letv.javax.ws.rs.core.CacheControl;
import com.letv.javax.ws.rs.core.Cookie;
import com.letv.javax.ws.rs.core.MediaType;
import com.letv.javax.ws.rs.core.MultivaluedMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public class ClientInvocationBuilder implements Invocation.Builder
{
   protected ClientInvocation invocation;

   public ClientInvocationBuilder(ResteasyClient client, URI uri, ClientConfiguration configuration)
   {
      invocation = new ClientInvocation(client, uri, new ClientRequestHeaders(configuration), configuration);
   }

   public ClientInvocation getInvocation()
   {
      return invocation;
   }

   public ClientRequestHeaders getHeaders()
   {
      return invocation.headers;
   }

   @Override
   public Invocation.Builder accept(String... mediaTypes)
   {
      getHeaders().accept(mediaTypes);
      return this;
   }

   @Override
   public Invocation.Builder accept(MediaType... mediaTypes)
   {
      getHeaders().accept(mediaTypes);
      return this;
   }

   @Override
   public Invocation.Builder acceptLanguage(Locale... locales)
   {
      getHeaders().acceptLanguage(locales);
      return this;
   }

   @Override
   public Invocation.Builder acceptLanguage(String... locales)
   {
      getHeaders().acceptLanguage(locales);
      return this;
   }

   @Override
   public Invocation.Builder acceptEncoding(String... encodings)
   {
      getHeaders().acceptEncoding(encodings);
      return this;
   }

   @Override
   public Invocation.Builder cookie(Cookie cookie)
   {
      getHeaders().cookie(cookie);
      return this;
   }

   @Override
   public Invocation.Builder cookie(String name, String value)
   {
      getHeaders().cookie(new Cookie(name, value));
      return this;
   }

   @Override
   public Invocation.Builder cacheControl(CacheControl cacheControl)
   {
      getHeaders().cacheControl(cacheControl);
      return this;
   }

   @Override
   public Invocation.Builder header(String name, Object value)
   {
      getHeaders().header(name, value);
      return this;
   }

   @Override
   public Invocation.Builder headers(MultivaluedMap<String, Object> headers)
   {
      getHeaders().setHeaders(headers);
      return this;
   }

   @Override
   public Invocation build(String method)
   {
      invocation.setMethod(method);
      return invocation;
   }

   @Override
   public Invocation build(String method, Entity<?> entity)
   {
      invocation.setMethod(method);
      invocation.setEntity(entity);
      return invocation;
   }

   @Override
   public Invocation buildGet()
   {
      return build(HttpMethod.GET);
   }

   @Override
   public Invocation buildDelete()
   {
      return build(HttpMethod.DELETE);
   }

   @Override
   public Invocation buildPost(Entity<?> entity)
   {
      return build(HttpMethod.POST, entity);
   }

   @Override
   public Invocation buildPut(Entity<?> entity)
   {
      return build(HttpMethod.PUT, entity);
   }

   @Override
   public Invocation.Builder property(String name, Object value)
   {
      invocation.property(name, value);
      return this;
   }
}
