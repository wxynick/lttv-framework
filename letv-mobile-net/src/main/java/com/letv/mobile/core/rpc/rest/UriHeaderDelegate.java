package com.letv.mobile.core.rpc.rest;
import java.net.URI;
import com.letv.javax.ws.rs.ext.RuntimeDelegate;
/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public class UriHeaderDelegate implements RuntimeDelegate.HeaderDelegate
{
   public Object fromString(String value) throws IllegalArgumentException
   {
      if (value == null) throw new IllegalArgumentException("URI value is null");
      return URI.create(value);
   }

   public String toString(Object value)
   {
      if (value == null) throw new IllegalArgumentException("param was null");
      URI uri = (URI) value;
      return uri.toASCIIString();
   }
}