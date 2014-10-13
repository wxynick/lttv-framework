package com.letv.datastatistics.dao;


public class StatisCacheBean {
	private String cacheId;//缓存id
	private String cacheData;//缓存数据
	private long cacheTime;//缓存时间
	
	public String getCacheId() {
		return cacheId;
	}
	public void setCacheId(String cacheId) {
		this.cacheId = cacheId;
	}
	public String getCacheData() {
		return cacheData;
	}
	public void setCacheData(String cacheData) {
		this.cacheData = cacheData;
	}
	public long getCacheTime() {
		return cacheTime;
	}
	public void setCacheTime(long cacheTime) {
		this.cacheTime = cacheTime;
	}
	public StatisCacheBean(String cacheId,String cacheData, long cacheTime) {
		super();
		this.cacheId = cacheId;
		this.cacheData = cacheData;
		this.cacheTime = cacheTime;
	}
	
	public StatisCacheBean() {
	}
	@Override
	public String toString() {
		return "LocalCacheBean [cacheId=" + cacheId + ", cacheData="+ cacheData.length() + ", cacheTime=" + cacheTime + "]";
	}
	
	
}
