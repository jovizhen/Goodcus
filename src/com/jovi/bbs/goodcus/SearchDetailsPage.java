package com.jovi.bbs.goodcus;

import java.io.InputStream;
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
import com.jovi.bbs.goodcus.widgets.SearchDetailsView;
import com.jovi.bbs.goodcus.widgets.tableView.UITableView;
import com.jovi.bbs.goodcus.widgets.tableView.UITableView.ClickListener;
import com.jovi.bbs.goodcus.widgets.touchGallery.CirclePageIndicator;
import com.jovi.bbs.goodcus.widgets.touchGallery.ReferencePagerAdapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

public class SearchDetailsPage extends Activity 
{
	protected static final String TAG = "Search Details Page";
	private   static final String MAP_DIRECTION_FRAGMENT  = "map_direction_fragment";

	private ViewPager mViewPager;
	private UITableView tableView;
	private RelativeLayout pageHeader;
	private SearchDetailsView detailsView;
	private CirclePageIndicator mIndicator;

	private ArrayList<String> m_photos = new ArrayList<String>();
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
		favoriteDataSource = FavoriteDBDataSource.getInSource(this);
		favoriteDataSource.open();
	}
	
	private void initView()
	{
		setContentView(R.layout.search_details_page);
		detailsView = (SearchDetailsView) findViewById(R.id.searchDetailsFrag);
		pagerAdapter = new ReferencePagerAdapter(this, m_photos);
		mViewPager = (ViewPager) findViewById(R.id.viewer);
		mViewPager.setAdapter(pagerAdapter);
		pageHeader = (RelativeLayout) findViewById(R.id.pagedetail_header);
		mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
		mIndicator.setViewPager(mViewPager);
		tableView = (UITableView) findViewById(R.id.tableView);
		setUpMapIfNeeded();
		createMenuList();
	}
	
	private void createMenuList() 
	{
		MenuItemClickListener listener = new MenuItemClickListener();
		tableView.setClickListener(listener);
		tableView.addBasicItem(R.drawable.ic_action_directions, "Direction", null);
		tableView.addBasicItem(R.drawable.ic_action_copy, "Reviews", null);
		tableView.addBasicItem(R.drawable.ic_action_call, "Telephone", null);
		tableView.commit();
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
	
	public void onAddToFavoriteClick(View v)
	{
		favoriteDataSource.addToFavorite(place);
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
			Thread aThread = new Thread()
			{
				@Override
				public void run()
				{
					Bitmap bitmap = null;
					if(place.getPhotos().size()>0)
					{
						InputStream is = place.getPhotos().get(0).download().getInputStream();
						bitmap = BitmapFactory.decodeStream(is);
					}
					DetailViewDisplayer displayer = new DetailViewDisplayer(place, bitmap);
					m_handler.post(displayer);
				}
			};
			
			aThread.start();
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
					m_photos.addAll(refList);
					pagerAdapter.notifyDataSetChanged();
				}
			});
		}
	}
	
	class DetailViewDisplayer implements Runnable
	{
		Place detailPlace;
		Bitmap bitmap;
		
		
		public DetailViewDisplayer(Place detailPlace, Bitmap bitmap)
		{
			this.detailPlace = detailPlace;
			this.bitmap = bitmap;
		}
		
		public void run()
		{
			detailsView.getBusinessName().setText(place.getName());
			detailsView.getBussinessAddr().setText(place.getAddress());
			detailsView.getRatingImg().setRating((float) place.getRating());
			if(bitmap != null)
			{
				detailsView.getHeadImgDetail().setImageBitmap(bitmap);
			}
		}
	}
	
	private class MenuItemClickListener implements ClickListener
	{
		@Override
		public void onClick(int index)
		{
			if(index == 0)
			{
				onDirectionClick();;
			}
			else if(index == 1)
			{
				onBrowseReviewClick();
			}
			else if(index == 2)
			{
				onPhoneClick();
			}
		}
	}
}
