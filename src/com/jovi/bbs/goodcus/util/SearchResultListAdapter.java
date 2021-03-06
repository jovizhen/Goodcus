package com.jovi.bbs.goodcus.util;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.jovi.bbs.goodcus.R;
import com.jovi.bbs.goodcus.net.googlePlacesApi.CustomGooglePlaces;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Place;
import com.jovi.bbs.goodcus.widgets.ImageViewWithCache;

public class SearchResultListAdapter extends BaseAdapter
{
	private GoogleImageLoader imageLoader;
	private ViewHolder viewHolder;
	private ArrayList<Place> m_model;
	private CustomGooglePlaces googlePlacesClient;
	private Activity hostActivity;
	private Location currentLocation;
	
	public SearchResultListAdapter(Activity hostActivity, ArrayList<Place>m_model, CustomGooglePlaces googlePlacesClient, Location currentLocation)
	{
		this.hostActivity = hostActivity;
		this.m_model = m_model;
		this.googlePlacesClient = googlePlacesClient;
		this.currentLocation = currentLocation;
		imageLoader = new GoogleImageLoader(hostActivity);
	}
	
	@Override
	public int getCount()
	{
		return (m_model == null) ? 0 : m_model.size();
	}

	@Override
	public Object getItem(int index)
	{
		if (m_model.size() != 0)
		{
			return m_model.get(index);
		}
		return null;
	}

	@Override
	public long getItemId(int arg0)
	{
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) hostActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.search_result_item, null);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.business_name);
			viewHolder.addr = (TextView) convertView.findViewById(R.id.business_address);
			viewHolder.ratingbar  =  (RatingBar) convertView.findViewById(R.id.MyRating);
			viewHolder.img = (ImageViewWithCache) convertView.findViewById(R.id.headImgDetail);
			viewHolder.bookmark = (ImageView) convertView.findViewById(R.id.bookmark);
			viewHolder.businessType = (TextView) convertView.findViewById(R.id.business_type);
			viewHolder.distance = (TextView) convertView.findViewById(R.id.distance);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.name.setText(m_model.get(position).getName());
		viewHolder.addr.setText(getAddress(m_model.get(position)));
		viewHolder.ratingbar.setRating((float) m_model.get(position).getRating());
		viewHolder.businessType.setText(m_model.get(position).getTypes().toString());
		if(m_model.get(position).getPhotos().size()>0)
		{
			imageLoader.DisplayImage(googlePlacesClient.buildPhotoDownloadUrl(m_model.get(position).getPhotos().get(0), 100, 100), 
					m_model.get(position).getPlaceId(),  viewHolder.img);
		}
		viewHolder.bookmark.setVisibility(m_model.get(position).isBookmark() ? View.VISIBLE : View.GONE);
		Location placeLocation = new Location("");
		placeLocation.setLatitude(m_model.get(position).getLatitude());
		placeLocation.setLongitude(m_model.get(position).getLongitude());
		viewHolder.distance.setText(Utils.computeDistance(currentLocation, placeLocation));
		return convertView;
	}
	
	public void setCurrentLocation(Location currentLocation)
	{
		this.currentLocation = currentLocation;
	}
	
	private String getAddress(Place place)
	{
		if(place.getVicinity()!=null)
		{
			return place.getVicinity();
		}
		
		else if(place.getAddress()!=null)
		{
			String[] addressComponents = place.getAddress().split(",");
			if (addressComponents.length > 2)
			{
				StringBuffer address = new StringBuffer();
				for (int i = 0; i < addressComponents.length - 2; i++)
				{
					if (i != addressComponents.length - 3)
					{
						address.append(addressComponents[i]).append(",");
					} else
					{
						address.append(addressComponents[i]);
					}
				}
				return address.toString();
			}
		}
		return null;
	}
	
	class ViewHolder
	{
		TextView name;
		TextView addr;
		RatingBar ratingbar;
		ImageViewWithCache img;
		ImageView bookmark;
		TextView businessType;
		TextView distance;
	}
}