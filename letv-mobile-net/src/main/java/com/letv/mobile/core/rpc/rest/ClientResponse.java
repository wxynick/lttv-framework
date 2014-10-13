package com.letv.mobile.core.rpc.rest;
import static java.lang.String.format;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import com.letv.javax.ws.rs.ProcessingException;
import com.letv.javax.ws.rs.core.MediaType;
import com.letv.javax.ws.rs.core.MultivaluedMap;
import com.letv.javax.ws.rs.ext.MessageBodyReader;
import com.letv.javax.ws.rs.ext.Providers;
import com.letv.javax.ws.rs.ext.ReaderInterceptor;
import com.letv.mobile.core.rpc.http.api.HttpHeaderNames;
import com.letv.mobile.core.rpc.http.api.HttpStatus;
import com.letv.mobile.core.util.InputStreamToByteArray;
import com.letv.mobile.core.util.ReadFromStream;
import com.letv.mobile.core.util.Types;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public abstract class ClientResponse extends BuiltResponse
{
   // One thing to note, I don't cache header objects because I was too lazy to proxy the headers multivalued map
   protected Map<String, Object> properties;
   protected ClientConfiguration configuration;
   protected byte[] bufferedEntity;

   protected ClientResponse(ClientConfiguration configuration)
   {
      setClientConfiguration(configuration);
   }

   public void setHeaders(MultivaluedMap<String, String> headers)
   {
      this.metadata = new Headers<Object>();
      this.metadata.putAll(headers);
   }

   public void setProperties(Map<String, Object> properties)
   {
      this.properties = properties;
   }

   public Map<String, Object> getProperties()
   {
      return properties;
   }

   public void setClientConfiguration(ClientConfiguration configuration)
   {
      this.configuration = configuration;
      this.processor = configuration;
   }

   @Override
   public Object getEntity()
   {
      abortIfClosed();
      return super.getEntity();
   }

   @Override
   public boolean hasEntity()
   {
      abortIfClosed();
      return entity != null || getMediaType() != null;
   }

   @Override
   public void close()
   {
      if (isClosed()) return;
      releaseConnection();
   }

   @Override
   protected void finalize() throws Throwable
   {
      if (isClosed()) return;
      releaseConnection();
   }

   @Override
   protected HeaderValueProcessor getHeaderValueProcessor()
   {
      return configuration;
   }

   protected abstract InputStream getInputStream();

   protected InputStream getEntityStream()
   {
      if (bufferedEntity != null) return new ByteArrayInputStream(bufferedEntity);
      if (isClosed()) throw new ProcessingException("Stream is closed");
      return getInputStream();
   }

   protected abstract void setInputStream(InputStream is);

   protected abstract void releaseConnection();


   public <T> T readEntity(Class<T> type, Type genericType, Annotation[] anns)
   {
      abortIfClosed();
      if (entity != null)
      {
         if (type.isInstance((this.entity)))
         {
            return (T)entity;
         }
         else if (entity instanceof InputStream)
         {
            setInputStream((InputStream)entity);
            entity = null;
         }
         else if (bufferedEntity == null)
         {
            throw new RuntimeException("The entity was already read, and it was of type "
                    + entity.getClass());
         }
         else
         {
            entity = null;
         }
      }

      if (entity == null)
      {
         if (status == HttpStatus.SC_NO_CONTENT)
            return null;

         try
         {
            entity = readFrom(type, genericType, getMediaType(), anns);
            if (entity != null && !InputStream.class.isInstance(entity)) close();
         }
         catch (RuntimeException e)
         {
            close();
            throw e;
         }
      }
      return (T) entity;
   }

   protected <T> Object readFrom(Class<T> type, Type genericType,
                                  MediaType media, Annotation[] annotations)
   {
      Type useGeneric = genericType == null ? type : genericType;
      Class<?> useType = type;
      media = media == null ? MediaType.WILDCARD_TYPE : media;
      boolean isMarshalledEntity = false;
      if (type.equals(MarshalledEntity.class))
      {
         isMarshalledEntity = true;
         ParameterizedType param = (ParameterizedType) useGeneric;
         useGeneric = param.getActualTypeArguments()[0];
         useType = Types.getRawType(useGeneric);
      }


      MessageBodyReader reader1 = configuration.getMessageBodyReader(useType,
              useGeneric, annotations, media);
      if (reader1 == null)
      {
         throw new ProcessingException(format(
                 "Unable to find a MessageBodyReader of content-type %s and type %s",
                 media, useType));
      }




      Providers current = ResteasyProviderFactory.getContextData(Providers.class);
      ResteasyProviderFactory.pushContext(Providers.class, configuration);
      try
      {
         InputStream is = getEntityStream();
         if (is == null)
         {
            throw new IllegalStateException("Input stream was empty, there is no entity");
         }
         if (isMarshalledEntity)
         {
            is = new InputStreamToByteArray(is);

         }

         ReaderInterceptor[] readerInterceptors = configuration.getReaderInterceptors(null, null);

         final Object obj = new ClientReaderInterceptorContext(readerInterceptors, reader1, useType,
                 useGeneric, this.annotations, media, getStringHeaders(), is, properties)
                 .proceed();
         if (isMarshalledEntity)
         {
            InputStreamToByteArray isba = (InputStreamToByteArray) is;
            final byte[] bytes = isba.toByteArray();
            return new MarshalledEntity()
            {
               @Override
               public byte[] getMarshalledBytes()
               {
                  return bytes;
               }

               @Override
               public Object getEntity()
               {
                  return obj;
               }
            };
         }
         else
         {
            return (T) obj;
         }

      }
      catch (IOException io)
      {
         throw new ProcessingException(io);
      }
      finally
      {
         ResteasyProviderFactory.popContextData(Providers.class);
         if (current != null) ResteasyProviderFactory.pushContext(Providers.class, current);

      }
   }

   @Override
   public boolean bufferEntity()
   {
      abortIfClosed();
      if (bufferedEntity != null) return true;
      if (entity != null) return false;
      if (metadata.getFirst(HttpHeaderNames.CONTENT_TYPE) == null) return false;
      try
      {
         bufferedEntity = ReadFromStream.readFromStream(1024, getInputStream());
      }
      catch (IOException e)
      {
         throw new ProcessingException(e);
      }
      return true;
   }

}
