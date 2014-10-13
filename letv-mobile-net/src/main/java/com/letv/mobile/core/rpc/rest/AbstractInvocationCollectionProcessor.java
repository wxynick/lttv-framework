package com.letv.mobile.core.rpc.rest;
/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public abstract class AbstractInvocationCollectionProcessor extends AbstractCollectionProcessor<ClientInvocationBuilder> implements InvocationProcessor
{
   public AbstractInvocationCollectionProcessor(String paramName)
   {
      super(paramName);
   }

   @Override
   public void process(ClientInvocationBuilder invocation, Object param)
   {
      buildIt(invocation, param);
   }
}
