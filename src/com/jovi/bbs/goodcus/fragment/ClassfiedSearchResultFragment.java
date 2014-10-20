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
import com.jovi.bbs.goodcus.util.Utils;
import com.jovi.bbs.goodcus.widgets.ImageViewWithCache;
import com.jovi.bbs.goodcus.widgets.XListView;
import com.jovi.bbs.goodcus.widgets.XListView.IXListViewListener;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class ClassfiedSearchResultFragment extends Fragment implements IXListViewListener, OnItemClickListener
{
	private static int previousIndex;
	private int status;

	private ProgressBar m_pBar;
	private XListView m_listView;

	private SearchResultListAdapter m_adapter;
	private ArrayList<Place> m_model = new ArrayList<Place>();
	
	private CustomGooglePlaces googlePalcesClient;
	private GooglePlaceFilter googlePlaceFilter;
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
		View view = inflater.inflate(R.layout.classfied_result_fragment, container, false);
		m_listView = (XListView) view.findViewById(R.id.fragment_result_list);
		m_listView.setPullLoadEnable(googlePalcesClient.getPageToken() != null);
		m_listView.setPullRefreshEnable(true);
		m_listView.setXListViewListener(this);
		m_listView.setOnItemClickListener(this);
		m_adapter = new SearchResultListAdapter();
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
		googlePalcesClient = new CustomGooglePlaces();
		googlePlaceFilter = searchType.getPlaceFilter();
		currentLocation = Utils.getCurrentLocation(getActivity());
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
	
	@Override
	public void onRefresh()
	{
		loadModel(null);
	}

	@Override
	public void onLoadMore()
	{
		loadModel(googlePalcesClient.getPageToken());
	}
	
	class SearchResultListAdapter extends BaseAdapter
	{
		GoogleImageLoader imageLoader = new GoogleImageLoader(getActivity());
		ViewHolder viewHolder;
		
		@Override
		public int getCount()
		{
			return (m_model == null) ? 0 : m_model.size();
		}

		@Override
		public Object getItem(int index)
		{
			if (m_model.size() != 0)
			{
				return m_model.get(index);
			}
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
				viewHolder = new ViewHolder();
				viewHolder.name = (TextView) convertView.findViewById(R.id.business_name);
				viewHolder.addr = (TextView) convertView.findViewById(R.id.business_address);
				viewHolder.ratingbar  =  (RatingBar) convertView.findViewById(R.id.MyRating);
				viewHolder.img = (ImageViewWithCache) convertView.findViewById(R.id.headImgDetail);
				convertView.setTag(viewHolder);
			}
			else
			{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			viewHolder.name.setText(m_model.get(position).getName());
			viewHolder.addr.setText(m_model.get(position).getVicinity());
			viewHolder.ratingbar.setRating((float) m_model.get(position).getRating());
			if(m_model.get(position).getPhotos().size()>0)
			{
				imageLoader.DisplayImage(googlePalcesClient.buildPhotoDownloadUrl(m_model.get(position).getPhotos().get(0), 100, 100), 
						m_model.get(position).getPlaceId(),  viewHolder.img);
			}
			return convertView;
		}
	}
	
	class ViewHolder
	{
		TextView name;
		TextView addr;
		RatingBar ratingbar;
		ImageViewWithCache img;
	}
	
	class SearchResultTask extends AsyncTask<String, Void, List<Place>>
	{
		@Override
		protected List<Place> doInBackground(String... urls)
		{
			List<Place> placeList = new ArrayList<Place>();
			try
			{
				placeList = googlePalcesClient.getNearbyPlaces(currentLocation.getLatitude(), 
						currentLocation.getLongitude(), 10000, googlePlaceFilter);
				status =googlePalcesClient.getStatusCode(); 
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
					m_model.addAll(searchResult);
					m_adapter.notifyDataSetChanged();
					break;

				case CustomGooglePlaces.STATUS_CODE_REQUEST_DENIED:
					Toast.makeText(getActivity(), R.string.net_timeout, Toast.LENGTH_SHORT).show();
					break;

				default:
					break;
			}
			if (googlePalcesClient.getPageToken() == null)
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
	}
	
	public static ClassfiedSearchResultFragment newInstance(SearchType searchType)
	{
		ClassfiedSearchResultFragment instance = new ClassfiedSearchResultFragment();
		instance.searchType = searchType;
		return instance;
	}
}

