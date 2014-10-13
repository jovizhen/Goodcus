package com.jovi.bbs.goodcus;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;
import com.jovi.bbs.goodcus.net.Api;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Place;
import com.jovi.bbs.goodcus.util.FavoriteDBDataSource;
import com.jovi.bbs.goodcus.util.GoogleImageLoader;
import com.jovi.bbs.goodcus.widgets.ImageViewWithCache;
import com.jovi.bbs.goodcus.widgets.deleteListView.BaseSwipeListViewListener;
import com.jovi.bbs.goodcus.widgets.deleteListView.SwipeListView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class SettingPage extends Activity
{
	private TextView m_version;
	private TextView m_loginUserName = null;
	private ImageView m_loginIcon = null;
	private ImageViewWithCache m_loginUserHeadImg = null;
	private SwipeListView favListView;
	
	private ArrayList<Place> m_model = new ArrayList<Place>();
	private FavoriteListAdapter m_adapter;
	private FavoriteDBDataSource favoritorDataSource;
	
	private Recv m_recv = null;
	private Api api;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		configure();
		initView();
		loadModel();
	}
	

	@Override
	protected void onResume()
	{
		favoritorDataSource.open();
		loadModel();
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		favoritorDataSource.close();
		super.onPause();
	}
	
	public void configure()
	{
		favoritorDataSource = FavoriteDBDataSource.getInSource(this);
		favoritorDataSource.open();
		api = Api.getInstance();
		m_recv = new Recv();
		IntentFilter filter = new IntentFilter();
		filter.addAction(App.LOGIN_STATE_CHANGE_ACTION);
		this.registerReceiver(m_recv, filter);
	}
	
	public void initView()
	{
		setContentView(R.layout.setting_page);
		m_version = (TextView) this.findViewById(R.id.settingAppVersion);
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
		m_loginUserHeadImg = (ImageViewWithCache) this.findViewById(R.id.settingPageLoginUserHeadImg);
		m_loginUserName = (TextView) this.findViewById(R.id.settingPageLoginUserName);
		m_loginIcon = (ImageView) this.findViewById(R.id.settingPageLoginIcon);
		favListView = (SwipeListView) findViewById(R.id.favorite_list);
		favListView.setSwipeListViewListener(new BaseSwipeListViewListener()
		{
			public void onClickFrontView(int position)
			{
				if (position < 0)
					return;
				Place selectPlace = m_model.get(position);
				Bundle data = new Bundle();
				data.putString("placeId", selectPlace.getPlaceId());
				Location location = new Location("");
				location.setLatitude(selectPlace.getLatitude());
				location.setLongitude(selectPlace.getLongitude());
				Gson gson = new Gson();
				String jsonLocation = gson.toJson(location);
				data.putSerializable("location", jsonLocation);
				Intent intent = new Intent(SettingPage.this, SearchDetailsPage.class);
				intent.putExtras(data);
				startActivity(intent);
			}
		});
		m_adapter = new FavoriteListAdapter();
		favListView.setAdapter(m_adapter);
	}

	public void onLoginItemClick(View v)
	{
		if (!api.getGooglePlusClient().isConnected())
		{
			api.connectToGooglePlus();
		}

		else
		{
			// api.getGooglePlusClient().clearDefaultAccount();
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
			m_loginUserName.setText("游客");
			m_loginIcon.setImageResource(R.drawable.social_add_person_dark);
		}
	}
	
	private void loadModel()
	{
		m_model = favoritorDataSource.getAllFavorites();
		m_adapter.notifyDataSetChanged();
	}
	
	public class FavoriteListAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			return m_model.size();
		}

		@Override
		public Object getItem(int position)
		{
			if(m_model.size()!=0)
			{
				return m_model.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int psosition)
		{
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			GoogleImageLoader imageLoader= new GoogleImageLoader(SettingPage.this);
			if (convertView == null)
			{
				convertView = getLayoutInflater().inflate(R.layout.delete_list_item, null);
			}
			TextView name = (TextView) convertView.findViewById(R.id.busin_name);
			TextView address = (TextView) convertView.findViewById(R.id.busin_address);
			RatingBar ratingbar = (RatingBar) convertView.findViewById(R.id.busRating);
			ImageViewWithCache img = (ImageViewWithCache) convertView.findViewById(R.id.businImgDetail);
			Button del_btn = (Button) convertView.findViewById(R.id.btn_delete);
			final int index = position;
			
			imageLoader.DisplayImage(null, m_model.get(position).getPlaceId(),  img);
			del_btn.setOnClickListener(new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					favoritorDataSource.removeFromFavorite(m_model.get(index));
					m_model.remove(index);
					notifyDataSetChanged();
				}
			});
			
			name.setText(m_model.get(position).getName());
			address.setText(m_model.get(position).getVicinity());
			ratingbar.setRating((float) m_model.get(position).getRating());
			return convertView;
		}
	}
}
