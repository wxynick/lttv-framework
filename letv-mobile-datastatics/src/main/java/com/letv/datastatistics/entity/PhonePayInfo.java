package com.letv.datastatistics.entity;

import java.io.Serializable;

public class PhonePayInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7092515393340680014L;
	/**
	 * 话费支付开关：开
	 */
	public static final String DATA_ON = "1";
	/**
	 * 话费支付开关：关
	 */
	public static final String DATA_OFF = "0";
	
	/**
	 * data	string	话费支付开关：1-开，0-关
	 */
	private String data = "";

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[PhonePayInfo data = " + data + "]";
	}
}
