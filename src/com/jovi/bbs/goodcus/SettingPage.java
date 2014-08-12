package com.jovi.bbs.goodcus;


import java.net.MalformedURLException;
import java.net.URL;

import com.google.android.gms.plus.model.people.Person;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingPage extends Activity 
{
	
	private TextView m_version;
	private ImageViewWithCache m_loginUserHeadImg = null;
	private TextView m_loginUserName = null;
	private ImageView m_loginIcon = null;
	private Recv m_recv = null;
	Api api;

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_page);
		api = Api.getInstance();
		
		m_version = (TextView)this.findViewById(R.id.settingAppVersion);
		PackageInfo pinfo = null;
		try
		{
			pinfo = getPackageManager().getPackageInfo(SettingPage.this.getPackageName(), PackageManager.GET_CONFIGURATIONS);
			m_version.setText(pinfo.versionName);
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		
		m_loginUserHeadImg = (ImageViewWithCache)this.findViewById(R.id.settingPageLoginUserHeadImg);
		m_loginUserName = (TextView)this.findViewById(R.id.settingPageLoginUserName);
		m_loginIcon = (ImageView)this.findViewById(R.id.settingPageLoginIcon);
		
		m_recv = new Recv();
		IntentFilter filter = new IntentFilter();
		filter.addAction(App.LOGIN_STATE_CHANGE_ACTION);
		this.registerReceiver(m_recv, filter);
	}
	
	
	
	public void onLoginItemClick(View v)
	{
		if(!api.getGooglePlusClient().isConnected())
		{
			api.connectToGooglePlus();
		}
		
		else
		{
//			api.getGooglePlusClient().clearDefaultAccount();
			api.disconnectFromGooglePlus();
		}
	}
	
	public void onCheckUpdateItemClick()
	{
		
	}
	
	public void onFeedbackItemClick()
	{
		
	}

	public void onAboutBtnClick()
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
			}
//			else if(isConnecting) 
//			{
//				Builder builder = new Builder(SettingPage.this);
//				builder.setMessage("登陆失败，请稍候再试");
//				builder.setPositiveButton("确定", null);
//				builder.create().show();
//				onLogoutState();
//			}
			else
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
			m_loginUserName.setText("游客");
			m_loginIcon.setImageResource(R.drawable.social_add_person_dark);
		}
	}

}
