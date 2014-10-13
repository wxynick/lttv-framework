package com.letv.mobile.core.rpc.rest;
import java.util.Set;

import com.letv.javax.ws.rs.core.MultivaluedMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public class HeaderHelper
{
   public static void setAllow(MultivaluedMap headers, String[] methods)
   {
      if (methods == null)
      {
         headers.remove("Allow");
         return;
      }
      StringBuilder builder = new StringBuilder();
      boolean isFirst = true;
      for (String l : methods)
      {
         if (isFirst)
         {
            isFirst = false;
         }
         else
         {
            builder.append(", ");
         }
         builder.append(l);
      }
      headers.putSingle("Allow", builder.toString());
   }

   public static void setAllow(MultivaluedMap headers, Set<String> methods)
   {
      if (methods == null)
      {
         headers.remove("Allow");
         return;
      }
      StringBuilder builder = new StringBuilder();
      boolean isFirst = true;
      for (String l : methods)
      {
         if (isFirst)
         {
            isFirst = false;
         }
         else
         {
            builder.append(", ");
         }
         builder.append(l);
      }
      headers.putSingle("Allow", builder.toString());
   }

}
