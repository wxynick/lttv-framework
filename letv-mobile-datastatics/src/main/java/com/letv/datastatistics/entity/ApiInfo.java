package com.letv.datastatistics.entity;

import java.io.Serializable;

/**
 * 接口初始化状态类
 * @author Administrator
 *
 */
public class ApiInfo implements Serializable{

	private static final long serialVersionUID = 5456796606305518886L;
	
	/**
	 * 正式接口
	 */
	public static final String APISTATUS_FORMAL = "1";
	/**
	 * 测试接口
	 */
	public static final String APISTATUS_TEST = "2";
	
	/**
	 * 应用的接口状态:1-正式接口，2-测试接口
	 */
	private String apistatus = null;

	public String getApistatus() {
		return apistatus;
	}

	public void setApistatus(String apistatus) {
		this.apistatus = apistatus;
	}

	@Override
	public String toString() {
		return "ApiInfo [apistatus=" + apistatus + "]";
	}
}
