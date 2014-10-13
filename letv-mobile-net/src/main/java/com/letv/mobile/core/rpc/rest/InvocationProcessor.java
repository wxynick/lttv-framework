package com.letv.mobile.core.rpc.rest;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public interface InvocationProcessor
{
   void process(ClientInvocationBuilder invocation, Object param);
}
