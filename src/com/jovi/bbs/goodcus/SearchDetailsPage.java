package com.jovi.bbs.goodcus;


import com.google.gson.Gson;
import com.jovi.bbs.goodcus.fragment.DetailFragmentNavigationListener;
import com.jovi.bbs.goodcus.fragment.MapDirectionFragment;
import com.jovi.bbs.goodcus.fragment.ReviewListFragment;
import com.jovi.bbs.goodcus.fragment.SearchDetailFragment;
import com.jovi.bbs.goodcus.widgets.ViewPagerWithSwipingControl;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

public class SearchDetailsPage extends FragmentActivity implements DetailFragmentNavigationListener
{
	private static final int NUM_PAGES = 3;
	public static final int FRAGMENT_TAG_DETAIL_INFO   = 0;
	public static final int FRAGMENT_TAG_DETAIL_MAP    = 1;
	public static final int FRAGMENT_TAG_DETAIL_REVIEW = 2;

    private ViewPagerWithSwipingControl mPager;
    private PagerAdapter mPagerAdapter;

	private PlaceValueHolder placeValueHolder;
    
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		configure();
		initView();
	}

	public void configure()
	{
		Gson gson = new Gson();
		String placeId =  this.getIntent().getExtras().getString("placeId");
		String jsonLocation =  this.getIntent().getExtras().getString("location");
		String jsonCurrentLocation =  this.getIntent().getExtras().getString("currentLocation");
		Location location = gson.fromJson(jsonLocation, Location.class);
		Location currentLocation = gson.fromJson(jsonCurrentLocation, Location.class);
		placeValueHolder = new PlaceValueHolder(placeId, location, currentLocation);
	}
	
	public void initView()
	{
		setContentView(R.layout.activity_search_details_page);
		mPager = (ViewPagerWithSwipingControl) findViewById(R.id.detail_pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		mPager.setPagingSwipingEnabled(false);
	}
	
	
	public void onBackBtnClick(View v)
	{
		this.finish();
	}
	
	private class ScreenSlidePagerAdapter extends FragmentPagerAdapter
	{
		public ScreenSlidePagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			if(position == FRAGMENT_TAG_DETAIL_INFO)
			{
				return SearchDetailFragment.newInstance(placeValueHolder);
			}
			else if(position == FRAGMENT_TAG_DETAIL_MAP)
			{
				return MapDirectionFragment.newInstance(placeValueHolder);
			}
			else if(position == FRAGMENT_TAG_DETAIL_REVIEW)
			{
				return ReviewListFragment.newInstance(placeValueHolder);
			}
			return null;
		}

		@Override
		public int getCount()
		{
			return NUM_PAGES;
		}
	}
    
	@Override
	public void onNavigateInvoked(int fragmentTag)
	{
		mPager.setCurrentItem(fragmentTag);
	}
	
	public class PlaceValueHolder
	{
		private String placeId;
	    private Location location;
		private Location currentLocation;
		
		public PlaceValueHolder(String placeId, Location location, Location currentLocation)
		{
			this.placeId = placeId;
			this.location = location;
			this.currentLocation = currentLocation;
		}

		public String getPlaceId()
		{
			return placeId;
		}

		public Location getLocation()
		{
			return location;
		}

		public Location getCurrentLocation()
		{
			return currentLocation;
		}
	}
}
