package com.jovi.bbs.goodcus;

import com.jovi.bbs.goodcus.fragment.SearchResultFragmentFactory;
import com.jovi.bbs.goodcus.widgets.PagerSlidingTabStrip;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;

public class NearbyPage extends FragmentActivity
{
	CollectionPagerAdapter mCollectionPagerAdapter;
    ViewPager mViewPager;
    private PagerSlidingTabStrip tabs;
    private Fragment currentFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
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
	
	public class CollectionPagerAdapter extends FragmentStatePagerAdapter
	{

		public CollectionPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int index)
		{

			Fragment fragment = SearchResultFragmentFactory.buildFragment(SearchResultFragmentFactory.navigation_menu[index].getKey());
			// Bundle args = new Bundle();
			// // Our object is just an integer :-P
			// args.putInt(DemoObjectFragment.ARG_OBJECT, i + 1);
			// fragment.setArguments(args);
			// return fragment;
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
