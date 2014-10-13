package com.letv.datastatistics.entity;

import android.text.TextUtils;

public class VideoIdentifyingMessage {

	private String pid ;
	
	private String vid ;
	
	private String mmsid ;
	
	public VideoIdentifyingMessage(String pid , String vid , String mmsid){
		this.vid = vid ;
		this.pid = pid ;
		this.mmsid = mmsid ;
	}

	public String getPid() {
		return pid;
	}

	public String getVid() {
		return vid;
	}

	public String getMmsid() {
		return mmsid;
	}
	
	public String toStringNoMmsid() {
		StringBuilder builder = new StringBuilder();
		builder.append(TextUtils.isEmpty(pid) ? "-" : pid);
		builder.append("_");
		builder.append(TextUtils.isEmpty(vid) ? "-" : vid);
		return builder.toString();
	}
}
