package com.letv.datastatistics.entity;
/**
 * 上报动作数据接口
 **/
public class DTypeActionInfo {
	
	 /**
	 * 动作码
	 */
	private String code;
	 /**
	 * 扩展信息cid_pid_vid;134（0或多个扩展信息，扩展信息以;"分号"分隔）
	 */
	private String extCode;
	 /**
	 * 乐视网用户id(没有的话传空)
	 */
	private String uid;
	 /**
	 * 产品代码渠道号 ex:010110000
	 */
	private String pcode;
	 /**
	 * 广告系统	string	1.5版本增加	ex：（kejie，hoye）；非广告动作该字段为空
	 */
	private String adsystem;
	 /**
	 * 码率	string	1.5版本增加
	 */
	private String rate;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getExtCode() {
		return extCode;
	}
	public void setExtCode(String extCode) {
		this.extCode = extCode;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getPcode() {
		return pcode;
	}
	public void setPcode(String pcode) {
		this.pcode = pcode;
	}
	public String getAdsystem() {
		return adsystem;
	}
	public void setAdsystem(String adsystem) {
		this.adsystem = adsystem;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public DTypeActionInfo(String code, String extCode, String uid, String pcode, String adsystem,
			String rate) {
		super();
		this.code = code;
		this.extCode = extCode;
		this.uid = uid;
		this.pcode = pcode;
		this.adsystem = adsystem;
		this.rate = rate;
	}
	
}
