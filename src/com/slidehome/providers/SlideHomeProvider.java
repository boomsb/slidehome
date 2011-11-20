package com.slidehome.providers;

import java.util.HashMap;

import com.slidehome.providers.AppTrayItem.AppTrayItems;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * 
 * @author Bradley Booms <bradley.booms@gmail.com>
 *
 */
public class SlideHomeProvider extends ContentProvider {
	
	public static final String URI_BASE = "com.slidehome.providers.SlideHomeProvider";
	
	private static final String DATABASE_NAME = "slidehome.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String APPTRAY_TABLE_NAME = "apptray";
	private static final int APPTRAYITEMS = 1;
	private static final HashMap<String, String> appTrayItemsProjectionMap;
	
	private static final UriMatcher sUriMatcher;
	
	private static class DatabaseHelper extends SQLiteOpenHelper{

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + APPTRAY_TABLE_NAME + "("
					+ AppTrayItems.APP_TRAY_ITEM_ID + " INTEGER PRIMARY KEY, "
					+ AppTrayItems.PAGE + " INTEGER, "
					+ AppTrayItems.POSITION + " INTEGER, "
					+ AppTrayItems.PACKAGE + " TEXT, "
					+ AppTrayItems.CLASS + " TEXT);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + APPTRAY_TABLE_NAME);
			onCreate(db);
		}
	}
	
	private DatabaseHelper mDatabaseHelper; 
	
	@Override
	public int delete(Uri uri, String whereClause, String[] whereArgs) {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
			case APPTRAYITEMS:
				count = db.delete(APPTRAY_TABLE_NAME, whereClause, whereArgs);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
			case APPTRAYITEMS:
				return AppTrayItems.CONTENT_TYPE;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		
		ContentValues values;
		if (initialValues == null) {
			values = new ContentValues();
		} else {
			values = new ContentValues(initialValues);
		}
		
		switch (sUriMatcher.match(uri)) {
			case APPTRAYITEMS:
				long rowId = db.insert(APPTRAY_TABLE_NAME, AppTrayItems.PACKAGE, values);
				if (rowId > 0) {
					Uri itemUri = ContentUris.withAppendedId(uri, rowId);
					getContext().getContentResolver().notifyChange(itemUri, null);
					return itemUri;
				}
				throw new SQLException("Failed to insert item at URI " + uri);
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public boolean onCreate() {
		mDatabaseHelper = new DatabaseHelper(getContext());
		mDatabaseHelper.getWritableDatabase();
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        
        switch (sUriMatcher.match(uri)) {
			case APPTRAYITEMS:
				qb.setTables(APPTRAY_TABLE_NAME);
				qb.setProjectionMap(appTrayItemsProjectionMap);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}

		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String whereClause, String[] whereArgs) {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
			case APPTRAYITEMS:
				count = db.update(APPTRAY_TABLE_NAME, values, whereClause, whereArgs);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(URI_BASE, APPTRAY_TABLE_NAME, APPTRAYITEMS);
		
		appTrayItemsProjectionMap = new HashMap<String, String>();
		appTrayItemsProjectionMap.put(AppTrayItems._ID, AppTrayItems._ID);
		appTrayItemsProjectionMap.put(AppTrayItems.PAGE, AppTrayItems.PAGE);
		appTrayItemsProjectionMap.put(AppTrayItems.POSITION, AppTrayItems.POSITION);
		appTrayItemsProjectionMap.put(AppTrayItems.PACKAGE, AppTrayItems.PACKAGE);
		appTrayItemsProjectionMap.put(AppTrayItems.CLASS, AppTrayItems.CLASS);
	}
}
