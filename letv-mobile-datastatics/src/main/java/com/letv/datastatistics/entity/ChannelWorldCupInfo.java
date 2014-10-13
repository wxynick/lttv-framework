package com.letv.datastatistics.entity;

import java.io.Serializable;

/**
 * 广告控制信息
 * @author Administrator
 *
 */
public class ChannelWorldCupInfo implements Serializable {
	private static final long serialVersionUID = 1734930004997919658L;
	/*
	channel_name	string	频道名称
	channel_url	string	频道URL
	channel_position	string	频道位置
	channel_status	string	频道状态:1-上线,0-下线*/
	
	private String channel_name;
	private String channel_url;
	private String channel_position;
	private int channel_status;
	/**
	 * @return the channel_name
	 */
	public String getChannel_name() {
		return channel_name;
	}
	/**
	 * @param channel_name the channel_name to set
	 */
	public void setChannel_name(String channel_name) {
		this.channel_name = channel_name;
	}
	/**
	 * @return the channel_url
	 */
	public String getChannel_url() {
		return channel_url;
	}
	/**
	 * @param channel_url the channel_url to set
	 */
	public void setChannel_url(String channel_url) {
		this.channel_url = channel_url;
	}
	/**
	 * @return the channel_position
	 */
	public String getChannel_position() {
		return channel_position;
	}
	/**
	 * @param channel_position the channel_position to set
	 */
	public void setChannel_position(String channel_position) {
		this.channel_position = channel_position;
	}
	/**
	 * @return the channel_status
	 */
	public int getChannel_status() {
		return channel_status;
	}
	public boolean getChannelStatus() {
		return channel_status == 1;
	}
	/**
	 * @param channel_status the channel_status to set
	 */
	public void setChannel_status(int channel_status) {
		this.channel_status = channel_status;
	}
	
	
	
}
