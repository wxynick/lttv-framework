package com.letv.datastatistics.entity;

import java.io.Serializable;

/**
 * 客户端设备信息上报,返回的androidUtp开关
 * @author Administrator
 *
 */
public class FreeTime implements Serializable {
	private static final long serialVersionUID = -8122318613551887371L;
	
	private String time = null;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	
}
