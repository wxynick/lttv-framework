/**
 * 
 */
package com.letv.mobile.core.rpc.http.apache;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;

import com.letv.mobile.core.log.api.Trace;
import com.letv.mobile.core.rpc.api.DataEntity;
import com.letv.mobile.core.rpc.api.IRequestTimeoutControl;
import com.letv.mobile.core.rpc.api.Request;
import com.letv.mobile.core.rpc.api.RequestCallback;
import com.letv.mobile.core.rpc.http.api.HttpMethod;
import com.letv.mobile.core.rpc.http.api.HttpRequest;
import com.letv.mobile.core.rpc.http.api.HttpResponse;
import com.letv.mobile.core.rpc.http.api.ParamConstants;
/**
 * @author  
 *
 */
public class HttpRequestImpl implements HttpRequest {
	private static final Trace log = Trace.register(HttpRequestImpl.class);
	private static final long[] retryIntervals = new long[] {100L, 500L, 2000L, 5000L};
	private IHttpClientContext clientContext;
	private Map<String,String> headers = new HashMap<String,String>();
	private DataEntity dataEntity;
	private String targetUrl;
	private Map<String, Object> httpParams;
	private HttpRequestBase httpRequest;
	private volatile boolean cancelled;
	
	
	public HttpRequestImpl(IHttpClientContext ctx, String url, Map<String, Object> params){
		this.clientContext = ctx;
		this.targetUrl = url;
		this.httpParams = params;
	}
	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.rpc.api.Request#submit(long, java.util.concurrent.TimeUnit, com.wxxr.mobile.core.rpc.api.RequestCallback)
	 */
	@Override
	public void invokeAsync(final RequestCallback<HttpResponse, Request<HttpResponse>> callback) {
		clientContext.getExecutor().execute(new Runnable() {
			
			@Override
			public void run() {
				IRequestTimeoutControl control = clientContext.getTimeoutControl();
				HttpResponse resp = null;
				try {
					control.registerRequest(HttpRequestImpl.this, callback);
					int retry = 1;
					while(true){
						try {
							resp = doInvoke();
							break;
						}catch(IOException e){
							if(retry < 3){
								Thread.sleep(retryIntervals[retry++]);
							}else{
								throw e;
							}
						}
					}
					if(resp != null){
						control.notifySuccess(HttpRequestImpl.this, resp);
					}
				} catch (Throwable e) {
					control.notifyFailed(HttpRequestImpl.this, e);
				} finally {
					control.unregisterRequest(HttpRequestImpl.this);
					if((resp != null)&&(resp.getResponseEntity() != null)){
						try {
							resp.getResponseEntity().consumeContent();
						} catch (Throwable e) {
						}
					}
				}
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.rpc.api.Request#setRequestEntity(com.wxxr.mobile.core.rpc.api.DataEntity)
	 */
	@Override
	public void setRequestEntity(DataEntity entity) {
		this.dataEntity = entity;
	}

	
	@Override
	public DataEntity getRequestEntity() {
		return this.dataEntity;
	}
	
	@Override
	public Map<String, String> getHeaders() {
		return Collections.unmodifiableMap(this.headers);
	}
	
	
	@Override
	public String getHeader(String key) {
		return this.headers.get(key);
	}
	
	
	@Override
	public void setHeader(String key, String value) {
		this.headers.put(key, value);
	}


	  protected HttpRequestBase createHttpMethod(String url, Map<String, Object> params)
	   {
		   HttpMethod method = (HttpMethod)params.get(ParamConstants.PARAMETER_KEY_HTTP_METHOD);
		   HttpRequestBase m = null;
		   switch(method){
		   case GET:
			   HttpGet get = new HttpGet(url);
			   HttpClientParams.setRedirecting(get.getParams(), true);
			   m = get;
			   break;
		   case POST:
			   m = new HttpPost(url);
			   break;
		   case DELETE:
			   m = new HttpDelete(url);
			   break;
		   case HEAD:
			   m = new HttpHead(url);
			   break;
		   case PUT:
		         m = new HttpPut(url);
		         break;
		   case OPTIONS:
			   m = new HttpOptions(url);
			   break;
		   }
		   if(m == null){
			   throw new IllegalArgumentException("Invalid http method :"+method);
		   }
		   for (String key : params.keySet()) {
			   if(ParamConstants.PARAMETER_KEY_HTTP_METHOD.equals(key)){
				   continue;
			   }
			   Object val = params.get(key);
			   if(val instanceof String){
				   m.addHeader(key, (String)val);
			   }
		   }
		   return m;
	   }



	   protected HttpRequestBase loadHttpMethod(final HttpRequestBase httpMethod) throws Exception
	   {
	      if (httpMethod instanceof HttpGet) // todo  && request.followRedirects())
	      {
	         HttpClientParams.setRedirecting(httpMethod.getParams(), true);
	      }
	      else
	      {
	         HttpClientParams.setRedirecting(httpMethod.getParams(), false);
	      }

	      if (this.dataEntity != null)
	      {
	         if (httpMethod instanceof HttpGet) {
	        	 throw new IOException("A GET request cannot have a body.");
	         }
        	 final DataEntity entity = this.dataEntity;
             HttpPost post = (HttpPost) httpMethod;
             commitHeaders(httpMethod);
             post.setEntity(new EntityWrapper(entity));
	      } else { // no body
	         commitHeaders(httpMethod);
	      }
	      return httpMethod;
	   }

	   protected void commitHeaders(HttpRequestBase httpMethod)
	   {
	      for (Map.Entry<String, String> header : headers.entrySet())
	      {
	         String value = header.getValue();
	         httpMethod.addHeader(header.getKey(), value);
	      }
	   }
//	@Override
//	public HttpResponse invoke(long timeout, TimeUnit unit) throws Exception {
//		if(timeout <= 0L){
//			httpRequest = loadHttpMethod(createHttpMethod(targetUrl, httpParams));
//			return clientContext.invoke(httpRequest);
//		}else{
//			long time = System.currentTimeMillis();
//			long timeoutInMills = TimeUnit.MILLISECONDS.convert(timeout, unit);
//			while(true){
//				if((System.currentTimeMillis() - time) > timeoutInMills){
//					throw new TimeoutException();
//				}
//				try {
//					Future<HttpResponse> future = clientContext.getExecutor().submit(new Callable<HttpResponse>() {
//		
//						@Override
//						public HttpResponse call() throws Exception {
//							httpRequest = loadHttpMethod(createHttpMethod(targetUrl, httpParams));
//							return clientContext.invoke(httpRequest);
//						}
//					});
//					timeoutInMills -= (System.currentTimeMillis() - time);
//					if(timeoutInMills <= 0){
//						timeoutInMills = 1;
//					}
//					return future.get(timeoutInMills, TimeUnit.MILLISECONDS);
//				}catch(RejectedExecutionException e){
//					log.warn("Caught RejectedExecutionException when submit remote http call, going to try in 100 ms");
//					Thread.sleep(100);
//				}
//			}
//		}
//	}
	
	@Override
	public String getURI() {
		return this.httpRequest != null ? this.httpRequest.getURI().toString() : null;
	}
	
//	@Override
//	public void abort() {
//		if(this.httpRequest != null){
//			this.httpRequest.abort();
//		}
//	}
	@Override
	public synchronized void cancel() {
		if(log.isDebugEnabled()){
			log.debug("Going to abort request :"+this);
		}
		if((this.httpRequest != null)&&(!this.httpRequest.isAborted())){
			this.httpRequest.abort();
		}
		this.cancelled = true;
	}
	
	@Override
	public synchronized boolean isCancelled() {
		return this.cancelled;
	}
	/**
	 * @return
	 * @throws Exception
	 */
	public HttpResponse doInvoke() throws Exception {
		synchronized(HttpRequestImpl.this){
			if(cancelled){
				if(log.isInfoEnabled()){
					log.info("Request is aborted :"+HttpRequestImpl.this);
				}
				return null;
			}
			httpRequest = loadHttpMethod(createHttpMethod(targetUrl, httpParams));
		}
		HttpResponse resp = clientContext.invoke(httpRequest);
		return resp;
	}
}
