package com.jovi.bbs.goodcus;

import com.jovi.bbs.goodcus.fragment.SearchResultFragmentFactory;
import com.jovi.bbs.goodcus.widgets.PagerSlidingTabStrip;

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
	private CollectionPagerAdapter mCollectionPagerAdapter;

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		initView();
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
	
	public Fragment getCurrentFragment()
	{
		return currentFragment;
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
