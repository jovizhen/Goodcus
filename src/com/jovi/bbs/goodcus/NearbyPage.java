package com.jovi.bbs.goodcus;

import com.jovi.bbs.goodcus.widgets.PagerSlidingTabStrip;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
	
	public class CollectionPagerAdapter extends FragmentStatePagerAdapter
	{

		public CollectionPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int index)
		{
			Fragment fragment = new DemoFragment();
//	        Bundle args = new Bundle();
//	        // Our object is just an integer :-P
//	        args.putInt(DemoObjectFragment.ARG_OBJECT, i + 1);
//	        fragment.setArguments(args);
//	        return fragment;
			return fragment;
		}

		@Override
		public int getCount()
		{
			return 50;
		}
		
		public CharSequence getPageTitle(int position) 
		{
	        return "OBJECT " + (position + 1);
	    }
	}
}
