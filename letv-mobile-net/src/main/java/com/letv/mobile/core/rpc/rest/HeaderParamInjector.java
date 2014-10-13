package com.letv.mobile.core.rpc.rest;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.List;

import com.letv.javax.ws.rs.HeaderParam;
import com.letv.mobile.core.rpc.rest.spi.HttpRequest;
import com.letv.mobile.core.rpc.rest.spi.HttpResponse;
import com.letv.mobile.core.rpc.rest.spi.ValueInjector;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public class HeaderParamInjector extends StringParameterInjector implements ValueInjector
{

   public HeaderParamInjector(Class type, Type genericType, AccessibleObject target, String header, String defaultValue, Annotation[] annotations, ResteasyProviderFactory factory)
   {
      super(type, genericType, header, HeaderParam.class, defaultValue, target, annotations, factory);
   }

   public Object inject(HttpRequest request, HttpResponse response)
   {
      List<String> list = request.getHttpHeaders().getRequestHeaders().get(paramName);
      return extractValues(list);
   }

   public Object inject()
   {
      throw new RuntimeException("It is illegal to inject a @HeaderParam into a singleton");
   }
}