package com.letv.datastatistics.util;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

public final class DataUtils {
	
	private static String uuid ;

	private DataUtils() {
	}

	public static String getData(String data) {
		if (data == null || data.length() <= 0) {
			return "-";
		} else {
			return data.replace(" ", "_");
		}
	}
	public static String getTrimData(String data) {
		if (data == null || data.length() <= 0) {
			return "-";
		} else {
			return data.trim();
		}
	}
	
	public static String getDataEmpty(String data) {
		if (data == null || data.length() <= 0) {
			return "";
		} else {
			return data.replace(" ", "_");
		}
	}

	public static String getDataUrl(String url) {
		return url.replace("&", "}").replace("?", "{");
	}

	public static NetworkInfo getAvailableNetWorkInfo(Context context) {
		
		if(context == null) {
			return null;
		}
		
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.isAvailable()) {
			return activeNetInfo;
		} else {
			return null;
		}
	}
	
	/**
	 * 返回联网类型
	 * 
	 * @param context
	 * @return wifi或3G
	 */
	public static String getNetType(Context context) {
		String netType = null;
		if(context == null)
			return netType;  
		ConnectivityManager connectivityMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityMgr != null) {
			NetworkInfo networkInfo = connectivityMgr.getActiveNetworkInfo();
			if (networkInfo != null) {
				if (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) {
					netType = "wifi";
				} else if (ConnectivityManager.TYPE_MOBILE == networkInfo
						.getType()) {
					netType = "3G";
				} else {
					netType = "wifi";
				}
			}
		}

		return netType;
	}
	
	/**
	 * 获得ip地址
	 * */
	public static String getLocalIpAddress()    
    {    
        try    
        {    
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)    
            {    
               NetworkInterface intf = en.nextElement();    
               for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)    
               {    
                   InetAddress inetAddress = enumIpAddr.nextElement();    
                   if (!inetAddress.isLoopbackAddress())    
                   {    
                       return inetAddress.getHostAddress().toString();    
                   }    
               }    
           }    
        }    
        catch (SocketException ex)    
        {    
            
        }    
        return "";    
    }   
	
	public static String generateDeviceId(Context context) {
		String str = getIMEI(context) + getIMSI(context) + getDeviceName()
				+ getBrandName() + getMacAddress(context);
		return MD5Helper(str);
	}

	private static String generate_DeviceId(Context context) {
		String str = getIMEI(context) + getDeviceName() + getBrandName()
				+ getMacAddress(context);
		return MD5Helper(str);
	}

	public static String getUUID(Context context) {
		if(TextUtils.isEmpty(uuid) && context != null){
			uuid = generateDeviceId(context) + "_" + System.currentTimeMillis();
		}
		return uuid ;
	}

	public static String MD5Helper(String str) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
			byte[] byteArray = messageDigest.digest();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < byteArray.length; i++) {
				if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
					sb.append("0").append(
							Integer.toHexString(0xFF & byteArray[i]));
				} else {
					sb.append(Integer.toHexString(0xFF & byteArray[i]));
				}
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		throw new RuntimeException("no device Id");
	}
	
	public static String getIMEI(Context context) {
		if(context==null){
			return "";
		}
		try {
			String deviceId = ((TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
			if (null == deviceId || deviceId.length() <= 0) {
				return "";
			} else {
				return deviceId.replace(" ", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getIMSI(Context context) {
		if(context==null){
			return "";
		}
		String subscriberId = null;
		
		try {
			subscriberId = ((TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
			if (null == subscriberId || subscriberId.length() <= 0) {
				subscriberId = generate_DeviceId(context);
			} else {
				subscriberId.replace(" ", "");
				if (TextUtils.isEmpty(subscriberId)) {
					subscriberId = generate_DeviceId(context);
				}
			}
			return subscriberId;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return subscriberId;
		}
	}

	public static String getMacAddress(Context context) {
		if(context==null){
			return "";
		}
		try {
			String macAddress = null;
			WifiInfo wifiInfo = ((WifiManager) context
					.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
			macAddress = wifiInfo.getMacAddress();
			if (macAddress == null || macAddress.length() <= 0) {
				return "";
			} else {
				return macAddress;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getResolution(Context context) {
		if(context==null){
			return "";
		}
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return new StringBuilder().append(dm.widthPixels).append("*")
				.append(dm.heightPixels).toString();
	}
	public static String getNewResolution(Context context) {
		if(context==null){
			return "";
		}
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return new StringBuilder().append(dm.widthPixels).append("_")
				.append(dm.heightPixels).toString();
	}
	public static String getDensity(Context context) {
		if(context==null){
			return "";
		}
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return String.valueOf(dm.density);
	}
	
	/**
	 * 得到设备名字
	 * */
	public static String getDeviceName() {
		String model = android.os.Build.MODEL;
		if (model == null || model.length() <= 0) {
			return "";
		} else {
			return model;
		}
	}
	
	/**
	 * 得到品牌名字
	 * */
	public static String getBrandName() {
		String brand = android.os.Build.BRAND;
		if (brand == null || brand.length() <= 0) {
			return "";
		} else {
			return brand;
		}
	}
	
	/**
	 * 得到客户端版本信息
	 * 
	 * @param context
	 * @return
	 */
	public static String getClientVersionName(Context context) {
		if (context == null) {
			return "";
		}
		try {
			PackageInfo packInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 得到设备名字
	 * */
	public static String getSystemName() {
		return "android";
	}
	
	/**
	 * 得到操作系统版本号
	 */
	public static String getOSVersionName() {
		return android.os.Build.VERSION.RELEASE;
	}
	
	/**
	 * 获得行为统计扩展信息
	 * @param params
	 * @return
	 */
	public static String getExtraStr(String... params){
		String retStr = null;
		StringBuilder sb = new StringBuilder();
		for(String s : params){
			sb.append(s).append(";");
		}
		
		retStr = sb.toString();
		if(retStr.length() > 1){
			retStr = retStr.substring(0, retStr.length()-1);
		}
		
		return retStr;
	}
	/**
	 * 获得行为统计扩展信息
	 * @param list
	 * @return
	 */
	public static String getExtraStr(List<String> list){
		final int size = list.size();
		String[] params = (String[])list.toArray(new String[size]);
		return getExtraStr(params);
	}
	/**
	 * 获得pid_vid
	 * @param pid
	 * @param vid
	 * @return
	 */
	public static String getIds(String pid,String vid){
		StringBuilder builder = new StringBuilder();
		builder.append(TextUtils.isEmpty(pid) ? "-" : pid);
		builder.append("_");
		builder.append(TextUtils.isEmpty(vid) ? "-" : vid);
		return builder.toString();
	}
	
	/**
	 * 获取错误码字符串,特殊用
	 */
	public static String getErrorMessage(String pcode,String did,String version,String actionId){
		StringBuilder sb = new StringBuilder();
		sb.append(did)
		  .append("_")
		  .append(System.currentTimeMillis())
		  .append("_")
		  .append(version)
		  .append("_")
		  .append(actionId)
		  .append("_")
		  .append(pcode);
		return sb.toString();
	}

    /**
     * 返回指定格式的时间
     *
     * @return
     */
    public static String timeClockString(String format) {
        // 按自定义格式SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat formatTime = new SimpleDateFormat(format);
        Date currentTime = new Date();
        return formatTime.format(currentTime);
    }
}
