package com.letv.datastatistics.entity;

import java.io.Serializable;

public class TimeOutInfo implements Serializable {
	private static final long serialVersionUID = -7092515393340680014L;

	/**
	 * timeValue	string	网络超时时间
	 */
	private double timeValue = 0;

    public double getTimeValue() {
        return timeValue;
    }
    public void setTimeValue(double timeValue) {
        this.timeValue = timeValue;
    }
    @Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[TimeOutInfo data = " + timeValue + "]";
	}
}
