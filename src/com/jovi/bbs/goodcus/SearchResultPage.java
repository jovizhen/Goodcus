package com.jovi.bbs.goodcus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.google.gson.Gson;
import com.jovi.bbs.goodcus.fragment.SearchFilterFragment;
import com.jovi.bbs.goodcus.net.googlePlacesApi.CustomGooglePlaces;
import com.jovi.bbs.goodcus.net.googlePlacesApi.GooglePlaceFilter;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Place;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Prediction;
import com.jovi.bbs.goodcus.util.BookmarkReciever;
import com.jovi.bbs.goodcus.util.CollecttionHelper;
import com.jovi.bbs.goodcus.util.FavoriteDBDataSource;
import com.jovi.bbs.goodcus.util.LocationChangeHandler;
import com.jovi.bbs.goodcus.util.SearchResultListAdapter;
import com.jovi.bbs.goodcus.util.Utils;
import com.jovi.bbs.goodcus.widgets.ClearableAutocompleteTextView;
import com.jovi.bbs.goodcus.widgets.ClearableAutocompleteTextView.OnClearListener;
import com.jovi.bbs.goodcus.widgets.XListView;
import com.jovi.bbs.goodcus.widgets.XListView.IXListViewListener;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class SearchResultPage extends Activity implements IXListViewListener, OnItemClickListener
{
	private static final String SEARCH_FILTER_FRAGMENT_TAG = "search_filter_fragment";
	
	private int status;
	private String keyword;
	private Location currentLocation;

	private ProgressBar m_pBar;
	private XListView m_listView;
	private ImageView searchIcon;
	private ClearableAutocompleteTextView searchBox;
	private LinearLayout searchGroup;

	private SearchResultListAdapter m_adapter;
	private ArrayList<Place> m_model = new ArrayList<Place>();
	private FavoriteDBDataSource favoriteDataSource;
	private BookmarkReciever mBookmarkReciever; 
	private LocationReciever mLocationReciever;
	private LocationChangeHandler locationChangeHandler;
	private AutocompleteAdapter autocompleteAdapter;
	
	private SharedPreferences filterPerferences;
	private GooglePlaceFilter googlePlaceFilter;
	private CustomGooglePlaces googlePalcesClient;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		configure();
		initView();
		loadModel(null, keyword);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		favoriteDataSource.open();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		favoriteDataSource.close();
	}

	public void configure()
	{
		favoriteDataSource = FavoriteDBDataSource.getInStance(this);
		favoriteDataSource.open();
		locationChangeHandler = new LocationChangeHandler(this);
		currentLocation = Utils.getCurrentLocation(this, locationChangeHandler);
		googlePalcesClient = new CustomGooglePlaces();
		m_adapter = new SearchResultListAdapter(this, m_model, googlePalcesClient, currentLocation);
		mBookmarkReciever = new BookmarkReciever(m_model, m_adapter);
		IntentFilter filter = new IntentFilter();
		filter.addAction(App.BOOKMARK_STATE_CHANGE_ACTION);
		registerReceiver(mBookmarkReciever, filter);
		mLocationReciever = new LocationReciever();
		IntentFilter filter_location = new IntentFilter();
		filter_location.addAction(App.GEO_LOCATION_UPDATE_ACTION);
		registerReceiver(mLocationReciever, filter_location);
	}
	
	public void initView()
	{
		setContentView(R.layout.activity_search_result_page);
		m_listView = (XListView) this.findViewById(R.id.result_list);
		searchBox = (ClearableAutocompleteTextView) findViewById(R.id.search_box);
		searchGroup = (LinearLayout) findViewById(R.id.search_box_group);
		searchGroup.setVisibility(View.GONE);
		searchIcon = (ImageView) findViewById(R.id.search_icon);
		m_pBar = (ProgressBar) this.findViewById(R.id.forumDisplayProgressBar);
		setupListView();
		setupSearchBox();
	}

	public void setupListView()
	{
		m_listView.setPullLoadEnable(false);
		m_listView.setPullRefreshEnable(true);
		m_listView.setXListViewListener(this);
		m_listView.setOnItemClickListener(this);
		m_listView.setAdapter(m_adapter);
	}

	public void setupSearchBox()
	{
		filterPerferences = getSharedPreferences("googleFilter", Context.MODE_PRIVATE);
		googlePlaceFilter = new GooglePlaceFilter();
		googlePlaceFilter.setLanguage("en");
		keyword = "resturant";
		searchBox = (ClearableAutocompleteTextView) findViewById(R.id.search_box);
		autocompleteAdapter = new AutocompleteAdapter();
		searchBox.setAdapter(autocompleteAdapter);
		searchIcon.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				toggleSearch(false);
			}
		});
		searchBox.setOnClearListener(new OnClearListener()
		{
			@Override
			public void onClear()
			{
				toggleSearch(true);
			}
		});
		
		// fire google place search query when drop down selected
		searchBox.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				keyword = (String) autocompleteAdapter.getItem(position);
				performSearch(keyword);
			}
		});

		// fire google place search query when press "Done/Search/Next"
		searchBox.setOnEditorActionListener(new OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH||actionId==EditorInfo.IME_ACTION_NEXT)
				{
					keyword = searchBox.getText().toString();
					performSearch(keyword);
					return true;
				}
				return false;
			}
		});
	}

	public void loadModel(String pageToken, String keyword)
	{
		if(keyword == null)
			return;
		if(currentLocation != null)
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
			updateFilters();
			SearchResultTask searchTask = new SearchResultTask();
			searchTask.execute(keyword);
		}
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
		data.putSerializable("location", jsonLocation);
		String jsonCurrentLocation = gson.toJson(currentLocation);
		data.putSerializable("currentLocation", jsonCurrentLocation);
		Intent intent = new Intent(this, SearchDetailsPage.class);
		intent.putExtras(data);
		this.startActivity(intent);
	}
	
	public void updateFilters()
	{
		filterPerferences = getSharedPreferences("googleFilter", Context.MODE_PRIVATE);
		googlePlaceFilter = new GooglePlaceFilter();
		googlePlaceFilter.setLanguage("en");
		googlePlaceFilter.setLocation(currentLocation.getLatitude()+","+currentLocation.getLongitude());
		googlePlaceFilter.setMaxprice(filterPerferences.getInt("max_price", GooglePlaceFilter.DEFAULT_MAX_PRICE));
		googlePlaceFilter.setRadius((double) filterPerferences.getLong("radius", GooglePlaceFilter.DEFAULT_SEARCH_RADIUS));
		googlePlaceFilter.setOpennow(filterPerferences.getBoolean("open_now", GooglePlaceFilter.DEFAULT_OPEN_NOW));
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
	{
		if (position < 1)
			return;
		openDetailPage(m_model.get(position - 1));
	}
	
	public void forceRefresh()
	{
		loadModel(null, keyword);
	}

	@Override
	public void onRefresh()
	{
		loadModel(null, keyword);
	}

	public void onBackBtnClick(View v)
	{
		this.finish();
	}
	
	public void onSearchBtnClicked(View v)
	{
		keyword = searchBox.getText().toString();
		performSearch(keyword);
	}

	private void performSearch(String keyword)
	{
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
		autocompleteAdapter.resultList.clear();
		autocompleteAdapter.notifyDataSetChanged();
		m_pBar.setVisibility(View.VISIBLE);
		loadModel(null, keyword);
	}
	
	@Override
	public void onLoadMore()
	{
		loadModel(googlePalcesClient.getPageToken(), keyword);
	}
	
	public void onCancelFilterSaveBtnClick(View v)
	{
		toggleFilterPanel(v);
	}
	
	public void onSaveFilterBtnClick(View v)
	{
		toggleFilterPanel(v);
	}
	
	public void toggleFilterPanel(View v)
	{
		Fragment f = getFragmentManager().findFragmentByTag(SEARCH_FILTER_FRAGMENT_TAG);
		if (f != null)
		{
			getFragmentManager().popBackStack();
		} 
		else
		{
			getFragmentManager()
					.beginTransaction()
					.setCustomAnimations(R.animator.slide_up, R.animator.slide_down, R.animator.slide_up, R.animator.slide_down)
					.add(R.id.list_fragment_container, SearchFilterFragment.instantiate(this, SearchFilterFragment.class.getName()), 
							SEARCH_FILTER_FRAGMENT_TAG)
					.addToBackStack(null).commit();
		}
	}
	
	protected void toggleSearch(boolean reset)
	{
		ImageView searchIcon = (ImageView) findViewById(R.id.search_icon);
		if (reset)
		{
			// hide search box and show search icon
			searchBox.setText("");
			searchGroup.setVisibility(View.GONE);
			searchIcon.setVisibility(View.VISIBLE);
			// hide the keyboard
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
		} 
		else
		{
			// hide search icon and show search box
			searchIcon.setVisibility(View.GONE);
			searchGroup.setVisibility(View.VISIBLE);
			// add FANCY animation
			Animation a = AnimationUtils.loadAnimation(this, R.animator.push_left_in);
			searchGroup.setVisibility(View.VISIBLE);
			searchGroup.findViewById(R.id.search_box_group).startAnimation(a);
			searchBox.requestFocus();
			// show the keyboard
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(searchBox, InputMethodManager.SHOW_IMPLICIT);
		}
	}

	class AutocompleteAdapter extends BaseAdapter implements Filterable
	{
		private ArrayList<String> resultList = new ArrayList<String>();

		@Override
		public int getCount()
		{
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
				LayoutInflater inflater = (LayoutInflater) getApplicationContext().
						getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
						HashSet<String> resultSet = new HashSet<String>();
						for (Prediction pred : predictionList)
						{
							resultSet.add(simplifyDescription(pred.getDescription()));
						}
						// Assign the data to the FilterResults
						filterResults.values = resultSet;
						filterResults.count = resultSet.size();
					}
					return filterResults;
				}

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint, FilterResults results)
				{
					if (results != null && results.count > 0)
					{
						resultList.clear();
						resultList.addAll((Collection<String>) results.values);
						notifyDataSetChanged();
					} else
					{
						notifyDataSetInvalidated();
					}
				}
			};
			return filter;
		}

		public String simplifyDescription(String description)
		{
			String[] stringArray = description.split(",");
			if (stringArray.length >= 2)
			{
				return stringArray[0] + "," + stringArray[1].trim();
			}
			return stringArray[0].trim();
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
		}
	}

	class SearchResultTask extends AsyncTask<String, Void, List<Place>>
	{
		@Override
		protected List<Place> doInBackground(String... urls)
		{
			List<Place> placeList = new ArrayList<Place>();
			placeList = googlePalcesClient.getPlacesByQuery(urls[0], googlePlaceFilter);
			status = googlePalcesClient.getStatusCode();
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
					Toast.makeText(SearchResultPage.this, R.string.net_timeout, Toast.LENGTH_SHORT).show();
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
}
