package com.letv.datastatistics.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.letv.datastatistics.util.DataConstant;

public class StatisContentProvider extends ContentProvider {

	public static String AUTHORITY;
	static {
		try {
			Properties properties = new Properties();
			InputStream in = StatisContentProvider.class.getClassLoader().getResourceAsStream("letv.properties");
			properties.load(in);
			AUTHORITY = properties.getProperty("StatisContentProvider.authorities");
			if(AUTHORITY==null){
				AUTHORITY="com.letv.datastatistics.db.StatisContentProvider";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static final Uri URI_STATIS = Uri.parse("content://" + AUTHORITY + "/" + DataConstant.StaticticsCacheTrace.TABLE_NAME);

	private static final int STATIS = 100;

	private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

	private SQLiteDataBase sqliteDataBase;

	static {
		URI_MATCHER.addURI(AUTHORITY, DataConstant.StaticticsCacheTrace.TABLE_NAME, STATIS);
	}
	
	@Override
	public boolean onCreate() {
		sqliteDataBase = new SQLiteDataBase(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		long rowId;
		Uri newUri = null;
		int match = URI_MATCHER.match(uri);
		SQLiteDatabase db = sqliteDataBase.getWritableDatabase();

		switch (match) {
		
		case STATIS:
			rowId = db.insert(DataConstant.StaticticsCacheTrace.TABLE_NAME , DataConstant.StaticticsCacheTrace.Field.CACHEID, values);
			if (rowId > 0) {
				newUri = ContentUris.withAppendedId(URI_STATIS, rowId);
			}
			break;

		default:
			throw new UnsupportedOperationException("Unknown or unsupported URL: " + uri.toString());

		}

		if (newUri != null) {
			getContext().getContentResolver().notifyChange(uri, null);
		}

		return newUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		int count;

		int match = URI_MATCHER.match(uri);
		SQLiteDatabase db = sqliteDataBase.getWritableDatabase();

		switch (match) {
		case STATIS:
			count = db.delete(DataConstant.StaticticsCacheTrace.TABLE_NAME, selection, selectionArgs);
			break;
		default:
			throw new UnsupportedOperationException("Unknown or unsupported URL: " + uri.toString());
		}

		this.getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

		int count;
		int match = URI_MATCHER.match(uri);
		SQLiteDatabase db = sqliteDataBase.getWritableDatabase();

		switch (match) {
		case STATIS:
			count = db.update(DataConstant.StaticticsCacheTrace.TABLE_NAME, values, selection, selectionArgs);
			break;
		default:
			throw new UnsupportedOperationException("Unknown or unsupported URL: " + uri.toString());
		}

		this.getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {

		Cursor cursor = null;
		int match = URI_MATCHER.match(uri);
		SQLiteDatabase db = sqliteDataBase.getWritableDatabase();
		switch (match) {
		case STATIS:
			cursor = db.query(DataConstant.StaticticsCacheTrace.TABLE_NAME, null, selection, selectionArgs,null, null, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			break;
		default:
			throw new UnsupportedOperationException("Unknown or unsupported URL: " + uri.toString());
		}
		return cursor;
	}
}
