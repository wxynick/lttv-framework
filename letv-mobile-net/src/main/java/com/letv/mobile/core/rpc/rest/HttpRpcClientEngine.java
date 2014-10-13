package com.letv.mobile.core.rpc.rest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;

import com.letv.javax.ws.rs.core.MultivaluedMap;
import com.letv.mobile.core.async.api.IAsyncCallback;
import com.letv.mobile.core.log.api.Trace;
import com.letv.mobile.core.rpc.api.DataEntity;
import com.letv.mobile.core.rpc.api.Request;
import com.letv.mobile.core.rpc.api.RequestCallback;
import com.letv.mobile.core.rpc.http.api.HttpMethod;
import com.letv.mobile.core.rpc.http.api.HttpRequest;
import com.letv.mobile.core.rpc.http.api.HttpResponse;
import com.letv.mobile.core.rpc.http.api.HttpRpcService;
import com.letv.mobile.core.rpc.http.api.ParamConstants;
import com.letv.mobile.core.rpc.util.CaseInsensitiveMap;
import com.letv.mobile.core.util.SelfExpandingBufferredInputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public class HttpRpcClientEngine implements ClientHttpEngine
{
	private static final Trace log = Trace.register(HttpRpcClientEngine.class);
   protected final HttpRpcService httpClient;


   public HttpRpcClientEngine(HttpRpcService service)
   {
      this.httpClient = service;
   }


   public void invoke(final ClientInvocation invocation,final IAsyncCallback<ClientResponse> callback)
   {
	   String uri = invocation.getUri().toString();
	   Map<String, Object> params = new HashMap<String, Object>();
	   params.put(ParamConstants.PARAMETER_KEY_HTTP_METHOD, HttpMethod.valueOf(invocation.getMethod()));
	   final HttpRequest httpMethod = (HttpRequest)this.httpClient.createRequest(uri, params);
//	   try {
//		   loadHttpMethod(invocation, httpMethod);
//	   } catch (Throwable t) {
//		   callback.failed(t);
//		   return;
//	   }
	   callback.setCancellable(httpMethod);
	   httpMethod.invokeAsync(new RequestCallback<HttpResponse, Request<HttpResponse>>() {

		   @Override
		   public void onResponseReceived(Request<HttpResponse> req,
				   final HttpResponse res) {
			   ClientResponse response = new ClientResponse(invocation.getClientConfiguration())
			   {
				   InputStream stream;
				   InputStream hc4Stream;

				   @Override
				   protected void setInputStream(InputStream is)
				   {
					   stream = is;
				   }

				   public InputStream getInputStream()
				   {
					   if (stream == null)
					   {
						   DataEntity entity = res.getResponseEntity();
						   if (entity == null) return null;
						   try
						   {
							   hc4Stream = entity.getContent();
							   stream = new SelfExpandingBufferredInputStream(hc4Stream);
						   }
						   catch (IOException e)
						   {
							   throw new RuntimeException(e);
						   }
					   }
					   return stream;
				   }

				   public void releaseConnection()
				   {
					   isClosed = true;
					   // Apache Client 4 is stupid,  You have to get the InputStream and close it if there is an entity
					   // otherwise the connection is never released.  There is, of course, no close() method on response
					   // to make this easier.
					   try
					   {
						   if (stream != null)
						   {
							   stream.close();
						   }
						   else
						   {
							   InputStream is = getInputStream();
							   if (is != null)
							   {
								   is.close();
							   }
						   }
						   if (hc4Stream != null)
						   {
							   // just in case the input stream was entirely replaced and not wrapped, we need
							   // to close the apache client input stream.
							   hc4Stream.close();
						   }
					   }
					   catch (Exception ignore)
					   {
					   }
				   }
			   };
			   response.setProperties(invocation.getMutableProperties());
			   response.setStatus(res.getStatusCode());
			   response.setHeaders(extractHeaders(res));
			   response.setClientConfiguration(invocation.getClientConfiguration());
			   callback.success(response);
		   }

		   @Override
		   public void onError(Request<HttpResponse> request, Throwable exception) {
			   callback.failed(exception);
		   }

		@Override
		public void onPrepare(Request<HttpResponse> request) throws Exception {
			loadHttpMethod(invocation,(HttpRequest)httpMethod);
		}
	   });
   }


   protected void loadHttpMethod(final ClientInvocation request, HttpRequest httpMethod) throws Exception
   {

      if (request.getEntity() != null)
      {

         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         request.getDelegatingOutputStream().setDelegate(baos);
         try
         {
            request.writeRequestBody(request.getEntityStream());
            final ByteArrayEntity entity = new ByteArrayEntity(baos.toByteArray())
            {
               @Override
               public org.apache.http.Header getContentType()
               {
                  return new BasicHeader("Content-Type", request.getHeaders().getMediaType().toString());
               }
            };
            if(log.isDebugEnabled()){
            	try {
            		log.debug("Request Body :",new String(baos.toByteArray(),"UTF-8"));
            	}catch(Throwable t){}
            }
            commitHeaders(request, httpMethod);
            httpMethod.setRequestEntity(new DataEntity() {
				
				@Override
				public boolean isRepeatable() {
					return entity.isRepeatable();
				}
				
				@Override
				public String getContentType() {
					return entity.getContentType().getValue();
				}
				
				@Override
				public long getContentLength() {
					return entity.getContentLength();
				}
				
				@Override
				public InputStream getContent() throws IOException {
					return entity.getContent();
				}

				@Override
				public String getContentEncoding() {
					return entity.getContentEncoding().getValue();
				}

				@Override
				public void consumeContent() throws IOException {
					entity.consumeContent();
					
				}
			});
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
      else // no body
      {
         commitHeaders(request, httpMethod);
      }
   }
   
   protected  CaseInsensitiveMap<String> extractHeaders(
           HttpResponse response)
   {
      final CaseInsensitiveMap<String> headers = new CaseInsensitiveMap<String>();
      Map<String,String> map = response.getHeaders();
      for (Entry<String,String> header : map.entrySet())
      {
         headers.putSingle(header.getKey(), header.getValue());
      }
      return headers;
   }


   protected void commitHeaders(ClientInvocation request, HttpRequest httpMethod)
   {
      MultivaluedMap<String, String> headers = request.getHeaders().asMap();
      for (Map.Entry<String, List<String>> header : headers.entrySet())
      {
         List<String> values = header.getValue();
         String val = null;
         if((values != null)&&(values.size() > 0)){
        	 StringBuffer buf = new StringBuffer();
        	 int cnt = 0;
        	 for (String s : values) {
        		 if(cnt > 0){
        			 buf.append(',');
        		 }
				buf.append(s);
				cnt++;
			}
        	val = buf.toString();
         }
         httpMethod.setHeader(header.getKey(), val);
      }
   }

   public void close()
   {
   }

   public boolean isClosed()
   {
      return false;
   }
}