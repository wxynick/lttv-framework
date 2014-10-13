/**
 * 
 */
package com.letv.mobile.android.http;

import java.util.Map;

import com.letv.mobile.android.app.IAndroidAppContext;
import com.letv.mobile.core.microkernel.api.AbstractModule;
import com.letv.mobile.core.rpc.http.apache.AbstractHttpRpcService;
import com.letv.mobile.core.rpc.http.api.HttpHeaderNames;
import com.letv.mobile.core.rpc.http.api.HttpRequest;
import com.letv.mobile.core.security.api.ISiteSecurityService;

/**
 * @author  
 *
 */
public class HttpRpcServiceModule<T extends IAndroidAppContext> extends AbstractModule<T> {

	private AbstractHttpRpcService service = new AbstractHttpRpcService() {
		@Override
		public HttpRequest createRequest(String endpointUrl, Map<String, Object> params) {
			HttpRequest request=super.createRequest(endpointUrl, params);
			request.setHeader("deviceid", context.getApplication().getDeviceId());
			request.setHeader("deviceType", context.getApplication().getDeviceType());
			request.setHeader("appName", context.getApplication().getApplicationName());
			request.setHeader("appVer", context.getApplication().getApplicationVersion());
			request.setHeader("buildNumber", context.getApplication().getApplicationBuildNnumber());
			if(context.getAttribute(HttpHeaderNames.USER_AGENT) != null){
				request.setHeader(HttpHeaderNames.USER_AGENT, (String)context.getAttribute(HttpHeaderNames.USER_AGENT));
			}
			return request;
		}
	};
	
	@Override
	protected void initServiceDependency() {
		addRequiredService(ISiteSecurityService.class);
	}

	@Override
	protected void startService() {
		service.startup(context);
	}

	@Override
	protected void stopService() {
		service.shutdown();
	}

	/**
	 * @return
	 * @see com.wxxr.mobile.core.rpc.http.apache.AbstractHttpRpcService#isDisableTrustManager()
	 */
	public boolean isDisableTrustManager() {
		return service.isDisableTrustManager();
	}

	/**
	 * @return
	 * @see com.wxxr.mobile.core.rpc.http.apache.AbstractHttpRpcService#getConnectionPoolSize()
	 */
	public int getConnectionPoolSize() {
		return service.getConnectionPoolSize();
	}

	/**
	 * @return
	 * @see com.wxxr.mobile.core.rpc.http.apache.AbstractHttpRpcService#getMaxPooledPerRoute()
	 */
	public int getMaxPooledPerRoute() {
		return service.getMaxPooledPerRoute();
	}

	/**
	 * @return
	 * @see com.wxxr.mobile.core.rpc.http.apache.AbstractHttpRpcService#getConnectionTTL()
	 */
	public long getConnectionTTL() {
		return service.getConnectionTTL();
	}

	/**
	 * @param disableTrustManager
	 * @see com.wxxr.mobile.core.rpc.http.apache.AbstractHttpRpcService#setDisableTrustManager(boolean)
	 */
	public void setDisableTrustManager(boolean disableTrustManager) {
		service.setDisableTrustManager(disableTrustManager);
	}

	/**
	 * @param connectionPoolSize
	 * @see com.wxxr.mobile.core.rpc.http.apache.AbstractHttpRpcService#setConnectionPoolSize(int)
	 */
	public void setConnectionPoolSize(int connectionPoolSize) {
		service.setConnectionPoolSize(connectionPoolSize);
	}

	/**
	 * @param connectionTTL
	 * @see com.wxxr.mobile.core.rpc.http.apache.AbstractHttpRpcService#setConnectionTTL(long)
	 */
	public void setConnectionTTL(long connectionTTL) {
		service.setConnectionTTL(connectionTTL);
	}

	/**
	 * @param maxPooledPerRoute
	 * @see com.wxxr.mobile.core.rpc.http.apache.AbstractHttpRpcService#setMaxPooledPerRoute(int)
	 */
	public void setMaxPooledPerRoute(int maxPooledPerRoute) {
		service.setMaxPooledPerRoute(maxPooledPerRoute);
	}

	/**
	 * @return
	 * @see com.wxxr.mobile.core.rpc.http.apache.AbstractHttpRpcService#isEnablegzip()
	 */
	public boolean isEnablegzip() {
		return service.isEnablegzip();
	}

	/**
	 * @param enablegzip
	 * @see com.wxxr.mobile.core.rpc.http.apache.AbstractHttpRpcService#setEnablegzip(boolean)
	 */
	public void setEnablegzip(boolean enablegzip) {
		service.setEnablegzip(enablegzip);
	}
	/**
	 * @return the requestTimeoutInSeconds
	 */
	public int getRequestTimeoutInSeconds() {
		return service.getRequestTimeoutInSeconds();
	}

	/**
	 * @param requestTimeoutInSeconds the requestTimeoutInSeconds to set
	 */
	public void setRequestTimeoutInSeconds(int requestTimeoutInSeconds) {
		service.setRequestTimeoutInSeconds(requestTimeoutInSeconds);
	}
}
