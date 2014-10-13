/**
 * 
 */
package com.letv.mobile.core.rpc.http.apache;

import java.io.IOException;
import java.net.URI;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.http.Header;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;

import com.letv.mobile.core.api.IUserAuthCredential;
import com.letv.mobile.core.api.IUserAuthManager;
import com.letv.mobile.core.log.api.Trace;
import com.letv.mobile.core.microkernel.api.IKernelContext;
import com.letv.mobile.core.rpc.api.IRequestTimeoutControl;
import com.letv.mobile.core.rpc.api.Request;
import com.letv.mobile.core.rpc.api.RequestCallback;
import com.letv.mobile.core.rpc.http.api.HttpHeaderNames;
import com.letv.mobile.core.rpc.http.api.HttpRequest;
import com.letv.mobile.core.rpc.http.api.HttpResponse;
import com.letv.mobile.core.rpc.http.api.HttpRpcService;
import com.letv.mobile.core.security.api.ISiteSecurityService;

/**
 * @author wangxuyang
 *
 */
public class AbstractHttpRpcService implements HttpRpcService {

	private static final Trace log = Trace.register(AbstractHttpRpcService.class);
	private static class RequestWrapper {
		private HttpRequest request;
		private RequestCallback<HttpResponse, Request<HttpResponse>> callback;
		long timestamp;
	}
	
	private class InternelTimeoutControl implements IRequestTimeoutControl,Runnable {
		private Thread thread;
		private volatile boolean keepAlive;
		
		private ArrayList<RequestWrapper> reqs = new ArrayList<AbstractHttpRpcService.RequestWrapper>();
		
		@Override
		public synchronized boolean unregisterRequest(HttpRequest request) {
			RequestWrapper match = findMatch(request);
			if(match != null){
				return reqs.remove(match);
			}
			return false;
		}

		/**
		 * @param request
		 * @return
		 */
		public RequestWrapper findMatch(HttpRequest request) {
			RequestWrapper match = null;
			for (RequestWrapper req : reqs) {
				if(req.request == request){
					match = req;
					break;
				}
			}
			return match;
		}
		
		@Override
		public synchronized void registerRequest(HttpRequest request,
				RequestCallback<HttpResponse, Request<HttpResponse>> callback) throws Exception {
			RequestWrapper match = findMatch(request);
			if(match == null){
				match = new RequestWrapper();
				match.callback = callback;
				match.request = request;
				match.timestamp = System.currentTimeMillis();
				reqs.add(match);
				if(callback != null){
					callback.onPrepare(request);
				}
			}
			
		}
		
		private synchronized void checkTimeout() {
			long timeout = requestTimeoutInSeconds*1000L;
			
			for (Iterator<RequestWrapper> itr = reqs.iterator();itr.hasNext();) {
				RequestWrapper req = itr.next();
				if((System.currentTimeMillis() - req.timestamp) >= timeout){
					itr.remove();
					handleTimeoutRequest(req);
				}
			}
		}

		/**
		 * @param req
		 */
		private void handleTimeoutRequest(RequestWrapper req) {
			log.warn("Http RPC request timeout, request url :"+req.request.getURI());
			try {
				if(req.callback != null){
					req.callback.onError(req.request, new TimeoutException());
				}
				req.request.cancel();
			}catch(Throwable t){
				log.warn("Failed to abort timeout request :["+req.request+"]", t);
			}
		}

		@Override
		public void run() {
			this.thread = Thread.currentThread();
			this.keepAlive = true;
			while(this.keepAlive) {
				checkTimeout();
				try {
					Thread.sleep(500L);
				} catch (InterruptedException e) {
				}
			}		
		}
		
		public void start() {
			new Thread(this, "HttpRequest timeout control Thread").start();
		}
		
		public void stop() {
			this.keepAlive = false;
			if(this.thread != null){
				this.thread.interrupt();
				try {
					this.thread.join(2000L);
				} catch (InterruptedException e) {
				}
				this.thread = null;
			}
		}

		@Override
		public void notifySuccess(HttpRequest request, HttpResponse response) {
			RequestWrapper req = findMatch(request);
			if((req != null)&&(req.callback != null)){
				req.callback.onResponseReceived(request, response);
			}
		}

		@Override
		public void notifyFailed(HttpRequest request, Throwable error) {
			RequestWrapper req = findMatch(request);
			if((req != null)&&(req.callback != null)){
				req.callback.onError(request, error);
			}
		}
	
	}

	private HttpClient httpClient;
	private IKernelContext appContext;

	protected boolean disableTrustManager;
	protected int connectionPoolSize = 6;
	protected int maxPooledPerRoute = 0;
	protected long connectionTTL = -1;
	private ExecutorService executor;
	private boolean enablegzip = true;
//	private HttpContext localContext = new BasicHttpContext();
	private  BasicCookieStore  cookies=new BasicCookieStore();
	private int requestTimeoutInSeconds = 30;
	
	private InternelTimeoutControl internalControl;
	
	private IHttpClientContext context = new IHttpClientContext() {

		private IRequestTimeoutControl control;
		@Override
		public HttpResponse invoke(HttpRequestBase request) throws Exception 
		{
			org.apache.http.HttpResponse resp = null;
			if(log.isDebugEnabled()){
				log.debug("Sending HttpRequest :{"+printRequest(request)+"\n}");
			}
			try {
				resp = httpClient.execute(request);
				if (resp.getStatusLine().getStatusCode()==302) {
					for (Header header : resp.getAllHeaders()) {
						if ("location".equalsIgnoreCase(header.getName())) {
							String endpointUrl =  header.getValue();
							request.setURI(URI.create(endpointUrl));
							if(log.isDebugEnabled()){
								log.debug("After redirect, Sending HttpRequest :{"+printRequest(request)+"\n}");
							}
							resp = httpClient.execute(request);
							break;
						}
					}
				}
				return new HttpResponseImpl(resp);
			}finally {
				if(log.isDebugEnabled()){
					if(resp != null){
						log.debug("HttpResponse :{"+printResponse(resp)+"\n}");
					}
				}
			}
		}

		@Override
		public ExecutorService getExecutor() {
			return executor;
		}

		@Override
		public IRequestTimeoutControl getTimeoutControl() {
			if(this.control == null){
				this.control = appContext.getService(IRequestTimeoutControl.class);
				if(this.control == null){
					internalControl = new InternelTimeoutControl();
					internalControl.start();
					this.control = internalControl;
				}
			}
			return this.control;
		}
	};

	protected String printRequest(org.apache.http.HttpRequest req) {
		RequestLine line = req.getRequestLine();
		Header[] headers = req.getAllHeaders();
		StringBuffer buf = new StringBuffer();
		buf.append("URI :").append(line.getUri()).append(" , Method :").append(line.getMethod()).append(" , Protocol :").append(line.getProtocolVersion()).append('\n');
		if(headers != null){
			buf.append("Headers :[\n");
			for (Header header : headers) {
				buf.append('\t').append(header.getName()).append(" -> ").append(header.getValue()).append('\n');
			}
			buf.append("]\n");
		}
		return buf.toString();
	}
	
	protected String printResponse(org.apache.http.HttpResponse resp) {
		StatusLine line = resp.getStatusLine();
		Header[] headers = resp.getAllHeaders();
		StringBuffer buf = new StringBuffer();
		buf.append("Status :").append(line.getReasonPhrase()).append(" , code :").append(line.getStatusCode()).append(" , Protocol :").append(line.getProtocolVersion()).append('\n');
		if(headers != null){
			buf.append("Request Headers :[\n");
			for (Header header : headers) {
				buf.append('\t').append(header.getName()).append(" -> ").append(header.getValue()).append('\n');
			}
			buf.append("]\n");
		}
		return buf.toString();
	}


	protected void initHttpEngine(ISiteSecurityService securityService)
	{
		final HostnameVerifier verifier = securityService != null ? securityService.getHostnameVerifier() : null;
		X509HostnameVerifier x509verifier = null;
		if (verifier != null)  {
			x509verifier = new X509HostnameVerifier() {

				@Override
				public void verify(String host, SSLSocket ssl) throws IOException
				{
					if (!verifier.verify(host, ssl.getSession())) throw new SSLException("Hostname verification failure");
				}

				@Override
				public void verify(String host, X509Certificate cert) throws SSLException
				{
					throw new SSLException("This verification path not implemented");
				}

				@Override
				public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException
				{
					throw new SSLException("This verification path not implemented");
				}

				@Override
				public boolean verify(String s, SSLSession sslSession)
				{
					return verifier.verify(s, sslSession);
				}
			};	      
		}
		else
		{
			x509verifier = new AllowAllHostnameVerifier();
		}
		try
		{
			KeyStore clientKeyStore = null;
			KeyStore truststore = null;
			clientKeyStore = securityService != null ? securityService.getSiteKeyStore() : null;
			truststore = securityService != null ? securityService.getTrustKeyStore() : null;
			SSLSocketFactory sslsf = null;
			if (clientKeyStore != null || truststore != null)
			{
				sslsf = new SSLSocketFactory(clientKeyStore, null, truststore);
				sslsf.setHostnameVerifier(x509verifier);
			}
			else
			{
				sslsf = SSLSocketFactory.getSocketFactory();
				sslsf.setHostnameVerifier(x509verifier);
			}
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(
					new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			Scheme httpsScheme = new Scheme("https", sslsf, 443);
			registry.register(httpsScheme);
			registry.register(new Scheme("https", sslsf, 8443));
			ClientConnectionManager cm = null;
			if (connectionPoolSize > 0)
			{
				BasicHttpParams params = new BasicHttpParams();
				ConnManagerParams.setMaxTotalConnections(params, connectionPoolSize);
				ConnManagerParams.setTimeout(params, connectionTTL < 0 ? requestTimeoutInSeconds : connectionTTL);
				if (maxPooledPerRoute == 0) maxPooledPerRoute = connectionPoolSize;
				ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRoute() {
			        
			        public int getMaxForRoute(HttpRoute route) {
			            return maxPooledPerRoute;
			        }
			        
			    });
				ThreadSafeClientConnManager tcm = new ThreadSafeClientConnManager(params,registry){
					
					private AtomicInteger requestCounter = new AtomicInteger(0);
					private AtomicInteger releaseCounter = new AtomicInteger(0);
					@Override
					public void releaseConnection(ManagedClientConnection conn,
							long validDuration, TimeUnit timeUnit) {
						try {
							this.releaseCounter.incrementAndGet();
							super.releaseConnection(conn, validDuration, timeUnit);
							if(log.isDebugEnabled()){
								log.debug("Release connection for :"+conn+", duration :"+validDuration+":"+timeUnit+", connection in using :"+this.getConnectionsInPool()+", request # :"+this.requestCounter.get()+", release #: "+this.releaseCounter.get());
							}
						}catch(RuntimeException e){
							log.error("caught runtime exception when release connection, connection in using :"+this.getConnectionsInPool()+", error message :",e);
						}
					}

					@Override
					public ClientConnectionRequest requestConnection(
							HttpRoute route, Object state) {
						final ClientConnectionRequest req = super.requestConnection(route, state);
						if(log.isDebugEnabled()){
							log.debug("Acquired connection :["+req+"] for :"+route+", state :"+state);
						}
						return new ClientConnectionRequest() {
							
							@Override
							public ManagedClientConnection getConnection(long timeout, TimeUnit tunit)
									throws InterruptedException, ConnectionPoolTimeoutException {
								ManagedClientConnection conn = req.getConnection(timeout, tunit);
								requestCounter.incrementAndGet();
								if(log.isDebugEnabled()){
									log.debug("Acquired connection :"+conn+" , connection in using :"+getConnectionsInPool()+", request # :"+requestCounter.get()+", release #: "+releaseCounter.get());
								}
								return conn;
							}
							
							@Override
							public void abortRequest() {
								req.abortRequest();
							}
						};
					}
					
				};
				cm = tcm;

			}
			else
			{
				cm = new SingleClientConnManager(new BasicHttpParams(),registry);
			}
			DefaultHttpClient client = new DefaultHttpClient(cm, new BasicHttpParams());
			client.setCredentialsProvider(new CredentialsProvider() {
				
				@Override
				public void setCredentials(AuthScope authscope, Credentials credentials) {					
				}
				
				@Override
				public Credentials getCredentials(AuthScope authscope) {
					IUserAuthManager authMgr = appContext.getService(IUserAuthManager.class);
					if(authMgr == null){
						return null;
					}
					String host = new StringBuffer().append(authscope.getHost()).append(':').append(authscope.getPort()).toString();					
					IUserAuthCredential cred = authMgr.getAuthCredential(host, authscope.getRealm());
					return (cred != null) ? new UsernamePasswordCredentials(cred.getUserName(), cred.getAuthPassword()) : null;
				}
				
				@Override
				public void clear() {
				}
			});
//			List<String> authpref = new ArrayList<String>();
//			authpref.add(AuthPolicy.DIGEST);
//			authpref.add(AuthPolicy.BASIC);
//			client.getParams().setParameter(AllClientPNames., authpref);
			this.httpClient = client;
			client.setCookieStore(cookies);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}


	@Override
	public HttpRequest createRequest(String endpointUrl, Map<String, Object> params) {
		HttpRequest request=new HttpRequestImpl(this.context, endpointUrl, params);
		if(isEnablegzip()){
			request.setHeader(HttpHeaderNames.ACCEPT_ENCODING, "gzip");
		}
		return request;
	}



	/**
	 * @return the disableTrustManager
	 */
	public boolean isDisableTrustManager() {
		return disableTrustManager;
	}


	/**
	 * @return the connectionPoolSize
	 */
	public int getConnectionPoolSize() {
		return connectionPoolSize;
	}

	/**
	 * @return the maxPooledPerRoute
	 */
	public int getMaxPooledPerRoute() {
		return maxPooledPerRoute;
	}

	/**
	 * @return the connectionTTL
	 */
	public long getConnectionTTL() {
		return connectionTTL;
	}

	/**
	 * @param disableTrustManager the disableTrustManager to set
	 */
	public void setDisableTrustManager(boolean disableTrustManager) {
		this.disableTrustManager = disableTrustManager;
	}

	/**
	 * @param connectionPoolSize the connectionPoolSize to set
	 */
	public void setConnectionPoolSize(int connectionPoolSize) {
		this.connectionPoolSize = connectionPoolSize;
	}

	/**
	 * @param connectionTTL the connectionTTL to set
	 */
	public void setConnectionTTL(long connectionTTL) {
		this.connectionTTL = connectionTTL;
	}


	public void startup(IKernelContext ctx) {
		this.appContext = ctx;
		this.executor = Executors.newFixedThreadPool(connectionPoolSize, new ThreadFactory() {
			private AtomicInteger seqNo = new AtomicInteger(1);
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "Http Request execution thread - "+seqNo.getAndIncrement());
			}
		});
		try {
			initHttpEngine(this.appContext.getService(ISiteSecurityService.class));
			appContext.registerService(HttpRpcService.class, AbstractHttpRpcService.this);
		} catch (Throwable e) {
			log.error("Failed to initialize http client",e);
		}
	}



	public void shutdown() {
		appContext.unregisterService(HttpRpcService.class, this);
		if(this.executor != null){
			this.executor.shutdown();
			this.executor = null;
		}
		if(this.httpClient != null){
			this.httpClient.getConnectionManager().shutdown();
			this.httpClient = null;
		}
		if(this.internalControl != null){
			this.internalControl.stop();
			this.internalControl = null;
		}

	}

	/**
	 * @param maxPooledPerRoute the maxPooledPerRoute to set
	 */
	public void setMaxPooledPerRoute(int maxPooledPerRoute) {
		this.maxPooledPerRoute = maxPooledPerRoute;
	}

	/**
	 * @return the enablegzip
	 */
	public boolean isEnablegzip() {
		return enablegzip;
	}

	/**
	 * @param enablegzip the enablegzip to set
	 */
	public void setEnablegzip(boolean enablegzip) {
		this.enablegzip = enablegzip;
	}
	
	@Override
	public void resetHttpClientContext(){
	    if(log.isDebugEnabled()){
            log.debug(" cookies " +cookies.getCookies().toString());
        }
	    cookies.clear();
	}

	/**
	 * @return the requestTimeoutInSeconds
	 */
	public int getRequestTimeoutInSeconds() {
		return requestTimeoutInSeconds;
	}

	/**
	 * @param requestTimeoutInSeconds the requestTimeoutInSeconds to set
	 */
	public void setRequestTimeoutInSeconds(int requestTimeoutInSeconds) {
		this.requestTimeoutInSeconds = requestTimeoutInSeconds;
	}

}
