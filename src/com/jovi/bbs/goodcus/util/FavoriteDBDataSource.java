package com.jovi.bbs.goodcus.util;

import java.util.ArrayList;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Place;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class FavoriteDBDataSource
{
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	private String[] allColumns =
	{ MySQLiteHelper.COLUMN_PLACE_ID, MySQLiteHelper.COLUMN_PLACE_NAME, MySQLiteHelper.COLUMN_PLACE_ADDRESS, 
			MySQLiteHelper.COLUMN_LAT, MySQLiteHelper.COLUMN_LON, MySQLiteHelper.COLUMN_PLACE_RATING };

	public FavoriteDBDataSource(Context context)
	{
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException
	{
		database = dbHelper.getWritableDatabase();
	}

	public void close()
	{
		dbHelper.close();
	}

	public void addToFavorite(Place place)
	{
		Cursor cursor = database.query(MySQLiteHelper.TABLE_FAVORITES, allColumns, MySQLiteHelper.COLUMN_PLACE_ID + " = '" + place.getPlaceId() + "'", null,
				null, null, null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast())
		{
			cursor.close();
			return;
		}
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_PLACE_ID, place.getPlaceId());
		values.put(MySQLiteHelper.COLUMN_PLACE_NAME, place.getName());
		values.put(MySQLiteHelper.COLUMN_PLACE_ADDRESS, place.getVicinity());
		values.put(MySQLiteHelper.COLUMN_LAT, place.getLatitude());
		values.put(MySQLiteHelper.COLUMN_LON, place.getLongitude());
		values.put(MySQLiteHelper.COLUMN_PLACE_RATING, place.getRating());
		database.insert(MySQLiteHelper.TABLE_FAVORITES, null, values);
	}

	public void removeFromFavorite(Place place)
	{
		database.delete(MySQLiteHelper.TABLE_FAVORITES, MySQLiteHelper.COLUMN_PLACE_ID + " = '" + place.getPlaceId() + "'", null);
	}

	public ArrayList<Place> getAllFavorites()
	{
		ArrayList<Place> favoriteList = new ArrayList<Place>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_FAVORITES, allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			Place aPlace = cursorToPlace(cursor);
			favoriteList.add(aPlace);
			cursor.moveToNext();
		}
		return favoriteList;
	}

	private Place cursorToPlace(Cursor cursor)
	{

		Place aPlace = new Place();
		aPlace.setPlaceId(cursor.getString(0)).setName(cursor.getString(1)).setVicinity(cursor.getString(2))
		.setLatitude(Double.parseDouble(cursor.getString(3))).setLongitude(Double.parseDouble(cursor.getString(4)))
		.setRating(Double.parseDouble(cursor.getString(5)));

		return aPlace;
	}

	private static FavoriteDBDataSource instance;

	public static FavoriteDBDataSource getInSource(Context context)
	{
		if (instance == null)
		{
			instance = new FavoriteDBDataSource(context);
		}
		return instance;
	}
}
