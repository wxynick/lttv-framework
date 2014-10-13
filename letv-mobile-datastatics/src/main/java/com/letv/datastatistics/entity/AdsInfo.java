package com.letv.datastatistics.entity;

import java.io.Serializable;

/**
 * 广告控制信息
 * @author Administrator
 *
 */
public class AdsInfo implements Serializable {
	
	private static final long serialVersionUID = -683518048521899692L;

	/**
	 * 广告打开
	 */
	public static final String ADS_OPEN = "1";
	/**
	 * 广告关闭
	 */
	public static final String ADS_CLOSE = "0";
	
	/**
	 * 配置项key
	 */
	private String key = null;
	
	/**
	 * 1 表示广告打开；0 表示广告关闭
	 */
	private String value = null;
	
	/**
	 * 配置项key
	 */
	private String pinKey = null ;
	
	/**
	 * 1 表示广告拼接打开；0 表示广告拼接关闭
	 */
	private String pinValue = null ;
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getPinKey() {
		return pinKey;
	}

	public void setPinKey(String pinKey) {
		this.pinKey = pinKey;
	}

	public String getPinValue() {
		return pinValue;
	}

	public void setPinValue(String pinValue) {
		this.pinValue = pinValue;
	}

	@Override
	public String toString() {
		return "AdsInfo [key=" + key + ", value=" + value + "]";
	}
}
