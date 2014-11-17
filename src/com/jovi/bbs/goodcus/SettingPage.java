package com.jovi.bbs.goodcus;

import com.jovi.bbs.goodcus.fragment.FragmentNavigationListener;
import com.jovi.bbs.goodcus.fragment.SettingAboutFragment;
import com.jovi.bbs.goodcus.fragment.SettingBookMarkFragment;
import com.jovi.bbs.goodcus.fragment.SettingFeedbackFragment;
import com.jovi.bbs.goodcus.fragment.SettingHomeFragment;
import com.jovi.bbs.goodcus.widgets.ViewPagerWithSwipingControl;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;



public class SettingPage extends FragmentActivity implements FragmentNavigationListener
{
	private static final int NUM_PAGES = 4;
	public static final int FRAGMENT_SETTING_HOME      = 0;
	public static final int FRAGMENT_SETTING_BOOKMARK  = 1;
	public static final int FRAGMENT_SETTING_ABOUT     = 2;
	public static final int FRAGMENT_SETTING_FEEDBACK  = 3;

    private ViewPagerWithSwipingControl mPager;
    private PagerAdapter mPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		configure();
		initView();
	}
	
	public void configure()
	{
	}
	
	public void initView()
	{
		setContentView(R.layout.activity_setting_page);
		mPager = (ViewPagerWithSwipingControl) findViewById(R.id.setting_pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		mPager.setPageSwipingEnabled(false);
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
			if(position == FRAGMENT_SETTING_HOME)
			{
				return new SettingHomeFragment();
			}
			else if(position == FRAGMENT_SETTING_BOOKMARK)
			{
				return new SettingBookMarkFragment();
			}
			else if(position == FRAGMENT_SETTING_FEEDBACK)
			{
				return new SettingFeedbackFragment();
			}
			else if(position == FRAGMENT_SETTING_ABOUT)
			{
				return new SettingAboutFragment();
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
}
