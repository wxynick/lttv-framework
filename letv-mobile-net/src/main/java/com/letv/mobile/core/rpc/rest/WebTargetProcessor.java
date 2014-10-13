package com.letv.mobile.core.rpc.rest;

import com.letv.javax.ws.rs.client.WebTarget;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public interface WebTargetProcessor
{
   WebTarget build(WebTarget target, Object param);
}
