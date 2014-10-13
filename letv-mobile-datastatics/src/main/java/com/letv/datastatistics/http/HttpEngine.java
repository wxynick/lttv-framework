package com.letv.datastatistics.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;

import com.letv.datastatistics.entity.LogoInfo;
import com.letv.datastatistics.util.DataUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

import com.letv.datastatistics.DataStatistics;
import com.letv.datastatistics.dao.StatisCacheBean;
import com.letv.datastatistics.db.StatisDBHandler;
import com.letv.datastatistics.exception.HttpDataConnectionException;
import com.letv.datastatistics.exception.HttpDataParserException;

public class HttpEngine {

	private static HttpEngine mInstance = null;

	private static final Object mInstanceSync = new Object();

	private DefaultHttpClient mDefaultHttpClient = null;

	private HttpEngine() {
		mDefaultHttpClient = createHttpClient();
		mDefaultHttpClient.setHttpRequestRetryHandler(new HttpRequestRetryHandler() {
			@Override
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {

				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, exception.getClass() + ":" + exception.getMessage() + ",executionCount:"
							+ executionCount);
				}

				if (executionCount >= 3) {
					return false;
				}

				if (exception instanceof NoHttpResponseException) {
					return true;
				} else if (exception instanceof ClientProtocolException) {
					return true;
				}

				return false;
			}
		});
	}

	public static HttpEngine getInstance() {
		synchronized (mInstanceSync) {
			if (mInstance != null) {
				return mInstance;
			}
			mInstance = new HttpEngine();
		}
		return mInstance;
	}

	public static final int CON_TIME_OUT_MS = 30000;
	public static final int SO_TIME_OUT_MS = 30000;
	public static final int SOCKET_BUFFER_SIZE = 8 * 1024;
	public static final int MAX_CONNECTIONS_PER_HOST = 3;
	public static final int MAX_TOTAL_CONNECTIONS = 3;
	public static final int GET_CONNECTION_TIMEOUT = 1000;

	public DefaultHttpClient createHttpClient() {
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

		HttpParams params = createHttpParams();

		ClientConnectionManager connManager = new ThreadSafeClientConnManager(params, registry);
		// SingleClientConnManager connManager = new
		// SingleClientConnManager(params,registry);
		// ThreadSafeClientConnManager connManager = new
		// ThreadSafeClientConnManager(params, registry);

		return new DefaultHttpClient(connManager, params);
	}

	private HttpParams createHttpParams() {
		HttpParams params = new BasicHttpParams();

		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(params, false);

		ConnPerRouteBean connPerRoute = new ConnPerRouteBean(MAX_CONNECTIONS_PER_HOST);
		ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);
		ConnManagerParams.setMaxTotalConnections(params, MAX_TOTAL_CONNECTIONS);
		ConnManagerParams.setTimeout(params, GET_CONNECTION_TIMEOUT);

		HttpConnectionParams.setConnectionTimeout(params, CON_TIME_OUT_MS);
		HttpConnectionParams.setSoTimeout(params, SO_TIME_OUT_MS);
		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		HttpConnectionParams.setTcpNoDelay(params, true);
		HttpConnectionParams.setSocketBufferSize(params, SOCKET_BUFFER_SIZE);

		HttpClientParams.setRedirecting(params, false);
		// HttpClientParams.setCookiePolicy(params,
		// CookiePolicy.BROWSER_COMPATIBILITY);

		return params;
	}

	public String doHttpGet(Context context, StatisCacheBean mStatisCacheBean) throws HttpDataConnectionException,
			HttpDataParserException {
		if(context==null){
			return null;
		}
		if (DataStatistics.getInstance().isDebug()) {
			Log.d(DataStatistics.TAG, "url:" + mStatisCacheBean.getCacheData());
		}
		if (mStatisCacheBean == null) {
			return null;
		}

        StatisDBHandler.saveLocalCache(context, mStatisCacheBean);
        if(DataUtils.getAvailableNetWorkInfo(context) == null){
            return null;
        }
        HttpGet httpGet = new HttpGet(mStatisCacheBean.getCacheData());
        try {
            HttpResponse httpResponse = mDefaultHttpClient.execute(httpGet);
            if(httpResponse == null){
                return null;
            }
			int responseCode = httpResponse.getStatusLine().getStatusCode();

			if (DataStatistics.getInstance().isDebug()) {
				Log.d(DataStatistics.TAG, "responseCode:" + responseCode);
			}
			if (responseCode >= 200 && responseCode < 300) {
                if(null == httpResponse.getEntity()){
                    return null;
                }
				String result = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "result:" + result);
				}
				StatisDBHandler.deleteByCacheId(context, mStatisCacheBean.getCacheId());
				return result;
			} else {
				if (!DataStatistics.getInstance().isDebug()) {
					if ((System.currentTimeMillis() - mStatisCacheBean.getCacheTime()) / 1000 >= 60 * 60 * 24 * 5) {// 5天前的记录强制删除
						StatisDBHandler.deleteByCacheId(context, mStatisCacheBean.getCacheId());
					}
				}
				throw new HttpDataConnectionException(httpResponse.getStatusLine().toString());
			}
		} catch (ParseException e) {
			e.printStackTrace();
			throw new HttpDataParserException(e);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new HttpDataConnectionException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new HttpDataConnectionException(e);
		} finally {
			httpGet.abort();
			mDefaultHttpClient.getConnectionManager().closeExpiredConnections();
		}
	}

	public String doHttpGet(String url) throws HttpDataConnectionException, HttpDataParserException {

		if (DataStatistics.getInstance().isDebug()) {
			Log.d(DataStatistics.TAG, "url:" + url);
		}
		if (url == null) {
			return null;
		}
		HttpGet httpGet = new HttpGet(url);

		try {
			HttpResponse httpResponse = mDefaultHttpClient.execute(httpGet);
			int responseCode = httpResponse.getStatusLine().getStatusCode();

			if (DataStatistics.getInstance().isDebug()) {
				Log.d(DataStatistics.TAG, "responseCode:" + responseCode);
			}

			if (responseCode >= 200 && responseCode < 300) {
				String result = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);

				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "result:" + result);
				}
				return result;
			} else {
				throw new HttpDataConnectionException(httpResponse.getStatusLine().toString());
			}
			// switch (responseCode) {
			// case HttpStatus.SC_OK:
			//
			// String result = EntityUtils.toString(httpResponse.getEntity(),
			// HTTP.UTF_8);
			//
			// if (DataStatistics.getInstance().isDebug()) {
			// Log.d(DataStatistics.TAG, "result:" + result);
			// }
			//
			// return result;
			// default:
			// throw new
			// HttpDataConnectionException(httpResponse.getStatusLine().toString());
			// }
		} catch (ParseException e) {
			e.printStackTrace();
			throw new HttpDataParserException(e);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new HttpDataConnectionException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new HttpDataConnectionException(e);
		} finally {
			httpGet.abort();
			mDefaultHttpClient.getConnectionManager().closeExpiredConnections();
		}
	}

	public void closeHttpEngine() {
		if (mDefaultHttpClient != null && mDefaultHttpClient.getConnectionManager() != null) {
			mDefaultHttpClient.getConnectionManager().shutdown();
		}

		if (mInstance != null) {
			mInstance = null;
		}
	}
}
