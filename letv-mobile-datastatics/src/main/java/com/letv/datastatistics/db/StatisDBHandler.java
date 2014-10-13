package com.letv.datastatistics.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.letv.datastatistics.dao.StatisCacheBean;
import com.letv.datastatistics.util.DataConstant;

public class StatisDBHandler {
	/**
	 * 保存记录
	 */
	public synchronized static boolean saveLocalCache(Context context, StatisCacheBean mStatisCacheBean) {
		if (mStatisCacheBean != null) {
//			Log.e("statisdbhandler", "saveLocalCache----->");
			if (hasByCacheId(context, mStatisCacheBean.getCacheId())) {
				updateByCacheId(context, mStatisCacheBean);
			} else {
				ContentValues cv = new ContentValues();
				cv.put(DataConstant.StaticticsCacheTrace.Field.CACHEID,
						mStatisCacheBean.getCacheId());
				cv.put(DataConstant.StaticticsCacheTrace.Field.CACHEDATA,
						mStatisCacheBean.getCacheData());
				cv.put(DataConstant.StaticticsCacheTrace.Field.CACHETIME,
						mStatisCacheBean.getCacheTime());
				context.getContentResolver().insert(StatisContentProvider.URI_STATIS, cv);
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据 cacheID 更新数据表
	 */
	public static void updateByCacheId(Context context, StatisCacheBean mStatisCacheBean) {
		// TODO Auto-generated method stub
		if (mStatisCacheBean != null) {
//			Log.e("statisdbhandler", "updateByCacheId---->");
			ContentValues cv = new ContentValues();
			cv.put(DataConstant.StaticticsCacheTrace.Field.CACHEID,
					mStatisCacheBean.getCacheId());
			cv.put(DataConstant.StaticticsCacheTrace.Field.CACHEDATA,
					mStatisCacheBean.getCacheData());
			cv.put(DataConstant.StaticticsCacheTrace.Field.CACHETIME,
					mStatisCacheBean.getCacheTime());
			context.getContentResolver().update(StatisContentProvider.URI_STATIS, cv,
					DataConstant.StaticticsCacheTrace.Field.CACHEID + "=?",
					new String[] { mStatisCacheBean.getCacheId() });
		}
	}

	/**
	 * 根据cacheId获取数据
	 * 
	 * @return LocalCacheBean
	 */
	public synchronized static StatisCacheBean getCacheByCacheId(Context context, String cacheId) {
		Cursor cursor = null;
		StatisCacheBean mLocalCacheBean = null;
		try {
			cursor = context.getContentResolver().query(StatisContentProvider.URI_STATIS,
					null, DataConstant.StaticticsCacheTrace.Field.CACHEID + "= ?",
					new String[] { cacheId }, null);
			if (cursor != null && cursor.moveToNext()) {
				mLocalCacheBean = new StatisCacheBean(
						cursor.getString(cursor
								.getColumnIndex(DataConstant.StaticticsCacheTrace.Field.CACHEID)),
						cursor.getString(cursor
								.getColumnIndex(DataConstant.StaticticsCacheTrace.Field.CACHEDATA)),
						cursor.getLong(cursor
								.getColumnIndex(DataConstant.StaticticsCacheTrace.Field.CACHETIME)));
			}
		} finally {
			closeCursor(cursor);
		}
		return mLocalCacheBean;
	}
	/**
	 * 获取所有的记录
	 * @param context
	 * @return
	 */
	public static ArrayList<StatisCacheBean> getAllCacheTrace(Context context){
		Cursor cursor = null ;
		ArrayList<StatisCacheBean> list = null;
		try{
			cursor = context.getContentResolver().query(StatisContentProvider.URI_STATIS, null, null, null, DataConstant.StaticticsCacheTrace.Field.CACHETIME + " desc");
			list = new ArrayList<StatisCacheBean>();
			while(cursor.moveToNext()){
				StatisCacheBean mStatisCacheBean = new StatisCacheBean(
						cursor.getString(cursor
								.getColumnIndex(DataConstant.StaticticsCacheTrace.Field.CACHEID)),
						cursor.getString(cursor
								.getColumnIndex(DataConstant.StaticticsCacheTrace.Field.CACHEDATA)),
						cursor.getLong(cursor
								.getColumnIndex(DataConstant.StaticticsCacheTrace.Field.CACHETIME)));
//				Log.e("statisdbhandler", "getAllCacheTrace---->");
				list.add(mStatisCacheBean);
			}
		}finally{
			closeCursor(cursor);
		}
		return list ;
	}
	/**
	 * 清除所有记录
	 * */
	public synchronized  static void clearAll(Context context) {
		context.getContentResolver().delete(StatisContentProvider.URI_STATIS, null, null);
	}

	/**
	 * 根据cacheId删除一条的记录
	 */
	public synchronized static void deleteByCacheId(Context context, String cacheId) {
//		Log.e("statisdbhandler", "deleteByCacheId----->");
		context.getContentResolver().delete(StatisContentProvider.URI_STATIS,
				DataConstant.StaticticsCacheTrace.Field.CACHEID + "= ?",
				new String[] { cacheId });
	}

	/**
	 * 根据cacheId查询数据表是否有记录
	 * 
	 * @param cacheId
	 * @return
	 */
	public synchronized static boolean hasByCacheId(Context context, String cacheId) {
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(StatisContentProvider.URI_STATIS,
					new String[] { DataConstant.StaticticsCacheTrace.Field.CACHEID },
					DataConstant.StaticticsCacheTrace.Field.CACHEID + "= ?",
					new String[] { cacheId }, null);
			return cursor.getCount() > 0;
		} finally {
			closeCursor(cursor);
		}
	}
	/**
	 * 关闭数据库Cursor对象
	 */
	public static void closeCursor(Cursor cursor) {
		if (null != cursor) {
			if (!cursor.isClosed()) {
				cursor.close();
			}
		}
	}
}
