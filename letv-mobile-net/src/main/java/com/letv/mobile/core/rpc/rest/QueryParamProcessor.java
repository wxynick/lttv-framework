package com.letv.mobile.core.rpc.rest;
import com.letv.javax.ws.rs.client.WebTarget;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public class QueryParamProcessor extends AbstractWebTargetCollectionProcessor
{
   public QueryParamProcessor(String paramName)
   {
      super(paramName);
   }

   @Override
   protected WebTarget apply(WebTarget target, Object object)
   {
      ResteasyWebTarget t = (ResteasyWebTarget)target;
      return t.queryParamNoTemplate(paramName, object);
   }


}