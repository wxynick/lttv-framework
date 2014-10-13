package com.letv.mobile.core.rpc.rest;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;

import com.letv.javax.ws.rs.CookieParam;
import com.letv.javax.ws.rs.core.Cookie;
import com.letv.mobile.core.rpc.rest.spi.HttpRequest;
import com.letv.mobile.core.rpc.rest.spi.HttpResponse;
import com.letv.mobile.core.rpc.rest.spi.ValueInjector;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public class CookieParamInjector extends StringParameterInjector implements ValueInjector
{

   public CookieParamInjector(Class type, Type genericType, AccessibleObject target, String cookieName, String defaultValue, Annotation[] annotations, ResteasyProviderFactory factory)
   {
      if (type.equals(Cookie.class))
      {
         this.type = type;
         this.paramName = cookieName;
         this.paramType = CookieParam.class;
         this.defaultValue = defaultValue;

      }
      else
      {
         initialize(type, genericType, cookieName, CookieParam.class, defaultValue, target, annotations, factory);
      }
   }

   public Object inject(HttpRequest request, HttpResponse response)
   {
      Cookie cookie = request.getHttpHeaders().getCookies().get(paramName);
      if (type.equals(Cookie.class)) return cookie;

      if (cookie == null) return extractValue(null);
      return extractValue(cookie.getValue());
   }

   public Object inject()
   {
      throw new RuntimeException("It is illegal to inject a @CookieParam into a singleton");
   }
}