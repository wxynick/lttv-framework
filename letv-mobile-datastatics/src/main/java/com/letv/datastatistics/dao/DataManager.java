package com.letv.datastatistics.dao;

import java.net.URLEncoder;

import com.letv.datastatistics.db.StatisDBHandler;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.letv.datastatistics.DataStatistics;
import com.letv.datastatistics.entity.DTypeActionInfo;
import com.letv.datastatistics.entity.DataStatusInfo;
import com.letv.datastatistics.entity.LogoInfo;
import com.letv.datastatistics.entity.StatisticsVideoInfo;
import com.letv.datastatistics.exception.HttpDataConnectionException;
import com.letv.datastatistics.exception.HttpDataParserException;
import com.letv.datastatistics.http.HttpEngine;
import com.letv.datastatistics.parse.DataStatusInfoParse;
import com.letv.datastatistics.util.DataConstant;
import com.letv.datastatistics.util.DataConstant.StaticticsCacheTrace.Field;
import com.letv.datastatistics.util.DataUtils;

import javax.security.auth.login.LoginException;

public class DataManager {

	public void sendUserInfo(final Context context, final String uid, final String pcode, final String source,
			final String stat, final String onlen) {
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(DataUtils.getData(DataUtils.getSystemName()) + "&");// $1:os,操作系统
																				// ex:android
				sb.append(DataUtils.getData(DataUtils.getOSVersionName()) + "&");// $2:osver,操作系统版本
				sb.append(DataUtils.getData(pcode) + "&");// $3:app,产品代码
															// ex:010410000
				sb.append(DataUtils.getData(DataUtils.getClientVersionName(context)) + "&");// $4:appver,应用版本
				sb.append(DataUtils.getData(DataUtils.getDeviceName()) + "&"); // $5:model,设备型号
				sb.append(DataUtils.getData(DataUtils.getBrandName()) + "&"); // $6:brand,品牌
				sb.append("-&"); // $7:did,(deviceid)设备唯一标识
									// ex:md5(imei+imsi+model+brand+macaddress)
				// 统计版本1.4后修改为用户类型。如果是新增用户上报n 如果是升级用户上报u 如果是活跃用户上报 - （不区分大小写）
				sb.append(DataUtils.getData(DataUtils.getResolution(context)) + "&"); // $8:resolution,分辨率
				sb.append(DataUtils.getData(DataUtils.getDensity(context)) + "&");// $9:密度
				sb.append(DataUtils.getData(DataUtils.getNetType(context)) + "&"); // $10:nettype,上网类型
																					// ex:wifi/3G
				sb.append(DataUtils.getData(DataUtils.getMacAddress(context)) + "&"); // $11:macaddress,mac地址
				sb.append(DataUtils.getData(uid) + "&"); // $12:uid,乐视网用户id
				sb.append(DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // auid,设备id
																							// 统计1.5版本新增
				sb.append("-&"); // $13:partner 渠道
				sb.append(DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $14:uuid,(did_timestamp)
				sb.append(DataUtils.getData(source) + "&"); // $15:终端类型，box、tv
				sb.append(DataUtils.getData(DataConstant.STAT_VERSION) + "&"); // $16:统计版本(1.5)
				sb.append("-&"); // $tv版用于统计登录来源

				sb.append(DataUtils.getData(stat) + "&"); // $17:0：登录状态 1：退出状态
				sb.append(DataUtils.getData(onlen)); // $18:在线时长 以秒为单位

				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendUserInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_LOGIN_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatLoginUrl() + sb.toString();
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 视频播放结束数据统计
	 * 
	 * @param <StatisticsVideoInfo>
	 * 
	 * @param context
	 * @param cid
	 *            频道id
	 * @param pid
	 * @param vid
	 * @param mmsid
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
	 *            播放时长（单位：s秒）15_10_59
	 * @param from
	 *            播放动作来源(1:频道列表 2:详情 3:播放记录 4:下载 5：搜索结果列表 6：排行 7：首页 8：
	 *            其它；9、追剧；10、收藏)
	 * @param err
	 *            （0、正常 1、调度错误 2、cdn资源错误 3、加载失败 4、UNKNOWN）
	 * @param code
	 *            码率123_456_678
	 * @throws HttpDataConnectionException
	 * @throws HttpDataParserException
	 */
	public void sendVideoInfo(final Context context, final StatisticsVideoInfo mStatisticsVideoInfo) {

		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {

				StringBuffer sb = new StringBuffer();
				sb.append(DataUtils.getData(DataUtils.getNetType(context)) + "&"); // $1:nettype,上网类型
																					// ex:wifi/3G
				sb.append(DataUtils.getData(mStatisticsVideoInfo.getUid()) + "&"); // $2:uid,乐视网用户id
				sb.append(DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // auid,设备id
				sb.append(DataUtils.getData(replaceStr(mStatisticsVideoInfo.getCid())) + "&");// $3:cid,频道id
				sb.append(replaceStr(DataUtils.getData(mStatisticsVideoInfo.getPid())) + "_"
						+ replaceStr(DataUtils.getData(mStatisticsVideoInfo.getVid())) + "_"
						+ DataUtils.getData(mStatisticsVideoInfo.getMmsid()) + "&");// $4:vinfo,视频信息pid_vid_mmsid
				String ddurl = mStatisticsVideoInfo.getDdurl();
				sb.append(URLEncoder.encode(DataUtils.getDataUrl(((null != ddurl && ddurl.length() > 5) ? bindPcodeAndVersion(
						DataUtils.getData(ddurl), mStatisticsVideoInfo.getPcode(),
						DataUtils.getClientVersionName(context)) : DataUtils.getData(ddurl))))
						+ "&");// lhz0627 $5:ddurl,调度地址
				// ：没有调度地址(ddurl长度小于5时认为不是调度地址)时，不加密调度地址
				// sb.append(URLEncoder.encode(DataUtils.getDataUrl(bindPcodeAndVersion(DataUtils.getData(ddurl),
				// pcode, DataUtils.getClientVersionName(context)))) + "&");//
				// $5:ddurl,调度地址
				sb.append(URLEncoder.encode(DataUtils.getDataUrl(DataUtils.getData(mStatisticsVideoInfo.getPlayurl())))
						+ "&");// $6:playurl,最终播放地址
				sb.append(DataUtils.getData(mStatisticsVideoInfo.getStatus()) + "&");// $7:status,（1:初始
				// 2:用户手动结束播放
				// 3：视频自动完成播放
				// 4：播放出错）
				int utime = mStatisticsVideoInfo.getUtime();
				sb.append("-_" + (utime > 0 ? DataUtils.getData(String.valueOf(utime)) : "-") + "&");// lhz
																										// 0627
																										// 最新wiki格式:
																										// 请求调度耗时_下载物料耗时（单位：s
																										// ex:2）
				/*
				 * sb.append("-_-_" + DataUtils.getData(String.valueOf(utime)) +
				 * "&");// $8:utime,加载耗时（请求调度耗时_请求cdn耗时_加载资源耗时（单位：s // ex:2））
				 */
				sb.append(DataUtils.getData(String.valueOf(mStatisticsVideoInfo.getBufcount())) + "&");// $9:bufcount,缓冲次数
				sb.append(DataUtils.getData(mStatisticsVideoInfo.getPlayedTime() + "") + "&");// $10:playedTime,播放时长（单位：s秒）15_10_59
				sb.append(DataUtils.getData(mStatisticsVideoInfo.getFrom()) + "&");// $11:from,播放动作来源(1:频道列表
				// 2:详情 3:播放记录 4:下载
				// 5：搜索结果列表 6：排行
				// 7：首页 8：
				// 其它；9、追剧；10、收藏)
				sb.append(DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $12:uuid,
																				// (did_timestamp)登录时生成的uuid
				sb.append(DataUtils.getData(mStatisticsVideoInfo.getErr()) + "&");// $13:err,（0、正常
																					// 1、调度错误
				// 2、cdn资源错误 3、加载失败
				// 4、UNKNOWN）
				sb.append(DataUtils.getData(mStatisticsVideoInfo.getCode()) + "&");// $14:码率123_456_678
				sb.append(DataConstant.STAT_VERSION + "&"); // $15: statver
															// 统计版本(1.1)
				// sb.append(DataStatistics.getInstance().getDeviceID(context) +
				// "&"); //$16：did 唯一标识符
				sb.append(DataUtils.getData(mStatisticsVideoInfo.getPcode()) + "&"); // $17：app
																						// 产品线
				sb.append(DataUtils.getData(DataUtils.getClientVersionName(context)) + "&"); // $18：appver
																								// 应用版本
				sb.append(DataUtils.getDataEmpty(DataUtils.getSystemName()) + "&"); // $19：os
																					// 操作系统
				sb.append(DataUtils.getData(mStatisticsVideoInfo.getTerminaltype()) + "&"); // $20：terminaltype
				// 终端类型
				sb.append(DataUtils.getData(mStatisticsVideoInfo.getAc()) + "&"); // $21：ac
																					// 贴片广告形式
				sb.append(DataUtils.getData(mStatisticsVideoInfo.getPtype()) + "&"); // $22：ptype
																						// 播放类型
				sb.append(DataUtils.getData(mStatisticsVideoInfo.getVlen()) + "&"); // $23：vlen
																					// 视频时长
				sb.append(DataUtils.getData(DataUtils.getBrandName()) + "&"); // $24：brand
																				// 品牌
				sb.append(DataUtils.getData(mStatisticsVideoInfo.getPtid())); // $25：ptid
																				// 播放时间标识

				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendVideoInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_VIDEOCLOSED_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatVideoCloseUrl() + sb.toString();
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 封装的方法，用于区分cid pid vid aid lid zid by glh
	 * @param str
	 * @return
	 */
	public String replaceStr(String str){
		String value = "-";
		if(str ==null ||str.equals("") || str.equals("0") || str.equals("-1")){
			return value;
		}
		return str;
	}
	
	
	/**
	 * 1.5版本
	 * 
	 * @param context
	 * @param uid
	 * @param pcode
	 * @param adtype
	 * @param adid
	 * @param actionid
	 * @param clicknum
	 * @param durTime
	 * @param playedTime
	 * @param utime
	 * @param cid
	 * @param pid
	 * @param vid
	 * @param vlen
	 * @param ptid
	 * @param adsystem
	 */
	public void sendADInfo(final Context context, final String uid, final String pcode, final String adtype,
			final String adid, final String actionid, final String clicknum, final String durTime,
			final String playedTime, final String utime, final String cid, final String pid, final String vid,
			final String vlen, final String ptid, final String adsystem) {

		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {

				StringBuffer sb = new StringBuffer();
				sb.append(DataUtils.getData(DataUtils.getNetType(context)) + "&"); // $1:nettype,上网类型//
																					// ex:wifi/3G
				sb.append(DataUtils.getData(uid) + "&"); // $2:uid,乐视网用户id
				sb.append(DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $3:uuid,//
																				// (did_timestamp)登录时生成的uuid
				sb.append(DataUtils.getData(pcode) + "&"); // $4：app 产品线
				sb.append(DataUtils.getData(DataUtils.getBrandName()) + "&"); // $5：brand//
																				// 品牌
				sb.append(DataUtils.getData(DataUtils.getClientVersionName(context)) + "&"); // $6：appver应用版本
				// sb.append(DataUtils.getData("5.2") + "&"); // $6：appver应用版本
				sb.append(DataUtils.getData(adtype) + "&"); // $7：adtype 广告位类型
															// 前贴视频100,后贴视频200
															// ,直播前贴视频
															// 101,前贴静态图41,
															// 后贴静态图42,
															// 直播前贴静态图44

				sb.append(DataUtils.getData(adid) + "&"); // $8：adid 广告编号
				sb.append(DataUtils.getData(actionid) + "&"); // $9：actionid//
																// 上报动作串
				sb.append(DataUtils.getData(clicknum) + "&"); // $9：clicknum//
																// 点击次数
				sb.append(DataUtils.getData(utime.trim()) + "&");// $10:utime,加载耗时（请求调度耗时_请求cdn耗时_加载资源耗时（单位：s//
																	// ex:2））

				sb.append(DataUtils.getData(durTime) + "&");// $11:广告时长,采用每个广告连接的行为，固定形式为：30_15_10,不存在时缺失，用"-"替代，其中30代表第3个广告，
															// 15代表第2个广告，10第1个广告
															// 统计1.5版本新增

				sb.append(DataUtils.getData(playedTime) + "&");// $11:playedTime,播放时长（单位：s秒）15_10_59

				sb.append(replaceStr(DataUtils.getData(cid)) + "&");// $12:cid,频道id
				sb.append(replaceStr(DataUtils.getData(pid)) + "_" + replaceStr(DataUtils.getData(vid)) + "&");// $13:vinfo,视频信息pid_vid_mmsid
				sb.append(DataUtils.getData(null) + "&"); // $14：vlen 视频时长
				sb.append(DataUtils.getData(ptid) + "&"); // $15：ptid 播放时间标识
				sb.append(DataConstant.STAT_VERSION + "&"); // $15: statver//
				// 统计版本(1.1)
				sb.append(DataUtils.getData(adsystem) + "&");// adsystem 广告系统
																// ex：（kejie，haoye）统计1.5版本新增
				sb.append(DataUtils.getData(DataUtils.generateDeviceId(context))); // auid,设备id
																					// 统计1.5版本新增

				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendVideoInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_AD_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatADUrl() + sb.toString();
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 1.5
	 * 
	 * @param context
	 * @param code
	 * @param extCode
	 * @param uid
	 * @param pcode
	 * @param adsystem
	 * @param rate
	 */
	public void sendDTypeActionInfo(final Context context, final DTypeActionInfo mDTypeActionInfo) {

		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(DataUtils.getData(DataUtils.getNetType(context)) + "&"); // $1:nettype,上网类型
																					// ex:wifi/3G
				sb.append(DataUtils.getData(mDTypeActionInfo.getUid()) + "&"); // $2:uid,乐视网用户id

				sb.append(DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // auid,设备id
																							// 统计1.5版本新增

				sb.append(DataUtils.getData(mDTypeActionInfo.getCode()) + "&"); // $3:act,动作码
				sb.append(System.currentTimeMillis() + "&"); // $4:t,时间戳
				sb.append(DataUtils.getUUID(context) + "&"); // $5:uuid,
																// (did_timestamp)登录时生成的uuid
				sb.append(URLEncoder.encode(DataUtils.getData(mDTypeActionInfo.getExtCode())) + "&"); // $6:ext,扩展信息cid_pid_vid;134（0或多个扩展信息，扩展信息以;"分号"分隔）
				sb.append(DataConstant.STAT_VERSION + "&"); // $7：statver
															// 统计版本(1.1)
				// sb.append(DataStatistics.getInstance().getDeviceID(context) +
				// "&"); //$8：did 唯一标识符
				sb.append(DataUtils.getData(mDTypeActionInfo.getPcode()) + "&"); // $9：app
																					// 产品线
				sb.append(DataUtils.getData(DataUtils.getClientVersionName(context)) + "&"); // $10：appver
				// sb.append(DataUtils.getData("5.2") + "&"); // $10：appver
				// 应用版本
				sb.append(DataUtils.getData(mDTypeActionInfo.getAdsystem()) + "&");// adsystem
																					// 广告系统
				// ex：（kejie，haoye）统计1.5版本新增
				// 非广告动作该字段为空
				sb.append(DataUtils.getData(mDTypeActionInfo.getRate())); // rate,码率
																			// 统计1.5版本新增
				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendDownloadInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_DOWNLOADLOG_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatDownloadLogUrl() + sb.toString();
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 上报错误信息
	 * */
	public void sendErrorInfo(final Context context, final String ptid, final String uuid, final String app,
			final String appver, final String course, final String act, final String error, final String ext) {

		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {


				StringBuffer sb = new StringBuffer();
				sb.append(ptid + "&");// 播放时间标识
				sb.append(uuid + "&");// 播放器生存的第一次播放的唯一标识，登录是生成的唯一标识
				sb.append(app + "&");// 产品线
				sb.append(appver + "&");// 产品线版本
				sb.append(course + "&");// 发生状态，播放器错误存在，页面行为缺失
				sb.append(act + "&");// 动作码
				sb.append(error + "&");// 错误码
				sb.append(ext);// 扩展字段
				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendUserInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_ERROR_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatErrorLogUrl() + sb.toString();
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public String bindPcodeAndVersion(String url, String pcode, String version) {
		return url + "&pcode=" + pcode + "&version=" + version;
	}

	/**
	 * 接口初始化状态、客户端设备信息上报、升级接口
	 * 
	 * @param context
	 * @throws HttpDataParserException
	 * @throws HttpDataConnectionException
	 */
	public void uploadClientData(final Context context, final String pcode) {

		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {


				StringBuffer sb = new StringBuffer();
				sb.append("osversion=" + DataUtils.getDataEmpty(DataUtils.getOSVersionName()) + "&"); // osversion:
																										// 操作系统版本号
				sb.append("accesstype=" + DataUtils.getDataEmpty(DataUtils.getNetType(context)) + "&"); // accesstype:
																										// 联网类型
				sb.append("resolution=" + DataUtils.getDataEmpty(DataUtils.getResolution(context)) + "&"); // resolution:
																											// 设备分辨率
				sb.append("brand=" + DataUtils.getDataEmpty(DataUtils.getBrandName() + "&")); // brand:
																								// 设备品牌
				sb.append("model=" + DataUtils.getDataEmpty(DataUtils.getDeviceName()) + "&"); // model:
																								// 设备型号
				sb.append("devid=" + DataUtils.getDataEmpty(DataStatistics.getInstance().getDeviceID(context)) + "&"); // devid:
				// 设备唯一标示
				sb.append("pcode=" + DataUtils.getDataEmpty(pcode) + "&"); // pcode:
																			// 客户端产品代码
				sb.append("version=" + DataUtils.getClientVersionName(context)); // version:
																					// 客户端版本号

				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "uploadClientData:" + sb.toString());
				}
				try {
					String cacheData = DataStatistics.getInstance().getUploadClientDataUrl() + sb.toString();
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 接口初始化状态、客户端设备信息上报、升级接口
	 * 
	 * @param context
	 * @throws HttpDataParserException
	 * @throws HttpDataConnectionException
	 * @throws JSONException
	 */
	public DataStatusInfo getDataStatusInfo(Context context, String pcode) {

		final StringBuffer sb = new StringBuffer();

		sb.append("osversion=" + DataUtils.getDataEmpty(DataUtils.getOSVersionName()) + "&"); // osversion:
																								// 操作系统版本号
		sb.append("accesstype=" + DataUtils.getDataEmpty(DataUtils.getNetType(context)) + "&"); // accesstype:
																								// 联网类型
		sb.append("resolution=" + DataUtils.getDataEmpty(DataUtils.getResolution(context)) + "&"); // resolution:
																									// 设备分辨率
		sb.append("brand=" + DataUtils.getDataEmpty(DataUtils.getBrandName()) + "&"); // brand:
																						// 设备品牌
		sb.append("model=" + DataUtils.getDataEmpty(DataUtils.getDeviceName()) + "&"); // model:
																						// 设备型号
		sb.append("devid=" + DataUtils.getDataEmpty(DataStatistics.getInstance().getDeviceID(context)) + "&"); // devid:
																												// 设备唯一标示
		sb.append("pcode=" + DataUtils.getDataEmpty(pcode) + "&"); // pcode:
																	// 客户端产品代码
		sb.append("version=" + DataUtils.getClientVersionName(context)); // version:
																			// 客户端版本号

		if (DataStatistics.getInstance().isDebug()) {
			Log.d(DataStatistics.TAG, "getDataStatusInfo:" + sb.toString());
		}
		String result = null;
		try {
			String cacheData = DataStatistics.getInstance().getUploadClientDataUrl() + sb.toString();
			result = HttpEngine.getInstance().doHttpGet(context,
					new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
			if (TextUtils.isEmpty(result)) {
				return null;
			} else {
				return new DataStatusInfoParse().parseJson(createJsonObject(result));
			}
		} catch (HttpDataParserException e) {
			e.printStackTrace();
		} catch (HttpDataConnectionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 提交出错的统计
	 * 
	 * @param context
	 * @param mStatisCacheBean
	 */
	public void submitErrorInfo(Context context, StatisCacheBean mStatisCacheBean) {
		try {
			HttpEngine.getInstance().doHttpGet(context, mStatisCacheBean);
		} catch (HttpDataParserException e) {
			e.printStackTrace();
		} catch (HttpDataConnectionException e) {
			e.printStackTrace();
		}
	}

	public JSONObject createJsonObject(String data) throws JSONException {

		JSONObject jsonObject = new JSONObject(data);

		return jsonObject;
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
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {

				StringBuffer sb = new StringBuffer();
				sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
																						// 新版从
																						// 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
																						// p1:一级产品线代码
																						// 为从0开始的数字
				sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
																							// p2:二级产品线代码
																							// 已一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001" + "&");// $4 p3 三级产品线代码 平台自定义
				sb.append("acode=" + acode + "&");// $5 acode:动作码
				sb.append("ap=" + URLEncoder.encode(DataUtils.getData(DataUtils.getTrimData(ap))) + "&");// $6
																											// ap:动作属性
																											// ap=URLEncoder.encode(fl=1&wz=1&name=91助手)

				sb.append("ar=" + DataUtils.getData(DataUtils.getTrimData(ar)) + "&");// $7
																						// ar:动作动作结果
																						// 0：成功
																						// 1：失败

				sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&");// $8
																	// cid:视频频道ID
																	// 全业务线统一
																	// 和大媒资一致
				sb.append("pid=" + replaceStr(DataUtils.getData(pid)) + "&");// $9 pid:专辑ID
																	// 如果不是专辑 用
																	// - 替代
				sb.append("vid=" + replaceStr(DataUtils.getData(vid)) + "&");// $10 vid:
																	// 视频ID

				sb.append("uid=" + DataUtils.getData(uid) + "&"); // $11
																	// uid,乐视网用户id
				sb.append("uuid=" + DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $12
																							// uuid:(did_timestamp)

				sb.append("lc=-" + "&"); // $13 lc:(letv cookie)
											// 是用来唯一标识用户的，用户即使用不同的浏览器，
											// lc都是相同的，可以参考flash cookie
											// 主要给 PC 端使用，移动、TV 传 -
				sb.append("cur_url=" + URLEncoder.encode(DataUtils.getData(cur_url)) + "&"); // $14
																								// cur_url:当前页面地址

				sb.append("ch=-" + "&");// $15 ch:渠道
				sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $16
																		// pcode

				sb.append("auid=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $17
																									// auid,设备id

				sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $19: ilu是否为登录用户
																// 0：登录 1：非登录
				sb.append("reid=" + System.currentTimeMillis() + "&"); // $19
																		// reid
				// 推荐反馈的随机数
				// String
				// [推荐结果点击时必须上报]推荐点击动作上报时必填
				sb.append("area=-" + "&"); // $20 area
				// 推荐区域标识
				// String
				// 会提供两个特定的区域[推荐点击动作上报时必填]
				sb.append("bucket=-" + "&"); // $21
				// bucket
				// 推荐的算法策略
				// Int
				// 推荐组维护
				// [推荐点击动作上报时必填]
				sb.append("rank=-" + "&"); // $22 rank
				// 点击视频在推荐区域的排序
				// Int
				// 最终要沟通确认
				// [推荐点击动作上报时必填]

				sb.append("r=" + System.currentTimeMillis()); // $20: 随机数

				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendRecommendInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_RECOMMEND_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatRecommendUrl() + sb.toString();
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
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
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {

				StringBuffer sb = new StringBuffer();
				sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
																						// 新版从
																						// 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
																						// p1:一级产品线代码
																						// 为从0开始的数字
				sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
																							// p2:二级产品线代码
																							// 以一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001" + "&");// $4 p3 三级产品线代码 平台自定义

				sb.append("ac=" + DataUtils.getData(ac) + "&");// $5 ac 动作名称
				sb.append("err=" + DataUtils.getData(err) + "&");// $6 err 错误代码
				sb.append("pt=" + DataUtils.getData(pt) + "&");// $7 pt 播放时长
				sb.append("ut=" + DataUtils.getData(ut) + "&");// $8 ut 动作耗时
				sb.append("uid=" + DataUtils.getData(uid) + "&");// $9 uid
																	// 乐视网注册用户ID
				sb.append("lc=-" + "&"); // $10 lc:(letv cookie)
											// 是用来唯一标识用户的，用户即使用不同的浏览器，
											// lc都是相同的，可以参考flash cookie

				sb.append("auid=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $11
																									// auid,设备id
				sb.append("uuid=" + DataUtils.getData(uuidTimeStamp) + "&"); // $12
																				// uuid:(did_timestamp)
				// 开始播放生成的UUID是 uuid_0 用户切换码率之后，UUID变为：uuid_1
				// 但是在播放器第一个动作上报环境信息的时候，不要带最后一个_和数字 [必填]

				sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&");// $13
																	// cid:视频频道ID
																	// 全业务线统一
																	// 和大媒资一致
				sb.append("pid=" + replaceStr(DataUtils.getData(pid)) + "&");// $14 pid:专辑ID
																	// 如果不是专辑 用
																	// - 替代
				sb.append("vid=" + replaceStr(DataUtils.getData(vid)) + "&");// $15 vid:
																	// 视频ID
				sb.append("vlen=" + DataUtils.getData(vlen) + "&");// $16 vlen:
																	// 视频时长
																	// 以秒为单位
				sb.append("ch=" + DataUtils.getData(ch) + "&");// $17 ch:渠道
				sb.append("ry=" + DataUtils.getData(retryCount) + "&");// $18
																		// ry:重试次数，从0开始
																		// 比如
																		// 第一次失败，第二次成功，那么只有在第二次上报，其中ry=1
																		// 如果第一次即成功，那么ry=0
																		// [必填]

				sb.append("ty=" + DataUtils.getData(type) + "&");// $19 ty:点播 0，
																	// 直播 1 ，轮播
																	// 2
				sb.append("vt=" + DataUtils.getData(vt) + "&");// $20
																// vt:播放器的vType
				sb.append("url=" + URLEncoder.encode(DataUtils.getData(url)) + "&");// $21
																					// url:视屏播放地址
				sb.append("ref=" + URLEncoder.encode(DataUtils.getData(ref)) + "&");// $22
																					// ref:播放页来源地址
				sb.append("pv=2.0" + "&");// $23 pv:Player version: 播放器版本

				sb.append("py=" + URLEncoder.encode(DataUtils.getData(DataUtils.getTrimData(py))) + "&");// $24
																											// py:
																											// Property
																											// 播放属性
				sb.append("st=" + URLEncoder.encode(DataUtils.getData(st)) + "&");// $25
																					// st:
																					// Station：轮播台

				sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $26: ilu是否为登录用户
																// 0：登录 1：非登录
				sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $27
																		// pcode
				sb.append("weid=" + DataUtils.getData(weid) + "&");// $28
																	// Webeventid
																	// 上报时获取js生成的页面weid
				sb.append("r=" + System.currentTimeMillis()); // $29: 随机数

				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendPlayInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_PLAYER_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatPlayerUrl() + sb.toString();
//                    android.util.Log.i("king",cacheData);
                    HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
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
	 *              
	 *              2014-04-30添加专辑id  zid字段
	 *              
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
	 * @param zid           
	 * 			  专辑id  String zid
	 */
	public void sendPlayInfo24New(final Context context, final String p1, final String p2, final String ac,
			final String err, final String pt, final String ut, final String uid, final String uuidTimeStamp,
			final String cid, final String pid, final String vid, final String vlen, final String retryCount,
			final String type, final String vt, final String url, final String ref, final String py, final String st,
			final String weid, final String pcode, final int ilu, final String ch,final String zid) {
        android.util.Log.i("king","ac ="+ac+" pt="+pt +"  py = " + py +" ,ref =" +ref+" ,cid =" +cid+" ,ut ="+ut+" ,vid ="+vid);
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {

				StringBuffer sb = new StringBuffer();
				sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
																						// 新版从
																						// 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
																						// p1:一级产品线代码
																						// 为从0开始的数字
				sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
																							// p2:二级产品线代码
																							// 以一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001" + "&");// $4 p3 三级产品线代码 平台自定义

				sb.append("ac=" + DataUtils.getData(ac) + "&");// $5 ac 动作名称
				sb.append("err=" + DataUtils.getData(err) + "&");// $6 err 错误代码
				sb.append("pt=" + DataUtils.getData(pt) + "&");// $7 pt 播放时长
				sb.append("ut=" + DataUtils.getData(ut) + "&");// $8 ut 动作耗时
				sb.append("uid=" + DataUtils.getData(uid) + "&");// $9 uid
																	// 乐视网注册用户ID
				sb.append("lc=-" + "&"); // $10 lc:(letv cookie)
											// 是用来唯一标识用户的，用户即使用不同的浏览器，
											// lc都是相同的，可以参考flash cookie

				sb.append("auid=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $11
																									// auid,设备id
				sb.append("uuid=" + DataUtils.getData(uuidTimeStamp) + "&"); // $12
																				// uuid:(did_timestamp)
				// 开始播放生成的UUID是 uuid_0 用户切换码率之后，UUID变为：uuid_1
				// 但是在播放器第一个动作上报环境信息的时候，不要带最后一个_和数字 [必填]

				sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&");// $13
																	// cid:视频频道ID
																	// 全业务线统一
																	// 和大媒资一致
				sb.append("pid=" + replaceStr(DataUtils.getData(pid)) + "&");// $14 pid:专辑ID
																	// 如果不是专辑 用
																	// - 替代
				sb.append("vid=" + replaceStr(DataUtils.getData(vid)) + "&");// $15 vid:
																	// 视频ID
				sb.append("vlen=" + DataUtils.getData(vlen) + "&");// $16 vlen:
																	// 视频时长
																	// 以秒为单位
				sb.append("ch=" + DataUtils.getData(ch) + "&");// $17 ch:渠道
				sb.append("ry=" + DataUtils.getData(retryCount) + "&");// $18
																		// ry:重试次数，从0开始
																		// 比如
																		// 第一次失败，第二次成功，那么只有在第二次上报，其中ry=1
																		// 如果第一次即成功，那么ry=0
																		// [必填]

				sb.append("ty=" + DataUtils.getData(type) + "&");// $19 ty:点播 0，
																	// 直播 1 ，轮播
																	// 2
				sb.append("vt=" + DataUtils.getData(vt) + "&");// $20
																// vt:播放器的vType
				sb.append("url=" + URLEncoder.encode(DataUtils.getData(url)) + "&");// $21
																					// url:视屏播放地址
				sb.append("ref=" + URLEncoder.encode(DataUtils.getData(ref)) + "&");// $22
																					// ref:播放页来源地址
				sb.append("pv=2.0" + "&");// $23 pv:Player version: 播放器版本

				sb.append("py=" + URLEncoder.encode(DataUtils.getData(DataUtils.getTrimData(py))) + "&");// $24
																											// py:
																											// Property
																											// 播放属性
				sb.append("st=" + URLEncoder.encode(DataUtils.getData(st)) + "&");// $25
																					// st:
																					// Station：轮播台

				sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $26: ilu是否为登录用户
																// 0：登录 1：非登录
				sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $27
																		// pcode
				sb.append("weid=" + DataUtils.getData(weid) + "&");// $28
																	// Webeventid
																	// 上报时获取js生成的页面weid
				sb.append("zid=" + replaceStr(DataUtils.getData(DataUtils.getTrimData(zid))) + "&");
				sb.append("r=" + System.currentTimeMillis()); // $29: 随机数

				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendPlayInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_PLAYER_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatPlayerUrl() + sb.toString();
//                    android.util.Log.i("king",cacheData);
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
	}

    public void sendPlayInfo24New(final Context context, final String p1, final String p2, final String ac,
                                  final String err, final String pt, final String ut, final String uid, final String uuidTimeStamp,
                                  final String cid, final String pid, final String vid, final String vlen, final String retryCount,
                                  final String type, final String vt, final String url, final String ref, final String py, final String st,
                                  final String weid, final String pcode, final int ilu, final String ch,final String zid,final int ap) {
       android.util.Log.i("king","ac = " + ac + "  pt =" +pt + "  py = "+py +" ap ="+ap);
        ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
            @Override
            public void run() {

                StringBuffer sb = new StringBuffer();
                sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
                // 新版从
                // 2.0开始
                sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
                // p1:一级产品线代码
                // 为从0开始的数字
                sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
                // p2:二级产品线代码
                // 以一级产品线代码为前缀，接下来从0开始
                sb.append("p3=001" + "&");// $4 p3 三级产品线代码 平台自定义

                sb.append("ac=" + DataUtils.getData(ac) + "&");// $5 ac 动作名称
                sb.append("err=" + DataUtils.getData(err) + "&");// $6 err 错误代码
                sb.append("pt=" + DataUtils.getData(pt) + "&");// $7 pt 播放时长
                sb.append("ut=" + DataUtils.getData(ut) + "&");// $8 ut 动作耗时
                sb.append("uid=" + DataUtils.getData(uid) + "&");// $9 uid
                // 乐视网注册用户ID
                sb.append("lc=-" + "&"); // $10 lc:(letv cookie)
                // 是用来唯一标识用户的，用户即使用不同的浏览器，
                // lc都是相同的，可以参考flash cookie

                sb.append("auid=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $11
                // auid,设备id
                sb.append("uuid=" + DataUtils.getData(uuidTimeStamp) + "&"); // $12
                // uuid:(did_timestamp)
                // 开始播放生成的UUID是 uuid_0 用户切换码率之后，UUID变为：uuid_1
                // 但是在播放器第一个动作上报环境信息的时候，不要带最后一个_和数字 [必填]

                sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&");// $13
                // cid:视频频道ID
                // 全业务线统一
                // 和大媒资一致
                sb.append("pid=" + replaceStr(DataUtils.getData(pid)) + "&");// $14 pid:专辑ID
                // 如果不是专辑 用
                // - 替代
                sb.append("vid=" + replaceStr(DataUtils.getData(vid)) + "&");// $15 vid:
                // 视频ID
                sb.append("vlen=" + DataUtils.getData(vlen) + "&");// $16 vlen:
                // 视频时长
                // 以秒为单位
                sb.append("ch=" + DataUtils.getData(ch) + "&");// $17 ch:渠道
                sb.append("ry=" + DataUtils.getData(retryCount) + "&");// $18
                // ry:重试次数，从0开始
                // 比如
                // 第一次失败，第二次成功，那么只有在第二次上报，其中ry=1
                // 如果第一次即成功，那么ry=0
                // [必填]

                sb.append("ty=" + DataUtils.getData(type) + "&");// $19 ty:点播 0，
                // 直播 1 ，轮播
                // 2
                sb.append("vt=" + DataUtils.getData(vt) + "&");// $20
                // vt:播放器的vType
                sb.append("url=" + URLEncoder.encode(DataUtils.getData(url)) + "&");// $21
                // url:视屏播放地址
                sb.append("ref=" + URLEncoder.encode(DataUtils.getData(ref)) + "&");// $22
                // ref:播放页来源地址
                sb.append("pv=2.0" + "&");// $23 pv:Player version: 播放器版本

                sb.append("py=" + URLEncoder.encode(DataUtils.getData(DataUtils.getTrimData(py))) + "&");// $24
                // py:
                // Property
                // 播放属性
                sb.append("st=" + URLEncoder.encode(DataUtils.getData(st)) + "&");// $25
                // st:
                // Station：轮播台

                sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $26: ilu是否为登录用户
                sb.append("ap=" + ap + "&"); // $26: ilu是否为登录用户
                // 0：登录 1：非登录
                sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $27
                // pcode
                sb.append("weid=" + DataUtils.getData(weid) + "&");// $28
                // Webeventid
                // 上报时获取js生成的页面weid
                sb.append("zid=" + replaceStr(DataUtils.getData(DataUtils.getTrimData(zid))) + "&");
                sb.append("r=" + System.currentTimeMillis()); // $29: 随机数

                if (DataStatistics.getInstance().isDebug()) {
                    Log.d(DataStatistics.TAG, "sendPlayInfo:" + sb.toString());
                }
                sendLocalTestStatis(context, DataConstant.STAT_LOCAL_PLAYER_URL, sb.toString());
                try {
                    String cacheData = DataStatistics.getInstance().getStatPlayerUrl() + sb.toString();
//                    android.util.Log.i("king",cacheData);
                    HttpEngine.getInstance().doHttpGet(context,
                            new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
                } catch (HttpDataParserException e) {
                    e.printStackTrace();
                } catch (HttpDataConnectionException e) {
                    e.printStackTrace();
                }
            }
        });
    }
	
	
	public void sendPlayInfo(final Context context, final String p1, final String p2, final String ac,
			final String err, final String pt, final String ut, final String uid, final String uuidTimeStamp,
			final String cid, final String pid, final String vid, final String vlen, final String retryCount,
			final String type, final String vt, final String url, final String ref, final String py, final String st,
			final String weid, final String pcode, final int ilu, final String ch, final String lc) {
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {
				
				StringBuffer sb = new StringBuffer();
				sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
				// 新版从
				// 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
				// p1:一级产品线代码
				// 为从0开始的数字
				sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
				// p2:二级产品线代码
				// 以一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001" + "&");// $4 p3 三级产品线代码 平台自定义
				
				sb.append("ac=" + DataUtils.getData(ac) + "&");// $5 ac 动作名称
				sb.append("err=" + DataUtils.getData(err) + "&");// $6 err 错误代码
				sb.append("pt=" + DataUtils.getData(pt) + "&");// $7 pt 播放时长
				sb.append("ut=" + DataUtils.getData(ut) + "&");// $8 ut 动作耗时
				sb.append("uid=" + DataUtils.getData(uid) + "&");// $9 uid
				// 乐视网注册用户ID
				sb.append("lc="+ DataUtils.getData(lc) + "&"); // $10 lc:(letv cookie)
				// 是用来唯一标识用户的，用户即使用不同的浏览器，
				// lc都是相同的，可以参考flash cookie
				
				sb.append("auid=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $11
				// auid,设备id
				sb.append("uuid=" + DataUtils.getData(uuidTimeStamp) + "&"); // $12
				// uuid:(did_timestamp)
				// 开始播放生成的UUID是 uuid_0 用户切换码率之后，UUID变为：uuid_1
				// 但是在播放器第一个动作上报环境信息的时候，不要带最后一个_和数字 [必填]
				
				sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&");// $13
				// cid:视频频道ID
				// 全业务线统一
				// 和大媒资一致
				sb.append("pid=" + replaceStr(DataUtils.getData(pid)) + "&");// $14 pid:专辑ID
				// 如果不是专辑 用
				// - 替代
				sb.append("vid=" + replaceStr(DataUtils.getData(vid)) + "&");// $15 vid:
				// 视频ID
				sb.append("vlen=" + DataUtils.getData(vlen) + "&");// $16 vlen:
				// 视频时长
				// 以秒为单位
				sb.append("ch=" + DataUtils.getData(ch) + "&");// $17 ch:渠道
				sb.append("ry=" + DataUtils.getData(retryCount) + "&");// $18
				// ry:重试次数，从0开始
				// 比如
				// 第一次失败，第二次成功，那么只有在第二次上报，其中ry=1
				// 如果第一次即成功，那么ry=0
				// [必填]
				
				sb.append("ty=" + DataUtils.getData(type) + "&");// $19 ty:点播 0，
				// 直播 1 ，轮播
				// 2
				sb.append("vt=" + DataUtils.getData(vt) + "&");// $20
				// vt:播放器的vType
				sb.append("url=" + URLEncoder.encode(DataUtils.getData(url)) + "&");// $21
				// url:视屏播放地址
				sb.append("ref=" + URLEncoder.encode(DataUtils.getData(ref)) + "&");// $22
				// ref:播放页来源地址
				sb.append("pv=2.0" + "&");// $23 pv:Player version: 播放器版本
				
				sb.append("py=" + URLEncoder.encode(DataUtils.getData(DataUtils.getTrimData(py))) + "&");// $24
				// py:
				// Property
				// 播放属性
				sb.append("st=" + URLEncoder.encode(DataUtils.getData(st)) + "&");// $25
				// st:
				// Station：轮播台
				
				sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $26: ilu是否为登录用户
				// 0：登录 1：非登录
				sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $27
				// pcode
				sb.append("weid=" + DataUtils.getData(weid) + "&");// $28
				// Webeventid
				// 上报时获取js生成的页面weid
				sb.append("r=" + System.currentTimeMillis()); // $29: 随机数
				
				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendPlayInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_PLAYER_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatPlayerUrl() + sb.toString();
//                    android.util.Log.i("king",cacheData);
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void sendPlayInfo25New(final Context context, final String p1, final String p2, final String ac,
			final String err, final String pt, final String ut, final String uid, final String uuidTimeStamp,
			final String cid, final String pid, final String vid, final String vlen, final String retryCount,
			final String type, final String vt, final String url, final String ref, final String py, final String st,
			final String weid, final String pcode, final int ilu, final String ch, final String lc,final String zid) {
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {
				
				StringBuffer sb = new StringBuffer();
				sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
				// 新版从
				// 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
				// p1:一级产品线代码
				// 为从0开始的数字
				sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
				// p2:二级产品线代码
				// 以一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001" + "&");// $4 p3 三级产品线代码 平台自定义
				
				sb.append("ac=" + DataUtils.getData(ac) + "&");// $5 ac 动作名称
				sb.append("err=" + DataUtils.getData(err) + "&");// $6 err 错误代码
				sb.append("pt=" + DataUtils.getData(pt) + "&");// $7 pt 播放时长
				sb.append("ut=" + DataUtils.getData(ut) + "&");// $8 ut 动作耗时
				sb.append("uid=" + DataUtils.getData(uid) + "&");// $9 uid
				// 乐视网注册用户ID
				sb.append("lc="+ DataUtils.getData(lc) + "&"); // $10 lc:(letv cookie)
				// 是用来唯一标识用户的，用户即使用不同的浏览器，
				// lc都是相同的，可以参考flash cookie
				
				sb.append("auid=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $11
				// auid,设备id
				sb.append("uuid=" + DataUtils.getData(uuidTimeStamp) + "&"); // $12
				// uuid:(did_timestamp)
				// 开始播放生成的UUID是 uuid_0 用户切换码率之后，UUID变为：uuid_1
				// 但是在播放器第一个动作上报环境信息的时候，不要带最后一个_和数字 [必填]
				
				sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&");// $13
				// cid:视频频道ID
				// 全业务线统一
				// 和大媒资一致
				sb.append("pid=" + replaceStr(DataUtils.getData(pid)) + "&");// $14 pid:专辑ID
				// 如果不是专辑 用
				// - 替代
				sb.append("vid=" + replaceStr(DataUtils.getData(vid)) + "&");// $15 vid:
				// 视频ID
				sb.append("vlen=" + DataUtils.getData(vlen) + "&");// $16 vlen:
				// 视频时长
				// 以秒为单位
				sb.append("ch=" + DataUtils.getData(ch) + "&");// $17 ch:渠道
				sb.append("ry=" + DataUtils.getData(retryCount) + "&");// $18
				// ry:重试次数，从0开始
				// 比如
				// 第一次失败，第二次成功，那么只有在第二次上报，其中ry=1
				// 如果第一次即成功，那么ry=0
				// [必填]
				
				sb.append("ty=" + DataUtils.getData(type) + "&");// $19 ty:点播 0，
				// 直播 1 ，轮播
				// 2
				sb.append("vt=" + DataUtils.getData(vt) + "&");// $20
				// vt:播放器的vType
				sb.append("url=" + URLEncoder.encode(DataUtils.getData(url)) + "&");// $21
				// url:视屏播放地址
				sb.append("ref=" + URLEncoder.encode(DataUtils.getData(ref)) + "&");// $22
				// ref:播放页来源地址
				sb.append("pv=2.0" + "&");// $23 pv:Player version: 播放器版本
				
				sb.append("py=" + URLEncoder.encode(DataUtils.getData(DataUtils.getTrimData(py))) + "&");// $24
				// py:
				// Property
				// 播放属性
				sb.append("st=" + URLEncoder.encode(DataUtils.getData(st)) + "&");// $25
				// st:
				// Station：轮播台
				
				sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $26: ilu是否为登录用户
				// 0：登录 1：非登录
				sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $27
				// pcode
				sb.append("weid=" + DataUtils.getData(weid) + "&");// $28
				// Webeventid
				// 上报时获取js生成的页面weid
				sb.append("zid=" + replaceStr(DataUtils.getData(DataUtils.getTrimData(zid))) + "&");
				sb.append("r=" + System.currentTimeMillis()); // $29: 随机数
				
				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendPlayInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_PLAYER_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatPlayerUrl() + sb.toString();
//                    android.util.Log.i("king",cacheData);
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
	}

    /**
     * 增加lid
     * */
    public void sendPlayInfo25New(final Context context, final String p1, final String p2, final String ac,
                                  final String err, final String pt, final String ut, final String uid, final String uuidTimeStamp,
                                  final String cid, final String pid, final String vid, final String vlen, final String retryCount,
                                  final String type, final String vt, final String url, final String ref, final String py, final String st,
                                  final String weid, final String pcode, final int ilu, final String ch, final String lc,final String zid,final String lid) {
        android.util.Log.i("king","acode="+ac+"  ,py="+py+"  ,lid="+lid + " ,ref = "+ref +" ,pt="+pt+" ,cid = "+cid+" ,vt = "+vt);
        ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
            @Override
            public void run() {

                StringBuffer sb = new StringBuffer();
                sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
                // 新版从
                // 2.0开始
                sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
                // p1:一级产品线代码
                // 为从0开始的数字
                sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
                // p2:二级产品线代码
                // 以一级产品线代码为前缀，接下来从0开始
                sb.append("p3=001" + "&");// $4 p3 三级产品线代码 平台自定义

                sb.append("ac=" + DataUtils.getData(ac) + "&");// $5 ac 动作名称
                sb.append("err=" + DataUtils.getData(err) + "&");// $6 err 错误代码
                sb.append("pt=" + DataUtils.getData(pt) + "&");// $7 pt 播放时长
                sb.append("ut=" + DataUtils.getData(ut) + "&");// $8 ut 动作耗时
                sb.append("uid=" + DataUtils.getData(uid) + "&");// $9 uid
                // 乐视网注册用户ID
                sb.append("lc="+ DataUtils.getData(lc) + "&"); // $10 lc:(letv cookie)
                // 是用来唯一标识用户的，用户即使用不同的浏览器，
                // lc都是相同的，可以参考flash cookie

                sb.append("auid=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $11
                // auid,设备id
                sb.append("uuid=" + DataUtils.getData(uuidTimeStamp) + "&"); // $12
                // uuid:(did_timestamp)
                // 开始播放生成的UUID是 uuid_0 用户切换码率之后，UUID变为：uuid_1
                // 但是在播放器第一个动作上报环境信息的时候，不要带最后一个_和数字 [必填]

                sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&");// $13
                // cid:视频频道ID
                // 全业务线统一
                // 和大媒资一致
                sb.append("pid=" + replaceStr(DataUtils.getData(pid)) + "&");// $14 pid:专辑ID
                // 如果不是专辑 用
                // - 替代
                sb.append("vid=" + replaceStr(DataUtils.getData(vid)) + "&");// $15 vid:
                // 视频ID
                sb.append("vlen=" + DataUtils.getData(vlen) + "&");// $16 vlen:
                // 视频时长
                // 以秒为单位
                sb.append("ch=" + DataUtils.getData(ch) + "&");// $17 ch:渠道
                sb.append("ry=" + DataUtils.getData(retryCount) + "&");// $18
                // ry:重试次数，从0开始
                // 比如
                // 第一次失败，第二次成功，那么只有在第二次上报，其中ry=1
                // 如果第一次即成功，那么ry=0
                // [必填]

                sb.append("ty=" + DataUtils.getData(type) + "&");// $19 ty:点播 0，
                // 直播 1 ，轮播
                // 2
                sb.append("vt=" + DataUtils.getData(vt) + "&");// $20
                // vt:播放器的vType
                sb.append("url=" + URLEncoder.encode(DataUtils.getData(url)) + "&");// $21
                // url:视屏播放地址
                sb.append("ref=" + URLEncoder.encode(DataUtils.getData(ref)) + "&");// $22
                // ref:播放页来源地址
                sb.append("pv=2.0" + "&");// $23 pv:Player version: 播放器版本

                sb.append("py=" + URLEncoder.encode(DataUtils.getData(DataUtils.getTrimData(py))) + "&");// $24
                // py:
                // Property
                // 播放属性
                sb.append("st=" + URLEncoder.encode(DataUtils.getData(st)) + "&");// $25
                // st:
                // Station：轮播台

                sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $26: ilu是否为登录用户
                // 0：登录 1：非登录
                sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $27
                // pcode
                sb.append("weid=" + DataUtils.getData(weid) + "&");// $28

                sb.append("lid=" + replaceStr(DataUtils.getData(lid)) + "&");
                // Webeventid
                // 上报时获取js生成的页面weid
                sb.append("zid=" + replaceStr(DataUtils.getData(DataUtils.getTrimData(zid))) + "&");
                sb.append("r=" + System.currentTimeMillis()); // $29: 随机数

                if (DataStatistics.getInstance().isDebug()) {
                    Log.d(DataStatistics.TAG, "sendPlayInfo:" + sb.toString());
                }
                sendLocalTestStatis(context, DataConstant.STAT_LOCAL_PLAYER_URL, sb.toString());
                try {
                    String cacheData = DataStatistics.getInstance().getStatPlayerUrl() + sb.toString();
//                    android.util.Log.i("king",cacheData);
                    HttpEngine.getInstance().doHttpGet(context,
                            new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
                } catch (HttpDataParserException e) {
                    e.printStackTrace();
                } catch (HttpDataConnectionException e) {
                    e.printStackTrace();
                }
            }
        });
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
			final String uid, final String cur_url, final String area, final String bucket, final String rank,
			final int ilu) {
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {

				StringBuffer sb = new StringBuffer();
				sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
																						// 新版从
																						// 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
																						// p1:一级产品线代码
																						// 为从0开始的数字
				sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
																							// p2:二级产品线代码
																							// 已一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001" + "&");// $4 p3 三级产品线代码 平台自定义
				sb.append("acode=" + acode + "&");// $5 acode:动作码
				sb.append("ap=" + URLEncoder.encode(DataUtils.getData(DataUtils.getTrimData(ap))) + "&");// $6
																											// ap:动作属性
																											// ap=URLEncoder.encode(fl=1&wz=1&name=91助手)

				sb.append("ar=" + DataUtils.getData(DataUtils.getTrimData(ar)) + "&");// $7
																						// ar:动作动作结果
																						// 0：成功
																						// 1：失败

				sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&");// $8
																	// cid:视频频道ID
																	// 全业务线统一
																	// 和大媒资一致
				sb.append("pid=" + replaceStr(DataUtils.getData(pid)) + "&");// $9 pid:专辑ID
																	// 如果不是专辑 用
																	// - 替代
				sb.append("vid=" + replaceStr(DataUtils.getData(vid)) + "&");// $10 vid:
																	// 视频ID

				sb.append("uid=" + DataUtils.getData(uid) + "&"); // $11
																	// uid,乐视网用户id
				sb.append("uuid=" + DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $12
																							// uuid:(did_timestamp)

				sb.append("lc=-" + "&"); // $13 lc:(letv cookie)
											// 是用来唯一标识用户的，用户即使用不同的浏览器，
											// lc都是相同的，可以参考flash cookie
											// 主要给 PC 端使用，移动、TV 传 -
				sb.append("cur_url=" + URLEncoder.encode(DataUtils.getData(cur_url)) + "&"); // $14
																								// cur_url:当前页面地址

				sb.append("ch=-" + "&");// $15 ch:渠道
				sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $16
																		// pcode

				sb.append("auid=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $17
                // auid,设备id

                if(ilu==-1){
                    sb.append("ilu=-"+ "&");
                }else{
                    sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $18: ilu是否为登录用户
                    // 0：登录 1：非登录
                }

				sb.append("reid=" + System.currentTimeMillis() + "&"); // $19
																		// reid
																		// 推荐反馈的随机数
																		// String
																		// [推荐结果点击时必须上报]推荐点击动作上报时必填
				sb.append("area=" + DataUtils.getData(area) + "&"); // $20 area
																	// 推荐区域标识
																	// String
																	// 会提供两个特定的区域[推荐点击动作上报时必填]
				sb.append("bucket=" + DataUtils.getData(bucket) + "&"); // $21
																		// bucket
																		// 推荐的算法策略
																		// Int
																		// 推荐组维护
																		// [推荐点击动作上报时必填]
				sb.append("rank=" + DataUtils.getData(rank) + "&"); // $22 rank
																	// 点击视频在推荐区域的排序
																	// Int
																	// 最终要沟通确认
																	// [推荐点击动作上报时必填]

				sb.append("r=" + System.currentTimeMillis()); // $23: 随机数

				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendActionInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_ACTION_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatActionUrl() + sb.toString();
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * @category说明： Action 字段名应该严格遵守，不能随意自己增加字段名，上报采用key-value格式
	 *              动作日志，记录的是用户的行为，比如：下载，评论，共享，收藏 所有业务线使用的日志格式一样，使用业务线代码进行区分
	 *              动作包括： 点击，评论，下载，收藏，分享，充值，缴费，后续可以继续扩充
	 *              动作码由数据部维护，不可以随意添加动作码，要向数据部申请 搜索、播放不在动作日志范畴，有独立的query
	 *              log和play log key和value的值都不允许包含 &
	 *              符号，如果有包含，要对key/value值进行URL编码 上报地址：http://dc.letv.com/op/?
	 *              
	 *             2014年4月30日增加
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
	 * 			  专辑id 2014-04-30 添加
	 */
	public void sendActionInfo(final Context context, final String p1, final String p2, final String pcode,
			final String acode, final String ap, final String ar, final String cid, final String pid, final String vid,
			final String uid, final String cur_url, final String area, final String bucket, final String rank,
			final int ilu,final String zid) {
       android.util.Log.i("king","acode = " + acode +" ap ="+ap + " ,vid="+vid + " ,cid="+cid + " ,pid="+pid);
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {

				StringBuffer sb = new StringBuffer();
				sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
																						// 新版从
																						// 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
																						// p1:一级产品线代码
																						// 为从0开始的数字
				sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
																							// p2:二级产品线代码
																							// 已一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001&");    //p3
				
				sb.append("acode=" + acode + "&");// $5 acode:动作码
				sb.append("ap=" + URLEncoder.encode(DataUtils.getData(DataUtils.getTrimData(ap))) + "&");// $6
																											// ap:动作属性
																											// ap=URLEncoder.encode(fl=1&wz=1&name=91助手)

				sb.append("ar=" + DataUtils.getData(DataUtils.getTrimData(ar)) + "&");// $7
																						// ar:动作动作结果
																						// 0：成功
																						// 1：失败

				sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&");// $8
																	// cid:视频频道ID
																	// 全业务线统一
																	// 和大媒资一致
				sb.append("pid=" + replaceStr(DataUtils.getData(pid)) + "&");// $9 pid:专辑ID
																	// 如果不是专辑 用
																	// - 替代
				sb.append("vid=" + replaceStr(DataUtils.getData(vid)) + "&");// $10 vid:
																	// 视频ID

				sb.append("uid=" + DataUtils.getData(uid) + "&"); // $11
																	// uid,乐视网用户id
				sb.append("uuid=" + DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $12
																							// uuid:(did_timestamp)

				sb.append("lc=-" + "&"); // $13 lc:(letv cookie)
											// 是用来唯一标识用户的，用户即使用不同的浏览器，
											// lc都是相同的，可以参考flash cookie
											// 主要给 PC 端使用，移动、TV 传 -
				sb.append("cur_url=" + URLEncoder.encode(DataUtils.getData(cur_url)) + "&"); // $14
																								// cur_url:当前页面地址

				sb.append("ch=-" + "&");// $15 ch:渠道
				sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $16
																		// pcode

				sb.append("auid=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $17

                if(ilu==-1){
                    sb.append("ilu=-"+ "&");
                }else{																	// auid,设备id
				    sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $18: ilu是否为登录用户
																// 0：登录 1：非登录
				}

				sb.append("reid=" + System.currentTimeMillis() + "&"); // $19
																		// reid
																		// 推荐反馈的随机数
																		// String
																		// [推荐结果点击时必须上报]推荐点击动作上报时必填
				sb.append("area=" + DataUtils.getData(area) + "&"); // $20 area
																	// 推荐区域标识
																	// String
																	// 会提供两个特定的区域[推荐点击动作上报时必填]
				sb.append("bucket=" + DataUtils.getData(bucket) + "&"); // $21
																		// bucket
																		// 推荐的算法策略
																		// Int
																		// 推荐组维护
																		// [推荐点击动作上报时必填]
				sb.append("rank=" + DataUtils.getData(rank) + "&"); // $22 rank
																	// 点击视频在推荐区域的排序
																	// Int
																	// 最终要沟通确认
																	// [推荐点击动作上报时必填]
				sb.append("zid=" + replaceStr(DataUtils.getData(DataUtils.getTrimData(zid))) + "&");
				sb.append("r=" + System.currentTimeMillis()); // $23: 随机数

				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendActionInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_ACTION_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatActionUrl() + sb.toString();
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * Add 直播id lid
	 * 			by glh
	 */
	public void sendActionInfoAddLid(final Context context, final String p1, final String p2, final String pcode,
			final String acode, final String ap, final String ar, final String cid, final String pid, final String vid,
			final String uid, final String cur_url, final String area, final String bucket, final String rank,
			final int ilu,final String zid,final String lid) {
        android.util.Log.i("king","acode = " + acode +" ap ="+ap + " ,vid="+vid + " ,cid="+cid + " ,pid="+pid + " ,lid="+lid);
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {

				StringBuffer sb = new StringBuffer();
				sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
																						// 新版从
																						// 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
																						// p1:一级产品线代码
																						// 为从0开始的数字
				sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
																							// p2:二级产品线代码
																							// 已一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001&");    //p3
				
				sb.append("acode=" + acode + "&");// $5 acode:动作码
				sb.append("ap=" + URLEncoder.encode(DataUtils.getData(DataUtils.getTrimData(ap))) + "&");// $6
																											// ap:动作属性
																											// ap=URLEncoder.encode(fl=1&wz=1&name=91助手)

				sb.append("ar=" + DataUtils.getData(DataUtils.getTrimData(ar)) + "&");// $7
																						// ar:动作动作结果
																						// 0：成功
																						// 1：失败

				sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&");// $8
																	// cid:视频频道ID
																	// 全业务线统一
																	// 和大媒资一致
				sb.append("pid=" + replaceStr(DataUtils.getData(pid)) + "&");// $9 pid:专辑ID
																	// 如果不是专辑 用
																	// - 替代
				sb.append("vid=" + replaceStr(DataUtils.getData(vid)) + "&");// $10 vid:
																	// 视频ID

				sb.append("uid=" + DataUtils.getData(uid) + "&"); // $11
																	// uid,乐视网用户id
				sb.append("uuid=" + DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $12
																							// uuid:(did_timestamp)

				sb.append("lc=-" + "&"); // $13 lc:(letv cookie)
											// 是用来唯一标识用户的，用户即使用不同的浏览器，
											// lc都是相同的，可以参考flash cookie
											// 主要给 PC 端使用，移动、TV 传 -
				sb.append("cur_url=" + URLEncoder.encode(DataUtils.getData(cur_url)) + "&"); // $14
																								// cur_url:当前页面地址

				sb.append("ch=-" + "&");// $15 ch:渠道
				sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $16
																		// pcode

				sb.append("auid=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $17

                if(ilu==-1){
                    sb.append("ilu=-"+ "&");
                }else{																	// auid,设备id
				    sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $18: ilu是否为登录用户
																// 0：登录 1：非登录
				}

				sb.append("reid=" + System.currentTimeMillis() + "&"); // $19
																		// reid
																		// 推荐反馈的随机数
																		// String
																		// [推荐结果点击时必须上报]推荐点击动作上报时必填
				sb.append("area=" + DataUtils.getData(area) + "&"); // $20 area
																	// 推荐区域标识
																	// String
																	// 会提供两个特定的区域[推荐点击动作上报时必填]
				sb.append("bucket=" + DataUtils.getData(bucket) + "&"); // $21
																		// bucket
																		// 推荐的算法策略
																		// Int
																		// 推荐组维护
																		// [推荐点击动作上报时必填]
				sb.append("rank=" + DataUtils.getData(rank) + "&"); // $22 rank
																	// 点击视频在推荐区域的排序
																	// Int
																	// 最终要沟通确认
																	// [推荐点击动作上报时必填]
				sb.append("zid=" + replaceStr(DataUtils.getData(DataUtils.getTrimData(zid))) + "&");
				 sb.append("lid=" + DataUtils.getData(DataUtils.getTrimData(lid)) + "&");
				sb.append("r=" + System.currentTimeMillis()); // $23: 随机数

				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendActionInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_ACTION_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatActionUrl() + sb.toString();
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * @category说明： Action 字段名应该严格遵守，不能随意自己增加字段名，上报采用key-value格式
	 *              动作日志，记录的是用户的行为，比如：下载，评论，共享，收藏 所有业务线使用的日志格式一样，使用业务线代码进行区分
	 *              动作包括： 点击，评论，下载，收藏，分享，充值，缴费，后续可以继续扩充
	 *              动作码由数据部维护，不可以随意添加动作码，要向数据部申请 搜索、播放不在动作日志范畴，有独立的query
	 *              log和play log key和value的值都不允许包含 &
	 *              符号，如果有包含，要对key/value值进行URL编码 上报地址：http://dc.letv.com/op/?
	 *              
	 *             2014年4月30日增加
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
	 * 			  专辑id 2014-04-30 添加
	 * @param ref
	 * 			5.5版本新增加的字段
	 */
	public void sendActionInforef(final Context context, final String p1, final String p2, final String pcode,
			final String acode, final String ap, final String ar, final String cid, final String pid, final String vid,
			final String uid, final String cur_url, final String area, final String bucket, final String rank,
			final int ilu,final String zid) {
        android.util.Log.i("king","acode = " + acode +" ap ="+ap + " ,vid="+vid + " ,cid="+cid);
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {

				StringBuffer sb = new StringBuffer();
				sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
																						// 新版从
																						// 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
																						// p1:一级产品线代码
																						// 为从0开始的数字
				sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
																							// p2:二级产品线代码
																							// 已一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001&");    //p3
				
				sb.append("acode=" + acode + "&");// $5 acode:动作码
				sb.append("ap=" + URLEncoder.encode(DataUtils.getData(DataUtils.getTrimData(ap))) + "&");// $6
																											// ap:动作属性
																											// ap=URLEncoder.encode(fl=1&wz=1&name=91助手)

				sb.append("ar=" + DataUtils.getData(DataUtils.getTrimData(ar)) + "&");// $7
																						// ar:动作动作结果
																						// 0：成功
																						// 1：失败

				sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&");// $8
																	// cid:视频频道ID
																	// 全业务线统一
																	// 和大媒资一致
				sb.append("pid=" + replaceStr(DataUtils.getData(pid)) + "&");// $9 pid:专辑ID
																	// 如果不是专辑 用
																	// - 替代
				sb.append("vid=" + replaceStr(DataUtils.getData(vid)) + "&");// $10 vid:
																	// 视频ID

				sb.append("uid=" + DataUtils.getData(uid) + "&"); // $11
																	// uid,乐视网用户id
				sb.append("uuid=" + DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $12
																							// uuid:(did_timestamp)

				sb.append("lc=-" + "&"); // $13 lc:(letv cookie)
											// 是用来唯一标识用户的，用户即使用不同的浏览器，
											// lc都是相同的，可以参考flash cookie
											// 主要给 PC 端使用，移动、TV 传 -
				sb.append("cur_url=" + URLEncoder.encode(DataUtils.getData(cur_url)) + "&"); // $14
																								// cur_url:当前页面地址

				sb.append("ch=-" + "&");// $15 ch:渠道
				sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $16
																		// pcode

				sb.append("auid=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $17

                if(ilu==-1){
                    sb.append("ilu=-"+ "&");
                }else{																	// auid,设备id
				    sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $18: ilu是否为登录用户
																// 0：登录 1：非登录
				}

				sb.append("reid=" + System.currentTimeMillis() + "&"); // $19
																		// reid
																		// 推荐反馈的随机数
																		// String
																		// [推荐结果点击时必须上报]推荐点击动作上报时必填
				sb.append("area=" + DataUtils.getData(area) + "&"); // $20 area
																	// 推荐区域标识
																	// String
																	// 会提供两个特定的区域[推荐点击动作上报时必填]
				sb.append("bucket=" + DataUtils.getData(bucket) + "&"); // $21
																		// bucket
																		// 推荐的算法策略
																		// Int
																		// 推荐组维护
																		// [推荐点击动作上报时必填]
				sb.append("rank=" + DataUtils.getData(rank) + "&"); // $22 rank
																	// 点击视频在推荐区域的排序
																	// Int
																	// 最终要沟通确认
																	// [推荐点击动作上报时必填]
				sb.append("zid=" + replaceStr(DataUtils.getData(DataUtils.getTrimData(zid))) + "&");
				sb.append("r=" + System.currentTimeMillis()); // $23: 随机数

				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendActionInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_ACTION_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatActionUrl() + sb.toString();
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * 在上面的方法的基础上增加time字段
	 */
	public void sendActionInfotime(final Context context, final String p1, final String p2, final String pcode,
			final String acode, final String ap, final String ar, final String cid, final String pid, final String vid,
			final String uid, final String cur_url, final String area, final String bucket, final String rank,
			final int ilu,final String zid,final String time) {
       android.util.Log.i("king","acode = " + acode +" ap ="+ap + " ,vid="+vid + " ,cid="+cid +" ,time="+time);
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {

				StringBuffer sb = new StringBuffer();
				sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
																						// 新版从
																						// 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
																						// p1:一级产品线代码
																						// 为从0开始的数字
				sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
																							// p2:二级产品线代码
																							// 已一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001&");    //p3
				
				sb.append("acode=" + acode + "&");// $5 acode:动作码
				sb.append("ap=" + URLEncoder.encode(DataUtils.getData(DataUtils.getTrimData(ap))) + "&");// $6
																											// ap:动作属性
																											// ap=URLEncoder.encode(fl=1&wz=1&name=91助手)

				sb.append("ar=" + DataUtils.getData(DataUtils.getTrimData(ar)) + "&");// $7
																						// ar:动作动作结果
																						// 0：成功
																						// 1：失败

				sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&");// $8
																	// cid:视频频道ID
																	// 全业务线统一
																	// 和大媒资一致
				sb.append("pid=" + replaceStr(DataUtils.getData(pid)) + "&");// $9 pid:专辑ID
																	// 如果不是专辑 用
																	// - 替代
				sb.append("vid=" + replaceStr(DataUtils.getData(vid)) + "&");// $10 vid:
																	// 视频ID

				sb.append("uid=" + DataUtils.getData(uid) + "&"); // $11
																	// uid,乐视网用户id
				sb.append("uuid=" + DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $12
																							// uuid:(did_timestamp)

				sb.append("lc=-" + "&"); // $13 lc:(letv cookie)
											// 是用来唯一标识用户的，用户即使用不同的浏览器，
											// lc都是相同的，可以参考flash cookie
											// 主要给 PC 端使用，移动、TV 传 -
				sb.append("cur_url=" + URLEncoder.encode(DataUtils.getData(cur_url)) + "&"); // $14
																								// cur_url:当前页面地址

				sb.append("ch=-" + "&");// $15 ch:渠道
				sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $16
																		// pcode

				sb.append("auid=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $17

                if(ilu==-1){
                    sb.append("ilu=-"+ "&");
                }else{																	// auid,设备id
				    sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $18: ilu是否为登录用户
																// 0：登录 1：非登录
				}

				sb.append("reid=" + System.currentTimeMillis() + "&"); // $19
																		// reid
																		// 推荐反馈的随机数
																		// String
																		// [推荐结果点击时必须上报]推荐点击动作上报时必填
				sb.append("area=" + DataUtils.getData(area) + "&"); // $20 area
																	// 推荐区域标识
																	// String
																	// 会提供两个特定的区域[推荐点击动作上报时必填]
				sb.append("bucket=" + DataUtils.getData(bucket) + "&"); // $21
																		// bucket
																		// 推荐的算法策略
																		// Int
																		// 推荐组维护
																		// [推荐点击动作上报时必填]
				sb.append("rank=" + DataUtils.getData(rank) + "&"); // $22 rank
																	// 点击视频在推荐区域的排序
																	// Int
																	// 最终要沟通确认
																	// [推荐点击动作上报时必填]
				sb.append("zid=" + replaceStr(DataUtils.getData(DataUtils.getTrimData(zid))) + "&");
				sb.append("time" + DataUtils.timeClockString("yyyyMMdd_hh:mm:ss") + "&");
				sb.append("r=" + System.currentTimeMillis()); // $23: 随机数

				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendActionInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_ACTION_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatActionUrl() + sb.toString();
					Log.i("glh", cacheData);
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
	}
    /**
     * @category说明： Action 字段名应该严格遵守，不能随意自己增加字段名，上报采用key-value格式
     *              动作日志，记录的是用户的行为，比如：下载，评论，共享，收藏 所有业务线使用的日志格式一样，使用业务线代码进行区分
     *              动作包括： 点击，评论，下载，收藏，分享，充值，缴费，后续可以继续扩充
     *              动作码由数据部维护，不可以随意添加动作码，要向数据部申请 搜索、播放不在动作日志范畴，有独立的query
     *              log和play log key和value的值都不允许包含 &
     *              符号，如果有包含，要对key/value值进行URL编码 上报地址：http://dc.letv.com/op/?
     *
     *             2014年4月30日增加
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
    public void sendActionInfoBigData(final Context context, final String p1, final String p2, final String pcode,
                               final String acode, final String ap, final String ar, final String cid, final String pid, final String vid,
                               final String uid, final String reid, final String area, final String bucket, final String rank,
                               final String cms_num,final int ilu) {
    	  android.util.Log.i("king","acode="+acode+",ap="+ap);
        ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
            @Override
            public void run() {

                StringBuffer sb = new StringBuffer();
                sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
                // 新版从
                // 2.0开始
                sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
                // p1:一级产品线代码
                // 为从0开始的数字
                sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
                // p2:二级产品线代码
                // 已一级产品线代码为前缀，接下来从0开始
                sb.append("p3=001&");    //p3

                sb.append("acode=" + acode + "&");// $5 acode:动作码
                sb.append("ap=" + URLEncoder.encode(DataUtils.getData(DataUtils.getTrimData(ap))) + "&");// $6
                // ap:动作属性
                // ap=URLEncoder.encode(fl=1&wz=1&name=91助手)

                sb.append("ar=" + DataUtils.getData(DataUtils.getTrimData(ar)) + "&");// $7
                // ar:动作动作结果
                // 0：成功
                // 1：失败

                sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&");// $8
                // cid:视频频道ID
                // 全业务线统一
                // 和大媒资一致
                sb.append("pid=" + replaceStr(DataUtils.getData(pid)) + "&");// $9 pid:专辑ID
                // 如果不是专辑 用
                // - 替代
                sb.append("vid=" + replaceStr(DataUtils.getData(vid)) + "&");// $10 vid:
                // 视频ID

                sb.append("uid=" + DataUtils.getData(uid) + "&"); // $11
                // uid,乐视网用户id
                sb.append("uuid=" + DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $12
                // uuid:(did_timestamp)

                sb.append("lc=-" + "&"); // $13 lc:(letv cookie)
                sb.append("ch=-" + "&");// $15 ch:渠道
                sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $16
                // pcode

                sb.append("auid=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $17
                // auid,设备id
                sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $18: ilu是否为登录用户
                // 0：登录 1：非登录

                sb.append("reid=" + System.currentTimeMillis() + "&"); // $19
                // reid
                // 推荐反馈的随机数
                // String
                // [推荐结果点击时必须上报]推荐点击动作上报时必填
                sb.append("area=" + DataUtils.getData(area) + "&"); // $20 area
                // 推荐区域标识
                // String
                // 会提供两个特定的区域[推荐点击动作上报时必填]
                sb.append("reid=" + DataUtils.getData(reid) + "&");
                sb.append("cms_num=" + DataUtils.getData(cms_num) + "&");

                sb.append("bucket=" + DataUtils.getData(bucket) + "&"); // $21
                // bucket
                // 推荐的算法策略
                // Int
                // 推荐组维护
                // [推荐点击动作上报时必填]
                sb.append("rank=" + DataUtils.getData(rank) + "&"); // $22 rank
                // 点击视频在推荐区域的排序
                // Int
                // 最终要沟通确认
                // [推荐点击动作上报时必填]
                sb.append("r=" + System.currentTimeMillis()); // $23: 随机数

                if (DataStatistics.getInstance().isDebug()) {
                    Log.d(DataStatistics.TAG, "sendActionInfo:" + sb.toString());
                }
                sendLocalTestStatis(context, DataConstant.STAT_LOCAL_ACTION_URL, sb.toString());
                try {
                    String cacheData = DataStatistics.getInstance().getStatActionUrl() + sb.toString();
                    Log.i("glh",cacheData );//检查推荐反应的问题area
                    HttpEngine.getInstance().doHttpGet(context,
                            new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
                } catch (HttpDataParserException e) {
                    e.printStackTrace();
                } catch (HttpDataConnectionException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * @category说明： Action 字段名应该严格遵守，不能随意自己增加字段名，上报采用key-value格式
     *              动作日志，记录的是用户的行为，比如：下载，评论，共享，收藏 所有业务线使用的日志格式一样，使用业务线代码进行区分
     *              动作包括： 点击，评论，下载，收藏，分享，充值，缴费，后续可以继续扩充
     *              动作码由数据部维护，不可以随意添加动作码，要向数据部申请 搜索、播放不在动作日志范畴，有独立的query
     *              log和play log key和value的值都不允许包含 &
     *              符号，如果有包含，要对key/value值进行URL编码 上报地址：http://dc.letv.com/op/?
     *
     *             2014年4月30日增加
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
     * 			  专辑id 2014-04-30 添加
     * @param lid
     *            直播id 2041-06-03 添加
     */
    public void sendActionInfo(final Context context, final String p1, final String p2, final String pcode,
                               final String acode, final String ap, final String ar, final String cid, final String pid, final String vid,
                               final String uid, final String cur_url, final String area, final String bucket, final String rank,
                               final int ilu,final String zid,final String lid) {
    	android.util.Log.i("king","acode = " + acode +" ap ="+ap + " ,vid="+vid + " ,cid="+cid +" ,lid="+lid);
        ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
            @Override
            public void run() {

                StringBuffer sb = new StringBuffer();
                sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
                // 新版从
                // 2.0开始
                sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
                // p1:一级产品线代码
                // 为从0开始的数字
                sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
                // p2:二级产品线代码
                // 已一级产品线代码为前缀，接下来从0开始
                sb.append("p3=001&");    //p3

                sb.append("acode=" + acode + "&");// $5 acode:动作码
                sb.append("ap=" + URLEncoder.encode(DataUtils.getData(DataUtils.getTrimData(ap))) + "&");// $6
                // ap:动作属性
                // ap=URLEncoder.encode(fl=1&wz=1&name=91助手)

                sb.append("ar=" + DataUtils.getData(DataUtils.getTrimData(ar)) + "&");// $7
                // ar:动作动作结果
                // 0：成功
                // 1：失败

                sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&");// $8
                // cid:视频频道ID
                // 全业务线统一
                // 和大媒资一致
                sb.append("pid=" + replaceStr(DataUtils.getData(pid)) + "&");// $9 pid:专辑ID
                // 如果不是专辑 用
                // - 替代
                sb.append("vid=" + replaceStr(DataUtils.getData(vid)) + "&");// $10 vid:
                // 视频ID

                sb.append("uid=" + DataUtils.getData(uid) + "&"); // $11
                // uid,乐视网用户id
                sb.append("uuid=" + DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $12
                // uuid:(did_timestamp)

                sb.append("lc=-" + "&"); // $13 lc:(letv cookie)
                // 是用来唯一标识用户的，用户即使用不同的浏览器，
                // lc都是相同的，可以参考flash cookie
                // 主要给 PC 端使用，移动、TV 传 -
                sb.append("cur_url=" + URLEncoder.encode(DataUtils.getData(cur_url)) + "&"); // $14
                // cur_url:当前页面地址

                sb.append("ch=-" + "&");// $15 ch:渠道
                sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $16
                // pcode

                sb.append("auid=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $17
                // auid,设备id

                if(ilu==-1){
                    sb.append("ilu=-"+ "&");
                }else{
                    sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $18: ilu是否为登录用户
                    // 0：登录 1：非登录
                }

                sb.append("reid=" + System.currentTimeMillis() + "&"); // $19
                // reid
                // 推荐反馈的随机数
                // String
                // [推荐结果点击时必须上报]推荐点击动作上报时必填
                sb.append("area=" + DataUtils.getData(area) + "&"); // $20 area
                // 推荐区域标识
                // String
                // 会提供两个特定的区域[推荐点击动作上报时必填]
                sb.append("bucket=" + DataUtils.getData(bucket) + "&"); // $21
                // bucket
                // 推荐的算法策略
                // Int
                // 推荐组维护
                // [推荐点击动作上报时必填]
                sb.append("rank=" + DataUtils.getData(rank) + "&"); // $22 rank
                // 点击视频在推荐区域的排序
                // Int
                // 最终要沟通确认
                // [推荐点击动作上报时必填]
                sb.append("zid=" + replaceStr(DataUtils.getData(DataUtils.getTrimData(zid))) + "&");
                sb.append("lid=" + DataUtils.getData(DataUtils.getTrimData(lid)) + "&"); // $23: 随机数
                sb.append("r=" + System.currentTimeMillis()+"&"); // $23: 随机数
               
                if (DataStatistics.getInstance().isDebug()) {
                    Log.d(DataStatistics.TAG, "sendActionInfo:" + sb.toString());
                }
                sendLocalTestStatis(context, DataConstant.STAT_LOCAL_ACTION_URL, sb.toString());
                try {
                    String cacheData = DataStatistics.getInstance().getStatActionUrl() + sb.toString();
                    HttpEngine.getInstance().doHttpGet(context,
                            new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
                } catch (HttpDataParserException e) {
                    e.printStackTrace();
                } catch (HttpDataConnectionException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * @category说明： Action 字段名应该严格遵守，不能随意自己增加字段名，上报采用key-value格式
     *              动作日志，记录的是用户的行为，比如：下载，评论，共享，收藏 所有业务线使用的日志格式一样，使用业务线代码进行区分
     *              动作包括： 点击，评论，下载，收藏，分享，充值，缴费，后续可以继续扩充
     *              动作码由数据部维护，不可以随意添加动作码，要向数据部申请 搜索、播放不在动作日志范畴，有独立的query
     *              log和play log key和value的值都不允许包含 &
     *              符号，如果有包含，要对key/value值进行URL编码 上报地址：http://dc.letv.com/op/?
     *
     *             2014年4月30日增加
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
     * 			  专辑id 2014-04-30 添加
     * @param targeturl
     *            目标页地址 2014-08-22 添加
     */
	public void sendActionInfoAddTargetUrl(final Context context, final String p1,
			final String p2, final String pcode, final String acode,
			final String ap, final String ar, final String cid,
			final String pid, final String vid, final String uid,
			final String cur_url, final String area, final String bucket,
			final String rank, final int ilu, final String zid, final String lid,final String targeturl) {
		android.util.Log.i("king", "ap=" + ap + "  ,targeturl=" + targeturl);
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {

				StringBuffer sb = new StringBuffer();
				sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION)
						+ "&");// $1:日志版本号
				// 新版从
				// 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1))
						+ "&");// $2
				// p1:一级产品线代码
				// 为从0开始的数字
				sb.append("p2="
						+ DataUtils.getData(DataUtils.getTrimData(p1 + p2))
						+ "&");// $3
				// p2:二级产品线代码
				// 已一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001&"); // p3

				sb.append("acode=" + acode + "&");// $5 acode:动作码
				sb.append("ap="
						+ URLEncoder.encode(DataUtils.getData(DataUtils
								.getTrimData(ap))) + "&");// $6
				// ap:动作属性
				// ap=URLEncoder.encode(fl=1&wz=1&name=91助手)

				sb.append("ar=" + DataUtils.getData(DataUtils.getTrimData(ar))
						+ "&");// $7
				// ar:动作动作结果
				// 0：成功
				// 1：失败

				sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&");// $8
				// cid:视频频道ID
				// 全业务线统一
				// 和大媒资一致
				sb.append("pid=" + replaceStr(DataUtils.getData(pid)) + "&");// $9 pid:专辑ID
				// 如果不是专辑 用
				// - 替代
				sb.append("vid=" + replaceStr(DataUtils.getData(vid)) + "&");// $10 vid:
				// 视频ID

				sb.append("uid=" + DataUtils.getData(uid) + "&"); // $11
				// uid,乐视网用户id
				sb.append("uuid="
						+ DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $12
				// uuid:(did_timestamp)

				sb.append("lc=-" + "&"); // $13 lc:(letv cookie)
				// 是用来唯一标识用户的，用户即使用不同的浏览器，
				// lc都是相同的，可以参考flash cookie
				// 主要给 PC 端使用，移动、TV 传 -
				sb.append("cur_url="
						+ URLEncoder.encode(DataUtils.getData(cur_url)) + "&"); // $14
				// cur_url:当前页面地址

				sb.append("ch=-" + "&");// $15 ch:渠道
				sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $16
				// pcode

				sb.append("auid="
						+ DataUtils.getData(DataUtils.generateDeviceId(context))
						+ "&"); // $17
				// auid,设备id

				if (ilu == -1) {
					sb.append("ilu=-" + "&");
				} else {
					sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $18:
																	// ilu是否为登录用户
					// 0：登录 1：非登录
				}

				sb.append("reid=" + System.currentTimeMillis() + "&"); // $19
				// reid
				// 推荐反馈的随机数
				// String
				// [推荐结果点击时必须上报]推荐点击动作上报时必填
				sb.append("area=" + DataUtils.getData(area) + "&"); // $20 area
				// 推荐区域标识
				// String
				// 会提供两个特定的区域[推荐点击动作上报时必填]
				sb.append("bucket=" + DataUtils.getData(bucket) + "&"); // $21
				// bucket
				// 推荐的算法策略
				// Int
				// 推荐组维护
				// [推荐点击动作上报时必填]
				sb.append("rank=" + DataUtils.getData(rank) + "&"); // $22 rank
				// 点击视频在推荐区域的排序
				// Int
				// 最终要沟通确认
				// [推荐点击动作上报时必填]
				sb.append("zid="
						+ replaceStr(DataUtils.getData(DataUtils.getTrimData(zid))) + "&");
				sb.append("r=" + System.currentTimeMillis() + "&"); // $23: 随机数
				
				sb.append("lid=" + DataUtils.getData(DataUtils.getTrimData(lid)) + "&"); 
				
				sb.append("targeturl="
						+ DataUtils.getData(DataUtils.getTrimData(targeturl)) + "&"); 
				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendActionInfo:" + sb.toString());
				}
				sendLocalTestStatis(context,
						DataConstant.STAT_LOCAL_ACTION_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance()
							.getStatActionUrl() + sb.toString();
					HttpEngine.getInstance().doHttpGet(
							context,
							new StatisCacheBean(cacheData, cacheData, System
									.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * * @category说明： Action 字段名应该严格遵守，不能随意自己增加字段名，上报采用key-value格式
     *              动作日志，记录的是用户的行为，比如：下载，评论，共享，收藏 所有业务线使用的日志格式一样，使用业务线代码进行区分
     *              动作包括： 点击，评论，下载，收藏，分享，充值，缴费，后续可以继续扩充
     *              动作码由数据部维护，不可以随意添加动作码，要向数据部申请 搜索、播放不在动作日志范畴，有独立的query
     *              log和play log key和value的值都不允许包含 &
     *              符号，如果有包含，要对key/value值进行URL编码 上报地址：http://dc.letv.com/op/?
     *
     *             2014年4月30日增加
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
     * 			  专辑id 2014-04-30 添加
     * @param scid
     * 			接口中提供的pageid 5.5版本
	 */
	public void sendActionInfos(final Context context, final String p1,
			final String p2, final String pcode, final String acode,
			final String ap, final String ar, final String cid,
			final String pid, final String vid, final String uid,
			final String cur_url, final String area, final String bucket,
			final String rank, final int ilu, final String zid, final String scid) {
		android.util.Log.i("king", "acode=" + acode+"ap=" + ap + ",cid="+cid);
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {

				StringBuffer sb = new StringBuffer();
				sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION)
						+ "&");// $1:日志版本号
				// 新版从
				// 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1))
						+ "&");// $2
				// p1:一级产品线代码
				// 为从0开始的数字
				sb.append("p2="
						+ DataUtils.getData(DataUtils.getTrimData(p1 + p2))
						+ "&");// $3
				// p2:二级产品线代码
				// 已一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001&"); // p3

				sb.append("acode=" + acode + "&");// $5 acode:动作码
				sb.append("ap="
						+ URLEncoder.encode(DataUtils.getData(DataUtils
								.getTrimData(ap))) + "&");// $6
				// ap:动作属性
				// ap=URLEncoder.encode(fl=1&wz=1&name=91助手)

				sb.append("ar=" + DataUtils.getData(DataUtils.getTrimData(ar))
						+ "&");// $7
				// ar:动作动作结果
				// 0：成功
				// 1：失败

				sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&");// $8
				// cid:视频频道ID
				// 全业务线统一
				// 和大媒资一致
				sb.append("pid=" + replaceStr(DataUtils.getData(pid)) + "&");// $9 pid:专辑ID
				// 如果不是专辑 用
				// - 替代
				sb.append("vid=" + replaceStr(DataUtils.getData(vid)) + "&");// $10 vid:
				// 视频ID

				sb.append("uid=" + DataUtils.getData(uid) + "&"); // $11
				// uid,乐视网用户id
				sb.append("uuid="
						+ DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $12
				// uuid:(did_timestamp)

				sb.append("lc=-" + "&"); // $13 lc:(letv cookie)
				// 是用来唯一标识用户的，用户即使用不同的浏览器，
				// lc都是相同的，可以参考flash cookie
				// 主要给 PC 端使用，移动、TV 传 -
				sb.append("cur_url="
						+ URLEncoder.encode(DataUtils.getData(cur_url)) + "&"); // $14
				// cur_url:当前页面地址

				sb.append("ch=-" + "&");// $15 ch:渠道
				sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $16
				// pcode

				sb.append("auid="
						+ DataUtils.getData(DataUtils.generateDeviceId(context))
						+ "&"); // $17
				// auid,设备id

				if (ilu == -1) {
					sb.append("ilu=-" + "&");
				} else {
					sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $18:
																	// ilu是否为登录用户
					// 0：登录 1：非登录
				}

				sb.append("reid=" + System.currentTimeMillis() + "&"); // $19
				// reid
				// 推荐反馈的随机数
				// String
				// [推荐结果点击时必须上报]推荐点击动作上报时必填
				sb.append("area=" + DataUtils.getData(area) + "&"); // $20 area
				// 推荐区域标识
				// String
				// 会提供两个特定的区域[推荐点击动作上报时必填]
				sb.append("bucket=" + DataUtils.getData(bucket) + "&"); // $21
				// bucket
				// 推荐的算法策略
				// Int
				// 推荐组维护
				// [推荐点击动作上报时必填]
				sb.append("rank=" + DataUtils.getData(rank) + "&"); // $22 rank
				// 点击视频在推荐区域的排序
				// Int
				// 最终要沟通确认
				// [推荐点击动作上报时必填]
				sb.append("zid="
						+ replaceStr(DataUtils.getData(DataUtils.getTrimData(zid))) + "&");
				sb.append("r=" + System.currentTimeMillis()); // $23: 随机数
				
				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendActionInfo:" + sb.toString());
				}
				sendLocalTestStatis(context,
						DataConstant.STAT_LOCAL_ACTION_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance()
							.getStatActionUrl() + sb.toString();
					Log.i("glh", cacheData);
					HttpEngine.getInstance().doHttpGet(
							context,
							new StatisCacheBean(cacheData, cacheData, System
									.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * 
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
	 * @param scid
	 * @param fragid
	 * 				接口中提供的碎片的id - 5.5.1版本 by glh (为频道页添加)
	 */
	public void sendActionInfoAddFragid(final Context context, final String p1,
			final String p2, final String pcode, final String acode,
			final String ap, final String ar, final String cid,
			final String pid, final String vid, final String uid,
			final String cur_url, final String area, final String bucket,
			final String rank, final int ilu, final String zid, final String scid, final String fragid) {
		android.util.Log.i("king", "acode=" + acode+"ap=" + ap + ",cid="+cid);
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {

				StringBuffer sb = new StringBuffer();
				sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION)
						+ "&");// $1:日志版本号
				// 新版从
				// 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1))
						+ "&");// $2
				// p1:一级产品线代码
				// 为从0开始的数字
				sb.append("p2="
						+ DataUtils.getData(DataUtils.getTrimData(p1 + p2))
						+ "&");// $3
				// p2:二级产品线代码
				// 已一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001&"); // p3

				sb.append("acode=" + acode + "&");// $5 acode:动作码
				sb.append("ap="
						+ URLEncoder.encode(DataUtils.getData(DataUtils
								.getTrimData(ap))) + "&");// $6
				// ap:动作属性
				// ap=URLEncoder.encode(fl=1&wz=1&name=91助手)

				sb.append("ar=" + DataUtils.getData(DataUtils.getTrimData(ar))
						+ "&");// $7
				// ar:动作动作结果
				// 0：成功
				// 1：失败

				sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&");// $8
				// cid:视频频道ID
				// 全业务线统一
				// 和大媒资一致
				sb.append("pid=" + replaceStr(DataUtils.getData(pid)) + "&");// $9 pid:专辑ID
				// 如果不是专辑 用
				// - 替代
				sb.append("vid=" + replaceStr(DataUtils.getData(vid)) + "&");// $10 vid:
				// 视频ID

				sb.append("uid=" + DataUtils.getData(uid) + "&"); // $11
				// uid,乐视网用户id
				sb.append("uuid="
						+ DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $12
				// uuid:(did_timestamp)

				sb.append("lc=-" + "&"); // $13 lc:(letv cookie)
				// 是用来唯一标识用户的，用户即使用不同的浏览器，
				// lc都是相同的，可以参考flash cookie
				// 主要给 PC 端使用，移动、TV 传 -
				sb.append("cur_url="
						+ URLEncoder.encode(DataUtils.getData(cur_url)) + "&"); // $14
				// cur_url:当前页面地址

				sb.append("ch=-" + "&");// $15 ch:渠道
				sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $16
				// pcode

				sb.append("auid="
						+ DataUtils.getData(DataUtils.generateDeviceId(context))
						+ "&"); // $17
				// auid,设备id

				if (ilu == -1) {
					sb.append("ilu=-" + "&");
				} else {
					sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $18:
																	// ilu是否为登录用户
					// 0：登录 1：非登录
				}

				sb.append("reid=" + System.currentTimeMillis() + "&"); // $19
				// reid
				// 推荐反馈的随机数
				// String
				// [推荐结果点击时必须上报]推荐点击动作上报时必填
				sb.append("area=" + DataUtils.getData(area) + "&"); // $20 area
				// 推荐区域标识
				// String
				// 会提供两个特定的区域[推荐点击动作上报时必填]
				sb.append("bucket=" + DataUtils.getData(bucket) + "&"); // $21
				// bucket
				// 推荐的算法策略
				// Int
				// 推荐组维护
				// [推荐点击动作上报时必填]
				sb.append("rank=" + DataUtils.getData(rank) + "&"); // $22 rank
				// 点击视频在推荐区域的排序
				// Int
				// 最终要沟通确认
				// [推荐点击动作上报时必填]
				sb.append("zid="
						+ replaceStr(DataUtils.getData(DataUtils.getTrimData(zid))) + "&");
				sb.append("r=" + System.currentTimeMillis()); // $23: 随机数
				
				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendActionInfo:" + sb.toString());
				}
				sendLocalTestStatis(context,
						DataConstant.STAT_LOCAL_ACTION_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance()
							.getStatActionUrl() + sb.toString();
					Log.i("glh", cacheData);
					HttpEngine.getInstance().doHttpGet(
							context,
							new StatisCacheBean(cacheData, cacheData, System
									.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * 
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
	 * @param lid
	 * @param fragid
	 * 				接口中提供的碎片的id - 5.5.1版本 by glh (为首页添加)
	 */
	public void sendActionInfoAddFragId(final Context context, final String p1,
			final String p2, final String pcode, final String acode,
			final String ap, final String ar, final String cid,
			final String pid, final String vid, final String uid,
			final String cur_url, final String area, final String bucket,
			final String rank, final int ilu, final String zid, final String lid,final String fragid) {
		android.util.Log.i("king", "acode=" + acode+"ap=" + ap + ",cid="+cid);
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {

				StringBuffer sb = new StringBuffer();
				sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION)
						+ "&");// $1:日志版本号
				// 新版从
				// 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1))
						+ "&");// $2
				// p1:一级产品线代码
				// 为从0开始的数字
				sb.append("p2="
						+ DataUtils.getData(DataUtils.getTrimData(p1 + p2))
						+ "&");// $3
				// p2:二级产品线代码
				// 已一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001&"); // p3

				sb.append("acode=" + acode + "&");// $5 acode:动作码
				sb.append("ap="
						+ URLEncoder.encode(DataUtils.getData(DataUtils
								.getTrimData(ap))) + "&");// $6
				// ap:动作属性
				// ap=URLEncoder.encode(fl=1&wz=1&name=91助手)

				sb.append("ar=" + DataUtils.getData(DataUtils.getTrimData(ar))
						+ "&");// $7
				// ar:动作动作结果
				// 0：成功
				// 1：失败

				sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&");// $8
				// cid:视频频道ID
				// 全业务线统一
				// 和大媒资一致
				sb.append("pid=" + replaceStr(DataUtils.getData(pid)) + "&");// $9 pid:专辑ID
				// 如果不是专辑 用
				// - 替代
				sb.append("vid=" + replaceStr(DataUtils.getData(vid)) + "&");// $10 vid:
				// 视频ID

				sb.append("uid=" + DataUtils.getData(uid) + "&"); // $11
				// uid,乐视网用户id
				sb.append("uuid="
						+ DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $12
				// uuid:(did_timestamp)

				sb.append("lc=-" + "&"); // $13 lc:(letv cookie)
				// 是用来唯一标识用户的，用户即使用不同的浏览器，
				// lc都是相同的，可以参考flash cookie
				// 主要给 PC 端使用，移动、TV 传 -
				sb.append("cur_url="
						+ URLEncoder.encode(DataUtils.getData(cur_url)) + "&"); // $14
				// cur_url:当前页面地址

				sb.append("ch=-" + "&");// $15 ch:渠道
				sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $16
				// pcode

				sb.append("auid="
						+ DataUtils.getData(DataUtils.generateDeviceId(context))
						+ "&"); // $17
				// auid,设备id

				if (ilu == -1) {
					sb.append("ilu=-" + "&");
				} else {
					sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $18:
																	// ilu是否为登录用户
					// 0：登录 1：非登录
				}

				sb.append("reid=" + System.currentTimeMillis() + "&"); // $19
				// reid
				// 推荐反馈的随机数
				// String
				// [推荐结果点击时必须上报]推荐点击动作上报时必填
				sb.append("area=" + DataUtils.getData(area) + "&"); // $20 area
				// 推荐区域标识
				// String
				// 会提供两个特定的区域[推荐点击动作上报时必填]
				sb.append("bucket=" + DataUtils.getData(bucket) + "&"); // $21
				// bucket
				// 推荐的算法策略
				// Int
				// 推荐组维护
				// [推荐点击动作上报时必填]
				sb.append("rank=" + DataUtils.getData(rank) + "&"); // $22 rank
				// 点击视频在推荐区域的排序
				// Int
				// 最终要沟通确认
				// [推荐点击动作上报时必填]
				sb.append("zid="
						+ replaceStr(DataUtils.getData(DataUtils.getTrimData(zid))) + "&");
				sb.append("lid=" + DataUtils.getData(DataUtils.getTrimData(lid)) + "&"); 
				sb.append("r=" + System.currentTimeMillis()); // $23: 随机数
				
				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendActionInfo:" + sb.toString());
				}
				sendLocalTestStatis(context,
						DataConstant.STAT_LOCAL_ACTION_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance()
							.getStatActionUrl() + sb.toString();
					Log.i("glh", cacheData);
					HttpEngine.getInstance().doHttpGet(
							context,
							new StatisCacheBean(cacheData, cacheData, System
									.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
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
	 *            
	 *此方法仅用于推送消息使用， 其他情况禁止调用
	 */
	public void sendPushActionInfo(final Context context, final String p1, final String p2, final String pcode,
			final String acode, final String ap, final String ar, final String cid, final String pid, final String vid,
			final String uid, final String cur_url, final String area, final String bucket, final String rank,
			final int ilu) {//此方法仅用于推送消息使用， 其他情况禁止调用
		StringBuffer sb = new StringBuffer();
		sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
		// 新版从
		// 2.0开始
		sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
		// p1:一级产品线代码
		// 为从0开始的数字
		sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
		// p2:二级产品线代码
		// 已一级产品线代码为前缀，接下来从0开始
		sb.append("p3=001" + "&");// $4 p3 三级产品线代码 平台自定义
		sb.append("acode=" + acode + "&");// $5 acode:动作码
		sb.append("ap=" + URLEncoder.encode(DataUtils.getData(DataUtils.getTrimData(ap))) + "&");// $6
		// ap:动作属性
		// ap=URLEncoder.encode(fl=1&wz=1&name=91助手)

		sb.append("ar=" + DataUtils.getData(DataUtils.getTrimData(ar)) + "&");// $7
		// ar:动作动作结果
		// 0：成功
		// 1：失败

		sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&");// $8
		// cid:视频频道ID
		// 全业务线统一
		// 和大媒资一致
		sb.append("pid=" + replaceStr(DataUtils.getData(pid)) + "&");// $9 pid:专辑ID
		// 如果不是专辑 用
		// - 替代
		sb.append("vid=" + replaceStr(DataUtils.getData(vid)) + "&");// $10 vid:
		// 视频ID

		sb.append("uid=" + DataUtils.getData(uid) + "&"); // $11
		// uid,乐视网用户id
		sb.append("uuid=" + DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $12
		// uuid:(did_timestamp)

		sb.append("lc=-" + "&"); // $13 lc:(letv cookie)
		// 是用来唯一标识用户的，用户即使用不同的浏览器，
		// lc都是相同的，可以参考flash cookie
		// 主要给 PC 端使用，移动、TV 传 -
		sb.append("cur_url=" + URLEncoder.encode(DataUtils.getData(cur_url)) + "&"); // $14
		// cur_url:当前页面地址

		sb.append("ch=-" + "&");// $15 ch:渠道
		sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $16
		// pcode

		sb.append("auid=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $17
		// auid,设备id
		sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $18: ilu是否为登录用户
		// 0：登录 1：非登录

		sb.append("reid=" + System.currentTimeMillis() + "&"); // $19
		// reid
		// 推荐反馈的随机数
		// String
		// [推荐结果点击时必须上报]推荐点击动作上报时必填
		sb.append("area=" + DataUtils.getData(area) + "&"); // $20 area
		// 推荐区域标识
		// String
		// 会提供两个特定的区域[推荐点击动作上报时必填]
		sb.append("bucket=" + DataUtils.getData(bucket) + "&"); // $21
		// bucket
		// 推荐的算法策略
		// Int
		// 推荐组维护
		// [推荐点击动作上报时必填]
		sb.append("rank=" + DataUtils.getData(rank) + "&"); // $22 rank
		// 点击视频在推荐区域的排序
		// Int
		// 最终要沟通确认
		// [推荐点击动作上报时必填]

		sb.append("r=" + System.currentTimeMillis()); // $23: 随机数

		if (DataStatistics.getInstance().isDebug()) {
			Log.d(DataStatistics.TAG, "sendPushActionInfo:" + sb.toString());
		}
		sendLocalTestStatis(context, DataConstant.STAT_LOCAL_ACTION_URL, sb.toString());
		try {
			String cacheData = DataStatistics.getInstance().getStatActionUrl() + sb.toString();
			HttpEngine.getInstance().doHttpGet(context,
					new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
		} catch (HttpDataParserException e) {
			e.printStackTrace();
		} catch (HttpDataConnectionException e) {
			e.printStackTrace();
		}
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
     *
     *此方法仅用于推送消息使用， 其他情况禁止调用
     *
     * 增加  lid  zid by king
     */
    public void sendPushActionInfo(final Context context, final String p1, final String p2, final String pcode,
                                   final String acode, final String ap, final String ar, final String cid, final String pid, final String vid,
                                   final String uid, final String cur_url, final String area, final String bucket, final String rank,
                                   final int ilu,final String lid,final  String zid) {//此方法仅用于推送消息使用， 其他情况禁止调用
        android.util.Log.i("king","push.....ap="+ap+"  ,lid="+lid);
        StringBuffer sb = new StringBuffer();
        sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
        // 新版从
        // 2.0开始
        sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
        // p1:一级产品线代码
        // 为从0开始的数字
        sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
        // p2:二级产品线代码
        // 已一级产品线代码为前缀，接下来从0开始
        sb.append("p3=001" + "&");// $4 p3 三级产品线代码 平台自定义
        sb.append("acode=" + acode + "&");// $5 acode:动作码
        sb.append("ap=" + URLEncoder.encode(DataUtils.getData(DataUtils.getTrimData(ap))) + "&");// $6
        // ap:动作属性
        // ap=URLEncoder.encode(fl=1&wz=1&name=91助手)

        sb.append("ar=" + DataUtils.getData(DataUtils.getTrimData(ar)) + "&");// $7
        // ar:动作动作结果
        // 0：成功
        // 1：失败

        sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&");// $8
        // cid:视频频道ID
        // 全业务线统一
        // 和大媒资一致
        sb.append("pid=" + replaceStr(DataUtils.getData(pid)) + "&");// $9 pid:专辑ID
        // 如果不是专辑 用
        // - 替代
        sb.append("vid=" + replaceStr(DataUtils.getData(vid)) + "&");// $10 vid:
        // 视频ID

        sb.append("uid=" + DataUtils.getData(uid) + "&"); // $11
        // uid,乐视网用户id
        sb.append("uuid=" + DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $12
        // uuid:(did_timestamp)

        sb.append("lc=-" + "&"); // $13 lc:(letv cookie)
        // 是用来唯一标识用户的，用户即使用不同的浏览器，
        // lc都是相同的，可以参考flash cookie
        // 主要给 PC 端使用，移动、TV 传 -
        sb.append("cur_url=" + URLEncoder.encode(DataUtils.getData(cur_url)) + "&"); // $14
        // cur_url:当前页面地址

        sb.append("ch=-" + "&");// $15 ch:渠道
        sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $16
        // pcode

        sb.append("auid=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $17
        // auid,设备id
        sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $18: ilu是否为登录用户
        // 0：登录 1：非登录

        sb.append("reid=" + System.currentTimeMillis() + "&"); // $19
        // reid
        // 推荐反馈的随机数
        // String
        // [推荐结果点击时必须上报]推荐点击动作上报时必填
        sb.append("area=" + DataUtils.getData(area) + "&"); // $20 area
        // 推荐区域标识
        // String
        // 会提供两个特定的区域[推荐点击动作上报时必填]
        sb.append("bucket=" + DataUtils.getData(bucket) + "&"); // $21
        // bucket
        // 推荐的算法策略
        // Int
        // 推荐组维护
        // [推荐点击动作上报时必填]
        sb.append("rank=" + DataUtils.getData(rank) + "&"); // $22 rank

        sb.append("lid=" + replaceStr(DataUtils.getData(lid)) + "&");

        sb.append("zid=" + replaceStr(DataUtils.getData(zid)) + "&");

        // 点击视频在推荐区域的排序
        // Int
        // 最终要沟通确认
        // [推荐点击动作上报时必填]

        sb.append("r=" + System.currentTimeMillis()); // $23: 随机数

        if (DataStatistics.getInstance().isDebug()) {
            Log.d(DataStatistics.TAG, "sendPushActionInfo:" + sb.toString());
        }
        sendLocalTestStatis(context, DataConstant.STAT_LOCAL_ACTION_URL, sb.toString());
        try {
            String cacheData = DataStatistics.getInstance().getStatActionUrl() + sb.toString();
            HttpEngine.getInstance().doHttpGet(context,
                    new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
        } catch (HttpDataParserException e) {
            e.printStackTrace();
        } catch (HttpDataConnectionException e) {
            e.printStackTrace();
        }
    }
/**
 * 增加app pushtype msgid by glh
 */
	public void sendPushActionInfoAddApp(final Context context, final String p1,
			final String p2, final String pcode, final String acode,
			final String ap, final String ar, final String cid,
			final String pid, final String vid, final String uid,
			final String cur_url, final String area, final String bucket,
			final String rank, final int ilu, final String lid, final String zid) {// 此方法仅用于推送消息使用，
																					// 其他情况禁止调用
		android.util.Log.i("king", "push.....ap=" + ap + "  ,lid=" + lid);
		StringBuffer sb = new StringBuffer();
		sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
		// 新版从
		// 2.0开始
		sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
		// p1:一级产品线代码
		// 为从0开始的数字
		sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2))
				+ "&");// $3
		// p2:二级产品线代码
		// 已一级产品线代码为前缀，接下来从0开始
		sb.append("p3=001" + "&");// $4 p3 三级产品线代码 平台自定义
		sb.append("acode=" + acode + "&");// $5 acode:动作码
		sb.append("ap="
				+ URLEncoder.encode(DataUtils.getData(DataUtils.getTrimData(ap)))
				+ "&");// $6
		// ap:动作属性
		// ap=URLEncoder.encode(fl=1&wz=1&name=91助手)

		sb.append("ar=" + DataUtils.getData(DataUtils.getTrimData(ar)) + "&");// $7
		// ar:动作动作结果
		// 0：成功
		// 1：失败

		sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&");// $8
		// cid:视频频道ID
		// 全业务线统一
		// 和大媒资一致
		sb.append("pid=" + replaceStr(DataUtils.getData(pid)) + "&");// $9 pid:专辑ID
		// 如果不是专辑 用
		// - 替代
		sb.append("vid=" + replaceStr(DataUtils.getData(vid)) + "&");// $10 vid:
		// 视频ID

		sb.append("uid=" + DataUtils.getData(uid) + "&"); // $11
		// uid,乐视网用户id
		sb.append("uuid=" + DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $12
		// uuid:(did_timestamp)

		sb.append("lc=-" + "&"); // $13 lc:(letv cookie)
		// 是用来唯一标识用户的，用户即使用不同的浏览器，
		// lc都是相同的，可以参考flash cookie
		// 主要给 PC 端使用，移动、TV 传 -
		sb.append("cur_url=" + URLEncoder.encode(DataUtils.getData(cur_url))
				+ "&"); // $14
		// cur_url:当前页面地址

		sb.append("ch=-" + "&");// $15 ch:渠道
		sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $16
		// pcode

		sb.append("auid="
				+ DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $17
		// auid,设备id
		sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $18: ilu是否为登录用户
		// 0：登录 1：非登录

		sb.append("reid=" + System.currentTimeMillis() + "&"); // $19
		// reid
		// 推荐反馈的随机数
		// String
		// [推荐结果点击时必须上报]推荐点击动作上报时必填
		sb.append("area=" + DataUtils.getData(area) + "&"); // $20 area
		// 推荐区域标识
		// String
		// 会提供两个特定的区域[推荐点击动作上报时必填]
		sb.append("bucket=" + DataUtils.getData(bucket) + "&"); // $21
		// bucket
		// 推荐的算法策略
		// Int
		// 推荐组维护
		// [推荐点击动作上报时必填]
		sb.append("rank=" + DataUtils.getData(rank) + "&"); // $22 rank

		sb.append("lid=" + replaceStr(DataUtils.getData(lid)) + "&");

		sb.append("zid=" + replaceStr(DataUtils.getData(zid)) + "&");
		// 点击视频在推荐区域的排序
		// Int
		// 最终要沟通确认
		// [推荐点击动作上报时必填]

		sb.append("r=" + System.currentTimeMillis()); // $23: 随机数

		if (DataStatistics.getInstance().isDebug()) {
			Log.d(DataStatistics.TAG, "sendPushActionInfo:" + sb.toString());
		}
		sendLocalTestStatis(context, DataConstant.STAT_LOCAL_ACTION_URL,
				sb.toString());
		try {
			String cacheData = DataStatistics.getInstance().getStatActionUrl()
					+ sb.toString();
			HttpEngine.getInstance().doHttpGet(
					context,
					new StatisCacheBean(cacheData, cacheData, System
							.currentTimeMillis()));
		} catch (HttpDataParserException e) {
			e.printStackTrace();
		} catch (HttpDataConnectionException e) {
			e.printStackTrace();
		}
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
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {

				StringBuffer sb = new StringBuffer();
				sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
																						// 新版从
																						// 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
																						// p1:一级产品线代码
																						// 为从0开始的数字
				sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
																							// p2:二级产品线代码
																							// 已一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001" + "&");// $4 p3 三级产品线代码 平台自定义

				sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&");// $5
																	// cid:视频频道ID
																	// 全业务线统一
																	// 和大媒资一致
				sb.append("pid=" + replaceStr(DataUtils.getData(pid)) + "&");// $6 pid:专辑ID
																	// 如果不是专辑 用
																	// - 替代
				sb.append("vid=" + replaceStr(DataUtils.getData(vid)) + "&");// $7 vid: 视频ID

				sb.append("uid=" + DataUtils.getData(uid) + "&"); // $8
																	// uid,乐视网用户id
				sb.append("uuid=" + DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $9
																							// uuid:(did_timestamp)
				sb.append("lc=-" + "&"); // $10 lc:(letv cookie)
											// 是用来唯一标识用户的，用户即使用不同的浏览器，
											// lc都是相同的，可以参考flash cookie
											// 主要给 PC 端使用，移动、TV 传 -
				sb.append("ref=" + URLEncoder.encode(DataUtils.getData(ref)) + "&"); // $11
																						// ref:页面来源
				sb.append("ct=" + DataUtils.getData(ct) + "&"); // $12 ct: 来源类型
																// 1：直接输入，2：站内，
																// 3：搜索，4：站外，5：合作
				sb.append("rcid=" + DataUtils.getData(rcid) + "&"); // $13 rcid:
																	// 来源频道
				sb.append("kw=" + URLEncoder.encode(DataUtils.getData(kw)) + "&"); // $14
																					// kw:
																					// 搜索关键字
																					// String
																					// 需要URL编码，如果不是搜索结果页，那么用
																					// –
																					// 代替
																					// [必填]
				sb.append("cur_url=" + URLEncoder.encode(DataUtils.getData(cur_url)) + "&"); // $15
																								// cur_url:当前页面地址
				sb.append("ch=-" + "&");// $16 ch:渠道

				sb.append("area=" + DataUtils.getData(area) + "&"); // $17 area
																	// 推荐区域标识
																	// String
																	// 会提供两个特定的区域[推荐点击动作上报时必填]
				sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $18
																		// pcode

				sb.append("auid=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $19
																									// auid,设备id
				sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $20: ilu是否为登录用户
																// 0：登录 1：非登录
				sb.append("weid=" + DataUtils.getData(weid) + "&");// $21
																	// Webeventid
																	// 上报时获取js生成的页面weid

				sb.append("r=" + System.currentTimeMillis()); // $22: 随机数

				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendPVInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_PV_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatPVUrl() + sb.toString();
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * @category:说明 PV 各业务线视需求不同，灵活掌握是否上报 PV 日志 PV日志主要用于统计、分析视频曝光相关的数据
	 *              PV日志包括用户信息、设备信息、视频信息 key和value的值都不允许包含 &
	 *              符号，如果有包含，要对key/value值进行URL编码 上报地址：http://dc.letv.com/pgv/?
	 *                  	 
	 *            2014-04-30  所有统计增加zid字段
	 *              
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
	 * 			      专题id  String 2014年4月30日增加                       
	 */
	public void sendPVInfo(final Context context, final String p1, final String p2, final String pcode,
			final String cid, final String pid, final String vid, final String uid, final String ref, final String ct,
			final String rcid, final String kw, final String cur_url, final String area, final String weid,
			final int ilu,final String zid) {
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {

				StringBuffer sb = new StringBuffer();
				sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
																						// 新版从
																						// 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
																						// p1:一级产品线代码
																						// 为从0开始的数字
				sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
																							// p2:二级产品线代码
																							// 已一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001" + "&");// $4 p3 三级产品线代码 平台自定义

				sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&");// $5
																	// cid:视频频道ID
																	// 全业务线统一
																	// 和大媒资一致
				sb.append("pid=" + replaceStr(DataUtils.getData(pid)) + "&");// $6 pid:专辑ID
																	// 如果不是专辑 用
																	// - 替代
				sb.append("vid=" + replaceStr(DataUtils.getData(vid)) + "&");// $7 vid: 视频ID

				sb.append("uid=" + DataUtils.getData(uid) + "&"); // $8
																	// uid,乐视网用户id
				sb.append("uuid=" + DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $9
																							// uuid:(did_timestamp)
				sb.append("lc=-" + "&"); // $10 lc:(letv cookie)
											// 是用来唯一标识用户的，用户即使用不同的浏览器，
											// lc都是相同的，可以参考flash cookie
											// 主要给 PC 端使用，移动、TV 传 -
				sb.append("ref=" + URLEncoder.encode(DataUtils.getData(ref)) + "&"); // $11
																						// ref:页面来源
				sb.append("ct=" + DataUtils.getData(ct) + "&"); // $12 ct: 来源类型
																// 1：直接输入，2：站内，
																// 3：搜索，4：站外，5：合作
				sb.append("rcid=" + DataUtils.getData(rcid) + "&"); // $13 rcid:
																	// 来源频道
				sb.append("kw=" + URLEncoder.encode(DataUtils.getData(kw)) + "&"); // $14
																					// kw:
																					// 搜索关键字
																					// String
																					// 需要URL编码，如果不是搜索结果页，那么用
																					// –
																					// 代替
																					// [必填]
				sb.append("cur_url=" + URLEncoder.encode(DataUtils.getData(cur_url)) + "&"); // $15
																								// cur_url:当前页面地址
				sb.append("ch=-" + "&");// $16 ch:渠道

				sb.append("area=" + DataUtils.getData(area) + "&"); // $17 area
																	// 推荐区域标识
																	// String
																	// 会提供两个特定的区域[推荐点击动作上报时必填]
				sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $18
																		// pcode

				sb.append("auid=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $19
																									// auid,设备id
				sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $20: ilu是否为登录用户
																// 0：登录 1：非登录
				sb.append("weid=" + DataUtils.getData(weid) + "&");// $21
																	// Webeventid
																	// 上报时获取js生成的页面weid
				sb.append("zid=" + replaceStr(DataUtils.getData(DataUtils.getTrimData(zid))) + "&");

				sb.append("r=" + System.currentTimeMillis()); // $22: 随机数

				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendPVInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_PV_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatPVUrl() + sb.toString();
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
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
	 */
	public void sendEnvInfo(final Context context, final String p1, final String p2, final String ip, final String xh) {
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				// sb.append("ver=" +
				// DataUtils.getData(DataConstant.STAT_VERSION) + "&");//
				// $1:日志版本号
				// // 新版从
				// // 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
																						// p1:一级产品线代码
																						// 为从0开始的数字
				sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
																							// p2:二级产品线代码
																							// 已一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001" + "&");// $4 p3 三级产品线代码 平台自定义

				sb.append("lc=-" + "&"); // $5 lc:(letv cookie)
											// 是用来唯一标识用户的，用户即使用不同的浏览器，
											// lc都是相同的，可以参考flash cookie
				sb.append("uuid=" + DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $6
																							// uuid:(did)

				sb.append("ip=" + DataUtils.getData(ip) + "&"); // $7 ip:IP地址
				sb.append("mac=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $8
																									// mac:设备Mac地址

				sb.append("nt=" + DataUtils.getData(DataUtils.getNetType(context)) + "&"); // $9:nettype,上网类型
																							// ex:wifi/3G

				sb.append("os=" + DataUtils.getData(DataUtils.getSystemName()) + "&");// $10:os,操作系统
																						// ex:android
				sb.append("osv=" + DataUtils.getData(DataUtils.getOSVersionName()) + "&");// $11:osv,操作系统版本
				sb.append("app=" + DataUtils.getData(DataUtils.getClientVersionName(context)) + "&");// $12:app,应用版本
				sb.append("bd=" + URLEncoder.encode(DataUtils.getData(DataUtils.getBrandName())) + "&"); // $13:brand,品牌
																											// url编码
				sb.append("xh=" + URLEncoder.encode(DataUtils.getData(xh)) + "&"); // $14:xh,设备型号
																					// url编码
				sb.append("ro=" + DataUtils.getDataEmpty(DataUtils.getNewResolution(context)) + "&"); // 15:
																										// resolution:设备分辨率
																										// 1024_768
				sb.append("br=chrome" + "&");// $16 br:Browser浏览器名称 String
												// 由数据部维护字典,（见附表三） [选填]
				sb.append("ep=" + URLEncoder.encode("model=" + DataUtils.getDataEmpty(DataUtils.getDeviceName())) + "&");// $17
																															// 设备型号
				sb.append("r=" + System.currentTimeMillis()); // $18: 随机数
				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendEnvInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_ENV_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatEnvUrl() + sb.toString();
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
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
	 *            2014-04-30  所有统计增加zid字段
	 *            
	 * @param context
	 * @param p1
	 *            一级产品线代码 为从0开始的数字
	 * @param p2
	 *            二级产品线代码 已一级产品线代码为前缀，接下来从0开始
	 * @param ip
	 *            ip地址
	 * @param zid 
	 * 			      专题id  String 2014年4月30日增加                       
	 */
	public void sendEnvInfo(final Context context, final String p1, final String p2, final String ip, final String xh,final String zid) {
        android.util.Log.i("king","sendEnvInfo");
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				// sb.append("ver=" +
				// DataUtils.getData(DataConstant.STAT_VERSION) + "&");//
				// $1:日志版本号
				// // 新版从
				// // 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
																						// p1:一级产品线代码
																						// 为从0开始的数字
				sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
																							// p2:二级产品线代码
																							// 已一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001" + "&");// $4 p3 三级产品线代码 平台自定义

				sb.append("lc=-" + "&"); // $5 lc:(letv cookie)
											// 是用来唯一标识用户的，用户即使用不同的浏览器，
											// lc都是相同的，可以参考flash cookie
				sb.append("uuid=" + DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $6
																							// uuid:(did)

				sb.append("ip=" + DataUtils.getData(ip) + "&"); // $7 ip:IP地址
				sb.append("mac=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $8
																									// mac:设备Mac地址

				sb.append("nt=" + DataUtils.getData(DataUtils.getNetType(context)) + "&"); // $9:nettype,上网类型
																							// ex:wifi/3G

				sb.append("os=" + DataUtils.getData(DataUtils.getSystemName()) + "&");// $10:os,操作系统
																						// ex:android
				sb.append("osv=" + DataUtils.getData(DataUtils.getOSVersionName()) + "&");// $11:osv,操作系统版本
				sb.append("app=" + DataUtils.getData(DataUtils.getClientVersionName(context)) + "&");// $12:app,应用版本
				sb.append("bd=" + URLEncoder.encode(DataUtils.getData(DataUtils.getBrandName())) + "&"); // $13:brand,品牌
																											// url编码
				sb.append("xh=" + URLEncoder.encode(DataUtils.getData(xh)) + "&"); // $14:xh,设备型号
																					// url编码
				sb.append("ro=" + DataUtils.getDataEmpty(DataUtils.getNewResolution(context)) + "&"); // 15:
																										// resolution:设备分辨率
																										// 1024_768
				sb.append("br=chrome" + "&");// $16 br:Browser浏览器名称 String
												// 由数据部维护字典,（见附表三） [选填]
				sb.append("ep=" + URLEncoder.encode("model=" + DataUtils.getDataEmpty(DataUtils.getDeviceName())) + "&");// $17
																															// 设备型号
				sb.append("zid=" + replaceStr(DataUtils.getData(DataUtils.getTrimData(zid))) + "&");
				sb.append("r=" + System.currentTimeMillis()); // $18: 随机数
				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendEnvInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_ENV_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatEnvUrl() + sb.toString();
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
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
		 android.util.Log.i("king","lp ="+lp+" ,ts ="+ts);
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
				// 新版从
				// 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
																						// p1:一级产品线代码
																						// 为从0开始的数字
				sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
																							// p2:二级产品线代码
																							// 已一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001" + "&");// $4 p3 三级产品线代码 平台自定义
				sb.append("uid=" + DataUtils.getData(uid) + "&"); // $5
																	// uid,乐视网用户id
				sb.append("lc=-" + "&"); // $6 lc:(letv cookie)
											// 是用来唯一标识用户的，用户即使用不同的浏览器，
											// lc都是相同的，可以参考flash cookie
				sb.append("auid=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $7
																									// auid,设备id
				sb.append("uuid=" + DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $8
																							// uuid:(did)

				sb.append("lp=" + URLEncoder.encode(DataUtils.getData(lp)) + "&"); // $9:lp,登录属性

				sb.append("ch=-" + "&");// $10 ch:登录渠道
				sb.append("ref=" + DataUtils.getData(ref) + "&"); // $11:ref,登录来源

				sb.append("ts=" + DataUtils.getData(ts) + "&"); // $12:ts,Timestamp登录时间,用秒数来表示[必填]
				sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $13
																		// pcode
				sb.append("st=" + st + "&");// $14 st :Status 登录状态 int 0:登录成功
											// 1:退出登录 用户按home键转入后台也算退出登录
											// 通过这个状态，可以计算用户的登录时长 [选填]
				sb.append("r=" + System.currentTimeMillis()); // $15: 随机数
				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendLoginInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_LOGIN2_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatNewLoginUrl() + sb.toString();
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * @category 说明：login 字段名应该严格遵守，不能随意自己增加字段名，上报采用key-value格式
	 *           如果用户从移动App上访问，那么app启动进入首页之后，就要上报一次 如果一天用户N次启动App，就发生N次上报
	 *           任何业务线，如果用户点击登录按钮并且登录成功之后，要进行一次上报，此时上报信息要携带用户的乐视网注册ID
	 *           key和value的值都不允许包含 & 符号，如果有包含，要对key/value值进行URL编码
	 *           上报地址：http://dc.letv.com/lg/?
	 *           	 
	 *            2014-04-30  所有统计增加zid字段
	 *           
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
	 * 			      专题id  String 2014年4月30日增加                       
	 */
	public void sendLoginInfo(final Context context, final String p1, final String p2, final String uid,
			final String lp, final String ref, final String ts, final String pcode, final int st,final String zid) {
		android.util.Log.i("king","lp ="+lp+" ,ts ="+ts);
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
				// 新版从
				// 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
																						// p1:一级产品线代码
																						// 为从0开始的数字
				sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
																							// p2:二级产品线代码
																							// 已一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001" + "&");// $4 p3 三级产品线代码 平台自定义
				sb.append("uid=" + DataUtils.getData(uid) + "&"); // $5
																	// uid,乐视网用户id
				sb.append("lc=-" + "&"); // $6 lc:(letv cookie)
											// 是用来唯一标识用户的，用户即使用不同的浏览器，
											// lc都是相同的，可以参考flash cookie
				sb.append("auid=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $7
																									// auid,设备id
				sb.append("uuid=" + DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $8
																							// uuid:(did)

				sb.append("lp=" + URLEncoder.encode(DataUtils.getData(lp)) + "&"); // $9:lp,登录属性

				sb.append("ch=-" + "&");// $10 ch:登录渠道
				sb.append("ref=" + DataUtils.getData(ref) + "&"); // $11:ref,登录来源

				sb.append("ts=" + DataUtils.getData(ts) + "&"); // $12:ts,Timestamp登录时间,用秒数来表示[必填]
				sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $13
																		// pcode
				sb.append("st=" + st + "&");// $14 st :Status 登录状态 int 0:登录成功
											// 1:退出登录 用户按home键转入后台也算退出登录
											// 通过这个状态，可以计算用户的登录时长 [选填]
				sb.append("zid=" + replaceStr(DataUtils.getData(DataUtils.getTrimData(zid))) + "&");
				sb.append("r=" + System.currentTimeMillis()); // $15: 随机数
				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendLoginInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_LOGIN2_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatNewLoginUrl() + sb.toString();
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
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
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
				// 新版从
				// 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
				// p1:一级产品线代码
				// 为从0开始的数字
				sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
				// p2:二级产品线代码
				// 已一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001" + "&");// $4 p3 三级产品线代码 平台自定义

				sb.append("sid=" + DataUtils.getData(sid) + "&"); // $5 sid
																	// :Search
																	// Id：用于标识一次搜索
																	// int [必填]
				sb.append("ty=" + DataUtils.getData(ty) + "&"); // $6 ty: 上报类型
																// int 0:搜索结果页
																// 1：搜索结果点击 [必填]
				sb.append("pos" + DataUtils.getData(pos) + "&"); // $7
																	// pos:点击的视频位置
																	// String
																	// [必填]如果上报类型是搜索结果页，那么这个字段记录的是来自上个页面的点击位置；如果上报类型是是搜索结果点击，位置是指点击精确搜索结果的第几个,如果第1个上报1,以此类推.
																	// 否则 选填

				sb.append("clk=" + DataUtils.getData(pid) + "_" + DataUtils.getData(vid) + "_" + DataUtils.getData(cid)
						+ "&");// $8:clk:点击的视频内容 String Pid_vid_cid
								// 如果上报类型是是搜索结果点击， 那么 必填 优先取vid，其他取不到上报- 否则 选填

				sb.append("uid=" + DataUtils.getData(uid) + "&"); // $9
																	// uid,乐视网用户id
				sb.append("uuid=" + DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $10
																							// uuid:(did)
				sb.append("lc=-" + "&"); // $11 lc:(letv cookie)
											// 是用来唯一标识用户的，用户即使用不同的浏览器，lc都是相同的，可以参考flash
											// cookie
				sb.append("auid=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $12
																									// auid,设备id
				sb.append("ch=-" + "&");// $13 ch:登录渠道
				sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $14: ilu是否为登录用户
																// 0：登录 1：非登录

				sb.append("q=" + URLEncoder.encode(DataUtils.getData(query)) + "&"); // $15:q,query:
																						// http开头的查询
																						// url，至少要包括用户搜索关键词
																						// String
																						// 要进行URL编码
																						// [必填]
				sb.append("p=" + DataUtils.getData(page) + "&"); // $16:p, page
																	// 搜索结果的当前页码
				sb.append("rt=" + DataUtils.getData(rt) + "&"); // $17:rt:Result:
																// 搜索结果 String
																// pid_vid_cid,pid_vid_cid
																// 按照搜索结果页展现的顺序，把搜索结果组合成以上格式进行上报，每页搜索结果上报一条
																// [必填]优先上报pid，以逗号分隔，如果取不到pid上报vid
				sb.append("r=" + System.currentTimeMillis()); // $18: 随机数
				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendQueryInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_QUERY_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatQueryUrl() + sb.toString();
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * @category 说明 query 说明： 字段名应该严格遵守，不能随意自己增加字段名，上报采用key-value格式
	 *           key和value的值都不允许包含 & 符号，如果有包含，要对key/value值进行URL编码
	 *           搜索日志包括搜索关键词，用户信息和搜索结果信息 上报地址：http://dc.letv.com/qy/?
	 *           
	 *            2014-04-30  所有统计增加zid字段
	 *           
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
	 * 			      专题id  String 2014年4月30日增加            
	 */
	public void sendQueryInfo(final Context context, final String p1, final String p2, final String sid,
			final String ty, final String pos, final String pid, final String vid, final String cid, final String uid,
			final int ilu, final String query, final String page, final String rt,final String zid) {
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
				// 新版从
				// 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
				// p1:一级产品线代码
				// 为从0开始的数字
				sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
				// p2:二级产品线代码
				// 已一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001" + "&");// $4 p3 三级产品线代码 平台自定义

				sb.append("sid=" + DataUtils.getData(sid) + "&"); // $5 sid
																	// :Search
																	// Id：用于标识一次搜索
																	// int [必填]
				sb.append("ty=" + DataUtils.getData(ty) + "&"); // $6 ty: 上报类型
																// int 0:搜索结果页
																// 1：搜索结果点击 [必填]
				sb.append("pos" + DataUtils.getData(pos) + "&"); // $7
																	// pos:点击的视频位置
																	// String
																	// [必填]如果上报类型是搜索结果页，那么这个字段记录的是来自上个页面的点击位置；如果上报类型是是搜索结果点击，位置是指点击精确搜索结果的第几个,如果第1个上报1,以此类推.
																	// 否则 选填

				sb.append("clk=" + DataUtils.getData(pid) + "_" + DataUtils.getData(vid) + "_" + DataUtils.getData(cid)
						+ "&");// $8:clk:点击的视频内容 String Pid_vid_cid
								// 如果上报类型是是搜索结果点击， 那么 必填 优先取vid，其他取不到上报- 否则 选填

				sb.append("uid=" + DataUtils.getData(uid) + "&"); // $9
																	// uid,乐视网用户id
				sb.append("uuid=" + DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $10
																							// uuid:(did)
				sb.append("lc=-" + "&"); // $11 lc:(letv cookie)
											// 是用来唯一标识用户的，用户即使用不同的浏览器，lc都是相同的，可以参考flash
											// cookie
				sb.append("auid=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $12
																									// auid,设备id
				sb.append("ch=-" + "&");// $13 ch:登录渠道
				sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $14: ilu是否为登录用户
																// 0：登录 1：非登录

				sb.append("q=" + URLEncoder.encode(DataUtils.getData(query)) + "&"); // $15:q,query:
																						// http开头的查询
																						// url，至少要包括用户搜索关键词
																						// String
																						// 要进行URL编码
																						// [必填]
				sb.append("p=" + DataUtils.getData(page) + "&"); // $16:p, page
																	// 搜索结果的当前页码
				sb.append("rt=" + DataUtils.getData(rt) + "&"); // $17:rt:Result:
																// 搜索结果 String
																// pid_vid_cid,pid_vid_cid
																// 按照搜索结果页展现的顺序，把搜索结果组合成以上格式进行上报，每页搜索结果上报一条
																// [必填]优先上报pid，以逗号分隔，如果取不到pid上报vid
				sb.append("zid=" + replaceStr(DataUtils.getData(DataUtils.getTrimData(zid))) + "&");
				sb.append("r=" + System.currentTimeMillis()); // $18: 随机数
				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendQueryInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_QUERY_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatQueryUrl() + sb.toString();
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
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
	public void sendADNewInfo(final Context context, final String p1, final String p2, final String ac,
			final String pp, final String cid, final String url, final String slotid, final String adid,
			final String murl, final String uid, final String ref, final String rcid, final String pcode, final int ilu) {
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
				// 新版从
				// 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
				// p1:一级产品线代码
				// 为从0开始的数字
				sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
				// p2:二级产品线代码
				// 已一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001" + "&");// $4 p3 三级产品线代码 平台自定义
				sb.append("ac=" + DataUtils.getData(ac) + "&"); // $5
																// ac:pv/click
																// int 0:pv;
																// 1:click
				sb.append("pp=" + URLEncoder.encode(DataUtils.getData(pp)) + "&"); // $15:pp:广告属性
																					// String
																					// 业务线自己维护，可以存储任何值或者多个值，但是如果包括
																					// &，要进行URL编码
																					// [选填]
				sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&"); // $5 cid
				sb.append("url=" + URLEncoder.encode(DataUtils.getData(url)) + "&"); // $15:url:当前页面url
																						// String
																						// 需要url编码[必填]

				sb.append("slotid=" + DataUtils.getData(slotid) + "&"); // $9
																		// slotid
				sb.append("adid=" + DataUtils.getData(adid) + "&"); // $9 adid
				sb.append("murl=" + URLEncoder.encode(DataUtils.getData(murl)) + "&"); // $9
																						// murl

				sb.append("uid=" + DataUtils.getData(uid) + "&"); // $9
				// uid,乐视网用户id
				sb.append("uuid=" + DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $10
				// uuid:(did)
				sb.append("lc=-" + "&"); // $11 lc:(letv cookie)
				// 是用来唯一标识用户的，用户即使用不同的浏览器，lc都是相同的，可以参考flash
				// cookie
				sb.append("ref=" + URLEncoder.encode(DataUtils.getData(ref)) + "&"); // $5
																						// ref
																						// 来源频道
				sb.append("rcid=" + DataUtils.getData(rcid) + "&"); // $5 rcid:
																	// 来源频道

				sb.append("ch=-" + "&");// $13 ch:登录渠道
				sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $13:pcode
				sb.append("auid=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $12
																									// auid,设备id
				sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $14: ilu是否为登录用户
																// 0：登录 1：非登录
				sb.append("r=" + System.currentTimeMillis()); // $18: 随机数
				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendQueryInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_AD2_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatNewADUrl() + sb.toString();
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * @category 说明 页面广告 本日志数据只包括页面广告的数据，不包括播放器广告(播放器广告上报标准不变)
	 *           本日志规范适用于移动、PC、TV等各个业务线，通过业务线代码区分 key和value的值都不允许包含 &
	 *           符号，如果有包含，要对key/value值进行URL编码 上报地址：http://dc.letv.com/pad/?
	 *           
	 *           2014-04-30  所有统计增加zid字段
	 *           
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
	 * 			      专题id  String 2014年4月30日增加
 	 */
	public void sendADNewInfo(final Context context, final String p1, final String p2, final String ac,
			final String pp, final String cid, final String url, final String slotid, final String adid,
			final String murl, final String uid, final String ref, final String rcid, final String pcode, final int ilu, final String zid) {
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
				// 新版从
				// 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
				// p1:一级产品线代码
				// 为从0开始的数字
				sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
				// p2:二级产品线代码
				// 已一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001" + "&");// $4 p3 三级产品线代码 平台自定义
				sb.append("ac=" + DataUtils.getData(ac) + "&"); // $5
																// ac:pv/click
																// int 0:pv;
																// 1:click
				sb.append("pp=" + URLEncoder.encode(DataUtils.getData(pp)) + "&"); // $15:pp:广告属性
																					// String
																					// 业务线自己维护，可以存储任何值或者多个值，但是如果包括
																					// &，要进行URL编码
																					// [选填]
				sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&"); // $5 cid
				sb.append("url=" + URLEncoder.encode(DataUtils.getData(url)) + "&"); // $15:url:当前页面url
																						// String
																						// 需要url编码[必填]

				sb.append("slotid=" + DataUtils.getData(slotid) + "&"); // $9
																		// slotid
				sb.append("adid=" + DataUtils.getData(adid) + "&"); // $9 adid
				sb.append("murl=" + URLEncoder.encode(DataUtils.getData(murl)) + "&"); // $9
																						// murl

				sb.append("uid=" + DataUtils.getData(uid) + "&"); // $9
				// uid,乐视网用户id
				sb.append("uuid=" + DataUtils.getData(DataUtils.getUUID(context)) + "&"); // $10
				// uuid:(did)
				sb.append("lc=-" + "&"); // $11 lc:(letv cookie)
				// 是用来唯一标识用户的，用户即使用不同的浏览器，lc都是相同的，可以参考flash
				// cookie
				sb.append("ref=" + URLEncoder.encode(DataUtils.getData(ref)) + "&"); // $5
																						// ref
																						// 来源频道
				sb.append("rcid=" + DataUtils.getData(rcid) + "&"); // $5 rcid:
																	// 来源频道

				sb.append("ch=-" + "&");// $13 ch:登录渠道
				sb.append("pcode=" + DataUtils.getData(pcode) + "&");// $13:pcode
				sb.append("auid=" + DataUtils.getData(DataUtils.generateDeviceId(context)) + "&"); // $12
																									// auid,设备id
				sb.append("ilu=" + (ilu > 0 ? 1 : 0) + "&"); // $14: ilu是否为登录用户
																// 0：登录 1：非登录
				sb.append("zid=" + replaceStr(DataUtils.getData(DataUtils.getTrimData(zid))) + "&");
				sb.append("r=" + System.currentTimeMillis()); // $18: 随机数
				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendQueryInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_AD2_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatNewADUrl() + sb.toString();
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	/**
	 * error 错误码上报
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
	 * 				
	 * @param vid
	 * 			      视频id
	 * @param src
	 *            用于区分不同日志上报的环境来源标识	String	取各日志接口的名称：如播放日志上报的，此字段填写pl，pv日志填写pgv [选填] 2013年11月28日增加
	 * @param ep
	 * 			      错误属性	 string	
	 *			     业务线自己维护，可以存储任何值或者多个值，但是必须要进行URL编码，例如：ep上报值为k1=v1&k2=v2，k1=v1&k2=v2 应该URL编码成：k1%3dv1%26k2%3dv2，也就是上报的内容为：py=k1%3dv1%26k2%3dv2
	 * @param zid 
	 * 			  String  专题id
	 */
	public  void sendErrorInfo(final Context context, final String p1, final String p2, final String error,
			final String src,final String ep,final String cid,final String pid,final String vid,final String zid, final String ip)
	{
       android.util.Log.i("king","error = " +error +" ,ep = " + ep);
		ThreadPoolManager.getInstance().executeThreadWithPool(new Runnable() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append("ver=" + DataUtils.getData(DataConstant.STAT_VERSION) + "&");// $1:日志版本号
				// 新版从
				// 2.0开始
				sb.append("p1=" + DataUtils.getData(DataUtils.getTrimData(p1)) + "&");// $2
				// p1:一级产品线代码
				// 为从0开始的数字
				sb.append("p2=" + DataUtils.getData(DataUtils.getTrimData(p1 + p2)) + "&");// $3
				// p2:二级产品线代码
				// 已一级产品线代码为前缀，接下来从0开始
				sb.append("p3=001" + "&");// $4 p3 三级产品线代码 平台自定义
				sb.append("err="+DataUtils.getData(DataUtils.getTrimData(error)) + "&");
				sb.append("lc=-" + "&"); // $11 lc:(letv cookie)
				// 是用来唯一标识用户的，用户即使用不同的浏览器，lc都是相同的，可以参考flash
				// cookie
				sb.append("auid=" + DataUtils.getData(DataUtils.getTrimData(DataUtils.generateDeviceId(context))) + "&");  //目前各端上报的参数名称还是auid，pc端auid为空，移动端和tv端均需要上报自定义的唯一设备id[必填]
				sb.append("ip=" + DataUtils.getData(DataUtils.getTrimData(ip)) + "&");  //ip地址
				sb.append("mac=" + DataUtils.getData(DataUtils.getTrimData(DataUtils.getMacAddress(context))) + "&");  //mac地址
				sb.append("nt=" + DataUtils.getData(DataUtils.getTrimData(DataUtils.getNetType(context))) + "&");  //Net type:上网类型   取值：wifi/4g/3g/2g/pc [必填]
				sb.append("os=" + DataUtils.getData(DataUtils.getTrimData(DataUtils.getSystemName())) + "&");  //操作系统   操作系统
				sb.append("osv=" + DataUtils.getData(DataUtils.getTrimData(DataUtils.getOSVersionName())) + "&");  //操作系统版本   [选填]
				sb.append("app=" + DataUtils.getData(DataUtils.getTrimData(DataUtils.getClientVersionName(context))) + "&");  //应用版本号
				sb.append("bd=" + URLEncoder.encode(DataUtils.getData(DataUtils.getTrimData(DataUtils.getBrandName()))) + "&");  //终端品牌
				sb.append("xh=phone" + "&");  //终端型号
				sb.append("model=" + DataUtils.getData(DataUtils.getTrimData(DataUtils.getDeviceName())) + "&");  //设备型号
				sb.append("ro=" + DataUtils.getNewResolution(context) + "&");  //Resolution:分辨率  示例：1024_768 [选填]
				sb.append("br=other" + "&");  //Browser浏览器名称
				sb.append("src=" + DataUtils.getData(DataUtils.getTrimData(src)) + "&");  //用于区分不同日志上报的环境来源标识
				//取各日志接口的名称：如播放日志上报的，此字段填写pl，pv日志填写pgv [选填] 2013年11月28日增加
                String eps;
                if(TextUtils.isEmpty(ep)){
                    eps="-"+"&time="+DataUtils.timeClockString("yyyyMMdd_hh:mm:ss");
                }else{
                    eps=ep+"&time="+DataUtils.timeClockString("yyyyMMdd_hh:mm:ss");
                }
				sb.append("ep=" + URLEncoder.encode(DataUtils.getData(DataUtils.getTrimData(eps))) + "&");  //er property 错误属性
				//业务线自己维护，可以存储任何值或者多个值，但是必须要进行URL编码，例如：ep上报值为k1=v1&k2=v2，k1=v1&k2=v2 应该URL编码成：k1%3dv1%26k2%3dv2，也就是上报的内容为：py=k1%3dv1%26k2%3dv2
				sb.append("cid=" + replaceStr(DataUtils.getData(cid)) + "&"); // $5 cid
				sb.append("pid=" + replaceStr(DataUtils.getData(pid)) + "&"); // $5 pid
				sb.append("vid=" + replaceStr(DataUtils.getData(vid)) + "&"); // $5 vid
				sb.append("zid=" + replaceStr(DataUtils.getData(DataUtils.getTrimData(zid))) + "&");
				sb.append("r=" + System.currentTimeMillis()); // $18: 随机数
				if (DataStatistics.getInstance().isDebug()) {
					Log.d(DataStatistics.TAG, "sendErrorInfo:" + sb.toString());
				}
				sendLocalTestStatis(context, DataConstant.STAT_LOCAL_ERROR_URL, sb.toString());
				try {
					String cacheData = DataStatistics.getInstance().getStatErrorLogUrl() + sb.toString();
					HttpEngine.getInstance().doHttpGet(context,
							new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis()));
				} catch (HttpDataParserException e) {
					e.printStackTrace();
				} catch (HttpDataConnectionException e) {
					e.printStackTrace();
				}
			}
		});
	}
	

	/**
	 * 测试期间 数据上报 刘博本地服务器一次
	 * 
	 * @param context
	 * @param upUrl
	 * @param datas
	 */
	private void sendLocalTestStatis(Context context, String upUrl, String datas) {
        String cacheData = upUrl + datas;
        StatisCacheBean statisCacheBean = new StatisCacheBean(cacheData, cacheData, System.currentTimeMillis());
		if (DataStatistics.getInstance().isDebug()) {
			try {
				HttpEngine.getInstance().doHttpGet(context,statisCacheBean);
			} catch (HttpDataParserException e) {
				e.printStackTrace();
			} catch (HttpDataConnectionException e) {
				e.printStackTrace();
			}
		}
	}
	// ================================================================================
	// =============================Version2.0===end===================================
	// ================================================================================
}
