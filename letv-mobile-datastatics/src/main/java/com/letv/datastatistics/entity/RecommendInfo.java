package com.letv.datastatistics.entity;

import java.io.Serializable;

/**
 * 精品推荐控制信息
 * 
 * @author Administrator
 * 
 */
public class RecommendInfo implements Serializable {

	private static final long serialVersionUID = 20463380336622797L;
	/**
	 * 精品推荐打开
	 */
	public static final String RECOMMEND_OPEN = "1";
	/**
	 * 精品推荐关闭
	 */
	public static final String RECOMMEND_CLOSE = "0";

	public static final String RECOMMEND_KEY_HOME = "recommend_home";
	public static final String RECOMMEND_KEY_CHANNEL = "recommend_channel";
	public static final String RECOMMEND_KEY_LIVE = "recommend_live";
	public static final String RECOMMEND_KEY_DOWNLOAD = "recommend_download";
	public static final String RECOMMEND_KEY_SETTING = "recommend_setting";
	public static final String RECOMMEND_SETTING_TAB = "recommend_setting_tab";

	/**
	 * 配置项key
	 */
	private String key = null;

	/**
	 * 1 表示精品推荐打开；0 表示精品推荐关闭
	 */
	private String value = null;

	private int num;

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

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public boolean isOpen() {
		return RECOMMEND_OPEN.equals(value);
	}

	@Override
	public String toString() {
		return "RecommendInfo [key=" + key + ", value=" + value + "]";
	}
}
