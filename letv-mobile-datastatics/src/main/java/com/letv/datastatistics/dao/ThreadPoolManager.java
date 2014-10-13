package com.letv.datastatistics.dao;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class ThreadPoolManager {

	private final static int CorePoolSize = 3;
	private final static int MaximumPoolSize = 5;
	private final static long KeepAliveTime = 10;

	private static ThreadPoolManager mInstance = null;
	
	private static final Object mInstanceSync = new Object();

	private ThreadPoolExecutor mThreadPoolExecutor;

	private ThreadPoolManager() {
//		mThreadPoolExecutor = new ThreadPoolExecutor(CorePoolSize, MaximumPoolSize,
//				KeepAliveTime, TimeUnit.SECONDS,
//				new ArrayBlockingQueue<Runnable>(5), new ThreadPoolExecutor.CallerRunsPolicy());
		mThreadPoolExecutor = new ThreadPoolExecutor(CorePoolSize, MaximumPoolSize,
				KeepAliveTime, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(), new ThreadPoolExecutor.CallerRunsPolicy());
	}

	public static ThreadPoolManager getInstance() {
		synchronized (mInstanceSync) {
			if (mInstance != null) {
				return mInstance;
			}
			mInstance = new ThreadPoolManager();
		}
		return mInstance;
	}

	public void executeThreadWithPool(Runnable runnable) {
		mThreadPoolExecutor.execute(runnable);
	}

	public void closeThreadPool() {
		if(mThreadPoolExecutor != null) {
			mThreadPoolExecutor.shutdown();
		}
		
		if(mInstance != null) {
			mInstance = null;
		}
	}
}
