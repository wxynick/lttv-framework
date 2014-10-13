package com.letv.datastatistics.entity;

import java.io.Serializable;

/**
 * 客户端设备信息上报,返回的H265频道开关
 * @author Administrator
 *
 */
public class H265Info implements Serializable {
	private static final long serialVersionUID = -5356484306662017266L;
	/**H265高清频道  1 开 **/
	public static final String RESULT_SUCCESS = "1";
	/**H265高清频道  0 关 **/
	public static final String RESULT_FAIL = "0";
	
	/**H265开关 1 开，0关；**/
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
