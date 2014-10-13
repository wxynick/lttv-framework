package com.letv.mobile.core.rpc.rest;

import com.letv.javax.ws.rs.client.Entity;
import com.letv.javax.ws.rs.core.Form;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public class FormParamProcessor extends AbstractInvocationCollectionProcessor
{

   public FormParamProcessor(String paramName)
   {
      super(paramName);
   }

   @Override
   protected ClientInvocationBuilder apply(ClientInvocationBuilder target, Object object)
   {
      Form form = null;
      Object entity = target.getInvocation().getEntity();
      if (entity != null)
      {
         if (entity instanceof Form)
         {
            form = (Form) entity;
         }
         else
         {
            throw new RuntimeException("Cannot set a form parameter if entity body already set");
         }
      }
      else
      {
         form = new Form();
         target.getInvocation().setEntity(Entity.form(form));
      }
      String value = target.getInvocation().getClientConfiguration().toString(object);
      form.param(paramName, value);
      return target;
   }

}