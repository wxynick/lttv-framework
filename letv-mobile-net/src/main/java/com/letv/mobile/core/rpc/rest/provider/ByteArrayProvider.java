package com.letv.mobile.core.rpc.rest.provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.letv.javax.ws.rs.Consumes;
import com.letv.javax.ws.rs.Produces;
import com.letv.javax.ws.rs.core.MediaType;
import com.letv.javax.ws.rs.core.MultivaluedMap;
import com.letv.javax.ws.rs.ext.MessageBodyReader;
import com.letv.javax.ws.rs.ext.MessageBodyWriter;
import com.letv.mobile.core.util.ReadFromStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
@Produces("*/*")
@Consumes("*/*")
public class ByteArrayProvider implements MessageBodyReader<byte[]>, MessageBodyWriter<byte[]>
{
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return type.isArray() && type.getComponentType().equals(byte.class);
   }

   public byte[] readFrom(Class<byte[]> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException
   {
      return ReadFromStream.readFromStream(1024, entityStream);
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return type.isArray() && type.getComponentType().equals(byte.class);
   }

   public long getSize(byte[] bytes, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return bytes.length;
   }

   public void writeTo(byte[] bytes, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException
   {
      entityStream.write(bytes);
   }
}
