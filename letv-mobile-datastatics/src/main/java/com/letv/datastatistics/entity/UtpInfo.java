package com.letv.datastatistics.entity;

import java.io.Serializable;

/**
 * 客户端设备信息上报,返回的androidUtp开关
 * @author Administrator
 *
 */
public class UtpInfo implements Serializable {
	private static final long serialVersionUID = -8122318613551887371L;
	/**androidUtp  1 开 **/
	public static final String RESULT_SUCCESS = "1";
	/**androidUtp  0 关 **/
	public static final String RESULT_FAIL = "0";
	
	/**androidUtp开关 1 开，0关；**/
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
