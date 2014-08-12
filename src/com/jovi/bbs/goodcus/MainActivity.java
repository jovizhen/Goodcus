package com.jovi.bbs.goodcus;


import com.jovi.bbs.goodcus.net.Api;
import com.jovi.bbs.goodcus.util.HttpUtil;

import android.os.Bundle;
import android.app.TabActivity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

public class MainActivity extends TabActivity
{

	private long m_exitTime = 0;
	private String[] m_tabTitle = new String[]
	{ "附近", "搜索", "设置" };
	
	private Class<?>[] m_tabIntent = new Class<?>[]
	{ NearbyPage.class, SearchResultPage.class, SettingPage.class };
	
	private int[] m_tabIcon = new int[]
	{ R.drawable.collections_view_as_grid, R.drawable.ic_action_search, R.drawable.action_settings };

	private Bundle[] m_data = new Bundle[]
	{ createBundle("附近"), createBundle("搜索"), createBundle("设置") };

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Api.getInstance().setActivity(this);
		HttpUtil.getInstance().setContext(this);
		
		TabHost tabHost = getTabHost();
		
		for (int i = 0; i < this.m_tabTitle.length; i++)
		{
			String title = this.m_tabTitle[i];
			Intent intent = new Intent(this, m_tabIntent[i]);
			if (m_data[i] != null)
			{
				intent.putExtras(m_data[i]);
			}
			View tab = getLayoutInflater().inflate(R.layout.forum_tab, null);
			ImageView imgView = (ImageView) tab.findViewById(R.id.tabIcon);
			imgView.setImageResource(m_tabIcon[i]);
			TabSpec spec = tabHost.newTabSpec(title).setIndicator(tab).setContent(intent);
			tabHost.addTab(spec);
		}
	}
	

	@Override
	public boolean dispatchKeyEvent(KeyEvent event)
	{
		int keyCode = event.getKeyCode();
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			if ((System.currentTimeMillis() - m_exitTime) > 2000)
			{
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				m_exitTime = System.currentTimeMillis();
			} 
			else
			{
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private Bundle createBundle(String title)
	{
		Bundle dataBundle = new Bundle();
		dataBundle.putString("title", title);
		return dataBundle;
	}
	

}
