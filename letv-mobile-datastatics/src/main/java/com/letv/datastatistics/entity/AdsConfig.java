package com.letv.datastatistics.entity;

import java.io.Serializable;

/**
 * 广告配置信息
 * @author Administrator
 *
 */
public class AdsConfig implements Serializable {
	private static final long serialVersionUID = 3538563084453175092L;
	private String adsConfig ;
	/**
	 * @return the adsConfig
	 */
	public String getAdsConfig() {
		return adsConfig;
	}
	/**
	 * @param adsConfig the adsConfig to set
	 */
	public void setAdsConfig(String adsConfig) {
		this.adsConfig = adsConfig;
	}

	
}
