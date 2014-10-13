package com.jovi.bbs.goodcus.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper
{
	public static final String TABLE_FAVORITES = "favorites";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_PLACE_ID = "place_id";
	public static final String COLUMN_PLACE_NAME = "place_name";
	public static final String COLUMN_PLACE_ADDRESS = "place_address";
	public static final String COLUMN_LAT = "place_lat";
	public static final String COLUMN_LON = "place_lon";
	public static final String COLUMN_PLACE_RATING = "place_rating";
	
	private static final String DATABASE_NAME = "goodcus.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = "create table " + TABLE_FAVORITES + "(" + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_PLACE_ID + " text not null, " + COLUMN_PLACE_NAME + " text, " + COLUMN_PLACE_ADDRESS + " text, "+COLUMN_LAT + " text, "+
			COLUMN_LON + " text, "+ COLUMN_PLACE_RATING + " text);";

	public MySQLiteHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.w(MySQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		onCreate(db);
	}
}
