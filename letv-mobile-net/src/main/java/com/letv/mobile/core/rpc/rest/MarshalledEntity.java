package com.letv.mobile.core.rpc.rest;

/**
 * Allows you to access the entity's raw bytes as well as the marshalled object.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public interface MarshalledEntity<T>
{
   byte[] getMarshalledBytes();

   T getEntity();

}
