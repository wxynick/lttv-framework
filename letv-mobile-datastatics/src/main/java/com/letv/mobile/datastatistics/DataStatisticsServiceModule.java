package com.letv.mobile.datastatistics;

import java.util.ArrayList;

import android.content.Context;
import android.provider.ContactsContract.Contacts.Data;

import com.letv.datastatistics.DataStatistics;
import com.letv.datastatistics.dao.StatisCacheBean;
import com.letv.datastatistics.entity.DataStatusInfo;
import com.letv.datastatistics.entity.StatisticsVideoInfo;
import com.letv.mobile.android.app.IAndroidAppContext;
import com.letv.mobile.core.microkernel.api.AbstractModule;

public class DataStatisticsServiceModule<T extends IAndroidAppContext> extends AbstractModule<T> implements IDataStatisticsService {

	@Override
	public void sendUserInfo(Context context, String uid, String pcode, String source, String stat, String onlen) {
		DataStatistics.getInstance().sendUserInfo(context, uid, pcode, source, stat, onlen);

	}

	@Override
	public void sendErrorT(Context context, String act, String error) {
		DataStatistics.getInstance().sendErrorT(context, act, error);
	}

	@Override
	public void sendVideoInfo(Context context, StatisticsVideoInfo mStatisticsVideoInfo) {
		DataStatistics.getInstance().sendVideoInfo(context, mStatisticsVideoInfo);
	}

	@Override
	public void sendADInfo(Context context, String uid, String pcode, String adtype, String adid, String actionid, String clicknum, String durTime, String playedTime, String utime, String cid,
			String pid, String vid, String vlen, String ptid, String adsystem) {
		DataStatistics.getInstance().sendADInfo(context, uid, pcode, adtype, adid, actionid, clicknum, durTime, playedTime, utime, cid, pid, vid, vlen, ptid, adsystem);
	}

	@Override
	public void sendActionCode(Context context, String code, String extCode, String uid, String pcode, String adsystem, String rate) {
		DataStatistics.getInstance().sendActionCode(context, code, extCode, uid, pcode, adsystem, rate);
	}

	@Override
	public void uploadClientData(Context context, String pcode) {
		DataStatistics.getInstance().uploadClientData(context, pcode);
	}

	@Override
	public DataStatusInfo getDataStatusInfo(Context context, String pcode) {
		return DataStatistics.getInstance().getDataStatusInfo(context, pcode);
	}

	@Override
	public void sendRecommendInfo(Context context, String p1, String p2, String pcode, int acode, String ap, String ar, String cid, String pid, String vid, String uid, String cur_url, int ilu) {
		DataStatistics.getInstance().sendRecommendInfo(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, ilu);
	}

	@Override
	public void sendPlayInfo(Context context, String p1, String p2, String ac, String err, String pt, String ut, String uid, String uuidTimeStamp, String cid, String pid, String vid, String vlen,
			String retryCount, String type, String vt, String url, String ref, String py, String st, String weid, String pcode, int ilu, String ch) {
		DataStatistics.getInstance().sendPlayInfo(context, p1, p2, ac, err, pt, ut, uid, uuidTimeStamp, cid, pid, vid, vlen, retryCount, type, vt, url, ref, py, st, weid, pcode, ilu, ch);
	}

	@Override
	public void sendPlayInfo24New(Context context, String p1, String p2, String ac, String err, String pt, String ut, String uid, String uuidTimeStamp, String cid, String pid, String vid,
			String vlen, String retryCount, String type, String vt, String url, String ref, String py, String st, String weid, String pcode, int ilu, String ch, String zid) {
		DataStatistics.getInstance().sendPlayInfo24New(context, p1, p2, ac, err, pt, ut, uid, uuidTimeStamp, cid, pid, vid, vlen, retryCount, type, vt, url, ref, py, st, weid, pcode, ilu, ch, zid);
	}

	@Override
	public void sendPlayInfo24New(Context context, String p1, String p2, String ac, String err, String pt, String ut, String uid, String uuidTimeStamp, String cid, String pid, String vid,
			String vlen, String retryCount, String type, String vt, String url, String ref, String py, String st, String weid, String pcode, int ilu, String ch, String zid, int ap) {
		DataStatistics.getInstance().sendPlayInfo24New(context, p1, p2, ac, err, pt, ut, uid, uuidTimeStamp, cid, pid, vid, vlen, retryCount, type, vt, url, ref, py, st, weid, pcode, ilu, ch, zid, ap);
	}

	@Override
	public void sendLivePlayInfo(Context context, String p1, String p2, String ac, String err, String pt, String ut, String uid, String uuidTimeStamp, String cid, String pid, String vid, String vlen,
			String retryCount, String type, String vt, String url, String ref, String py, String st, String weid, String pcode, int ilu, String ch, String lc) {
		DataStatistics.getInstance().sendLivePlayInfo(context, p1, p2, ac, err, pt, ut, uid, uuidTimeStamp, cid, pid, vid, vlen, retryCount, type, vt, url, ref, py, st, weid, pcode, ilu, ch, lc);
	}

	@Override
	public void sendLivePlayInfo25New(Context context, String p1, String p2, String ac, String err, String pt, String ut, String uid, String uuidTimeStamp, String cid, String pid, String vid,
			String vlen, String retryCount, String type, String vt, String url, String ref, String py, String st, String weid, String pcode, int ilu, String ch, String lc, String zid) {
		DataStatistics.getInstance().sendLivePlayInfo25New(context, p1, p2, ac, err, pt, ut, uid, uuidTimeStamp, cid, pid, vid, vlen, retryCount, type, vt, url, ref, py, st, weid, pcode, ilu, ch, lc, zid);
	}

	@Override
	public void sendLivePlayInfo25New(Context context, String p1, String p2, String ac, String err, String pt, String ut, String uid, String uuidTimeStamp, String cid, String pid, String vid,
			String vlen, String retryCount, String type, String vt, String url, String ref, String py, String st, String weid, String pcode, int ilu, String ch, String lc, String zid, String lid) {
		DataStatistics.getInstance().sendLivePlayInfo25New(context, p1, p2, ac, err, pt, ut, uid, uuidTimeStamp, cid, pid, vid, vlen, retryCount, type, vt, url, ref, py, st, weid, pcode, ilu, ch, lc, zid, lid);
	}

	@Override
	public void sendActionInfo(Context context, String p1, String p2, String pcode, String acode, String ap, String ar, String cid, String pid, String vid, String uid, String cur_url, String area,
			String bucket, String rank, int ilu) {
		DataStatistics.getInstance().sendActionInfo(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu);
	}

	@Override
	public void sendActionInfo(Context context, String p1, String p2, String pcode, String acode, String ap, String ar, String cid, String pid, String vid, String uid, String cur_url, String area,
			String bucket, String rank, int ilu, String zid) {
		DataStatistics.getInstance().sendActionInfo(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu, zid);
	}

	@Override
	public void sendActionInfoAddLid(Context context, String p1, String p2, String pcode, String acode, String ap, String ar, String cid, String pid, String vid, String uid, String cur_url,
			String area, String bucket, String rank, int ilu, String zid, String lid) {
		DataStatistics.getInstance().sendActionInfoAddLid(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu, zid, lid);
	}

	@Override
	public void sendActionInforef(Context context, String p1, String p2, String pcode, String acode, String ap, String ar, String cid, String pid, String vid, String uid, String cur_url, String area,
			String bucket, String rank, int ilu, String zid) {
		DataStatistics.getInstance().sendActionInforef(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu, zid);
	}

	@Override
	public void sendActionInfotime(Context context, String p1, String p2, String pcode, String acode, String ap, String ar, String cid, String pid, String vid, String uid, String cur_url,
			String area, String bucket, String rank, int ilu, String zid, String time) {
		DataStatistics.getInstance().sendActionInfotime(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu, zid, time);
	}

	@Override
	public void sendActionInfo(Context context, String p1, String p2, String pcode, String acode, String ap, String ar, String cid, String pid, String vid, String uid, String cur_url, String area,
			String bucket, String rank, int ilu, String zid, String lid) {
		DataStatistics.getInstance().sendActionInfo(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu);
	}

	@Override
	public void sendActionInfoAddTargetUrl(Context context, String p1, String p2, String pcode, String acode, String ap, String ar, String cid, String pid, String vid, String uid, String cur_url,
			String area, String bucket, String rank, int ilu, String zid, String lid, String targeturl) {
		DataStatistics.getInstance().sendActionInfoAddTargetUrl(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu, zid, lid, targeturl);
	}

	@Override
	public void sendActionInfos(Context context, String p1, String p2, String pcode, String acode, String ap, String ar, String cid, String pid, String vid, String uid, String cur_url, String area,
			String bucket, String rank, int ilu, String zid, String scid) {
		DataStatistics.getInstance().sendActionInfos(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu, zid, scid);
	}

	@Override
	public void sendActionInfoAddFragid(Context context, String p1, String p2, String pcode, String acode, String ap, String ar, String cid, String pid, String vid, String uid, String cur_url,
			String area, String bucket, String rank, int ilu, String zid, String scid, String fragid) {
		DataStatistics.getInstance().sendActionInfoAddFragid(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu, zid, scid, fragid);
	}

	@Override
	public void sendActionInfoAddFragId(Context context, String p1, String p2, String pcode, String acode, String ap, String ar, String cid, String pid, String vid, String uid, String cur_url,
			String area, String bucket, String rank, int ilu, String zid, String lid, String fragid) {
		DataStatistics.getInstance().sendActionInfoAddFragId(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu, zid, lid, fragid);
	}

	@Override
	public void sendPushActionInfo(Context context, String p1, String p2, String pcode, String acode, String ap, String ar, String cid, String pid, String vid, String uid, String cur_url,
			String area, String bucket, String rank, int ilu) {
		DataStatistics.getInstance().sendPushActionInfo(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu);
	}

	@Override
	public void sendPushActionInfo(Context context, String p1, String p2, String pcode, String acode, String ap, String ar, String cid, String pid, String vid, String uid, String cur_url,
			String area, String bucket, String rank, int ilu, String lid, String zid) {
		DataStatistics.getInstance().sendPushActionInfo(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu, lid, zid);
	}

	@Override
	public void sendPushActionInfoAddApp(Context context, String p1, String p2, String pcode, String acode, String ap, String ar, String cid, String pid, String vid, String uid, String cur_url,
			String area, String bucket, String rank, int ilu, String lid, String zid) {
		DataStatistics.getInstance().sendPushActionInfoAddApp(context, p1, p2, pcode, acode, ap, ar, cid, pid, vid, uid, cur_url, area, bucket, rank, ilu, lid, zid);
	}

	@Override
	public void sendPVInfo(Context context, String p1, String p2, String pcode, String cid, String pid, String vid, String uid, String ref, String ct, String rcid, String kw, String cur_url,
			String area, String weid, int ilu) {
		DataStatistics.getInstance().sendPVInfo(context, p1, p2, pcode, cid, pid, vid, uid, ref, ct, rcid, kw, cur_url, area, weid, ilu);
	}

	@Override
	public void sendPVInfo(Context context, String p1, String p2, String pcode, String cid, String pid, String vid, String uid, String ref, String ct, String rcid, String kw, String cur_url,
			String area, String weid, int ilu, String zid) {
		DataStatistics.getInstance().sendPVInfo(context, p1, p2, pcode, cid, pid, vid, uid, ref, ct, rcid, kw, cur_url, area, weid, ilu, zid);
	}

	@Override
	public void sendEnvInfo(Context context, String p1, String p2, String ip, String xh) {
		DataStatistics.getInstance().sendEnvInfo(context, p1, p2, ip, xh);
	}

	@Override
	public void sendEnvInfo(Context context, String p1, String p2, String ip, String xh, String zid) {
		DataStatistics.getInstance().sendEnvInfo(context, p1, p2, ip, xh, zid);
	}

	@Override
	public void sendLoginInfo(Context context, String p1, String p2, String uid, String lp, String ref, String ts, String pcode, int st) {
		DataStatistics.getInstance().sendLoginInfo(context, p1, p2, uid, lp, ref, ts, pcode, st);
	}

	@Override
	public void sendLoginInfo(Context context, String p1, String p2, String uid, String lp, String ref, String ts, String pcode, int st, String zid) {
		DataStatistics.getInstance().sendLoginInfo(context, p1, p2, uid, lp, ref, ts, pcode, st, zid);
	}

	@Override
	public void sendQueryInfo(Context context, String p1, String p2, String sid, String ty, String pos, String pid, String vid, String cid, String uid, int ilu, String query, String page, String rt) {
		DataStatistics.getInstance().sendQueryInfo(context, p1, p2, sid, ty, pos, pid, vid, cid, uid, ilu, query, page, rt);
	}

	@Override
	public void sendQueryInfo(Context context, String p1, String p2, String sid, String ty, String pos, String pid, String vid, String cid, String uid, int ilu, String query, String page, String rt,
			String zid) {
		DataStatistics.getInstance().sendQueryInfo(context, p1, p2, sid, ty, pos, pid, vid, cid, uid, ilu, query, page, rt, zid);
	}

	@Override
	public void sendADInfo(Context context, String p1, String p2, String ac, String pp, String cid, String url, String slotid, String adid, String murl, String uid, String ref, String rcid,
			String pcode, int ilu) {
		DataStatistics.getInstance().sendADInfo(context, p1, p2, ac, pp, cid, url, slotid, adid, murl, uid, ref, rcid, pcode, ilu);
	}

	@Override
	public void sendADInfo(Context context, String p1, String p2, String ac, String pp, String cid, String url, String slotid, String adid, String murl, String uid, String ref, String rcid,
			String pcode, int ilu, String zid) {
		DataStatistics.getInstance().sendADInfo(context, p1, p2, ac, pp, cid, url, slotid, adid, murl, uid, ref, rcid, pcode, ilu, zid);
	}

	@Override
	public void sendErrorInfo(Context context, String p1, String p2, String error, String src, String ep, String cid, String pid, String vid, String zid) {
		DataStatistics.getInstance().sendErrorInfo(context, p1, p2, error, src, ep, cid, pid, vid, zid);
	}

	@Override
	public ArrayList<StatisCacheBean> getAllErrorCache(Context context) {
		return DataStatistics.getInstance().getAllErrorCache(context);
	}

	@Override
	public void submitErrorInfo(Context context, StatisCacheBean mStatisCacheBean) {
		DataStatistics.getInstance().submitErrorInfo(context, mStatisCacheBean);
	}

	@Override
	public String getStatLoginUrl() {
		return DataStatistics.getInstance().getStatLoginUrl();
	}

	@Override
	public String getStatRecommendUrl() {
		return DataStatistics.getInstance().getStatRecommendUrl();
	}

	@Override
	public String getStatPlayerUrl() {
		
		return DataStatistics.getInstance().getStatPlayerUrl();
	}

	@Override
	public String getStatActionUrl() {
		return DataStatistics.getInstance().getStatActionUrl();
	}

	@Override
	public String getStatPVUrl() {
		return DataStatistics.getInstance().getStatPVUrl();
	}

	@Override
	public String getStatEnvUrl() {
		return DataStatistics.getInstance().getStatEnvUrl();
	}

	@Override
	public String getStatNewLoginUrl() {
		return DataStatistics.getInstance().getStatNewLoginUrl();
	}

	@Override
	public String getStatQueryUrl() {
		return DataStatistics.getInstance().getStatQueryUrl();
	}

	@Override
	public String getStatNewADUrl() {
		
		return DataStatistics.getInstance().getStatNewADUrl();
	}

	@Override
	public String getStatVideoCloseUrl() {
		return DataStatistics.getInstance().getStatVideoCloseUrl();
	}

	@Override
	public String getStatADUrl() {
		return DataStatistics.getInstance().getStatADUrl();
	}

	@Override
	public String getStatActionCodeUrl() {
		return DataStatistics.getInstance().getStatADUrl();
	}

	@Override
	public String getStatDownloadLogUrl() {
		return DataStatistics.getInstance().getStatDownloadLogUrl();
	}

	@Override
	public String getStatErrorLogUrl() {
		return DataStatistics.getInstance().getStatErrorLogUrl();
	}

	@Override
	public String getUploadClientDataUrl() {
		return DataStatistics.getInstance().getUploadClientDataUrl();
	}

	@Override
	public void close() {
		DataStatistics.getInstance().close();
	}

	@Override
	protected void initServiceDependency() {

	}

	@Override
	protected void startService() {
		context.registerService(IDataStatisticsService.class, this);

	}

	@Override
	protected void stopService() {
		context.unregisterService(IDataStatisticsService.class, this);
	}

}
