package com.jovi.bbs.goodcus.fragment;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.jovi.bbs.goodcus.App;
import com.jovi.bbs.goodcus.NearbyPage;
import com.jovi.bbs.goodcus.R;
import com.jovi.bbs.goodcus.SearchDetailsPage;
import com.jovi.bbs.goodcus.fragment.SearchResultFragmentFactory.SearchType;
import com.jovi.bbs.goodcus.net.googlePlacesApi.CustomGooglePlaces;
import com.jovi.bbs.goodcus.net.googlePlacesApi.GooglePlaceFilter;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Place;
import com.jovi.bbs.goodcus.util.BookmarkReciever;
import com.jovi.bbs.goodcus.util.CollecttionHelper;
import com.jovi.bbs.goodcus.util.FavoriteDBDataSource;
import com.jovi.bbs.goodcus.util.SearchResultListAdapter;
import com.jovi.bbs.goodcus.widgets.XListView;
import com.jovi.bbs.goodcus.widgets.XListView.IXListViewListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ClassfiedSearchResultFragment extends Fragment implements IXListViewListener, OnItemClickListener
{
	private static int previousIndex;
	private int status;

	private ProgressBar m_pBar;
	private XListView m_listView;

	private SearchResultListAdapter m_adapter;
	private ArrayList<Place> m_model = new ArrayList<Place>();
	
	private CustomGooglePlaces googlePlacesClient;
	private GooglePlaceFilter googlePlaceFilter;
	private BookmarkReciever mBookmarkReciever; 
	private LocationReciever mLocationReciever;
	private FavoriteDBDataSource favoriteDataSource;
	private Location currentLocation;
	private SearchType searchType; 

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		configure();
		loadModel(null);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_classfied_result, container, false);
		m_listView = (XListView) view.findViewById(R.id.fragment_result_list);
		m_listView.setPullLoadEnable(googlePlacesClient.getPageToken() != null);
		m_listView.setPullRefreshEnable(true);
		m_listView.setXListViewListener(this);
		m_listView.setOnItemClickListener(this);
		m_listView.setAdapter(m_adapter);
		m_pBar = (ProgressBar) view.findViewById(R.id.forumDisplayProgressBar);
		if(m_model.size()!=0)
		{
			m_pBar.setVisibility(View.GONE);
		}
		return view;
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		previousIndex = m_listView.getLastVisiblePosition();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if(m_listView != null)
		{
		    if(m_listView.getCount() > previousIndex)
		    	m_listView.setSelectionFromTop(previousIndex, 0);
		    else
		    	m_listView.setSelectionFromTop(0, 0);
		}
	}
	
	private void configure()
	{
		googlePlacesClient = new CustomGooglePlaces();
		googlePlaceFilter = searchType.getPlaceFilter();
		currentLocation = ((NearbyPage)getActivity()).getCurrentLocation();
		m_adapter = new SearchResultListAdapter(getActivity(), m_model,googlePlacesClient, currentLocation);
		mBookmarkReciever = new BookmarkReciever(m_model, m_adapter);
		mLocationReciever = new LocationReciever();
		favoriteDataSource = FavoriteDBDataSource.getInStance(getActivity());
		favoriteDataSource.open();
		IntentFilter filter_bookmark = new IntentFilter();
		filter_bookmark.addAction(App.BOOKMARK_STATE_CHANGE_ACTION);
		
		IntentFilter filter_location = new IntentFilter();
		filter_location.addAction(App.GEO_LOCATION_UPDATE_ACTION);
		
		getActivity().registerReceiver(mBookmarkReciever, filter_bookmark);
		getActivity().registerReceiver(mLocationReciever, filter_location);
	}
	
	public void loadModel(String pageToken)
	{
		if (currentLocation != null)
		{
			if(pageToken != null)
			{
				googlePlaceFilter.setPagetoken(pageToken);
			}
			else
			{
				googlePlaceFilter.setPagetoken(null);
				m_model.clear();
			}
			SearchResultTask searchTask = new SearchResultTask();
			searchTask.execute("");
		} 
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
	{
		if(position < 1)
			return;
		openDetailPage(m_model.get(position -1));
	}
	
	public void openDetailPage(Place selectedPlace)
	{
		Bundle data = new Bundle();
		data.putString("placeId", selectedPlace.getPlaceId());
		Location location = new Location("");
		location.setLatitude(selectedPlace.getLatitude());
		location.setLongitude(selectedPlace.getLongitude());
		Gson gson = new Gson();
		String jsonLocation = gson.toJson(location);
		data.putSerializable("location",  jsonLocation);
		String jsonCurrentLocation = gson.toJson(currentLocation);
		data.putSerializable("currentLocation", jsonCurrentLocation);
		Intent intent = new Intent(getActivity(), SearchDetailsPage.class);
		intent.putExtras(data);
		this.startActivity(intent);
	}
	
	@Override
	public void onRefresh()
	{
		loadModel(null);
	}

	@Override
	public void onLoadMore()
	{
		loadModel(googlePlacesClient.getPageToken());
	}
	
	class SearchResultTask extends AsyncTask<String, Void, List<Place>>
	{
		@Override
		protected List<Place> doInBackground(String... urls)
		{
			List<Place> placeList = new ArrayList<Place>();
			placeList = googlePlacesClient.getNearbyPlaces(currentLocation.getLatitude(), currentLocation.getLongitude(), 10000, googlePlaceFilter);
			status = googlePlacesClient.getStatusCode();
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
					Toast.makeText(getActivity(), R.string.net_timeout, Toast.LENGTH_SHORT).show();
					break;

				default:
					break;
			}
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
			m_pBar.setVisibility(View.GONE);
		}
		
		private void restoreBookmarkToSearchResults(ArrayList<Place> searchResult)
		{
			ArrayList<String> bookmarkPresentList = favoriteDataSource.getAllFavoritePresentInList(CollecttionHelper.getPlaceIdList(searchResult));
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
	
	public class LocationReciever extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context paramContext, Intent paramIntent)
		{
			Bundle dataBundle = paramIntent.getExtras();
			currentLocation = new Location("");
			currentLocation.setLatitude(dataBundle.getDouble("Latitude"));
			currentLocation.setLongitude(dataBundle.getDouble("Longtitude"));
			m_adapter.setCurrentLocation(currentLocation);
			if(m_model.size()==0)
			{
				loadModel(null);
			}
		}
	}
	
	public static ClassfiedSearchResultFragment newInstance(SearchType searchType)
	{
		ClassfiedSearchResultFragment instance = new ClassfiedSearchResultFragment();
		instance.searchType = searchType;
		return instance;
	}
}

