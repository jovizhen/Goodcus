package com.jovi.bbs.goodcus.util;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
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
	
	public SearchResultListAdapter(Activity hostActivity, ArrayList<Place>m_model, CustomGooglePlaces googlePlacesClient)
	{
		this.hostActivity = hostActivity;
		this.m_model = m_model;
		this.googlePlacesClient = googlePlacesClient;
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
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.name.setText(m_model.get(position).getName());
		viewHolder.addr.setText(m_model.get(position).getVicinity());
		viewHolder.ratingbar.setRating((float) m_model.get(position).getRating());
		if(m_model.get(position).getPhotos().size()>0)
		{
			imageLoader.DisplayImage(googlePlacesClient.buildPhotoDownloadUrl(m_model.get(position).getPhotos().get(0), 100, 100), 
					m_model.get(position).getPlaceId(),  viewHolder.img);
		}
		viewHolder.bookmark.setVisibility(m_model.get(position).isBookmark() ? View.VISIBLE : View.GONE);
		return convertView;
	}
	
	class ViewHolder
	{
		TextView name;
		TextView addr;
		RatingBar ratingbar;
		ImageViewWithCache img;
		ImageView bookmark;
	}
}