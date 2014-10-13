package com.letv.datastatistics.entity;

import java.io.Serializable;

/**
 * 客户端设备信息上报,返回数据
 * 
 * @author Administrator
 * 
 */
public class LogoInfo implements Serializable {

	private static final long serialVersionUID = 5718270457840740780L;

	/**
	 * 我是歌手开
	 */
	public static final String RESULT_SUCCESS = "1";
	/**
	 * 我是歌手关
	 */
	public static final String RESULT_FAIL = "0";

	/**
	 * 1 开，0 关；
	 */
	private String status = null;
	/**
	 * 图片地址
	 */
	private String icon;
	/**
	 * 跳转地址
	 */
	private String jumpUrl;

	/**
	 * 首页logo点击跳转标题
	 */
	private String comments;
	
	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @param icon
	 *            the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * @return the jumpUrl
	 */
	public String getJumpUrl() {
		return jumpUrl;
	}

	/**
	 * @param jumpUrl
	 *            the jumpUrl to set
	 */
	public void setJumpUrl(String jumpUrl) {
		this.jumpUrl = jumpUrl;
	}

}
