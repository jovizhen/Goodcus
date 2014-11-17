package com.jovi.bbs.goodcus.fragment;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jovi.bbs.goodcus.App;
import com.jovi.bbs.goodcus.R;
import com.jovi.bbs.goodcus.SearchDetailsPage;
import com.jovi.bbs.goodcus.SearchDetailsPage.PlaceValueHolder;
import com.jovi.bbs.goodcus.net.googlePlacesApi.CustomGooglePlaces;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Photo;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Place;
import com.jovi.bbs.goodcus.util.FavoriteDBDataSource;
import com.jovi.bbs.goodcus.widgets.SearchDetailsView;
import com.jovi.bbs.goodcus.widgets.tableView.CustomUITableView;
import com.jovi.bbs.goodcus.widgets.tableView.CustomUITableView.CustomItemClickListener;
import com.jovi.bbs.goodcus.widgets.touchGallery.CirclePageIndicator;
import com.jovi.bbs.goodcus.widgets.touchGallery.ReferencePagerAdapter;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchDetailFragment extends Fragment
{
	private ViewPager mViewPager;
	private ImageView viewPagerHolder;
	private CustomUITableView tableView;
	private SearchDetailsView detailsView;
	private CirclePageIndicator mIndicator;

	private ArrayList<String> m_photo_reference_list = new ArrayList<String>();
	private ReferencePagerAdapter pagerAdapter;
	private CustomGooglePlaces googlePalcesClient;
	private FavoriteDBDataSource favoriteDataSource;
	private FragmentNavigationListener navigationListener;
	
	private String placeId;
	private Place place;
	private PlaceValueHolder valueHolder;
	private SupportMapFragment mMapFragment;
	private Location location;
	private Location currentLocation;
	private Handler m_handler = new Handler();
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		configure();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = initView(inflater, container, savedInstanceState);
		return view;
	}

	@Override
	public void onPause()
	{
		FragmentManager fm = getActivity().getSupportFragmentManager();
		Fragment fragment = (fm.findFragmentById(R.id.business_map));
		if(fragment!=null)
		{
			FragmentTransaction ft = fm.beginTransaction();
			ft.remove(fragment);
			ft.commit();
		}
		super.onPause();
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		setNavigateListener((FragmentNavigationListener) activity);
	}

	private void configure()
	{
		googlePalcesClient = new CustomGooglePlaces();
		placeId = valueHolder.getPlaceId();
		location = valueHolder.getLocation();
		currentLocation = valueHolder.getCurrentLocation();
		favoriteDataSource = FavoriteDBDataSource.getInStance(getActivity());
		favoriteDataSource.open();
	}
	
	private View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_details_screen, container, false);
		detailsView = (SearchDetailsView) view.findViewById(R.id.searchDetailsFrag);
		pagerAdapter = new ReferencePagerAdapter(getActivity(), m_photo_reference_list);
		viewPagerHolder = (ImageView) view.findViewById(R.id.view_page_place_holder);
		mViewPager = (ViewPager) view.findViewById(R.id.viewer);
		mViewPager.setAdapter(pagerAdapter);
		mIndicator = (CirclePageIndicator)view.findViewById(R.id.indicator);
		mIndicator.setViewPager(mViewPager);
		tableView = (CustomUITableView) view.findViewById(R.id.tableView);
		mMapFragment = null;
		setUpMapIfNeeded();
		createMenuList();
		new PlaceDetailTask().execute("");
		return view;
	}
	
	private void createMenuList() 
	{
		MenuItemClickListener listener = new MenuItemClickListener();
		tableView.setCustomListener(listener);
		tableView.addBasicItem(R.drawable.ic_action_directions, "Direction", null);
		tableView.addBasicItem(R.drawable.ic_action_copy, "Reviews", null);
		tableView.addBasicItem(R.drawable.ic_action_web_site, "Website", null);
		tableView.addBasicItem(R.drawable.ic_action_call, "Telephone", null, false);
		boolean isBookmarked = favoriteDataSource.isFavoriteAdded(placeId);
		tableView.addBasicItem(isBookmarked? R.drawable.ic_bookmarked: R.drawable.ic_bookmark, 
				isBookmarked? "Bookmarked":"Bookmark", null, false);
		tableView.commit();
	}
	
	private void setUpMapIfNeeded()
	{
		if (mMapFragment == null)
		{
			mMapFragment = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.business_map));
		}
		mMapFragment.getMap().setPadding(0, 0, 0, 0);
		try
		{
			double lat = location.getLatitude();
			double lont = location.getLongitude();
			Marker mMarker = mMapFragment.getMap().addMarker(new MarkerOptions().position(new LatLng(lat, lont)).title("You are here!"));
			mMarker.setDraggable(true);
			mMapFragment.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lont), 14.00f));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void navigateTo(int fragmentTag)
	{
		if (navigationListener != null)
		{
			navigationListener.onNavigateInvoked(fragmentTag);
		}
	}
	
	private void setNavigateListener(FragmentNavigationListener listener)
	{
		this.navigationListener = listener;
	}
	
	private void onWebsiteClick()
	{
		if(place == null)
			return;
		String webLink = place.getWebsite() != null? place.getWebsite():place.getGoogleUrl();
		if(webLink == null)
			return;
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webLink));
		startActivity(browserIntent);
	}
	
	private void onPhoneClick()
	{
	}
	
    private void onBookmarkClick(View view)
    {
    	if(place == null)
			return;
		
		ImageView icon = (ImageView) view.findViewById(R.id.image);
		TextView textView = (TextView) view.findViewById(R.id.title);
		if("Bookmark".equals(textView.getText()))
		{
			favoriteDataSource.addToFavorite(place);
			icon.setImageResource(R.drawable.ic_bookmarked);
			textView.setText("Bookmarked");
			
			Intent intent = new Intent(App.BOOKMARK_STATE_CHANGE_ACTION);
			Bundle data = new Bundle();
			data.putBoolean("isBookmarked", true);
			data.putString("placeId", placeId);
			intent.putExtras(data);
			getActivity().sendBroadcast(intent);
		}
		else if("Bookmarked".equals(textView.getText()))
		{
			favoriteDataSource.removeFromFavorite(place);
			icon.setImageResource(R.drawable.ic_bookmark);
			textView.setText("Bookmark");
			
			Intent intent = new Intent(App.BOOKMARK_STATE_CHANGE_ACTION);
			Bundle data = new Bundle();
			data.putBoolean("isBookmarked", false);
			data.putString("placeId", placeId);
			intent.putExtras(data);
			getActivity().sendBroadcast(intent);
		}
    }
	
	class PlaceDetailTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... arg0)
		{
			place = googlePalcesClient.getPlace(placeId);
			return null;
		}
		
		protected void onPostExecute(String result)
		{
			detailsView.loadPlaceDetails(place, currentLocation, location);
			m_handler.post(new Runnable()
			{
				@Override
				public void run()
				{
					ArrayList<String> refList = new ArrayList<String>();
					for(Photo photo :place.getPhotos())
					{
						refList.add(photo.getReference());
					}
					if(refList.size()==0)
					{
						mViewPager.setVisibility(View.GONE);
						viewPagerHolder.setVisibility(View.VISIBLE);
					}
					m_photo_reference_list.clear();
					m_photo_reference_list.addAll(refList);
					pagerAdapter.notifyDataSetChanged();
				}
			});
		}
	}
	
	private class MenuItemClickListener implements CustomItemClickListener
	{
		@Override
		public void onClick(View view)
		{
			int index = (Integer) view.getTag();
			if(index == 0)
			{
				navigateTo(SearchDetailsPage.FRAGMENT_TAG_DETAIL_MAP);
			}
			else if(index == 1)
			{
				navigateTo(SearchDetailsPage.FRAGMENT_TAG_DETAIL_REVIEW);
			}
			else if(index == 2)
			{
				onWebsiteClick();
			}
			else if(index == 3)
			{
				onPhoneClick();
			}
			else if(index == 4)
			{
				onBookmarkClick(view);
			}
		}
	}
	
	public static SearchDetailFragment newInstance(PlaceValueHolder valueHolder)
	{
		SearchDetailFragment instance = new SearchDetailFragment();
		instance.valueHolder = valueHolder;
		return instance;
	}
}
