package com.letv.datastatistics.entity;

/**
 * 上报广告贴片数据
 *
 */
public class StatisticsAdInfo {
	 /**
	 * 乐视网用户id
	 */
	private String uid;
	 /**
	 * 产品线
	 */
	private String pcode;
	 /**
	 * 广告位类型	string	1.5版本修改	ex：（100:前贴广告，200：后贴广告）
	 */
	private String adtype;
	 /**
	 * 广告编号	string	1.2版本	广告系统返回的tfid(默认存在3个贴片)，素材之间用下横杆连接。如123_234_321中123代表第3个广告的tfid、234代表第2个广告的tfid,321代表第1个素材的tfid
	 */
	private String adid;
	 /**
	 * 上报动作串	string	1.2版本	以3个前帖广告为例，由0000四位组成，从左到右分别为贴片3、贴片2、贴片1、调取动作。状态为：0：无状态，1：成功，2：失败，3：用户中止
	 */
	private String actionid;
	 /**
	 * 点击次数	int	1.2版本	多个广告的点击数采用下横杆连接：10_20_15, 即第3个广告产生10次点击，第2个广告产生20次点击,第1个为15次，没用点击为0，不存在广告缺失。
	 */
	private String clicknum;
	 /**
	 * 广告时长	string	1.5版本增加	采用每个广告连接的行为，固定形式为：30_15_10,不存在时缺失，用"-"替代，其中30代表第3个广告，15代表第2个广告，10第1个广告
	 */
	private String durTime;
	 /**
	 * 广告播放时长	string	1.2版本	采用每个广告连接的行为，固定形式为：30_15_10,不存在时缺失，用"-"替代，其中30代表第3个广告，15代表第2个广告，10第1个广告
	 */
	private String playedTime;
	 /**
	 * 加载耗时	string	1.2版本	（单位：s）
	 */
	private String utime;
	 /**
	 * 频道ID
	 */
	private String cid;
	 /**
	 * 专辑ID
	 */
	private String pid;
	 /**
	 * 视频ID
	 */
	private String vid;
	 /**
	 * 视频时长	string	1.2版本
	 */
	private String vlen;
	 /**
	 * 播放时间标识	string	1.2版本	播放开始时生成一个时间戳，并且在播放过程中一致保持
	 */
	private String ptid;
	 /**
	 * 	广告系统	string	1.5版本增加	ex：（kejie，haoye）
	 */
	private String adsystem;
	//===============================================
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
	public String getAdtype() {
		return adtype;
	}
	public void setAdtype(String adtype) {
		this.adtype = adtype;
	}
	public String getAdid() {
		return adid;
	}
	public void setAdid(String adid) {
		this.adid = adid;
	}
	public String getActionid() {
		return actionid;
	}
	public void setActionid(String actionid) {
		this.actionid = actionid;
	}
	public String getClicknum() {
		return clicknum;
	}
	public void setClicknum(String clicknum) {
		this.clicknum = clicknum;
	}
	public String getDurTime() {
		return durTime;
	}
	public void setDurTime(String durTime) {
		this.durTime = durTime;
	}
	public String getPlayedTime() {
		return playedTime;
	}
	public void setPlayedTime(String playedTime) {
		this.playedTime = playedTime;
	}
	public String getUtime() {
		return utime;
	}
	public void setUtime(String utime) {
		this.utime = utime;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getVid() {
		return vid;
	}
	public void setVid(String vid) {
		this.vid = vid;
	}
	public String getVlen() {
		return vlen;
	}
	public void setVlen(String vlen) {
		this.vlen = vlen;
	}
	public String getPtid() {
		return ptid;
	}
	public void setPtid(String ptid) {
		this.ptid = ptid;
	}
	public String getAdsystem() {
		return adsystem;
	}
	public void setAdsystem(String adsystem) {
		this.adsystem = adsystem;
	}
}
