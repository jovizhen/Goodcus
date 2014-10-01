package com.jovi.bbs.goodcus;



import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.jovi.bbs.goodcus.fragment.SearchFilterFragment;
import com.jovi.bbs.goodcus.net.googlePlacesApi.CustomGooglePlaces;
import com.jovi.bbs.goodcus.net.googlePlacesApi.GooglePlaceFilter;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Place;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Prediction;
import com.jovi.bbs.goodcus.util.GoogleImageLoader;
import com.jovi.bbs.goodcus.widgets.ClearableAutocompleteTextView;
import com.jovi.bbs.goodcus.widgets.ImageViewWithCache;
import com.jovi.bbs.goodcus.widgets.RefreshActionBtn;
import com.jovi.bbs.goodcus.widgets.XListView;
import com.jovi.bbs.goodcus.widgets.XListView.IXListViewListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class SearchResultPage extends Activity implements IXListViewListener,
OnItemClickListener
{
	private static final String SEARCH_FILTER_FRAGMENT_TAG = "search_filter_fragment";
	private int m_currentPage = 1;
	private XListView m_listView;
	private ClearableAutocompleteTextView searchBox;
	private SearchResultListAdapter m_adapter;
	private ProgressBar m_pBar;
	private RefreshActionBtn m_refreshBtn;
	private ArrayList<Place> m_model = new ArrayList<Place>();
	private ArrayList<Place> dummy_model = new ArrayList<Place>();
//	private YelpFilter yelpFilter;
	private GooglePlaceFilter googlePlaceFilter;
	private CustomGooglePlaces googlePalcesClient;
	private int status;
	private Location currentLocation;
	
	

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
					Toast.makeText(SearchResultPage.this, R.string.net_timeout, Toast.LENGTH_SHORT).show();
					break;
				
				default:
					break;
			}

			if (m_refreshBtn.isRefreshing())
			{
				m_listView.setSelection(1);
				m_refreshBtn.endRefresh();
			}

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
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result_page);
		
		m_listView = (XListView) this.findViewById(R.id.result_list);
		m_listView.setPullLoadEnable(false);
		m_listView.setPullRefreshEnable(true);
		m_listView.setXListViewListener(this);
		m_listView.setOnItemClickListener(this);
		m_adapter = new SearchResultListAdapter();
		m_listView.setAdapter(m_adapter);

		m_refreshBtn = (RefreshActionBtn) this
				.findViewById(R.id.forumDisplayRefreshBtn);
		m_pBar = (ProgressBar) this.findViewById(R.id.forumDisplayProgressBar);

		Bundle data = this.getIntent().getExtras();
		
//		yelpFilter =  new YelpFilter.FilterBuilder("Resturant").build();
//		yelpFilter.setLatitude(30.361471);
//		yelpFilter.setLongitude(-87.164326);
		currentLocation = getCurrentLocation();
		googlePlaceFilter = new GooglePlaceFilter();
		googlePlaceFilter.setKeyword("Szechuan");
		googlePlaceFilter.setLanguage("zh-TW");
		
		googlePalcesClient = new CustomGooglePlaces();
		searchBox = (ClearableAutocompleteTextView) findViewById(R.id.search_box);
		searchBox.setAdapter(new AutocompleteAdapter());
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(App.LOGIN_STATE_CHANGE_ACTION);
		loadModel(m_currentPage++);
	}
	
	public Location getCurrentLocation()
	{
		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
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

	
	public void notifyModelDataChanged()
	{
		m_model.addAll(dummy_model);
		m_adapter.notifyDataSetChanged();
	}

	public void loadModel(int numOfPages)
	{
		SearchResultTask searchTask = new SearchResultTask();
		searchTask.execute("");
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
	{
//		if (position < 1)
//			return;
//		SearchResult result = m_model.get(position - 1);
//		Gson gson = new Gson();
//		String jsonResult = gson.toJson(result);
//		Bundle data = new Bundle();
//		data.putSerializable("searchResult", jsonResult);
//		Intent intent = new Intent(this, SearchDetailsPage.class);
//		intent.putExtras(data);
//		this.startActivity(intent);
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
		Intent intent = new Intent(this, SearchDetailsPage.class);
		intent.putExtras(data);
		this.startActivity(intent);
	}
	
	public void forceRefresh()
	{
		m_currentPage = 1;
		loadModel(m_currentPage++);
	}

	@Override
	public void onRefresh()
	{
		m_refreshBtn.startRefresh();
		forceRefresh();
	}

	public void onBackBtnClick(View v)
	{
		this.finish();
	}
	
	public void onPageTitleClick(View v)
	{
		// 滑动listView到顶端
		m_listView.setSelection(0);
	}
	
	public void toggleFilterPanel(View v)
	{
		Fragment f = getFragmentManager().findFragmentByTag(SEARCH_FILTER_FRAGMENT_TAG);
        if (f != null) {
            getFragmentManager().popBackStack();
        } else {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.list_fragment_container, SearchFilterFragment
                                    .instantiate(this, SearchFilterFragment.class.getName()),
                                    SEARCH_FILTER_FRAGMENT_TAG
                    ).addToBackStack(null).commit();
        }
	}
	
	public void onRefreshBtnClick(View v)
	{
		if (m_refreshBtn.isRefreshing())
		{
			return;
		}
		m_refreshBtn.startRefresh();
		m_listView.setSelection(0);
		m_listView.pullRefreshing();
		forceRefresh();
	}
	
	@Override
	public void onLoadMore()
	{
		loadModel(m_currentPage++);
	}
	
	
	class AutocompleteAdapter extends BaseAdapter implements Filterable
	{
		private ArrayList<String> resultList = new ArrayList<String>();
		
		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return resultList.size();
		}

		@Override
		public Object getItem(int index)
		{
			return resultList.get(index);
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
				LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.dropdown_item, parent, false);
			}
			((TextView) convertView.findViewById(R.id.dropdown_item)).setText((CharSequence) getItem(position));
			return convertView;
		}

		@Override
		public Filter getFilter()
		{
			Filter filter = new Filter()
			{
				@Override
				protected FilterResults performFiltering(CharSequence constraint)
				{
					FilterResults filterResults = new FilterResults();
					if (constraint != null && googlePalcesClient != null)
					{
						List<Prediction> predictionList = googlePalcesClient.getPlacePredictions(constraint.toString());
						resultList.clear();
						for (Prediction pred : predictionList)
						{
							resultList.add(pred.getDescription());
						}
						// Assign the data to the FilterResults
						filterResults.values = resultList;
						filterResults.count = resultList.size();
					}

					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence constraint, FilterResults results)
				{
					if (results != null && results.count > 0)
					{
						notifyDataSetChanged();
					} else
					{
						notifyDataSetInvalidated();
					}
				}
			};
			return filter;
		}
	}
	
	
	class SearchResultListAdapter extends BaseAdapter
	{

		GoogleImageLoader imageLoader = new GoogleImageLoader(getApplicationContext());
		@Override
		public int getCount()
		{
			return (m_model == null) ? 0 : m_model.size();
		}

		@Override
		public Object getItem(int arg0)
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
				convertView = getLayoutInflater().inflate(
						R.layout.search_result_item, null);
			}
			
			TextView name = (TextView) convertView.findViewById(R.id.business_name);
			RatingBar ratingbar  =  (RatingBar) convertView.findViewById(R.id.MyRating);
			ImageViewWithCache  img = (ImageViewWithCache ) convertView.findViewById(R.id.headImgDetail);
			
			name.setText(m_model.get(position).getName());
			if(m_model.get(position).getPhotos().size()>0)
			{
				imageLoader.DisplayImage(googlePalcesClient.buildPhotoUrl(m_model.get(position).getPhotos().get(0)), img);
			}
			ratingbar.setRating((float) m_model.get(position).getRating());
			
			return convertView;
		}
	}

	class SearchResultTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			try
			{
				List<Place> placeList = googlePalcesClient.getNearbyPlaces(currentLocation.getLatitude(), 
						currentLocation.getLongitude(), 10000, googlePlaceFilter);
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
}
