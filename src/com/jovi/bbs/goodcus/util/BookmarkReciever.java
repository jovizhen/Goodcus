package com.jovi.bbs.goodcus.util;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Place;

public class BookmarkReciever extends BroadcastReceiver
{
	private ArrayList<Place> m_model;
	private SearchResultListAdapter m_adapter;
	
	public BookmarkReciever(ArrayList<Place> m_model, SearchResultListAdapter m_adapter)
	{
		this.m_model = m_model;
		this.m_adapter = m_adapter;
	}
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Bundle dataBundle = intent.getExtras();
		boolean isBookmarked = dataBundle.getBoolean("isBookmarked");
		String placeId = dataBundle.getString("placeId");
		Place place = CollecttionHelper.findPlaceById(m_model, placeId);
		if(place!=null)
		{
			place.setBookmark(isBookmarked);
			m_adapter.notifyDataSetChanged();
		}
	}
}