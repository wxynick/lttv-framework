package com.letv.mobile.core.rpc.rest;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.List;

import com.letv.javax.ws.rs.QueryParam;
import com.letv.mobile.core.rpc.rest.spi.HttpRequest;
import com.letv.mobile.core.rpc.rest.spi.HttpResponse;
import com.letv.mobile.core.rpc.rest.spi.ValueInjector;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public class QueryParamInjector extends StringParameterInjector implements ValueInjector
{
   private boolean encode;
   private String encodedName;

   public QueryParamInjector(Class type, Type genericType, AccessibleObject target, String paramName, String defaultValue, boolean encode, Annotation[] annotations, ResteasyProviderFactory factory)
   {
      super(type, genericType, paramName, QueryParam.class, defaultValue, target, annotations, factory);
      this.encode = encode;
      try
      {
         this.encodedName = URLDecoder.decode(paramName, "UTF-8");
      }
      catch (UnsupportedEncodingException e)
      {
         throw new BadRequestException("Unable to decode query string", e);
      }
   }

   public Object inject(HttpRequest request, HttpResponse response)
   {
      if (encode)
      {
         List<String> list = request.getUri().getQueryParameters(false).get(encodedName);
         return extractValues(list);
      }
      else
      {
         List<String> list = request.getUri().getQueryParameters().get(paramName);
         return extractValues(list);

      }
   }

   public Object inject()
   {
      throw new RuntimeException("It is illegal to inject a @QueryParam into a singleton");
   }


}
