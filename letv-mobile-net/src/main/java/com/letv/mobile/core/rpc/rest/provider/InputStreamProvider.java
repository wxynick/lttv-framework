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
import com.letv.javax.ws.rs.ext.Provider;
import com.letv.mobile.core.rpc.http.api.HttpHeaderNames;

/**
 * @author <a href="mailto:bill@burkecentral.com">BillBurke</a>
 * @version $Revision$
 */
@Provider
@Produces("*/*")
@Consumes("*/*")
public class InputStreamProvider implements MessageBodyReader<InputStream>, MessageBodyWriter<InputStream>
{
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return type.equals(InputStream.class);
   }

   public InputStream readFrom(Class<InputStream> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException
   {
      return entityStream;
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return InputStream.class.isAssignableFrom(type);
   }

   public long getSize(InputStream inputStream, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   public void writeTo(InputStream inputStream, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException
   {
	   try
	   {
		   int c = inputStream.read();
		   if (c == -1)
		   {
			   httpHeaders.putSingle(HttpHeaderNames.CONTENT_LENGTH, Integer.toString(0));
			   entityStream.write(new byte[0]); // fix RESTEASY-204
			   return;
		   }
		   else
			   entityStream.write(c);
		   while ((c = inputStream.read()) != -1)
		   {
			   entityStream.write(c);
		   }
	   }
	   finally
	   {
		   try
		   {
			   inputStream.close();
		   }
		   catch (IOException e)
		   {
			   // Drop the exception so we don't mask real IO errors
		   }
	   }
   }
}
