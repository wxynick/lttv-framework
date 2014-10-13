/**
 * 
 */
package com.letv.mobile.android.app;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;

import com.letv.mobile.core.api.ApplicationFactory;
import com.letv.mobile.core.log.api.Trace;
import com.letv.mobile.core.microkernel.api.AbstractMicroKernel;
import com.letv.mobile.core.microkernel.api.IKernelModule;
import com.letv.mobile.core.util.StringUtils;

/**
 * @author
 * 
 */
public abstract class AndroidFramework<C extends IAndroidAppContext, M extends IKernelModule<C>> extends AbstractMicroKernel<C, M> implements IAndroidFramework<C, M> {

	private static final Trace log = Trace.register(AndroidFramework.class);
	private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

	// private ExecutorService executor;
	private int maxThread = 10;
	private String uniqueID;
	private Handler uiThreadHandler;
	private UnexpectingExceptionHandler handler = new UnexpectingExceptionHandler() {
		@Override
		public void uncaughtException(Thread t, Throwable e) {
			handleSysCrash(e);
			super.uncaughtException(t, e);
		}
	};
	//private Map<String, String> info = new HashMap<String, String>();

	// private Log4jConfigurator logConfig = new Log4jConfigurator();
	public AndroidFramework() {
		super();
		this.uiThreadHandler = new Handler();
		Thread.setDefaultUncaughtExceptionHandler(this.handler);
		ApplicationFactory.getInstance().setApplication(this);
	}

	public AndroidFramework(int maxThreads) {
		this.maxThread = maxThreads;
		Thread.setDefaultUncaughtExceptionHandler(this.handler);

	}

	// /* (non-Javadoc)
	// * @see com.wxxr.mobile.core.api.IApplication#getExecutor()
	// */
	// @Override
	// public ExecutorService getExecutor() {
	// return getExecutorService();
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wxxr.mobile.android.app.AndroidFramework#start()
	 */
	@Override
	public void start() {
		try {
			collectDeviceInfo();
			extractChannelInfo();
		} catch (Exception e1) {
			log.warn("Error when collect Device Info ", e1);
		}
		if (log.isInfoEnabled()) {
			log.info("UnexpectingExceptionHandler installed, ui thread :" + handler.getUiThread());
		}
		log.warn("Starting up application ...");
		try {
			// this.executor = new ThreadPoolExecutor(maxThread, maxThread, 20,
			// TimeUnit.SECONDS,
			// new LinkedBlockingDeque<Runnable>(100),
			// new ThreadFactory() {
			// private AtomicInteger sq = new AtomicInteger(1);
			// @Override
			// public Thread newThread(Runnable r) {
			// return new Thread(r, "application thread-"+sq.getAndIncrement());
			// }
			// });
			// ((ThreadPoolExecutor)this.executor).allowCoreThreadTimeOut(true);
			super.start();
			log.warn("Application started !");
		} catch (Exception e) {
			log.fatal("Failed to start application", e);
			throw new RuntimeException("Failed to start application", e);
		}
	}

	protected void extractChannelInfo() {
		ApplicationInfo appInfo;
		try {
			appInfo = getAndroidApplication().getPackageManager().getApplicationInfo(getApplicationId(), PackageManager.GET_META_DATA);
			String pchannel = appInfo.metaData.getString("UMENG_CHANNEL");
			getContext().setAttribute(AppConstants.KEY_PUBLISH_CHANNEL, pchannel);
		} catch (NameNotFoundException e) {
			log.warn("Failed to extract channel info",e);
		}
		

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wxxr.mobile.core.microkernel.api.AbstractMicroKernel#stop()
	 */
	@Override
	public void stop() {
		log.warn("Stopping application ....");
		// if(this.executor != null){
		// this.executor.shutdownNow();
		// this.executor = null;
		// }
		super.stop();
		log.warn("Application stopped !");
	}

	// @Override
	// protected ExecutorService getExecutorService() {
	// return executor;
	// }

	@Override
	public boolean isInDebugMode() {
		return (0 != (getAndroidApplication().getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE));
	}

/*	public Map<String, String> getDeviceInfo() {
		return Collections.unmodifiableMap(this.info);
	}*/

	protected void collectDeviceInfo() throws Exception {

		Context context = getAndroidApplication();
		PackageManager pm = context.getPackageManager();
		PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
		if (pi != null) {
			String versionName = pi.versionName == null ? "null" : pi.versionName;
			String versionCode = pi.versionCode + "";
			getContext().setAttribute(AppConstants.KEY_VERSION_NAME, versionName);
			getContext().setAttribute(AppConstants.KEY_VERSION_CODE, versionCode);
			if (log.isInfoEnabled()) {
				log.info("versionName -> ", versionName);
				log.info("versionCode -> ", versionCode);
			}
		}

	}

	@Override
	public String getMacIdentity() {

		return null;
	}

	@Override
	public String getApplicationId() {
		return getAndroidApplication().getPackageName();
	}

	@Override
	public String getApplicationVersion() {
		return (String)getContext().getAttribute(AppConstants.KEY_VERSION_NAME);
	}

	@Override
	public String getApplicationBuildNnumber() {
		return (String)getContext().getAttribute(AppConstants.KEY_VERSION_CODE);
	}

	@Override
	public String getDeviceId() {
		if (getContext().getAttribute(AppConstants.KEY_DEVICE_ID) == null) {
			TelephonyManager telephonyManager = (TelephonyManager) getAndroidApplication().getSystemService(Context.TELEPHONY_SERVICE);
			String deviceId = telephonyManager.getDeviceId();
			getContext().setAttribute(AppConstants.KEY_DEVICE_ID, StringUtils.trimToEmpty(deviceId));
		}

		return (String)getContext().getAttribute(AppConstants.KEY_DEVICE_ID);
	}

	@Override
	public String getDeviceType() {
		return "2";
	}

	/**
	 * @return the maxThread
	 */
	public int getMaxThread() {
		return maxThread;
	}

	/**
	 * @param maxThread
	 *            the maxThread to set
	 */
	public void setMaxThread(int maxThread) {
		this.maxThread = maxThread;
	}

	public String getDeviceUUID() {
		if (uniqueID == null) {
			SharedPreferences sharedPrefs = getAndroidApplication().getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE);
			uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
			if (uniqueID == null) {
				uniqueID = generateDeviceUUID();
				Editor editor = sharedPrefs.edit();
				editor.putString(PREF_UNIQUE_ID, uniqueID);
				editor.commit();
			}
		}
		return uniqueID;
	}

	protected void handleSysCrash(Throwable t) {

	}

	protected String generateDeviceUUID() {
		final TelephonyManager tm = (TelephonyManager) getAndroidApplication().getSystemService(Context.TELEPHONY_SERVICE);

		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = "" + android.provider.Settings.Secure.getString(getAndroidApplication().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		return deviceUuid.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wxxr.mobile.android.app.IAndroidFramework#getDataDir(java.lang.String
	 * , int)
	 */
	@Override
	public File getDataDir(String name, int mode) {
		return getAndroidApplication().getDir(name, mode);
	}

	protected Runnable safeRunnable(final Runnable task) {
		if (isInDebugMode()) {
			return task;
		}
		return new Runnable() {

			@Override
			public void run() {
				try {
					task.run();
				} catch (RuntimeException t) {
					log.error("Caught runtime exception in ui thread", t);
					if (isInDebugMode()) {
						throw t;
					}
				}

			}
		};
	}

	@Override
	public void runOnUIThread(Runnable task, long delay, TimeUnit unit) {
		if (delay == 0) {
			this.uiThreadHandler.post(safeRunnable(task));
		} else {
			this.uiThreadHandler.postDelayed(safeRunnable(task), TimeUnit.MILLISECONDS.convert(delay, unit));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wxxr.mobile.core.api.IApplication#runOnUIThread(java.lang.Runnable)
	 */
	@Override
	public void runOnUIThread(Runnable task) {
		if (Looper.myLooper() == this.uiThreadHandler.getLooper()) {
			safeRunnable(task).run();
		} else {
			this.uiThreadHandler.post(safeRunnable(task));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wxxr.mobile.core.api.IApplication#isCurrentUIThread()
	 */
	@Override
	public boolean isCurrentUIThread() {
		return Looper.myLooper() == this.uiThreadHandler.getLooper();
	}

	@Override
	public boolean isAppForeground() {
		ActivityManager activityManager = (ActivityManager) getAndroidApplication().getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);
		if (services.get(0).topActivity.getPackageName().toString().equalsIgnoreCase(getApplicationId())) {
			return true;
		}
		return false;
	}

	@Override
	public String getPChannel() {
		return getPChannel(AppConstants.KEY_PUBLISH_CHANNEL);
	}

	@Override
	public String getPChannel(String channelType) {
		if (StringUtils.isBlank(channelType)) {
			throw new IllegalArgumentException("Invalid channel type!!!");
		}
		String channel = (String)getContext().getAttribute(channelType);
		if (StringUtils.isBlank(channel)) {
			channel = "letv";
		}
		return channel;
	}

}
