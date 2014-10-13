package com.letv.datastatistics.entity;

import java.io.Serializable;

/**
 * 客户端设备信息上报,返回数据
 * @author Administrator
 *
 */
public class StatInfo implements Serializable {

	private static final long serialVersionUID = 5718270457840740780L;
	
	/**
	 * 上报成功
	 */
	public static final String RESULT_SUCCESS = "1";
	/**
	 * 上报失败
	 */
	public static final String RESULT_FAIL = "0";
	
	/**
	 * 1 成功，0失败；
	 */
	private String result = null;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "StatInfo [result=" + result + "]";
	}
}
