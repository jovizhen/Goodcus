package com.jovi.bbs.goodcus.fragment;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.jovi.bbs.goodcus.App;
import com.jovi.bbs.goodcus.R;
import com.jovi.bbs.goodcus.SearchDetailsPage;
import com.jovi.bbs.goodcus.SettingPage;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Place;
import com.jovi.bbs.goodcus.util.FavoriteDBDataSource;
import com.jovi.bbs.goodcus.util.GoogleImageLoader;
import com.jovi.bbs.goodcus.util.LocationChangeHandler;
import com.jovi.bbs.goodcus.util.Utils;
import com.jovi.bbs.goodcus.widgets.ImageViewWithCache;
import com.jovi.bbs.goodcus.widgets.deleteListView.BaseSwipeListViewListener;
import com.jovi.bbs.goodcus.widgets.deleteListView.SwipeListView;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

public class SettingBookMarkFragment extends Fragment
{
	
	private SwipeListView favListView;
	private ImageButton bookmarkBackBtn;

	private ArrayList<Place> m_model = new ArrayList<Place>();
	private FavoriteListAdapter m_adapter;
	private FavoriteDBDataSource favoritorDataSource;
	
	private LocationChangeHandler locationChangeHandler;
	private FragmentNavigationListener navigationListener;
	private Location currentLocation;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		configure();
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		setNavigateListener((FragmentNavigationListener) activity);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = initView(inflater, container);
		initView(inflater, container);
		return view;
	}
	
	@Override
	public void onResume()
	{
		favoritorDataSource.open();
		loadModel();
		super.onResume();
	}
	
	private View initView(LayoutInflater inflater, ViewGroup container)
	{
		View view = inflater.inflate(R.layout.fragment_bookmark, container, false);
		
		favListView = (SwipeListView) view.findViewById(R.id.bookmark_list);
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
				String jsonCurrentLocation = gson.toJson(currentLocation);
				data.putSerializable("currentLocation", jsonCurrentLocation);
				Intent intent = new Intent(getActivity(), SearchDetailsPage.class);
				intent.putExtras(data);
				startActivity(intent);
			}
		});
		m_adapter = new FavoriteListAdapter();
		favListView.setAdapter(m_adapter);
		bookmarkBackBtn = (ImageButton) view.findViewById(R.id.bookmarkBackBtn);
		bookmarkBackBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				navigateTo(SettingPage.FRAGMENT_SETTING_HOME);
			}
		});
		
		return view;
	}
	
	private void configure()
	{
		favoritorDataSource = FavoriteDBDataSource.getInStance(getActivity());
		favoritorDataSource.open();
		locationChangeHandler = new LocationChangeHandler(getActivity());
		currentLocation = Utils.getCurrentLocation(getActivity(), locationChangeHandler);
	}
	
	private void loadModel()
	{
		m_model = favoritorDataSource.getAllFavorites();
		m_adapter.notifyDataSetChanged();
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
			GoogleImageLoader imageLoader= new GoogleImageLoader(getActivity());
			if (convertView == null)
			{
				convertView = getActivity().getLayoutInflater().inflate(R.layout.delete_list_item, null);
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
					Bundle dataBundle = new Bundle();
					dataBundle.putString("placeId", m_model.get(index).getPlaceId());
					dataBundle.putBoolean("isBookmarked", false);
					Intent intent = new Intent(App.BOOKMARK_STATE_CHANGE_ACTION);
					intent.putExtras(dataBundle);
					getActivity().sendBroadcast(intent);
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
