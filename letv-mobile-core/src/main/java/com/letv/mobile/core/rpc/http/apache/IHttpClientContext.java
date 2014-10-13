/**
 * 
 */
package com.letv.mobile.core.rpc.http.apache;

import java.util.concurrent.ExecutorService;

import org.apache.http.client.methods.HttpRequestBase;

import com.letv.mobile.core.rpc.api.IRequestTimeoutControl;
import com.letv.mobile.core.rpc.http.api.HttpResponse;

/**
 * @author  
 *
 */
public interface IHttpClientContext {
	
	HttpResponse invoke(HttpRequestBase request) throws Exception;
	
	ExecutorService getExecutor();
	
	IRequestTimeoutControl getTimeoutControl();
}
