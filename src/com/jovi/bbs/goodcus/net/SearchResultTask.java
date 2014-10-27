package com.jovi.bbs.goodcus.net;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jovi.bbs.goodcus.R;
import com.jovi.bbs.goodcus.net.googlePlacesApi.CustomGooglePlaces;
import com.jovi.bbs.goodcus.net.googlePlacesApi.GooglePlaceFilter;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Place;
import com.jovi.bbs.goodcus.util.CollecttionHelper;
import com.jovi.bbs.goodcus.util.FavoriteDBDataSource;
import com.jovi.bbs.goodcus.util.SearchResultListAdapter;
import com.jovi.bbs.goodcus.widgets.XListView;



public class SearchResultTask extends AsyncTask<String, Void, List<Place>>
{

	private Activity hostActivity;
	private CustomGooglePlaces googlePlacesClient;
	private GooglePlaceFilter  googlePlaceFilter;
	
	private ArrayList<Place> m_model;
	private SearchResultListAdapter m_adapter;
	private FavoriteDBDataSource favoriteDBDataSource;
	
	private ProgressBar m_pBar;
	private XListView m_listView; 
	
	private Location currentLocation;
	private int status;
	
	public SearchResultTask(Activity hostActivity, 
			CustomGooglePlaces googlePlacesClient, 
			GooglePlaceFilter googlePlaceFilter, 
			ArrayList<Place> m_model, 
			SearchResultListAdapter m_adapter, 
			FavoriteDBDataSource favoriteDBDataSource, 
			ProgressBar m_pBar,
			XListView m_ListView,
			Location currentLocation)
	{
		this.hostActivity = hostActivity;
		this.googlePlacesClient = googlePlacesClient;
		this.googlePlaceFilter = googlePlaceFilter;
		this.m_model = m_model;
		this.m_adapter = m_adapter;
		this.favoriteDBDataSource = favoriteDBDataSource;
		this.m_pBar = m_pBar;
		this.m_listView = m_ListView;
		this.currentLocation = currentLocation;
	}
	
	@Override
	protected List<Place> doInBackground(String... urls)
	{
		List<Place> placeList = new ArrayList<Place>();
		try
		{
			placeList = googlePlacesClient.getNearbyPlaces(currentLocation.getLatitude(), 
					currentLocation.getLongitude(), 10000, googlePlaceFilter);
			status =googlePlacesClient.getStatusCode(); 
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return placeList;
	}
	
	@Override
	protected void onPostExecute(List<Place> searchResult)
	{
		super.onPostExecute(searchResult);
		switch (status)
		{
			case CustomGooglePlaces.STATUS_CODE_OK:
				restoreBookmarkToSearchResults((ArrayList<Place>) searchResult);
				m_model.addAll(searchResult);
				m_adapter.notifyDataSetChanged();
				break;

			case CustomGooglePlaces.STATUS_CODE_REQUEST_DENIED:
				Toast.makeText(hostActivity, R.string.net_timeout, Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
		}
		if(m_listView!=null)
		{
			if (googlePlacesClient.getPageToken() == null)
			{
				m_listView.setPullLoadEnable(false);
			}
			else 
			{
				m_listView.setPullLoadEnable(true);
			}
			
			if(m_listView.getPullLoading())
			{
				m_listView.stopLoadMore();
			}
			if(m_listView.getPullRefreshing())
			{
				m_listView.stopRefresh();
			}
		}
		
		m_pBar.setVisibility(View.GONE);
	}
	
	private void restoreBookmarkToSearchResults(ArrayList<Place> searchResult)
	{
		ArrayList<String> bookmarkPresentList = favoriteDBDataSource.getAllFavoritePresentInList(CollecttionHelper.getPlaceIdList(searchResult));
		for (String placeId : bookmarkPresentList)
		{
			Place aPlace = CollecttionHelper.findPlaceById(searchResult, placeId);
			if (aPlace != null)
			{
				aPlace.setBookmark(true);
			}
		}
	}
}
