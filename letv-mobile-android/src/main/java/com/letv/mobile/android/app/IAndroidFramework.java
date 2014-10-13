/**
 * 
 */
package com.letv.mobile.android.app;

import java.io.File;

import android.app.Application;

import com.letv.mobile.core.api.IApplication;
import com.letv.mobile.core.microkernel.api.IKernelModule;

/**
 * @author  
 *
 */
public interface IAndroidFramework<C extends IAndroidAppContext, M extends IKernelModule<C>> extends IApplication<C, M> {
	Application getAndroidApplication();
	File getDataDir(String name, int mode);
	String getMacIdentity();
	String getApplicationId();
	String getApplicationVersion();
	String getApplicationBuildNnumber();
	String getApplicationName();
	String getDeviceId();
	String getDeviceType();
	String getDeviceUUID();
	/**
	 * 获取渠道标示
	 * @return
	 */
	String getPChannel();
	/**
	 * 兼容某些应用含多个渠道标示，需要根据渠道类型来区分
	 * @param channelType
	 * @return
	 */
	String getPChannel(String channelType);
	/**
	 * 判断app是否为当前运行的应用
	 * @return
	 */
	boolean isAppForeground();
	
}
