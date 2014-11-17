package com.jovi.bbs.goodcus.fragment;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.android.gms.plus.model.people.Person;
import com.jovi.bbs.goodcus.App;
import com.jovi.bbs.goodcus.R;
import com.jovi.bbs.goodcus.SettingPage;
import com.jovi.bbs.goodcus.net.Api;
import com.jovi.bbs.goodcus.widgets.ImageViewWithCache;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingHomeFragment extends Fragment
{
	private FragmentNavigationListener navigationListener;

	private TextView m_version;
	private TextView m_loginUserName = null;
	private ImageView m_loginIcon = null;
	private ImageViewWithCache m_loginUserHeadImg = null;
	
	private LinearLayout feedbackBtn;
	private LinearLayout aboutBtn;
	private LinearLayout bookmarkBtn;
	private LinearLayout loginBtn;
	
	private Recv m_recv = null;
	private Api api;
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		configure();
		setNavigateListener((FragmentNavigationListener) activity);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = initView(inflater, container);
		return view;
	}
	
	private View initView(LayoutInflater inflater, ViewGroup container)
	{
		View view = inflater.inflate(R.layout.fragment_setting_home, container, false);
		m_version = (TextView) view.findViewById(R.id.settingAppVersion);
		PackageInfo pinfo = null;
		try
		{
			pinfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), PackageManager.GET_CONFIGURATIONS);
			m_version.setText(pinfo.versionName);
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		m_loginUserHeadImg = (ImageViewWithCache) view.findViewById(R.id.settingPageLoginUserHeadImg);
		m_loginUserName = (TextView) view.findViewById(R.id.settingPageLoginUserName);
		m_loginIcon = (ImageView) view.findViewById(R.id.settingPageLoginIcon);
		loginBtn = (LinearLayout) view.findViewById(R.id.loginBtn);
		loginBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onLoginItemClick(v);
			}
		});
		feedbackBtn = (LinearLayout) view.findViewById(R.id.feedbackBtn);
		feedbackBtn.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				navigateTo(SettingPage.FRAGMENT_SETTING_FEEDBACK);
			}
		});
		aboutBtn = (LinearLayout) view.findViewById(R.id.aboutBtn);
		aboutBtn.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				navigateTo(SettingPage.FRAGMENT_SETTING_ABOUT);
				
			}
		});
		bookmarkBtn = (LinearLayout) view.findViewById(R.id.bookmarkBtn);
		bookmarkBtn.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				navigateTo(SettingPage.FRAGMENT_SETTING_BOOKMARK);
			}
		});
		
		return view;
	}
	
	private void configure()
	{
		api = Api.getInstance();
		m_recv = new Recv();
		IntentFilter filter = new IntentFilter();
		filter.addAction(App.LOGIN_STATE_CHANGE_ACTION);
		getActivity().registerReceiver(m_recv, filter);
	}
	
	private void setNavigateListener(FragmentNavigationListener listener)
	{
		this.navigationListener = listener;
	}
	
	private void navigateTo(int fragmentTag)
	{
		if (navigationListener != null)
		{
			navigationListener.onNavigateInvoked(fragmentTag);
		}
	}
	
	public void onLoginItemClick(View v)
	{
		if (!api.getGooglePlusClient().isConnected())
		{
			api.connectToGooglePlus();
		}

		else
		{
			api.disconnectFromGooglePlus();
		}
	}
	
	public void onCheckUpdateItemClick()
	{
	}

	private class Recv extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (api.getGooglePlusClient().isConnected())
			{
				onLoginState();
			} else
			{
				onLogoutState();
			}
		}

		public void onLoginState()
		{
			if (api.getGooglePlusClient().getCurrentPerson() != null)
			{
				Person currUser = api.getGooglePlusClient().getCurrentPerson();
				String url = currUser.getImage().getUrl();
				try
				{
					m_loginUserHeadImg.setImageUrl(new URL(url));
				}
				catch (MalformedURLException e)
				{
					e.printStackTrace();
				}
				m_loginIcon.setImageResource(R.drawable.logout_dark);
				m_loginUserName.setText(currUser.getDisplayName());
			}
		}

		public void onLogoutState()
		{
			m_loginUserHeadImg.setImageResource(R.drawable.default_user_head_img);
			m_loginUserName.setText("Guest");
			m_loginIcon.setImageResource(R.drawable.social_add_person_dark);
		}
	}
}
