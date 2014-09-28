package com.jovi.bbs.goodcus.fragment;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.jovi.bbs.goodcus.R;
import com.jovi.bbs.goodcus.SearchDetailsPage;
import com.jovi.bbs.goodcus.fragment.SearchResultFragmentFactory.SearchType;
import com.jovi.bbs.goodcus.net.googlePlacesApi.CustomGooglePlaces;
import com.jovi.bbs.goodcus.net.googlePlacesApi.GooglePlaceFilter;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Place;
import com.jovi.bbs.goodcus.util.GoogleImageLoader;
import com.jovi.bbs.goodcus.widgets.ImageViewWithCache;
import com.jovi.bbs.goodcus.widgets.XListView;
import com.jovi.bbs.goodcus.widgets.XListView.IXListViewListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ClassfiedSearchResultFragment extends Fragment implements IXListViewListener, OnItemClickListener, LocationListener
{
	private int m_currentPage = 1;
	private XListView m_listView;
	private static int previousIndex;
	private SearchResultListAdapter m_adapter;
	private ProgressBar m_pBar;
	private ArrayList<Place> m_model = new ArrayList<Place>();
	//dummy_model loads up data from external yelp API from AynTask, then have mHandler to call notifyModelDataChanged
    //post list data changes in UI thread to avoid illegalstatesException caused by list adapter change and listView 
	//change in different threads
	private ArrayList<Place> dummy_model = new ArrayList<Place>();
	private int status;
	
	private CustomGooglePlaces googlePalcesClient;
	private Location currentLocation;
	private SearchType searchType; 

	
	@SuppressLint("HandlerLeak") private Handler m_handler = new Handler()
	{
		public void handleMessage(Message msg) 
		{
			switch (msg.what)
			{
				case CustomGooglePlaces.STATUS_CODE_OK:
					notifyModelDataChanged();
					break;

				case CustomGooglePlaces.STATUS_CODE_REQUEST_DENIED:
					Toast.makeText(getActivity(), R.string.net_timeout, Toast.LENGTH_SHORT).show();
					break;
				
				default:
					break;
			}
			//toggle off the refresh btn animation on host activity action bar
			
//			if (m_refreshBtn.isRefreshing())
//			{
//				m_listView.setSelection(1);
//				m_refreshBtn.endRefresh();
//			}

			if (m_listView.getPullLoading())
			{
				// 加载到最后一页时禁用pull load
				m_listView.stopLoadMore();
			}

			if (m_listView.getPullRefreshing())
			{
				m_listView.stopRefresh();
			}
			
			m_pBar.setVisibility(View.GONE);

		}
	};
	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		googlePalcesClient = new CustomGooglePlaces();
		loadModel(m_currentPage++);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.classfied_result_fragment, container, false);
		m_listView = (XListView) view.findViewById(R.id.fragment_result_list);
		m_listView.setPullLoadEnable(false);
		m_listView.setPullRefreshEnable(true);
		m_listView.setXListViewListener(this);
		m_listView.setOnItemClickListener(this);
		m_adapter = new SearchResultListAdapter();
		m_listView.setAdapter(m_adapter);
		m_pBar = (ProgressBar) view.findViewById(R.id.forumDisplayProgressBar);
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
	{
		if(position < 1)
			return;
		Place selectPlace = m_model.get(position -1);
		Bundle data = new Bundle();
		data.putString("placeId", selectPlace.getPlaceId());
		Location location = new Location("");
		location.setLatitude(selectPlace.getLatitude());
		location.setLongitude(selectPlace.getLongitude());
		Gson gson = new Gson();
		String jsonLocation = gson.toJson(location);
		data.putSerializable("location",  jsonLocation);
		Intent intent = new Intent(getActivity(), SearchDetailsPage.class);
		intent.putExtras(data);
		this.startActivity(intent);
	}
	
	public Location getCurrentLocation()
	{
		LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String bestProvider = locationManager.getBestProvider(criteria, false);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
	            new LocationListener()
				{
					
					@Override
					public void onStatusChanged(String arg0, int arg1, Bundle arg2)
					{
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onProviderEnabled(String arg0)
					{
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onProviderDisabled(String provider)
					{
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onLocationChanged(Location loc)
					{
						loc.getLatitude();
						loc.getLongitude();
						
					}
				});

		return locationManager.getLastKnownLocation(bestProvider);
	}

	public void loadModel(int numOfPages)
	{
		currentLocation = getCurrentLocation();
		if (currentLocation != null)
		{
			GooglePlaceFilter googlePlaceFilter = searchType.getPlaceFilter();
			googlePlaceFilter.setLanguage("zh-TW");
			SearchResultTask searchTask = new SearchResultTask();
			searchTask.execute("");
		} 
	}
	
	public void notifyModelDataChanged()
	{
		m_model.addAll(dummy_model);
		m_adapter.notifyDataSetChanged();
	}
	@Override
	public void onRefresh()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void onLoadMore()
	{
		// TODO Auto-generated method stub
	}
	
	class SearchResultTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			try
			{
				List<Place> placeList = googlePalcesClient.getNearbyPlaces(currentLocation.getLatitude(), 
						currentLocation.getLongitude(), 10000, searchType.getPlaceFilter());
				dummy_model.clear();
				dummy_model.addAll(placeList);
				status =googlePalcesClient.getStatusCode(); 
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			m_handler.sendEmptyMessage(status);
		}
	}
	
	class SearchResultListAdapter extends BaseAdapter
	{
		GoogleImageLoader imageLoader = new GoogleImageLoader(getActivity());
		@Override
		public int getCount()
		{
			return (m_model == null) ? 0 : m_model.size();
		}

		@Override
		public Object getItem(int position)
		{
			return null;
		}

		@Override
		public long getItemId(int arg0)
		{
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if (convertView == null)
			{
				LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.search_result_item, null);
			}

			TextView name = (TextView) convertView.findViewById(R.id.business_name);
			RatingBar ratingbar  =  (RatingBar) convertView.findViewById(R.id.MyRating);
			ImageViewWithCache img = (ImageViewWithCache) convertView.findViewById(R.id.headImgDetail);

			name.setText(m_model.get(position).getName());
			if(m_model.get(position).getPhotos().size()>0)
			{
				imageLoader.DisplayImage(googlePalcesClient.buildPhotoUrl(m_model.get(position).getPhotos().get(0)), img);
			}
			ratingbar.setRating((float) m_model.get(position).getRating());

			return convertView;
		}
	}
	
	
	public static ClassfiedSearchResultFragment newInstance(SearchType searchType)
	{
		ClassfiedSearchResultFragment instance = new ClassfiedSearchResultFragment();
		instance.searchType = searchType;
		return instance;
	}

	@Override
	public void onLocationChanged(Location location)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		// TODO Auto-generated method stub
		
	}
}

