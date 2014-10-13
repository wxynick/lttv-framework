package com.letv.datastatistics.entity;

import java.io.Serializable;

public class UpgradeInfo implements Serializable {

	private static final long serialVersionUID = -881155820859279568L;
	
	/**
	 * 强制升级
	 */
	public static final String UPTYPE_FORCE = "1";
	/**
	 * 不强制升级
	 */
	public static final String UPTYPE_NOT_FORCE = "2";
	/**
	 * 升级
	 */
	public static final String UPGRADE_YES = "1";
	/**
	 * 不升级
	 */
	public static final String UPGRADE_NO = "2";
	
	/**
	 * 最新版本号
	 */
	private String v = null;
	
	/**
	 * 提示语标题
	 */
	private String title = null;
	
	/**
	 * 提示语
	 */
	private String msg = null;
	
	/**
	 * 升级类型：1，强制升级，2，不强制升级
	 */
	private String uptype = null;
	
	/**
	 * 升级地址
	 */
	private String url = null;
	
	/**
	 * 是否升级：1 是, 2 否
	 */
	private String upgrade = null;
	
	public String getV() {
		return v;
	}

	public void setV(String v) {
		this.v = v;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getUptype() {
		return uptype;
	}

	public void setUptype(String uptype) {
		this.uptype = uptype;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUpgrade() {
		return upgrade;
	}

	public void setUpgrade(String upgrade) {
		this.upgrade = upgrade;
	}

	@Override
	public String toString() {
		return "UpgradeInfo [v=" + v + ", title=" + title + ", msg=" + msg
				+ ", uptype=" + uptype + ", url=" + url + ", upgrade="
				+ upgrade + "]";
	}
}
