package com.letv.datastatistics;

import java.util.ArrayList;

import org.json.JSONException;

import android.content.Context;
import android.text.TextUtils;

import com.letv.datastatistics.dao.DataManager;
import com.letv.datastatistics.dao.StatisCacheBean;
import com.letv.datastatistics.dao.ThreadPoolManager;
import com.letv.datastatistics.db.StatisDBHandler;
import com.letv.datastatistics.entity.DTypeActionInfo;
import com.letv.datastatistics.entity.DataStatusInfo;
import com.letv.datastatistics.entity.StatisticsVideoInfo;
import com.letv.datastatistics.exception.HttpDataConnectionException;
import com.letv.datastatistics.exception.HttpDataParserException;
import com.letv.datastatistics.http.HttpEngine;
import com.letv.datastatistics.util.DataConstant;
import com.letv.datastatistics.util.DataUtils;

/**
 * 数据统计类
 * 
 */
public final class DataStatistics {

	public final static String TAG = "DataStatistics";

	private static DataStatistics mInstance = null;

	private static final Object mInstanceSync = new Object();

	private boolean isUseTest = false;

	private String deviceID = null;

	private DataManager mDataManager = null;

	private String ip = "";
	
	private DataStatistics() {
		mDataManager = new DataManager();
	}

	public static DataStatistics getInstance() {
		synchronized (mInstanceSync) {
			if (mInstance != null) {
				return mInstance;
			}
			mInstance = new DataStatistics();
		}
		return mInstance;
	}
	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	public boolean isDebug() {
		return isUseTest;
	}

	public void setDebug(boolean isTest) {
		isUseTest = isTest;
	}
	public String getDeviceID(Context context) {

		if (TextUtils.isEmpty(deviceID)) {
			deviceID = DataUtils.generateDeviceId(context);
		}

		return deviceID;
	}

	/**
	 * 上报用户数据接口
	 * 
	 * @param context
	 * @param uid
	 *            乐视网用户id(没有的话传空)
	 * @param pcode
	 *            产品代码渠道号 ex:010110000
	 * @param source
	 *            终端类型，ex:phone、box、tv
	 * @throws HttpDataParserException
	 * @throws HttpDataConnectionException
	 */
	public void sendUserInfo(Context context, String uid, String pcode, String source, String stat, String onlen) {
		mDataManager.sendUserInfo(context, uid, pcode, source, stat, onlen);
	}

	/**
	 * 上报特殊错误数据
	 */
	public void sendErrorT(Context context, String act, String error) {

		mDataManager.sendErrorInfo(context, "-", DataUtils.getUUID(context), "-", "-", "-", act, "-", error);
	}

	/**
	 * 上报播放数据接口
	 * 
	 * @param context
	 * @param cid
	 *            频道id
	 * @param pid
	 *            专辑id
	 * @param vid
	 *            视频id
	 * @param mmsid
	 *            媒资id
	 * @param ddurl
	 *            调度地址
	 * @param playurl
	 *            最终播放地址
	 * @param status
	 *            播放完成状态（1:初始 2:用户手动结束播放 3：视频自动完成播放 4：播放出错）
	 * @param utime
	 *            加载资源耗时（单位：s ex:2）
	 * @param bufcount
	 *            缓冲次数
	 * @param playedTime
	 *            播放时长（单位：s秒）
	 * @param from
	 *            播放动作来源(1:频道列表 2:详情 3:播放记录 4:下载 5：搜索结果列表 6：排行 7：首页 8：
	 *            其它；9、追剧；10、收藏)
	 * @param err
	 *            （0、正常 1、调度错误 2、cdn资源错误 3、加载失败 4、UNKNOWN）
	 * @param code
	 *            码率 350或800
	 * @param uid
	 *            乐视网用户id(没有的话传空)
	 * @param pcode
	 *            产品代码渠道号 ex:010110000
	 * @param terminaltype
	 *            终端类型
	 * @param ac
	 *            贴片广告形式
	 * @param ptype
	 *            播放类型
	 * @param vlen
	 *            视频时长
	 * @param ptid
	 *            播放时间标识
	 */
	public void sendVideoInfo(Context context, StatisticsVideoInfo mStatisticsVideoInfo) {
		mDataManager.sendVideoInfo(context, mStatisticsVideoInfo);
	}

	/**
	 * 上报广告播放数据接口
	 * 
	 * @param context
	 * @param cid
	 *            频道id
	 * @param pid
	 *            专辑id
	 * @param vid
	 *            视频id
	 * @param mmsid
	 *            媒资id
	 * @param ddurl
	 *            调度地址
	 * @param playurl
	 *            最终播放地址
	 * @param status
	 *            播放完成状态（1:初始 2:用户手动结束播放 3：视频自动完成播放 4：播放出错）
	 * @param utime
	 *            加载资源耗时（单位：s ex:2）
	 * @param bufcount
	 *            缓冲次数
	 * @param playedTime
	 *            播放时长（单位：s秒）
	 * @param from
	 *            播放动作来源(1:频道列表 2:详情 3:播放记录 4:下载 5：搜索结果列表 6：排行 7：首页 8：
	 *            其它；9、追剧；10、收藏)
	 * @param err
	 *            （0、正常 1、调度错误 2、cdn资源错误 3、加载失败 4、UNKNOWN）
	 * @param code
	 *            码率 350或800
	 * @param uid
	 *            乐视网用户id(没有的话传空)
	 * @param pcode
	 *            产品代码渠道号 ex:010110000
	 * @param terminaltype
	 *            终端类型
	 * @param ac
	 *            贴片广告形式
	 * @param ptype
	 *            播放类型
	 * @param vlen
	 *            视频时长
	 * @param ptid
	 *            播放时间标识
	 */
	public void sendADInfo(Context context, String uid, String pcode, String adtype, String adid, String actionid,
			String clicknum, String durTime, String playedTime, String utime, String cid, String pid, String vid,
			String vlen, String ptid, String adsystem) {
		mDataManager.sendADInfo(context, uid, pcode, adtype, adid, actionid, clicknum, durTime, playedTime, utime, cid,
				pid, vid, vlen, ptid, adsystem);
	}

	/**
	 * 上报统计数据接口
	 * 
	 * @param context
	 * @param code
	 *            动作码
	 * @param extCode
	 *            扩展信息cid_pid_vid;134（0或多个扩展信息，扩展信息以;"分号"分隔）
	 * @param uid
	 *            乐视网用户id(没有的话传空)
	 * @param pcode
	 *            产品代码渠道号 ex:010110000
	 * @throws HttpDataParserException
	 * @throws HttpDataConnectionException
	 * @param adsystem
	 *            广告系统 ex：（kejie，hoye）；非广告动作该字段为空
	 * @param rate
	 *            码率
	 */
	public void sendActionCode(Context context, String code, String extCode, String uid, String pcode, String adsystem,
			String rate) {
		DTypeActionInfo mDTypeActionInfo = new DTypeActionInfo(code, extCode, uid, pcode, adsystem, rate);
		mDataManager.sendDTypeActionInfo(context, mDTypeActionInfo);
	}

	/**
	 * 客户端设备信息上报接口
	 * 
	 * @param context
	 * @param pcode
	 *            产品代码渠道号 ex:010110000
	 * @return DataStatusInfo(包含了：接口初始化状态、客户端设备信息上报、升级接口)
	 * @throws HttpDataParserException
	 * @throws HttpDataConnectionException
	 * @throws JSONException
	 */
	public void uploadClientData(Context context, String pcode) {
		mDataManager.uploadClientData(context, pcode);
	}

	/**
	 * 客户端设备信息上报接口(获取<接口初始化状态、客户端设备信息上报、升级接口>信息)
	 * 
	 * @param context
	 * @param pcode
	 *            产品代码渠道号 ex:010110000
	 * @return DataStatusInfo(包含了：接口初始化状态、客户端设备信息上报、升级接口)
	 * @throws HttpDataParserException
	 * @throws HttpDataConnectionException
	 * @throws JSONException
	 */
	public DataStatusInfo getDataStatusInfo(Context context, String pcode) {
		return mDataManager.getDataStatusInfo(context, pcode);
	}

	// ================================================================================
	// =============================Version2.0===start=================================
	// ================================================================================
	/**
	 * 精品换量统计上报
	 * 
	 * @param context
	 * @param p1
	 *            一级产品线代码 为从0开始的数字
	 * @param p2
	 *            二级产品线代码 已一级产品线代码为前缀，接下来从0开始
	 * @param pcode
	 * @param acode
	 *            动作码
	 * @param ap
	 *            动作属性
	 * @param ar
	 *            动作动作结果 0：成功 1：失败
	 * @param cid
	 *            视频频道ID 全业务线统一 和大媒资一致
	 * @param pid
	 *            专辑ID 如果不是专辑 用 - 替代
	 * @param vid
	 *            视频ID
	 * @param uid
	 *            乐视网用户id
	 * @param cur_url
	 *            当前页面地址
	 * @param ilu
	 *            是否为登录用户 0：登录 1：非登录
	 */
	public void sendRecommendInfo(final Context context, final String p1, final String p2, final String pcode,
			final int acode, final String ap, final String ar, final String cid, final String pid, final String vid,
			final String uid, final String cur_url, final int ilu) {
		mDataManager.sendRecommendInfo(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, ilu);
	}

	/**
	 * @category说明： Play 字段名应该严格遵守，不能随意自己增加字段名，上报采用key-value格式
	 *              播放日志由播放器分阶段的动作和播放时长构成 播放器每个阶段结束，都要对动作的结果进行上报,
	 *              动作码由播放器团队自己维护，一般是简单的英文缩写，比如gslb, init
	 *              等等如果失败，需要重试，在成功之后再上报，失败的不用上报 播放日志，只能由播放器上报
	 *              播放日志的动作包括卡顿，卡顿时间就是动作时长 播放日志，不包括播放器广告日志，播放器广告日志还是按照原来的规范上报
	 *              key和value的值都不允许包含 & 符号，如果有包含，要对key/value值进行URL编码
	 *              在播放器的第一个动作上报一次环境信息(uuid不带后缀数字)，后续的动作都不需要再重复上报，这样可以缩短上报信息
	 *              上报地址：http://dc.letv.com/pl/? 播放日志，只能由播放器上报
	 * @author haitian
	 * @param context
	 * @param p1
	 *            一级产品线代码 为从0开始的数字 [必填]
	 * @param p2
	 *            二级产品线代码 已一级产品线代码为前缀，接下来从0开始 [必填]
	 * @param ac
	 *            动作名称 [必填]
	 * @param err
	 *            错误代码 [必填]
	 * @param pt
	 *            播放时长
	 * @param ut
	 *            Utime: 动作耗时 [必填]
	 * @param uid
	 *            乐视网用户注册ID [必填]
	 * @param uuidTimeStamp
	 *            一次播放过程，播放器生成唯一的UUID, 如果一次播放过程出现了切换码率，那么uuid的后缀加1 [必填]
	 * @param cid
	 *            频道ID [必填]
	 * @param pid
	 *            专辑ID [必填]
	 * @param vid
	 *            视频ID [必填]
	 * @param vlen
	 *            视频时长 [必填]
	 * @param retryCount
	 *            重试次数，从0开始 比如 第一次失败，第二次成功，那么只有在第二次上报，其中ry=1 如果第一次即成功，那么ry=0
	 *            [必填]
	 * @param type
	 *            播放类型 [必填]
	 * @param vt
	 *            播放器的vtype [必填]
	 * @param url
	 *            视频播放地址 需要URL编码 [必填] [必填]
	 * @param ref
	 *            播放页来源地址 需要URL编码 [必填]
	 * @param py
	 *            Property: 播放属性 [必填]
	 * @param st
	 *            Station：轮播台 String 如果是中文，需要进行URL编码 [选填]
	 * @param weid
	 *            上报时获取js生成的页面weid [选填]
	 * @param pcode
	 *            见附表五 目前仅移动上报此参数,其他可以不报 [选填]
	 * @param ilu
	 *            0是登陆用户,1非登陆用户 [必填]
	 */
	public void sendPlayInfo(final Context context, final String p1, final String p2, final String ac,
			final String err, final String pt, final String ut, final String uid, final String uuidTimeStamp,
			final String cid, final String pid, final String vid, final String vlen, final String retryCount,
			final String type, final String vt, final String url, final String ref, final String py, final String st,
			final String weid, final String pcode, final int ilu, final String ch) {
		mDataManager.sendPlayInfo(context, p1, p2, ac, err, pt, ut, uid, uuidTimeStamp, cid, pid, vid, vlen,
				retryCount, type, vt, url, ref, py, st, weid, pcode, ilu, ch);
	}
	
	public void sendPlayInfo24New(final Context context, final String p1, final String p2, final String ac,
			final String err, final String pt, final String ut, final String uid, final String uuidTimeStamp,
			final String cid, final String pid, final String vid, final String vlen, final String retryCount,
			final String type, final String vt, final String url, final String ref, final String py, final String st,
			final String weid, final String pcode, final int ilu, final String ch, final String zid) {
		mDataManager.sendPlayInfo24New(context, p1, p2, ac, err, pt, ut, uid, uuidTimeStamp, cid, pid, vid, vlen,
				retryCount, type, vt, url, ref, py, st, weid, pcode, ilu, ch, zid);
	}

    public void sendPlayInfo24New(final Context context, final String p1, final String p2, final String ac,
                                  final String err, final String pt, final String ut, final String uid, final String uuidTimeStamp,
                                  final String cid, final String pid, final String vid, final String vlen, final String retryCount,
                                  final String type, final String vt, final String url, final String ref, final String py, final String st,
                                  final String weid, final String pcode, final int ilu, final String ch, final String zid,final int ap) {
        mDataManager.sendPlayInfo24New(context, p1, p2, ac, err, pt, ut, uid, uuidTimeStamp, cid, pid, vid, vlen,
                retryCount, type, vt, url, ref, py, st, weid, pcode, ilu, ch, zid,ap);
    }

	
	public void sendLivePlayInfo(final Context context, final String p1, final String p2, final String ac,
			final String err, final String pt, final String ut, final String uid, final String uuidTimeStamp,
			final String cid, final String pid, final String vid, final String vlen, final String retryCount,
			final String type, final String vt, final String url, final String ref, final String py, final String st,
			final String weid, final String pcode, final int ilu, final String ch, final String lc) {
		mDataManager.sendPlayInfo(context, p1, p2, ac, err, pt, ut, uid, uuidTimeStamp, cid, pid, vid, vlen,
				retryCount, type, vt, url, ref, py, st, weid, pcode, ilu, ch, lc);
	}
	
	public void sendLivePlayInfo25New(final Context context, final String p1, final String p2, final String ac,
			final String err, final String pt, final String ut, final String uid, final String uuidTimeStamp,
			final String cid, final String pid, final String vid, final String vlen, final String retryCount,
			final String type, final String vt, final String url, final String ref, final String py, final String st,
			final String weid, final String pcode, final int ilu, final String ch, final String lc, final String zid) {
		mDataManager.sendPlayInfo25New(context, p1, p2, ac, err, pt, ut, uid, uuidTimeStamp, cid, pid, vid, vlen,
				retryCount, type, vt, url, ref, py, st, weid, pcode, ilu, ch, lc, zid);
	}

    public void sendLivePlayInfo25New(final Context context, final String p1, final String p2, final String ac,
                                      final String err, final String pt, final String ut, final String uid, final String uuidTimeStamp,
                                      final String cid, final String pid, final String vid, final String vlen, final String retryCount,
                                      final String type, final String vt, final String url, final String ref, final String py, final String st,
                                      final String weid, final String pcode, final int ilu, final String ch, final String lc, final String zid,final String lid) {
        mDataManager.sendPlayInfo25New(context, p1, p2, ac, err, pt, ut, uid, uuidTimeStamp, cid, pid, vid, vlen,
                retryCount, type, vt, url, ref, py, st, weid, pcode, ilu, ch, lc, zid,lid);
    }

	/**
	 * @category说明： Action 字段名应该严格遵守，不能随意自己增加字段名，上报采用key-value格式
	 *              动作日志，记录的是用户的行为，比如：下载，评论，共享，收藏 所有业务线使用的日志格式一样，使用业务线代码进行区分
	 *              动作包括： 点击，评论，下载，收藏，分享，充值，缴费，后续可以继续扩充
	 *              动作码由数据部维护，不可以随意添加动作码，要向数据部申请 搜索、播放不在动作日志范畴，有独立的query
	 *              log和play log key和value的值都不允许包含 &
	 *              符号，如果有包含，要对key/value值进行URL编码 上报地址：http://dc.letv.com/op/?
	 * @param context
	 * @param p1
	 *            一级产品线代码 为从0开始的数字
	 * @param p2
	 *            二级产品线代码 已一级产品线代码为前缀，接下来从0开始
	 * @param pcode
	 * @param acode
	 *            动作码
	 * @param ap
	 *            动作属性
	 * @param ar
	 *            动作动作结果 0：成功 1：失败
	 * @param cid
	 *            视频频道ID 全业务线统一 和大媒资一致
	 * @param pid
	 *            专辑ID 如果不是专辑 用 - 替代
	 * @param vid
	 *            视频ID
	 * @param uid
	 *            乐视网用户id
	 * @param cur_url
	 *            当前页面地址
	 * @param reid
	 *            推荐反馈的随机数 String [推荐结果点击时必须上报]推荐点击动作上报时必填
	 * @param area
	 *            推荐区域标识 String 会提供两个特定的区域[推荐点击动作上报时必填]
	 * @param bucket
	 *            推荐的算法策略 Int 推荐组维护 [推荐点击动作上报时必填]
	 * @param rank
	 *            点击视频在推荐区域的排序 Int 最终要沟通确认 [推荐点击动作上报时必填]
	 * @param ilu
	 *            是否为登录用户 0：登录 1：非登录
	 */
	public void sendActionInfo(final Context context, final String p1, final String p2, final String pcode,
			final String acode, final String ap, final String ar, final String cid, final String pid, final String vid,
			final String uid, final String cur_url, final String area, final String bucket,
			final String rank, final int ilu) {
		mDataManager.sendActionInfo(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu);
	}
	
	/**
	 * @category说明： Action 字段名应该严格遵守，不能随意自己增加字段名，上报采用key-value格式
	 *              动作日志，记录的是用户的行为，比如：下载，评论，共享，收藏 所有业务线使用的日志格式一样，使用业务线代码进行区分
	 *              动作包括： 点击，评论，下载，收藏，分享，充值，缴费，后续可以继续扩充
	 *              动作码由数据部维护，不可以随意添加动作码，要向数据部申请 搜索、播放不在动作日志范畴，有独立的query
	 *              log和play log key和value的值都不允许包含 &
	 *              符号，如果有包含，要对key/value值进行URL编码 上报地址：http://dc.letv.com/op/?
	 * @param context
	 * @param p1
	 *            一级产品线代码 为从0开始的数字
	 * @param p2
	 *            二级产品线代码 已一级产品线代码为前缀，接下来从0开始
	 * @param pcode
	 * @param acode
	 *            动作码
	 * @param ap
	 *            动作属性
	 * @param ar
	 *            动作动作结果 0：成功 1：失败
	 * @param cid
	 *            视频频道ID 全业务线统一 和大媒资一致
	 * @param pid
	 *            专辑ID 如果不是专辑 用 - 替代
	 * @param vid
	 *            视频ID
	 * @param uid
	 *            乐视网用户id
	 * @param cur_url
	 *            当前页面地址
	 * @param reid
	 *            推荐反馈的随机数 String [推荐结果点击时必须上报]推荐点击动作上报时必填
	 * @param area
	 *            推荐区域标识 String 会提供两个特定的区域[推荐点击动作上报时必填]
	 * @param bucket
	 *            推荐的算法策略 Int 推荐组维护 [推荐点击动作上报时必填]
	 * @param rank
	 *            点击视频在推荐区域的排序 Int 最终要沟通确认 [推荐点击动作上报时必填]
	 * @param ilu
	 *            是否为登录用户 0：登录 1：非登录
	 * @param zid
	 * 			  String zid
	 */
	public void sendActionInfo(final Context context, final String p1, final String p2, final String pcode,
			final String acode, final String ap, final String ar, final String cid, final String pid, final String vid,
			final String uid, final String cur_url, final String area, final String bucket,
			final String rank, final int ilu ,final String zid) {
		mDataManager.sendActionInfo(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu, zid);
	}
	/**
	 * Add直播id lid
	 *			by glh
	 */
	public void sendActionInfoAddLid(final Context context, final String p1, final String p2, final String pcode,
			final String acode, final String ap, final String ar, final String cid, final String pid, final String vid,
			final String uid, final String cur_url, final String area, final String bucket,
			final String rank, final int ilu ,final String zid,final String lid) {
		mDataManager.sendActionInfoAddLid(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu, zid , lid);
	}
	/**
	 * 动作统计增加ref参数
	 * @param context
	 * @param p1
	 * @param p2
	 * @param pcode
	 * @param acode
	 * @param ap
	 * @param ar
	 * @param cid
	 * @param pid
	 * @param vid
	 * @param uid
	 * @param cur_url
	 * @param area
	 * @param bucket
	 * @param rank
	 * @param ilu
	 * @param zid
	 * @param ref
	 */
	public void sendActionInforef(final Context context, final String p1, final String p2, final String pcode,
			final String acode, final String ap, final String ar, final String cid, final String pid, final String vid,
			final String uid, final String cur_url, final String area, final String bucket,
			final String rank, final int ilu ,final String zid) {
		mDataManager.sendActionInforef(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu, zid);
	}
	/**
	 * 动作的统计-增加一个time参数
	 * @author glh
	 */
	public void sendActionInfotime(final Context context, final String p1, final String p2, final String pcode,
			final String acode, final String ap, final String ar, final String cid, final String pid, final String vid,
			final String uid, final String cur_url, final String area, final String bucket,
			final String rank, final int ilu ,final String zid,final String time) {
		mDataManager.sendActionInfotime(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu, zid,time);
	}
    public void sendActionInfoBigData(final Context context, final String p1, final String p2, final String pcode,
                                      final String acode, final String ap, final String ar, final String cid, final String pid, final String vid,
                                      final String uid, final String reid, final String area, final String bucket, final String rank,
                                      final String cms_num,final int ilu) {
        mDataManager.sendActionInfoBigData(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, reid, area, bucket, rank,cms_num, ilu);
    }


    /**
     * @category说明： Action 字段名应该严格遵守，不能随意自己增加字段名，上报采用key-value格式
     *              动作日志，记录的是用户的行为，比如：下载，评论，共享，收藏 所有业务线使用的日志格式一样，使用业务线代码进行区分
     *              动作包括： 点击，评论，下载，收藏，分享，充值，缴费，后续可以继续扩充
     *              动作码由数据部维护，不可以随意添加动作码，要向数据部申请 搜索、播放不在动作日志范畴，有独立的query
     *              log和play log key和value的值都不允许包含 &
     *              符号，如果有包含，要对key/value值进行URL编码 上报地址：http://dc.letv.com/op/?
     * @param context
     * @param p1
     *            一级产品线代码 为从0开始的数字
     * @param p2
     *            二级产品线代码 已一级产品线代码为前缀，接下来从0开始
     * @param pcode
     * @param acode
     *            动作码
     * @param ap
     *            动作属性
     * @param ar
     *            动作动作结果 0：成功 1：失败
     * @param cid
     *            视频频道ID 全业务线统一 和大媒资一致
     * @param pid
     *            专辑ID 如果不是专辑 用 - 替代
     * @param vid
     *            视频ID
     * @param uid
     *            乐视网用户id
     * @param cur_url
     *            当前页面地址
     * @param reid
     *            推荐反馈的随机数 String [推荐结果点击时必须上报]推荐点击动作上报时必填
     * @param area
     *            推荐区域标识 String 会提供两个特定的区域[推荐点击动作上报时必填]
     * @param bucket
     *            推荐的算法策略 Int 推荐组维护 [推荐点击动作上报时必填]
     * @param rank
     *            点击视频在推荐区域的排序 Int 最终要沟通确认 [推荐点击动作上报时必填]
     * @param ilu
     *            是否为登录用户 0：登录 1：非登录
     * @param zid
     * 			  String zid
     * @param lid
     *            直播id
     */
    public void sendActionInfo(final Context context, final String p1, final String p2, final String pcode,
                               final String acode, final String ap, final String ar, final String cid, final String pid, final String vid,
                               final String uid, final String cur_url, final String area, final String bucket,
                               final String rank, final int ilu ,final String zid,final String lid) {
        mDataManager.sendActionInfo(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu, zid,lid);
    }
    /**
     * Action统计说明
     * @param context
     * @param p1
     * @param p2
     * @param pcode
     * @param acode
     * @param ap
     * @param ar
     * @param cid
     * @param pid
     * @param vid
     * @param uid
     * @param cur_url
     * @param area
     * @param bucket
     * @param rank
     * @param ilu
     * @param zid
     * @param targeturl 目标页地址
     */
    public void sendActionInfoAddTargetUrl(final Context context, final String p1, final String p2, final String pcode,
            final String acode, final String ap, final String ar, final String cid, final String pid, final String vid,
            final String uid, final String cur_url, final String area, final String bucket,
            final String rank, final int ilu ,final String zid,final String lid,final String targeturl) {
mDataManager.sendActionInfoAddTargetUrl(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu, zid,lid,targeturl);
}
	
    /**
	 * @param scid
	 *            接口中提供的pageid的值　-5.5版  by glh
	 */
    public void sendActionInfos(final Context context, final String p1, final String p2, final String pcode,
            final String acode, final String ap, final String ar, final String cid, final String pid, final String vid,
            final String uid, final String cur_url, final String area, final String bucket,
            final String rank, final int ilu ,final String zid,final String scid){
    	mDataManager.sendActionInfos(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu,zid,scid);
    }
    /**
     * @param fragid
     * 				接口中提供的碎片的id - 5.5.1版(为频道页添加)      by glh
     */
    public void sendActionInfoAddFragid(final Context context, final String p1, final String p2, final String pcode,
            final String acode, final String ap, final String ar, final String cid, final String pid, final String vid,
            final String uid, final String cur_url, final String area, final String bucket,
            final String rank, final int ilu ,final String zid,final String scid,final String fragid){
    	mDataManager.sendActionInfoAddFragid(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu,zid,scid,fragid);
    }
    /**
     * @param fragid
     * 				接口中提供的碎片的id - 5.5.1版(为首页添加)      by glh
     */
    public void sendActionInfoAddFragId(final Context context, final String p1, final String p2, final String pcode,
            final String acode, final String ap, final String ar, final String cid, final String pid, final String vid,
            final String uid, final String cur_url, final String area, final String bucket,
            final String rank, final int ilu ,final String zid,final String lid,final String fragid){
    	mDataManager.sendActionInfoAddFragId(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu,zid,lid,fragid);
    }
	/**
	 * 此方法仅用于推送消息使用， 其他情况禁止调用
	 * @category说明： Action 字段名应该严格遵守，不能随意自己增加字段名，上报采用key-value格式
	 *              动作日志，记录的是用户的行为，比如：下载，评论，共享，收藏 所有业务线使用的日志格式一样，使用业务线代码进行区分
	 *              动作包括： 点击，评论，下载，收藏，分享，充值，缴费，后续可以继续扩充
	 *              动作码由数据部维护，不可以随意添加动作码，要向数据部申请 搜索、播放不在动作日志范畴，有独立的query
	 *              log和play log key和value的值都不允许包含 &
	 *              符号，如果有包含，要对key/value值进行URL编码 上报地址：http://dc.letv.com/op/?
	 * @param context
	 * @param p1
	 *            一级产品线代码 为从0开始的数字
	 * @param p2
	 *            二级产品线代码 已一级产品线代码为前缀，接下来从0开始
	 * @param pcode
	 * @param acode
	 *            动作码
	 * @param ap
	 *            动作属性
	 * @param ar
	 *            动作动作结果 0：成功 1：失败
	 * @param cid
	 *            视频频道ID 全业务线统一 和大媒资一致
	 * @param pid
	 *            专辑ID 如果不是专辑 用 - 替代
	 * @param vid
	 *            视频ID
	 * @param uid
	 *            乐视网用户id
	 * @param cur_url
	 *            当前页面地址
	 * @param reid
	 *            推荐反馈的随机数 String [推荐结果点击时必须上报]推荐点击动作上报时必填
	 * @param area
	 *            推荐区域标识 String 会提供两个特定的区域[推荐点击动作上报时必填]
	 * @param bucket
	 *            推荐的算法策略 Int 推荐组维护 [推荐点击动作上报时必填]
	 * @param rank
	 *            点击视频在推荐区域的排序 Int 最终要沟通确认 [推荐点击动作上报时必填]
	 * @param ilu
	 *            是否为登录用户 0：登录 1：非登录
	 *
	 *此方法仅用于推送消息使用， 其他情况禁止调用
	 */
	public void sendPushActionInfo(final Context context, final String p1, final String p2, final String pcode,
			final String acode, final String ap, final String ar, final String cid, final String pid, final String vid,
			final String uid, final String cur_url, final String area, final String bucket,
			final String rank, final int ilu) {//此方法仅用于推送消息使用， 其他情况禁止调用
		mDataManager.sendPushActionInfo(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu);
	}


    /**
     * 增加lid zid
     * */
    public void sendPushActionInfo(final Context context, final String p1, final String p2, final String pcode,
                                   final String acode, final String ap, final String ar, final String cid, final String pid, final String vid,
                                   final String uid, final String cur_url, final String area, final String bucket,
                                   final String rank, final int ilu,final String lid,final  String zid) {//此方法仅用于推送消息使用， 其他情况禁止调用
        mDataManager.sendPushActionInfo(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu,lid,zid);
    }
    /**
     * 增加  msgid
     */
	public void sendPushActionInfoAddApp(final Context context, final String p1,
			final String p2, final String pcode, final String acode,
			final String ap, final String ar, final String cid,
			final String pid, final String vid, final String uid,
			final String cur_url, final String area, final String bucket,
			final String rank, final int ilu, final String lid, final String zid) {// 此方法仅用于推送消息使用，
																					// 其他情况禁止调用
		mDataManager.sendPushActionInfoAddApp(context, p1, p2, pcode, acode, ap, ar,
				cid, pid, vid, uid, cur_url, area, bucket, rank, ilu, lid, zid);
	}

 	/**
	 * @category:说明 PV 各业务线视需求不同，灵活掌握是否上报 PV 日志 PV日志主要用于统计、分析视频曝光相关的数据
	 *              PV日志包括用户信息、设备信息、视频信息 key和value的值都不允许包含 &
	 *              符号，如果有包含，要对key/value值进行URL编码 上报地址：http://dc.letv.com/pgv/?
	 * @param context
	 * @param p1
	 *            一级产品线代码 为从0开始的数字
	 * @param p2
	 *            二级产品线代码 已一级产品线代码为前缀，接下来从0开始
	 * @param pcode
	 * @param cid
	 *            视频频道ID 全业务线统一 和大媒资一致
	 * @param pid
	 *            专辑ID 如果不是专辑 用 - 替代
	 * @param vid
	 *            视频ID
	 * @param uid
	 *            乐视网用户id
	 * @param ref
	 *            页面来源 String 要进行URL编码[必填]
	 * @param ct
	 *            来源类型 1：直接输入，2：站内， 3：搜索，4：站外，5：合作
	 * @param rcid
	 *            来源频道 String 全业务线统一[必填]
	 * @param kw
	 *            搜索关键字 String 需要URL编码，如果不是搜索结果页，那么用 – 代替 [必填]
	 * @param cur_url
	 *            当前页面地址 String 要进行URL编码 [必填]
	 * @param area
	 *            区域 String 当前页面是由哪个区域点击过来的[选填]
	 * @param weid
	 *            用户每打开一个页面js随机生成一个webeventid,保证每个页面id唯一即可,同时页面有播放器时将此参数传递给播放器,
	 *            如果一个页面有多个播放器,那么要将这个传递给多个播放器..建议可以使用毫秒数生成id[必填]
	 * @param ilu
	 *            Is login user：是否为登录用户 0：是登录用户 1：非登录用户 [必填]
	 */
	public void sendPVInfo(final Context context, final String p1, final String p2, final String pcode,
			final String cid, final String pid, final String vid, final String uid, final String ref, final String ct,
			final String rcid, final String kw, final String cur_url, final String area, final String weid,
			final int ilu) {
		mDataManager.sendPVInfo(context, p1, p2, pcode, cid, pid, vid, uid, ref, ct, rcid, kw, cur_url, area, weid, ilu);
	}
	
	/**
	 * @category:说明 PV 各业务线视需求不同，灵活掌握是否上报 PV 日志 PV日志主要用于统计、分析视频曝光相关的数据
	 *              PV日志包括用户信息、设备信息、视频信息 key和value的值都不允许包含 &
	 *              符号，如果有包含，要对key/value值进行URL编码 上报地址：http://dc.letv.com/pgv/?
	 * @param context
	 * @param p1
	 *            一级产品线代码 为从0开始的数字
	 * @param p2
	 *            二级产品线代码 已一级产品线代码为前缀，接下来从0开始
	 * @param pcode
	 * @param cid
	 *            视频频道ID 全业务线统一 和大媒资一致
	 * @param pid
	 *            专辑ID 如果不是专辑 用 - 替代
	 * @param vid
	 *            视频ID
	 * @param uid
	 *            乐视网用户id
	 * @param ref
	 *            页面来源 String 要进行URL编码[必填]
	 * @param ct
	 *            来源类型 1：直接输入，2：站内， 3：搜索，4：站外，5：合作
	 * @param rcid
	 *            来源频道 String 全业务线统一[必填]
	 * @param kw
	 *            搜索关键字 String 需要URL编码，如果不是搜索结果页，那么用 – 代替 [必填]
	 * @param cur_url
	 *            当前页面地址 String 要进行URL编码 [必填]
	 * @param area
	 *            区域 String 当前页面是由哪个区域点击过来的[选填]
	 * @param weid
	 *            用户每打开一个页面js随机生成一个webeventid,保证每个页面id唯一即可,同时页面有播放器时将此参数传递给播放器,
	 *            如果一个页面有多个播放器,那么要将这个传递给多个播放器..建议可以使用毫秒数生成id[必填]
	 * @param ilu
	 *            Is login user：是否为登录用户 0：是登录用户 1：非登录用户 [必填]
	 * @param zid
	 * 			  String
	 */
	public void sendPVInfo(final Context context, final String p1, final String p2, final String pcode,
			final String cid, final String pid, final String vid, final String uid, final String ref, final String ct,
			final String rcid, final String kw, final String cur_url, final String area, final String weid,
			final int ilu, final String zid) {
		mDataManager.sendPVInfo(context, p1, p2, pcode, cid, pid, vid, uid, ref, ct, rcid, kw, cur_url, area, weid, ilu, zid);
	}
	
	/**
	 * @category 说明 env 字段名应该严格遵守，不能随意自己增加字段名，上报采用key-value格式
	 *           环境日志是系统信息，比如操作系统名称，版本，终端品牌，型号等等 环境日志在用户的每次登陆上报一次即可，其他时间不用再次上报
	 *           比如：用户打开移动客户端，此时上报登录日志，同时也要上报环境日志，在此之后的播放、动作等等均不需要再重复上报环境日志
	 *           。再比如：用户在PC端打开浏览器，此时上报环境日志。当需要把用户的环境信息做数据分析，比如统计动作日志需要环境信息，
	 *           那么需要用动作日志里面的key 去join 环境日志，移动、TV等有设备ID的业务线可以用 UUID(设备ID_时间)
	 *           作为key，来join环境日志 PC 可以用 lc 来join环境日志
	 *           4：上报地址：http://dc.letv.com/env/?
	 * 
	 * @param context
	 * @param p1
	 *            一级产品线代码 为从0开始的数字
	 * @param p2
	 *            二级产品线代码 已一级产品线代码为前缀，接下来从0开始
	 * @param ip
	 *            ip地址
	 * @param xh
	 * 			终端型号	
	 */
	public void sendEnvInfo(final Context context, final String p1, final String p2, final String ip, final String xh) {
		mDataManager.sendEnvInfo(context, p1, p2, ip, xh);
	}
	
	/**
	 * @category 说明 env 字段名应该严格遵守，不能随意自己增加字段名，上报采用key-value格式
	 *           环境日志是系统信息，比如操作系统名称，版本，终端品牌，型号等等 环境日志在用户的每次登陆上报一次即可，其他时间不用再次上报
	 *           比如：用户打开移动客户端，此时上报登录日志，同时也要上报环境日志，在此之后的播放、动作等等均不需要再重复上报环境日志
	 *           。再比如：用户在PC端打开浏览器，此时上报环境日志。当需要把用户的环境信息做数据分析，比如统计动作日志需要环境信息，
	 *           那么需要用动作日志里面的key 去join 环境日志，移动、TV等有设备ID的业务线可以用 UUID(设备ID_时间)
	 *           作为key，来join环境日志 PC 可以用 lc 来join环境日志
	 *           4：上报地址：http://dc.letv.com/env/?
	 * 
	 * @param context
	 * @param p1
	 *            一级产品线代码 为从0开始的数字
	 * @param p2
	 *            二级产品线代码 已一级产品线代码为前缀，接下来从0开始
	 * @param ip
	 *            ip地址
	 * @param xh
	 * 			终端型号	
	 * @param zid
	 * 			String 
	 */
	public void sendEnvInfo(final Context context, final String p1, final String p2, final String ip, final String xh, final String zid) {
		mDataManager.sendEnvInfo(context, p1, p2, ip, xh, zid);
	}
	
	
	/**
	 * @category 说明：login 字段名应该严格遵守，不能随意自己增加字段名，上报采用key-value格式
	 *           如果用户从移动App上访问，那么app启动进入首页之后，就要上报一次 如果一天用户N次启动App，就发生N次上报
	 *           任何业务线，如果用户点击登录按钮并且登录成功之后，要进行一次上报，此时上报信息要携带用户的乐视网注册ID
	 *           key和value的值都不允许包含 & 符号，如果有包含，要对key/value值进行URL编码
	 *           上报地址：http://dc.letv.com/lg/?
	 * @param context
	 * @param p1
	 *            一级产品线代码 为从0开始的数字
	 * @param p2
	 *            二级产品线代码 已一级产品线代码为前缀，接下来从0开始
	 * @param uid
	 *            乐视网用户注册ID String [必填]
	 * @param lp
	 *            登录属性 String 业务线自己维护，可以存储任何值或者多个值，但是如果包括 &，要进行URL编码 [选填]
	 * @param ref
	 *            登录来源 String 必须为英文 业务线自己维护取值字典 [选填]
	 * @param ts
	 *            Timestamp登录时间 String 用秒数来表示[必填]
	 * @param pcode
	 *            渠道
	 * @param st
	 *            Status 登录状态 int 0:登录成功 1:退出登录 用户按home键转入后台也算退出登录
	 *            通过这个状态，可以计算用户的登录时长 [选填]
	 */
	public void sendLoginInfo(final Context context, final String p1, final String p2, final String uid,
			final String lp, final String ref, final String ts, final String pcode, final int st) {
		mDataManager.sendLoginInfo(context, p1, p2, uid, lp, ref, ts, pcode, st);
	}
	
	/**
	 * @category 说明：login 字段名应该严格遵守，不能随意自己增加字段名，上报采用key-value格式
	 *           如果用户从移动App上访问，那么app启动进入首页之后，就要上报一次 如果一天用户N次启动App，就发生N次上报
	 *           任何业务线，如果用户点击登录按钮并且登录成功之后，要进行一次上报，此时上报信息要携带用户的乐视网注册ID
	 *           key和value的值都不允许包含 & 符号，如果有包含，要对key/value值进行URL编码
	 *           上报地址：http://dc.letv.com/lg/?
	 * @param context
	 * @param p1
	 *            一级产品线代码 为从0开始的数字
	 * @param p2
	 *            二级产品线代码 已一级产品线代码为前缀，接下来从0开始
	 * @param uid
	 *            乐视网用户注册ID String [必填]
	 * @param lp
	 *            登录属性 String 业务线自己维护，可以存储任何值或者多个值，但是如果包括 &，要进行URL编码 [选填]
	 * @param ref
	 *            登录来源 String 必须为英文 业务线自己维护取值字典 [选填]
	 * @param ts
	 *            Timestamp登录时间 String 用秒数来表示[必填]
	 * @param pcode
	 *            渠道
	 * @param st
	 *            Status 登录状态 int 0:登录成功 1:退出登录 用户按home键转入后台也算退出登录
	 *            通过这个状态，可以计算用户的登录时长 [选填]
	 * @param zid 
	 * 			  String
	 */
	public void sendLoginInfo(final Context context, final String p1, final String p2, final String uid,
			final String lp, final String ref, final String ts, final String pcode, final int st, final String zid) {
		mDataManager.sendLoginInfo(context, p1, p2, uid, lp, ref, ts, pcode, st, zid);
	}
	
	/**
	 * @category 说明 query 说明： 字段名应该严格遵守，不能随意自己增加字段名，上报采用key-value格式
	 *           key和value的值都不允许包含 & 符号，如果有包含，要对key/value值进行URL编码
	 *           搜索日志包括搜索关键词，用户信息和搜索结果信息 上报地址：http://dc.letv.com/qy/?
	 * @param context
	 * @param p1
	 *            一级产品线代码 为从0开始的数字
	 * @param p2
	 *            二级产品线代码 已一级产品线代码为前缀，接下来从0开始
	 * @param sid
	 *            Search Id：用于标识一次搜索 int [必填]
	 * @param ty
	 *            上报类型 int 0:搜索结果页 1：搜索结果点击 [必填]
	 * @param pos
	 *            点击的视频位置 String
	 *            [必填]如果上报类型是搜索结果页，那么这个字段记录的是来自上个页面的点击位置；如果上报类型是是搜索结果点击
	 *            ，位置是指点击精确搜索结果的第几个,如果第1个上报1,以此类推. 否则 选填
	 * @param pid
	 *            专辑ID 如果不是专辑 用 - 替代
	 * @param vid
	 *            视频ID
	 * @param cid
	 *            视频频道ID 全业务线统一 和大媒资一致
	 * @param uid
	 *            乐视网用户注册ID String [必填]
	 * @param ilu
	 *            是否为登录用户 int 0：是登录用户 1：非登录用户 [必填]
	 * @param query
	 *            query: http开头的查询 url，至少要包括用户搜索关键词 String 要进行URL编码 [必填]
	 * @param page
	 *            Page: 搜索结果的当前页码 int [必填]
	 * @param rt
	 *            Result: 搜索结果 String pid_vid_cid,pid_vid_cid
	 *            按照搜索结果页展现的顺序，把搜索结果组合成以上格式进行上报，每页搜索结果上报一条
	 *            [必填]优先上报pid，以逗号分隔，如果取不到pid上报vid
	 */
	public void sendQueryInfo(final Context context, final String p1, final String p2, final String sid,
			final String ty, final String pos, final String pid, final String vid, final String cid, final String uid,
			final int ilu, final String query, final String page, final String rt) {
		mDataManager.sendQueryInfo(context, p1, p2, sid, ty, pos, pid, vid, cid, uid, ilu, query, page, rt);
	}
	
	/**
	 * @category 说明 query 说明： 字段名应该严格遵守，不能随意自己增加字段名，上报采用key-value格式
	 *           key和value的值都不允许包含 & 符号，如果有包含，要对key/value值进行URL编码
	 *           搜索日志包括搜索关键词，用户信息和搜索结果信息 上报地址：http://dc.letv.com/qy/?
	 * @param context
	 * @param p1
	 *            一级产品线代码 为从0开始的数字
	 * @param p2
	 *            二级产品线代码 已一级产品线代码为前缀，接下来从0开始
	 * @param sid
	 *            Search Id：用于标识一次搜索 int [必填]
	 * @param ty
	 *            上报类型 int 0:搜索结果页 1：搜索结果点击 [必填]
	 * @param pos
	 *            点击的视频位置 String
	 *            [必填]如果上报类型是搜索结果页，那么这个字段记录的是来自上个页面的点击位置；如果上报类型是是搜索结果点击
	 *            ，位置是指点击精确搜索结果的第几个,如果第1个上报1,以此类推. 否则 选填
	 * @param pid
	 *            专辑ID 如果不是专辑 用 - 替代
	 * @param vid
	 *            视频ID
	 * @param cid
	 *            视频频道ID 全业务线统一 和大媒资一致
	 * @param uid
	 *            乐视网用户注册ID String [必填]
	 * @param ilu
	 *            是否为登录用户 int 0：是登录用户 1：非登录用户 [必填]
	 * @param query
	 *            query: http开头的查询 url，至少要包括用户搜索关键词 String 要进行URL编码 [必填]
	 * @param page
	 *            Page: 搜索结果的当前页码 int [必填]
	 * @param rt
	 *            Result: 搜索结果 String pid_vid_cid,pid_vid_cid
	 *            按照搜索结果页展现的顺序，把搜索结果组合成以上格式进行上报，每页搜索结果上报一条
	 *            [必填]优先上报pid，以逗号分隔，如果取不到pid上报vid
	 * @param zid
	 * 			  String
	 */
	public void sendQueryInfo(final Context context, final String p1, final String p2, final String sid,
			final String ty, final String pos, final String pid, final String vid, final String cid, final String uid,
			final int ilu, final String query, final String page, final String rt, final String zid) {
		mDataManager.sendQueryInfo(context, p1, p2, sid, ty, pos, pid, vid, cid, uid, ilu, query, page, rt, zid);
	}
	
	
	/**
	 * @category 说明 页面广告 本日志数据只包括页面广告的数据，不包括播放器广告(播放器广告上报标准不变)
	 *           本日志规范适用于移动、PC、TV等各个业务线，通过业务线代码区分 key和value的值都不允许包含 &
	 *           符号，如果有包含，要对key/value值进行URL编码 上报地址：http://dc.letv.com/pad/?
	 * @param context
	 * @param p1
	 *            一级产品线代码 为从0开始的数字
	 * @param p2
	 *            二级产品线代码 已一级产品线代码为前缀，接下来从0开始
	 * @param ac
	 *            pv/click int 0:pv; 1:click
	 * @param pp
	 *            广告属性 String 业务线自己维护，可以存储任何值或者多个值，但是如果包括 &，要进行URL编码 [选填]
	 * @param cid
	 *            视频频道ID int 全业务线统一，与大媒资保持一致[必填]
	 * @param url
	 *            当前页面url String 需要url编码[必填]
	 * @param slotid
	 *            广告位id String [必填]
	 * @param adid
	 *            广告ID String [必填]
	 * @param murl
	 *            Material url:素材地址 String [必填]
	 * @param uid
	 *            乐视网用户的标识 String [必填]
	 * @param ref
	 *            页面来源 String 要进行URL编码[必填]
	 * @param rcid
	 *            来源频道 String 全业务线统一[必填]
	 * @param pcode
	 *            渠道
	 * @param ilu
	 *            Is login user：是否为登录用户 Int 0：是登录用户 1：非登录用户 [必填]
	 */
	public void sendADInfo(final Context context, final String p1, final String p2, final String ac, final String pp,
			final String cid, final String url, final String slotid, final String adid, final String murl,
			final String uid, final String ref, final String rcid, final String pcode, final int ilu) {
		mDataManager.sendADNewInfo(context, p1, p2, ac, pp, cid, url, slotid, adid, murl, uid, ref, rcid, pcode, ilu);
	}
	
	/**
	 * @category 说明 页面广告 本日志数据只包括页面广告的数据，不包括播放器广告(播放器广告上报标准不变)
	 *           本日志规范适用于移动、PC、TV等各个业务线，通过业务线代码区分 key和value的值都不允许包含 &
	 *           符号，如果有包含，要对key/value值进行URL编码 上报地址：http://dc.letv.com/pad/?
	 * @param context
	 * @param p1
	 *            一级产品线代码 为从0开始的数字
	 * @param p2
	 *            二级产品线代码 已一级产品线代码为前缀，接下来从0开始
	 * @param ac
	 *            pv/click int 0:pv; 1:click
	 * @param pp
	 *            广告属性 String 业务线自己维护，可以存储任何值或者多个值，但是如果包括 &，要进行URL编码 [选填]
	 * @param cid
	 *            视频频道ID int 全业务线统一，与大媒资保持一致[必填]
	 * @param url
	 *            当前页面url String 需要url编码[必填]
	 * @param slotid
	 *            广告位id String [必填]
	 * @param adid
	 *            广告ID String [必填]
	 * @param murl
	 *            Material url:素材地址 String [必填]
	 * @param uid
	 *            乐视网用户的标识 String [必填]
	 * @param ref
	 *            页面来源 String 要进行URL编码[必填]
	 * @param rcid
	 *            来源频道 String 全业务线统一[必填]
	 * @param pcode
	 *            渠道
	 * @param ilu
	 *            Is login user：是否为登录用户 Int 0：是登录用户 1：非登录用户 [必填]
	 * @param zid 
	 * 			  String
	 */
	public void sendADInfo(final Context context, final String p1, final String p2, final String ac, final String pp,
			final String cid, final String url, final String slotid, final String adid, final String murl,
			final String uid, final String ref, final String rcid, final String pcode, final int ilu, final String zid) {
		mDataManager.sendADNewInfo(context, p1, p2, ac, pp, cid, url, slotid, adid, murl, uid, ref, rcid, pcode, ilu, zid);
	}
	
	
	/**
	 * @category 说明  字段名应该严格遵守，不能随意自己增加字段名，上报采用key-value格式
	 * 错误日志是错误相关信息，比如调取接口错误等等
	 * 上报地址：http://dc.letv.com/er/?
	 * @param context
	 * @param p1
	 *            一级产品线代码 为从0开始的数字
	 * @param p2
	 *            二级产品线代码 已一级产品线代码为前缀，接下来从0开始
	 * @param ac
	 *            pv/click int 0:pv; 1:click
	 * @param error
	 *            错误代码   业务方自己维护 0表示成功, 这个全业务线统一 是否上报错误码及具体上报错误的逻辑由业务方自行决定 [必填] 
	 * @param cid
	 *            视频频道ID int 全业务线统一，与大媒资保持一致[必填]
	 * @param pid
	 * 			      专辑id
	 * @param vid
	 * 			      视频id
	 * @param src
	 *            用于区分不同日志上报的环境来源标识	String	取各日志接口的名称：如播放日志上报的，此字段填写pl，pv日志填写pgv [选填] 2013年11月28日增加
	 * @param ep
	 * 			      错误属性	 string	
	 *			     业务线自己维护，可以存储任何值或者多个值，但是必须要进行URL编码，例如：ep上报值为k1=v1&k2=v2，k1=v1&k2=v2 应该URL编码成：k1%3dv1%26k2%3dv2，也就是上报的内容为：py=k1%3dv1%26k2%3dv2
	 * @param zid 
	 * 			     专题id  String
	 */
	public void sendErrorInfo(final Context context, final String p1, final String p2, final String error,
			final String src,final String ep,final String cid,final String pid,final String vid,final String zid){
		mDataManager.sendErrorInfo(context, p1, p2, error, src, ep, cid, pid, vid, zid, ip);
	}
	
	
	
	// ================================================================================
	// =============================Version2.0===end===================================
	// ================================================================================

	/**
	 * 获取所有提交错误的记录
	 * 
	 * @param context
	 * @return
	 */
	public ArrayList<StatisCacheBean> getAllErrorCache(Context context) {
		ArrayList<StatisCacheBean> allCacheTrace = StatisDBHandler.getAllCacheTrace(context);
		return allCacheTrace;
	}

	/**
	 * 提交出错的统计
	 * 
	 * @param context
	 * @param mStatisCacheBean
	 */
	public void submitErrorInfo(Context context, StatisCacheBean mStatisCacheBean) {
		mDataManager.submitErrorInfo(context, mStatisCacheBean);
	}

	public String getStatLoginUrl() {
		if (isUseTest) {
			return DataConstant.STAT_LOGIN_URL_TEST;
		} else {
			return DataConstant.STAT_LOGIN_URL;
		}
	}

	// ==============================================================================
	// ==============================================================================
	public String getStatRecommendUrl() {
		if (isUseTest) {
			return DataConstant.STAT_RECOMMEND_URL_TEST;
		} else {
			return DataConstant.STAT_RECOMMEND_URL;
		}
	}

	public String getStatPlayerUrl() {
		if (isUseTest) {
			return DataConstant.STAT_PLAYER_URL_TEST;
		} else {
			return DataConstant.STAT_PLAYER_URL;
		}
	}

	public String getStatActionUrl() {
		if (isUseTest) {
			return DataConstant.STAT_ACTION_URL_TEST;
		} else {
			return DataConstant.STAT_ACTION_URL;
		}
	}

	public String getStatPVUrl() {
		if (isUseTest) {
			return DataConstant.STAT_PV_URL_TEST;
		} else {
			return DataConstant.STAT_PV_URL;
		}
	}

	public String getStatEnvUrl() {
		if (isUseTest) {
			return DataConstant.STAT_ENV_URL_TEST;
		} else {
			return DataConstant.STAT_ENV_URL;
		}
	}

	public String getStatNewLoginUrl() {
		if (isUseTest) {
			return DataConstant.STAT_LOGIN2_URL_TEST;
		} else {
			return DataConstant.STAT_LOGIN2_URL;
		}
	}

	public String getStatQueryUrl() {
		if (isUseTest) {
			return DataConstant.STAT_QUERY_URL_TEST;
		} else {
			return DataConstant.STAT_QUERY_URL;
		}
	}

	public String getStatNewADUrl() {
		if (isUseTest) {
			return DataConstant.STAT_AD2_URL_TEST;
		} else {
			return DataConstant.STAT_AD2_URL;
		}
	}

	// ==============================================================================
	// ==============================================================================

	public String getStatVideoCloseUrl() {
		if (isUseTest) {
			return DataConstant.STAT_VIDEOCLOSED_URL_TEST;
		} else {
			return DataConstant.STAT_VIDEOCLOSED_URL;
		}
	}

	public String getStatADUrl() {
		if (isUseTest) {
			return DataConstant.STAT_AD_URL_TEST;
		} else {
			return DataConstant.STAT_AD_URL;
		}
	}

	public String getStatActionCodeUrl() {
		if (isUseTest) {
			return DataConstant.STAT_ACTIONCODE_URL_TEST;
		} else {
			return DataConstant.STAT_ACTIONCODE_URL;
		}
	}

	public String getStatDownloadLogUrl() {
		if (isUseTest) {
			return DataConstant.STAT_DOWNLOADLOG_URL_TEST;
		} else {
			return DataConstant.STAT_DOWNLOADLOG_URL;
		}
	}

	public String getStatErrorLogUrl() {
		if (isUseTest) {
			return DataConstant.STAT_ERROR_URL_TEST;
		} else {
			return DataConstant.STAT_ERROR_URL;
		}
	}

	public String getUploadClientDataUrl() {
		if (isUseTest) {
			return DataConstant.UPLOAD_CLIENTDATA_URL_TEST;
		} else {
			return DataConstant.UPLOAD_CLIENTDATA_URL;
		}
	}
	public void close() {
		HttpEngine.getInstance().closeHttpEngine();
		ThreadPoolManager.getInstance().closeThreadPool();
	}
}
