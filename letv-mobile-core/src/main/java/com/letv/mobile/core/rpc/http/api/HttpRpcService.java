/**
 * 
 */
package com.letv.mobile.core.rpc.http.api;

import com.letv.mobile.core.rpc.api.RpcService;

/**
 * @author  
 *
 */
public interface HttpRpcService extends RpcService<HttpRequest> {
	void resetHttpClientContext();
}
