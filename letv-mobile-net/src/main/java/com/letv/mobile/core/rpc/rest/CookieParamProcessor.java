package com.letv.mobile.core.rpc.rest;

import com.letv.javax.ws.rs.core.Cookie;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public class CookieParamProcessor implements InvocationProcessor
{
   private String cookieName;

   public CookieParamProcessor(String cookieName)
   {
      this.cookieName = cookieName;
   }

   public String getCookieName()
   {
      return cookieName;
   }

   @Override
   public void process(ClientInvocationBuilder request, Object object)
   {
      if (object == null) return;  // don't set a null value
      if (object instanceof Cookie)
      {
         Cookie cookie = (Cookie) object;
         request.cookie(cookie);
      }
      else
      {
         request.cookie(new Cookie(cookieName, request.getInvocation().getClientConfiguration().toString(object)));
      }
   }
}
