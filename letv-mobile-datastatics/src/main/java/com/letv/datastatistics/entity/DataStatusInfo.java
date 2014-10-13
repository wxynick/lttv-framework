package com.letv.datastatistics.entity;

import java.io.Serializable;
import java.util.HashMap;

public class DataStatusInfo implements Serializable{

	private static final long serialVersionUID = -4203703140646543081L;
	
	/**
	 * 接口初始化状态
	 */
	private ApiInfo mApiInfo = null;
	
	/**
	 * 客户端设备信息上报,返回数据
	 */
	private StatInfo mStatInfo = null;
	
	/**
	 * jpush- 极光推送消息
	 */
	private JpushInfo mJpushInfo = null;
	
	/**关于H265高清开关信息**/
	private H265Info mH265Info = null;
	
	/**关于utp开关信息**/
	private UtpInfo mUtpInfo = null;
	private FreeTime mFreeTime = null;
	/**
	 * 广告配置信息
	 */
	private AdsConfig mAdsConfig = null;
	
	/**
	 * 升级信息
	 */
	private UpgradeInfo mUpgradeInfo = null;
	
	/**
	 * 广告控制信息
	 */
	private AdsInfo mAdsInfo = null;
	
	/**
	 * 精品推荐控制信息
	 */
	private HashMap<String, RecommendInfo> recommendInfos = null;
	
	/**
	 * 播放码流控制
	 * */
	private Defaultbr playDefaultbr ;
	
	/**
	 * 下载流控制
	 * */
	private Defaultbr downloadDefaultbr ;

	/**
	 * 首页底部精品推荐 开关
	 * @return
	 */
	private boolean bottomRecommendSwitch = true;

	/**
	 * 精品推荐频道开关
	 * @return
	 */
	private boolean channelRecommendSwitch = true;

    /**
     * 首页应用弹窗推荐开关
     */
    private boolean popRecommendSwitch = false;

    /**
     * 离线广告开关
     */
    private boolean adOffline = false;

    /**
     * 联通合作开关
     */
	private boolean chinaUnicom = false;

    /**
     * linkshell开关
     */
    private boolean isLinkShell = false;

    /**
     * mp4 utp开关
     */
    private boolean isMp4Utp = false;

	private LogoInfo mLogoInfo;

    private TimeOutInfo mTimeOutInfo;
	/**
	 * 服务器时间戳
	 * */
	private int tm ;
	
	private int androidOpen350 = 0;
	
	private PhonePayInfo mPhonePayInfo;
	
	private ChannelWorldCupInfo mChannelWorldCupInfo;
	
	/**
	 * @return the mChannelWorldCupInfo
	 */
	public ChannelWorldCupInfo getmChannelWorldCupInfo() {
		return mChannelWorldCupInfo;
	}

	/**
	 * @param mChannelWorldCupInfo the mChannelWorldCupInfo to set
	 */
	public void setmChannelWorldCupInfo(ChannelWorldCupInfo mChannelWorldCupInfo) {
		this.mChannelWorldCupInfo = mChannelWorldCupInfo;
	}

	public boolean isBottomRecommendSwitch() {
		return bottomRecommendSwitch;
	}

	public void setBottomRecommendSwitch(boolean bottomRecommendSwitch) {
		this.bottomRecommendSwitch = bottomRecommendSwitch;
	}

	public boolean isChannelRecommendSwitch() {
		return channelRecommendSwitch;
	}

	public void setChannelRecommendSwitch(boolean channelRecommendSwitch) {
		this.channelRecommendSwitch = channelRecommendSwitch;
	}

    public boolean isPopRecommendSwitch() {
        return popRecommendSwitch;
    }

    public void setPopRecommendSwitch(boolean popRecommendSwitch) {
        this.popRecommendSwitch = popRecommendSwitch;
    }

    public ApiInfo getApiInfo() {
		return mApiInfo;
	}

	public void setApiInfo(ApiInfo mApiInfo) {
		this.mApiInfo = mApiInfo;
	}

	public StatInfo getStatInfo() {
		return mStatInfo;
	}

	public void setStatInfo(StatInfo mStatInfo) {
		this.mStatInfo = mStatInfo;
	}
	
	/**
	 * @return the mJpushInfo
	 */
	public JpushInfo getmJpushInfo() {
		return mJpushInfo;
	}

	public H265Info getmH265Info() {
		return mH265Info;
	}

	public void setmH265Info(H265Info mH265Info) {
		this.mH265Info = mH265Info;
	}

	/**
	 * @param mJpushInfo the mJpushInfo to set
	 */
	public void setmJpushInfo(JpushInfo mJpushInfo) {
		this.mJpushInfo = mJpushInfo;
	}

	public UpgradeInfo getUpgradeInfo() {
		return mUpgradeInfo;
	}

	public void setUpgradeInfo(UpgradeInfo mUpgradeInfo) {
		this.mUpgradeInfo = mUpgradeInfo;
	}
	
	public AdsInfo getAdsInfo() {
		return mAdsInfo;
	}

	public void setAdsInfo(AdsInfo mAdsInfo) {
		this.mAdsInfo = mAdsInfo;
	}
	
	public HashMap<String, RecommendInfo> getRecommendInfos() {
		return recommendInfos;
	}

	public void setRecommendInfos(HashMap<String, RecommendInfo> recommendInfos) {
		this.recommendInfos = recommendInfos;
	}

	public Defaultbr getPlayDefaultbr() {
		return playDefaultbr;
	}

	public void setPlayDefaultbr(Defaultbr playDefaultbr) {
		this.playDefaultbr = playDefaultbr;
	}

	public Defaultbr getDownloadDefaultbr() {
		return downloadDefaultbr;
	}

	public void setDownloadDefaultbr(Defaultbr downloadDefaultbr) {
		this.downloadDefaultbr = downloadDefaultbr;
	}

	public int getTm() {
		return tm;
	}

	public void setTm(int tm) {
		this.tm = tm;
	}

	int getAndroidOpen350() {
		return androidOpen350;
	}

	public void setAndroidOpen350(int androidOpen350) {
		this.androidOpen350 = androidOpen350;
	}
	
	public boolean istAndroidOpen350() {
		return this.androidOpen350 == 1;
	}

	/**
	 * @return the mLogoInfo
	 */
	public LogoInfo getmLogoInfo() {
		return mLogoInfo;
	}

	/**
	 * @param mLogoInfo the mLogoInfo to set
	 */
	public void setmLogoInfo(LogoInfo mLogoInfo) {
		this.mLogoInfo = mLogoInfo;
	}

	/**
	 * @return the mUtpInfo
	 */
	public UtpInfo getmUtpInfo() {
		return mUtpInfo;
	}

	/**
	 * @param mUtpInfo the mUtpInfo to set
	 */
	public void setmUtpInfo(UtpInfo mUtpInfo) {
		this.mUtpInfo = mUtpInfo;
	}
	
	public FreeTime getmFreeTime() {
		return mFreeTime;
	}

	public void setmFreeTime(FreeTime mFreeTime) {
		this.mFreeTime = mFreeTime;
	}

	/**
	 * @return the mAdsConfig
	 */
	public AdsConfig getmAdsConfig() {
		return mAdsConfig;
	}

	/**
	 * @param mAdsConfig the mAdsConfig to set
	 */
	public void setmAdsConfig(AdsConfig mAdsConfig) {
		this.mAdsConfig = mAdsConfig;
	}

	public PhonePayInfo getPhonePayInfo() {
		return mPhonePayInfo;
	}

	public void setPhonePayInfo(PhonePayInfo mPhonePayInfo) {
		this.mPhonePayInfo = mPhonePayInfo;
	}

    public TimeOutInfo getTimeOutInfo() {
        return mTimeOutInfo;
    }

    public void setTimeOutInfo(TimeOutInfo mTimeOutInfo) {
        this.mTimeOutInfo = mTimeOutInfo;
    }

    public boolean isAdOffline() {
        return adOffline;
    }

    public void setAdOffline(boolean adOffline) {
        this.adOffline = adOffline;
    }

    public boolean isChinaUnicom() {
        return chinaUnicom;
    }

    public void setChinaUnicom(boolean chinaUnicom) {
        this.chinaUnicom = chinaUnicom;
    }

    public boolean isLinkShell() {
        return isLinkShell;
    }

    public void setLinkShell(boolean isLinkShell) {
        this.isLinkShell = isLinkShell;
    }

    public boolean isMp4Utp() {
        return isMp4Utp;
    }

    public void setMp4Utp(boolean isMp4Utp) {
        this.isMp4Utp = isMp4Utp;
    }
}
