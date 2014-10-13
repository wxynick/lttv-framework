/**
 * 
 */
package com.letv.mobile.android.network;

import java.util.LinkedList;

import android.content.Context;
import android.net.ConnectivityManager;

import com.letv.mobile.android.app.IAndroidAppContext;
import com.letv.mobile.core.api.IDataExchangeCoordinator;
import com.letv.mobile.core.api.IExchangeHandler;
import com.letv.mobile.core.log.api.Trace;
import com.letv.mobile.core.microkernel.api.AbstractModule;

/**
 * @author  
 *
 */
public class NetworkManagementModule<T extends IAndroidAppContext> extends AbstractModule<T> implements
IDataExchangeCoordinator {
	private static final Trace log = Trace.register(NetworkManagementModule.class);

	private LinkedList<IExchangeHandler> handlers = new LinkedList<IExchangeHandler>();
	private long lastActiveTime = 0;
	private long gsmActiveInterval = 3*60*1000L;		// 3 minute
	private long wifiActiveInterval = 1*60*1000L;		// 1 minute
	private NetworkMonitor monitor;

	private class NetworkMonitor implements Runnable {
		private boolean stopRequired = false;
		private Thread thread;

		public void run() {
			this.thread = Thread.currentThread();
			this.thread.setName("Mobile network monitor thread");
			stopRequired = false;
			while(!stopRequired){
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
					continue;
				}
				switch(checkAvailableConnection()){
				case ConnectivityManager.TYPE_WIFI:
					if((System.currentTimeMillis() - lastActiveTime) > wifiActiveInterval){
						activateDataExchange();
					}
					break;
				case ConnectivityManager.TYPE_MOBILE:
					if((System.currentTimeMillis() - lastActiveTime) > gsmActiveInterval){
						activateDataExchange();
					}
					break;
				default:
					break;
				}
			}
			this.thread = null;
		}

		public void stop() {
			stopRequired = true;
			try {
				if(thread != null){
					thread.interrupt();
					try {
						thread.join(3000L);
					} catch (InterruptedException e) {
					}
				}
			}catch(Throwable t){}
		}

	}

	protected void activateDataExchange() {
		IExchangeHandler[] vals = null;
		synchronized (handlers) {
			if(handlers.size() > 0){
				vals = handlers.toArray(new IExchangeHandler[handlers.size()]);
			}
		}
		if(vals != null){
			for (IExchangeHandler h : vals) {
				try {
					h.startExchange();
				}catch(Throwable t){
					log.warn("Caught exception when invoke exchange handler :"+h,t);
				}
			}
		}
		lastActiveTime = System.currentTimeMillis();
	}

	protected int checkAvailableConnection()
	{
		try {
			ConnectivityManager connMgr = (ConnectivityManager)
					context.getApplication().getAndroidApplication().getSystemService(Context.CONNECTIVITY_SERVICE);    

			final android.net.NetworkInfo wifi =
					connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  

			final android.net.NetworkInfo mobile =
					connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

			if( wifi.isConnected() ){
				return ConnectivityManager.TYPE_WIFI;
			}
			else if( mobile.isConnected()&&(!mobile.isRoaming()) ){
				return ConnectivityManager.TYPE_MOBILE;
			}
		}catch(Throwable t){
			log.warn("Caught exception when checking network status", t);
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.api.IDataExchangeCoordinator#registerHandler(com.wxxr.mobile.core.api.IExchangeHandler)
	 */
	@Override
	public void registerHandler(IExchangeHandler handler) {
		synchronized(this.handlers){
			if((handler != null)&&(!this.handlers.contains(handler))){
				this.handlers.add(handler);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.api.IDataExchangeCoordinator#unregisterHandler(com.wxxr.mobile.core.api.IExchangeHandler)
	 */
	@Override
	public boolean unregisterHandler(IExchangeHandler handler) {
		synchronized(this.handlers){
			return this.handlers.remove(handler);
		}
	}

	@Override
	protected void initServiceDependency() {

	}

	@Override
	protected void startService() {
		this.monitor = new NetworkMonitor();
		new Thread(this.monitor).start();
		context.registerService(IDataExchangeCoordinator.class, this);
	}

	@Override
	protected void stopService() {
		if(this.monitor != null){
			this.monitor.stop();
			this.monitor = null;
		}
		context.unregisterService(IDataExchangeCoordinator.class, this);

	}

	@Override
	public int checkAvailableNetwork() {
		switch(checkAvailableConnection()){
		case ConnectivityManager.TYPE_WIFI:
			return NETWORK_ID_WIFI;
		case ConnectivityManager.TYPE_MOBILE:
			return NETWORK_ID_GSM;
		default:
			return -1;
		}

	}

}
