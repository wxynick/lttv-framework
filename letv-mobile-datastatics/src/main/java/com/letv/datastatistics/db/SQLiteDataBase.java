package com.letv.datastatistics.db;

import com.letv.datastatistics.util.DataConstant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDataBase extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;

	/**
	 * 数据库名称
	 */
	public static final String DATABASE_NAME = "datastatistics.db";

	public SQLiteDataBase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTable_StaticticsCacheTrace(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	private void createTable_StaticticsCacheTrace(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + DataConstant.StaticticsCacheTrace.TABLE_NAME + "("
				+ DataConstant.StaticticsCacheTrace.Field.CACHEID + " TEXT PRIMARY KEY,"
				+ DataConstant.StaticticsCacheTrace.Field.CACHETIME + " TEXT,"
				+ DataConstant.StaticticsCacheTrace.Field.CACHEDATA + " TEXT" + ");");
	}

}
