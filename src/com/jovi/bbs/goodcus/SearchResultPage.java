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
import com.jovi.bbs.goodcus.util.Utils;
import com.jovi.bbs.goodcus.widgets.ClearableAutocompleteTextView;
import com.jovi.bbs.goodcus.widgets.ClearableAutocompleteTextView.OnClearListener;
import com.jovi.bbs.goodcus.widgets.ImageViewWithCache;
import com.jovi.bbs.goodcus.widgets.XListView;
import com.jovi.bbs.goodcus.widgets.XListView.IXListViewListener;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class SearchResultPage extends Activity implements IXListViewListener, OnItemClickListener
{
	private static final String SEARCH_FILTER_FRAGMENT_TAG = "search_filter_fragment";
	private int status;
	private Location currentLocation;

	private ProgressBar m_pBar;
	private XListView m_listView;
	private ImageView searchIcon;
	private ClearableAutocompleteTextView searchBox;

	private SearchResultListAdapter m_adapter;
	private ArrayList<Place> m_model = new ArrayList<Place>();

	private GooglePlaceFilter googlePlaceFilter;
	private CustomGooglePlaces googlePalcesClient;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		initView();
		loadModel(null);
	}

	public void initView()
	{
		setContentView(R.layout.search_result_page);
		m_listView = (XListView) this.findViewById(R.id.result_list);
		searchBox = (ClearableAutocompleteTextView) findViewById(R.id.search_box);
		searchBox.setVisibility(View.GONE);
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
		m_adapter = new SearchResultListAdapter();
		m_listView.setAdapter(m_adapter);
	}

	public void setupSearchBox()
	{
		currentLocation = Utils.getCurrentLocation(this);
		googlePlaceFilter = new GooglePlaceFilter();
		googlePlaceFilter.setKeyword("restaurant");
		googlePlaceFilter.setLanguage("zh-CN");

		googlePalcesClient = new CustomGooglePlaces();
		searchBox = (ClearableAutocompleteTextView) findViewById(R.id.search_box);
		final AutocompleteAdapter adapter = new AutocompleteAdapter();
		searchBox.setAdapter(adapter);

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
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
				String keyString = (String) adapter.getItem(position);
				googlePlaceFilter.setKeyword(keyString);
				m_pBar.setVisibility(View.VISIBLE);
				loadModel(null);
			}
		});

		// fire google place search query when press "done/search"
		searchBox.setOnEditorActionListener(new OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH)
				{
					String keyString = searchBox.getText().toString();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
					adapter.resultList.clear();
					adapter.notifyDataSetChanged();
					googlePlaceFilter.setKeyword(keyString);
					m_pBar.setVisibility(View.VISIBLE);
					loadModel(null);
					return true;
				}
				return false;
			}
		});
	}

	public void loadModel(String pageToken)
	{
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
			SearchResultTask searchTask = new SearchResultTask();
			searchTask.execute("");
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
	{
		if (position < 1)
			return;
		Place selectPlace = m_model.get(position - 1);
		Bundle data = new Bundle();
		data.putString("placeId", selectPlace.getPlaceId());
		Location location = new Location("");
		location.setLatitude(selectPlace.getLatitude());
		location.setLongitude(selectPlace.getLongitude());
		Gson gson = new Gson();
		String jsonLocation = gson.toJson(location);
		data.putSerializable("location", jsonLocation);
		Intent intent = new Intent(this, SearchDetailsPage.class);
		intent.putExtras(data);
		this.startActivity(intent);
	}

	public void forceRefresh()
	{
		loadModel(null);
	}

	@Override
	public void onRefresh()
	{
		loadModel(null);
	}

	public void onBackBtnClick(View v)
	{
		this.finish();
	}

	@Override
	public void onLoadMore()
	{
		loadModel(googlePalcesClient.getPageToken());
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
			searchBox.setVisibility(View.GONE);
			searchIcon.setVisibility(View.VISIBLE);
			// hide the keyboard
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
		} 
		else
		{
			// hide search icon and show search box
			searchIcon.setVisibility(View.GONE);
			searchBox.setVisibility(View.VISIBLE);
			// add FANCY animation
			Animation a = AnimationUtils.loadAnimation(this, R.animator.push_left_in);
			searchBox.setVisibility(View.VISIBLE);
			searchBox.findViewById(R.id.search_box).startAnimation(a);
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
				convertView = getLayoutInflater().inflate(R.layout.search_result_item, null);
			}

			TextView name = (TextView) convertView.findViewById(R.id.business_name);
			TextView address = (TextView) convertView.findViewById(R.id.business_address);
			RatingBar ratingbar = (RatingBar) convertView.findViewById(R.id.MyRating);
			ImageViewWithCache img = (ImageViewWithCache) convertView.findViewById(R.id.headImgDetail);
			name.setText(m_model.get(position).getName());
			address.setText(m_model.get(position).getVicinity());
			if (m_model.get(position).getPhotos().size() > 0)
			{
				imageLoader.DisplayImage(googlePalcesClient.buildPhotoDownloadUrl(m_model.get(position).getPhotos().get(0), 100, 100), 
						m_model.get(position).getPlaceId(),  img);
			}
			ratingbar.setRating((float) m_model.get(position).getRating());
			return convertView;
		}
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
				status = googlePalcesClient.getStatusCode();
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
	}
}
