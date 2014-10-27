package com.jovi.bbs.goodcus;

import com.jovi.bbs.goodcus.fragment.SearchResultFragmentFactory;
import com.jovi.bbs.goodcus.util.FavoriteDBDataSource;
import com.jovi.bbs.goodcus.util.LocationChangeHandler;
import com.jovi.bbs.goodcus.util.Utils;
import com.jovi.bbs.goodcus.widgets.PagerSlidingTabStrip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;

public class NearbyPage extends FragmentActivity
{
	ViewPager mViewPager;
	private PagerSlidingTabStrip tabs;
	private Fragment currentFragment;
	private Location currentLocation;
	private LocationReciever mLocationReciever;
	private CollectionPagerAdapter mCollectionPagerAdapter;
	private LocationChangeHandler locationChangeHandler;

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		configure();
		initView();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		FavoriteDBDataSource.getInStance(this).close();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		FavoriteDBDataSource.getInStance(this).open();
	}

	public void configure()
	{
		FavoriteDBDataSource.getInStance(this).open();
		mLocationReciever = new LocationReciever();
		IntentFilter filter_location = new IntentFilter();
		filter_location.addAction(App.GEO_LOCATION_UPDATE_ACTION);
		registerReceiver(mLocationReciever, filter_location);
		
		locationChangeHandler = new LocationChangeHandler(this);
		currentLocation = Utils.getCurrentLocation(this, locationChangeHandler);
		
		
	}
	
	public void initView()
	{
		setContentView(R.layout.nearby_page);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		mCollectionPagerAdapter = new CollectionPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mCollectionPagerAdapter);
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		mViewPager.setPageMargin(pageMargin);
		tabs.setViewPager(mViewPager);
	}
	
	
	
	public Location getCurrentLocation()
	{
		return currentLocation;
	}

	public Fragment getCurrentFragment()
	{
		return currentFragment;
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
		}
	}
	
	public class CollectionPagerAdapter extends FragmentPagerAdapter
	{
		public CollectionPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int index)
		{
			Fragment fragment = SearchResultFragmentFactory.buildFragment(SearchResultFragmentFactory.navigation_menu[index]);
			currentFragment = fragment;
			return fragment;
		}

		@Override
		public int getCount()
		{
			return SearchResultFragmentFactory.navigation_menu.length;
		}
		
		public CharSequence getPageTitle(int position) 
		{
	        return SearchResultFragmentFactory.navigation_menu[position].getName();
	    }
	}
}
