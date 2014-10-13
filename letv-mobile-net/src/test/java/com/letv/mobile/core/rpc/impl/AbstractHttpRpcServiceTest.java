package com.letv.mobile.core.rpc.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.letv.mobile.core.microkernel.api.IKernelContext;
import com.letv.mobile.core.rpc.api.DataEntity;
import com.letv.mobile.core.rpc.api.Request;
import com.letv.mobile.core.rpc.api.RequestCallback;
import com.letv.mobile.core.rpc.http.apache.AbstractHttpRpcService;
import com.letv.mobile.core.rpc.http.api.HttpMethod;
import com.letv.mobile.core.rpc.http.api.HttpParamsBean;
import com.letv.mobile.core.rpc.http.api.HttpRequest;
import com.letv.mobile.core.rpc.http.api.HttpResponse;
import com.letv.mobile.core.rpc.http.api.HttpStatus;
import com.letv.mobile.core.security.api.ISiteSecurityService;

public class AbstractHttpRpcServiceTest {
	@Before
	public void setUp() throws Exception {
		
	}
	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void testCreateRequest() throws Exception {
		AbstractHttpRpcService service = new AbstractHttpRpcService();
		MockApplication app = new MockApplication(){

			@Override
			protected void initModules() {
				
				
			}

		};
		IKernelContext context = app.getContext();
		service.startup(context);
		context.registerService(ISiteSecurityService.class, new ISiteSecurityService() {
			
			@Override
			public KeyStore getTrustKeyStore() {
				// TODO Auto-generated method stub
				return null;
			}
						
			@Override
			public KeyStore getSiteKeyStore() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public HostnameVerifier getHostnameVerifier() {
				// TODO Auto-generated method stub
				return null;
			}
		});
		
		HttpRequest req = service.createRequest("http://www.baidu.com", new HttpParamsBean().setHttpMethod(HttpMethod.GET).toMap());
		final CountDownLatch latch = new CountDownLatch(1);
		final HttpResponse[] respHolder = new HttpResponse[1];
		final String[] values = new String[1];
		req.invokeAsync(new RequestCallback<HttpResponse, Request<HttpResponse>>() {
			
			@Override
			public void onResponseReceived(Request<HttpResponse> request,
					HttpResponse response) {
				respHolder[0] = response;
				try {
					DataEntity entity = response.getResponseEntity();
					InputStream is = entity.getContent();
					ByteArrayOutputStream aos = new ByteArrayOutputStream();
					byte[] bytes = new byte[512];
					int len = 0;
					while((len = is.read(bytes)) != -1){
						aos.write(bytes, 0, len);
					}
					is.close();
					aos.close();
					byte[] data = aos.toByteArray();
					values[0] = new String(data);
				}catch(Throwable t){
					t.printStackTrace();
				}
				latch.countDown();
				
			}
			
			@Override
			public void onError(Request<HttpResponse> request, Throwable exception) {
				latch.countDown();
			}

			@Override
			public void onPrepare(Request<HttpResponse> request)
					throws Exception {
			}
		});
		latch.await(30, TimeUnit.SECONDS);
		HttpResponse resp = respHolder[0];
		Assert.assertTrue(resp != null);
		Assert.assertEquals(HttpStatus.SC_OK, resp.getStatusCode());
		System.out.println("Headers :["+resp.getHeaders()+"]");
		DataEntity entity = resp.getResponseEntity();
		Assert.assertNotNull(entity);
		System.out.println("ContentType :"+entity.getContentType());
		System.out.println("ContentLength :"+entity.getContentLength());
		System.out.println("Content Encoding :"+entity.getContentEncoding());
		System.out.print(values[0]);
	}

}
