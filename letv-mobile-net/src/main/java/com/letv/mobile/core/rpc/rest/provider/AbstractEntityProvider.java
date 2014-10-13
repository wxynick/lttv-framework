package com.letv.mobile.core.rpc.rest.provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.letv.javax.ws.rs.core.MediaType;
import com.letv.javax.ws.rs.ext.MessageBodyReader;
import com.letv.javax.ws.rs.ext.MessageBodyWriter;

/**
 * A AbstractEntityProvider.
 *
 * @param <T>
 * @author <a href="ryan@damnhandy.com>Ryan J. McDonough</a>
 * @version $Revision$
 */
public abstract class AbstractEntityProvider<T>
        implements MessageBodyReader<T>, MessageBodyWriter<T>
{

   public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

}
