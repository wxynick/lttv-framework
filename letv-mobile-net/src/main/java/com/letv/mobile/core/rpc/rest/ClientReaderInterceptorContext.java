package com.letv.mobile.core.rpc.rest;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import com.letv.javax.ws.rs.core.MediaType;
import com.letv.javax.ws.rs.core.MultivaluedMap;
import com.letv.javax.ws.rs.ext.MessageBodyReader;
import com.letv.javax.ws.rs.ext.ReaderInterceptor;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public class ClientReaderInterceptorContext extends AbstractReaderInterceptorContext
{
   protected Map<String, Object> properties;

   public ClientReaderInterceptorContext(ReaderInterceptor[] interceptors, MessageBodyReader reader, Class type,
                                         Type genericType, Annotation[] annotations, MediaType mediaType,
                                         MultivaluedMap<String, String> headers, InputStream inputStream,
                                         Map<String, Object> properties)
   {
      super(mediaType, reader, annotations, interceptors, headers, genericType, type, inputStream);
      this.properties = properties;
   }

   @Override
   public Object getProperty(String name)
   {
      return properties.get(name);
   }

   @Override
   public Collection<String> getPropertyNames()
   {
      return properties.keySet();
   }

   @Override
   public void setProperty(String name, Object object)
   {
      if (object == null)
      {
         properties.remove(name);
      }
      else
      {
         properties.put(name, object);
      }
   }

   @Override
   public void removeProperty(String name)
   {
      properties.remove(name);
   }
}