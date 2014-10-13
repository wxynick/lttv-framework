package com.letv.mobile.core.rpc.rest;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.letv.javax.ws.rs.FormParam;
import com.letv.mobile.core.rpc.rest.spi.HttpRequest;
import com.letv.mobile.core.rpc.rest.spi.HttpResponse;
import com.letv.mobile.core.rpc.rest.spi.ValueInjector;
import com.letv.mobile.core.rpc.util.Encode;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public class FormParamInjector extends StringParameterInjector implements ValueInjector
{
   private boolean encode;
   
   public FormParamInjector(Class type, Type genericType, AccessibleObject target, String header, String defaultValue, boolean encode, Annotation[] annotations, ResteasyProviderFactory factory)
   {
      super(type, genericType, header, FormParam.class, defaultValue, target, annotations, factory);
      this.encode = encode;
   }

   public Object inject(HttpRequest request, HttpResponse response)
   {
      List<String> list = request.getDecodedFormParameters().get(paramName);
      if (list == null)
      {
         list = new ArrayList<String>();
      }
      else if (encode)
      {
         List<String> encodedList = new ArrayList<String>();
         for (String s : list)
         {
            encodedList.add(Encode.encodePath(s));
         }
         list = encodedList;
      }
      return extractValues(list);
   }

   public Object inject()
   {
      throw new RuntimeException("It is illegal to inject a @FormParam into a singleton");
   }
}