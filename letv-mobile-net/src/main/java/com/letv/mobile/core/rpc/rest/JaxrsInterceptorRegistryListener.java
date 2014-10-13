package com.letv.mobile.core.rpc.rest;
/**
 * Callback interface for when an interceptor registry changes
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public interface JaxrsInterceptorRegistryListener
{
   void registryUpdated(JaxrsInterceptorRegistry registry);

}
