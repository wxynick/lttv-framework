package com.letv.datastatistics.entity;

/**
 * 上报播放数据
 *
 */
public class StatisticsVideoInfo {
	 /**
	 * 频道id
	 */
	private String cid;//不用写
	 /**
	 *  专辑id
	 */
	private String pid;//不用写
	 /**
	 * 视频id
	 */
	private String vid;//不用写
	 /**
	 * 媒资id
	 */
	private String mmsid;//不用写
	 /**
	 * 调度地址
	 */
	private String ddurl;//写了
	 /**
	 * 最终播放地址
	 */
	private String playurl;//写了
	 /**
	 * 播放完成状态（1:初始 2:用户手动结束播放 3：视频自动完成播放 4：播放出错）
	 */
	private String status;//写了
	 /**
	 * 加载资源耗时（单位：s ex:2）
	 */
	private int utime;//写了
	 /**
	 * 缓冲次数
	 */
	private int bufcount;
	 /**
	 * 播放时长（单位：s秒）
	 */
	private long playedTime;//写了
	 /**
	 * 播放动作来源(1:频道列表 2:详情 3:播放记录 4:下载 5：搜索结果列表 6：排行 7：首页 8： 其它；9、追剧；10、收藏)
	 */
	private String from;//自己写去
	 /**
	 * （0、正常 1、调度错误 2、cdn资源错误 3、加载失败 4、UNKNOWN）
	 */
	private String err = "0";//写了(默认0)
	 /**
	 * 码率 350或800
	 */
	private String code;//写了
	 /**
	 *  乐视网用户id(没有的话传空)
	 */
	private String uid;//自己加
	 /**
	 * 产品代码渠道号 ex:010110000
	 */
	private String pcode;//自己加
	 /**
	 *  终端类型
	 */
	private String terminaltype;//自己加
	 /**
	 * 贴片广告形式
	 */
	private String ac = "000_0";//完成
	 /**
	 * 播放类型
	 */
	private String ptype;//自己家
	 /**
	 *  视频时长
	 */
	private String vlen;//自己加
	 /**
	 *  播放时间标识
	 */
	private String ptid;//完成
	
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
	public String getMmsid() {
		return mmsid;
	}
	public void setMmsid(String mmsid) {
		this.mmsid = mmsid;
	}
	public String getDdurl() {
		return ddurl;
	}
	public void setDdurl(String ddurl) {
		this.ddurl = ddurl;
	}
	public String getPlayurl() {
		return playurl;
	}
	public void setPlayurl(String playurl) {
		this.playurl = playurl;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getUtime() {
		return utime;
	}
	public void setUtime(int utime) {
		this.utime = utime;
	}
	public int getBufcount() {
		return bufcount;
	}
	public void setBufcount(int bufcount) {
		this.bufcount = bufcount;
	}
	public long getPlayedTime() {
		return playedTime;
	}
	public void setPlayedTime(long playedTime) {
		this.playedTime = playedTime;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getErr() {
		return err;
	}
	public void setErr(String err) {
		this.err = err;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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
	public String getTerminaltype() {
		return terminaltype;
	}
	public void setTerminaltype(String terminaltype) {
		this.terminaltype = terminaltype;
	}
	public String getAc() {
		return ac;
	}
	public void setAc(String ac) {
		this.ac = ac;
	}
	public String getPtype() {
		return ptype;
	}
	public void setPtype(String ptype) {
		this.ptype = ptype;
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
	@Override
	public String toString() {
		return "cid=" + cid ;
//		return "StatisticsVideoInfo [cid=" + cid + ", pid=" + pid + ", vid=" + vid + ", mmsid="
//				+ mmsid + ", ddurl=" + ddurl + ", playurl=" + playurl + ", status=" + status
//				+ ", utime=" + utime + ", bufcount=" + bufcount + ", playedTime=" + playedTime
//				+ ", from=" + from + ", err=" + err + ", code=" + code + ", uid=" + uid
//				+ ", pcode=" + pcode + ", terminaltype=" + terminaltype + ", ac=" + ac + ", ptype="
//				+ ptype + ", vlen=" + vlen + ", ptid=" + ptid + "]";
	}
	
}
