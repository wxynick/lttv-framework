package com.letv.mobile.core.rpc.rest;

import com.letv.mobile.core.async.api.IAsyncCallback;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public interface ClientHttpEngine
{
   void invoke(ClientInvocation request,IAsyncCallback<ClientResponse> callback);
   void close();

}
