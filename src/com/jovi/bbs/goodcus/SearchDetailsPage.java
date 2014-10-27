package com.jovi.bbs.goodcus;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.jovi.bbs.goodcus.fragment.MapDirectionFragment;
import com.jovi.bbs.goodcus.fragment.ReviewListFragment;
import com.jovi.bbs.goodcus.net.googlePlacesApi.CustomGooglePlaces;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Photo;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Place;
import com.jovi.bbs.goodcus.util.FavoriteDBDataSource;
import com.jovi.bbs.goodcus.widgets.RefreshActionBtn;
import com.jovi.bbs.goodcus.widgets.SearchDetailsView;
import com.jovi.bbs.goodcus.widgets.tableView.CustomUITableView;
import com.jovi.bbs.goodcus.widgets.tableView.CustomUITableView.CustomItemClickListener;
import com.jovi.bbs.goodcus.widgets.touchGallery.CirclePageIndicator;
import com.jovi.bbs.goodcus.widgets.touchGallery.ReferencePagerAdapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SearchDetailsPage extends Activity 
{
	protected static final String TAG = "Search Details Page";
	private   static final String MAP_DIRECTION_FRAGMENT  = "map_direction_fragment";

	private ViewPager mViewPager;
	private ImageView viewPagerHolder;
	private CustomUITableView tableView;
	private RelativeLayout pageHeader;
	private SearchDetailsView detailsView;
	private CirclePageIndicator mIndicator;
	private RefreshActionBtn mRefreshBtn;

	private ArrayList<String> m_photo_reference_list = new ArrayList<String>();
	private ReferencePagerAdapter pagerAdapter;
	private CustomGooglePlaces googlePalcesClient;
	private FavoriteDBDataSource favoriteDataSource;
	
	private String placeId;
	private Place place;
	private GoogleMap mMap;
	private Location location;
	private Handler m_handler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		configure();
		initView();
		new PlaceDetailTask().execute("");
	}
	
	protected void onResume()
	{
		favoriteDataSource.open();
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		favoriteDataSource.close();
		super.onPause();
	}
	
	private void configure()
	{
		googlePalcesClient = new CustomGooglePlaces();
		Gson gson = new Gson();
		placeId =  this.getIntent().getExtras().getString("placeId");
		String jsonLocation = this.getIntent().getExtras().getString("location");
		location = gson.fromJson(jsonLocation, Location.class);
		favoriteDataSource = FavoriteDBDataSource.getInStance(this);
		favoriteDataSource.open();
	}
	
	private void initView()
	{
		setContentView(R.layout.search_details_page);
		mRefreshBtn = (RefreshActionBtn) findViewById(R.id.detail_info_RefreshBtn);
		mRefreshBtn.startRefresh();
		detailsView = (SearchDetailsView) findViewById(R.id.searchDetailsFrag);
		pagerAdapter = new ReferencePagerAdapter(this, m_photo_reference_list);
		viewPagerHolder = (ImageView) findViewById(R.id.view_page_place_holder);
		mViewPager = (ViewPager) findViewById(R.id.viewer);
		mViewPager.setAdapter(pagerAdapter);
		
		pageHeader = (RelativeLayout) findViewById(R.id.pagedetail_header);
		mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
		mIndicator.setViewPager(mViewPager);
		tableView = (CustomUITableView) findViewById(R.id.tableView);
		setUpMapIfNeeded();
		createMenuList();
	}
	
	private void createMenuList() 
	{
		MenuItemClickListener listener = new MenuItemClickListener();
		tableView.setCustomListener(listener);
		tableView.addBasicItem(R.drawable.ic_action_directions, "Direction", null);
		tableView.addBasicItem(R.drawable.ic_action_copy, "Reviews", null);
		tableView.addBasicItem(R.drawable.ic_action_call, "Telephone", null, false);
		boolean isBookmarked = favoriteDataSource.isFavoriteAdded(placeId);
		tableView.addBasicItem(isBookmarked? R.drawable.ic_bookmarked: R.drawable.ic_bookmark, 
				isBookmarked? "Bookmarked":"Bookmark", null, false);
		tableView.commit();
	}
	
	public void onRefreshBtnClicked(View view)
	{
		mRefreshBtn.startRefresh();
		new PlaceDetailTask().execute("");
	}
	
	public void onDirectionClick()
	{
		Fragment f = getFragmentManager().findFragmentByTag(MAP_DIRECTION_FRAGMENT);
		if(f!=null)
		{
			getFragmentManager().popBackStack();
            pageHeader.setVisibility(View.VISIBLE);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.remove(((MapDirectionFragment)f).getMap());
            fragmentTransaction.commit();
		}
		else
		{
			Location location = new Location("");
			location.setLatitude(place.getLatitude());
			location.setLongitude(place.getLongitude());
			Gson gson = new Gson();
			String jsonLocation = gson.toJson(location);
			
			Bundle data = new Bundle();
			data.putSerializable("bussiness_location", jsonLocation);
			getFragmentManager().beginTransaction().
			setCustomAnimations(R.animator.slide_from_right, 
					R.animator.slide_to_right, 
					R.animator.slide_from_right, 
					R.animator.slide_to_right)
				.add(R.id.review_fragment_container, MapDirectionFragment
						.instantiate(this, MapDirectionFragment.class.getName(), data)
						,MAP_DIRECTION_FRAGMENT).addToBackStack(null).commit();
			pageHeader.setVisibility(View.GONE);
		}
	}
	
	public void onBackBtnClick(View v)
	{
		this.finish();
	}
	
	public void onDirectionBackBtnClick(View v)
	{
		onDirectionClick(); 
	}
	
	public void onBrowseReviewClick()
	{
		Fragment f = getFragmentManager().findFragmentByTag(MAP_DIRECTION_FRAGMENT);
		if(f!=null)
		{
			getFragmentManager().popBackStack();
            pageHeader.setVisibility(View.VISIBLE);
		}
		else 
		{
			Bundle data = new Bundle();
			data.putString("place_id", place.getPlaceId());
			getFragmentManager().beginTransaction().
			setCustomAnimations(R.animator.slide_from_right, 
					R.animator.slide_to_right, 
					R.animator.slide_from_right, 
					R.animator.slide_to_right)
				.add(R.id.review_fragment_container, ReviewListFragment
						.instantiate(this, ReviewListFragment.class.getName(), data)
						,MAP_DIRECTION_FRAGMENT).addToBackStack(null).commit();
			pageHeader.setVisibility(View.GONE);
		}
	}
	
	public void onPhoneClick()
	{
	}
	
	public void onReviewBackBtnClick(View v)
	{
		onBrowseReviewClick();
	}
	
	public void onBookmarkClick(View view)
	{
		ImageView icon = (ImageView) view.findViewById(R.id.image);
		TextView textView = (TextView) view.findViewById(R.id.title);
		if("Bookmark".equals(textView.getText()))
		{
			favoriteDataSource.addToFavorite(place);
			icon.setImageResource(R.drawable.ic_bookmarked);
			textView.setText("Bookmarked");
			
			Intent intent = new Intent(App.BOOKMARK_STATE_CHANGE_ACTION);
			Bundle data = new Bundle();
			data.putBoolean("isBookmarked", true);
			data.putString("placeId", placeId);
			intent.putExtras(data);
			sendBroadcast(intent);
		}
		else if("Bookmarked".equals(textView.getText()))
		{
			favoriteDataSource.removeFromFavorite(place);
			icon.setImageResource(R.drawable.ic_bookmark);
			textView.setText("Bookmark");
			
			Intent intent = new Intent(App.BOOKMARK_STATE_CHANGE_ACTION);
			Bundle data = new Bundle();
			data.putBoolean("isBookmarked", false);
			data.putString("placeId", placeId);
			intent.putExtras(data);
			sendBroadcast(intent);
		}
	}
	
	private void setUpMapIfNeeded()
	{
		if (mMap == null)
		{
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.business_map)).getMap();
			mMap.setPadding(0, 10, 0, 10);
			try
			{
				double lat = location.getLatitude();
				double lont = location.getLongitude();
				Marker mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lont)).title("You are here!"));
				mMarker.setDraggable(true);
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lont), 14.00f));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	} 
	
	class PlaceDetailTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... arg0)
		{
			place = googlePalcesClient.getPlace(placeId);
			return null;
		}
		
		protected void onPostExecute(String result)
		{
			detailsView.getBusinessName().setText(place.getName());
			detailsView.getBussinessAddr().setText(place.getAddress());
			detailsView.getRatingImg().setRating((float) place.getRating());
			m_handler.post(new Runnable()
			{
				
				@Override
				public void run()
				{
					ArrayList<String> refList = new ArrayList<String>();
					for(Photo photo :place.getPhotos())
					{
						refList.add(photo.getReference());
					}
					if(refList.size()==0)
					{
						mViewPager.setVisibility(View.GONE);
						viewPagerHolder.setVisibility(View.VISIBLE);
					}
					m_photo_reference_list.clear();
					m_photo_reference_list.addAll(refList);
					pagerAdapter.notifyDataSetChanged();
				}
			});
			mRefreshBtn.endRefresh();
		}
	}
	
	private class MenuItemClickListener implements CustomItemClickListener
	{
		@Override
		public void onClick(View view)
		{
			int index = (Integer) view.getTag();
			if(index == 0)
			{
				onDirectionClick();
			}
			else if(index == 1)
			{
				onBrowseReviewClick();
			}
			else if(index == 2)
			{
				onPhoneClick();
			}
			else if(index == 3)
			{
				onBookmarkClick(view);
			}
		}
	}
}
