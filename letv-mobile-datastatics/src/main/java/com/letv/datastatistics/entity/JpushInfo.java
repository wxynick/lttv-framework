package com.letv.datastatistics.entity;

import java.io.Serializable;

/**
 * 客户端设备信息上报,返回数据
 * @author Administrator
 *
 */
public class JpushInfo implements Serializable {
	private static final long serialVersionUID = 6814189904058068995L;
	/**
	 * 极光推送开关 1 开
	 */
	public static final String RESULT_SUCCESS = "1";
	/**
	 * 极光推送开关 0 关
	 */
	public static final String RESULT_FAIL = "0";
	
	/**
	 * 极光推送开关 1 开，0关；
	 */
	private String status = null;

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
}
