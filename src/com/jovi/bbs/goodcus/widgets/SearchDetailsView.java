package com.jovi.bbs.goodcus.widgets;


import com.jovi.bbs.goodcus.R;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Place;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Price;
import com.jovi.bbs.goodcus.util.GooglePlaceHelper;
import com.jovi.bbs.goodcus.util.Utils;

import android.content.Context;
import android.location.Location;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class SearchDetailsView extends LinearLayout
{
	private LinearLayout mContainer;
	private TextView businessName;
	private TextView bussinessAddr;
	private TextView distance;
	private TextView priceLabel;
	private TextView price;
	private TextView hoursLabel;
	private TextView hours;
	private TextView status;
	private RatingBar ratingImg;

	public SearchDetailsView(Context context)
	{
		super(context);
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.search_detail_view, this);
		businessName = (TextView) findViewById(R.id.business_name);
		bussinessAddr = (TextView) findViewById(R.id.business_addr);
		ratingImg = (RatingBar) findViewById(R.id.MyRating2);
		distance = (TextView) findViewById(R.id.detail_distance);
		priceLabel = (TextView) findViewById(R.id.detail_priceLabel);
		price = (TextView) findViewById(R.id.detail_price);
		hoursLabel = (TextView) findViewById(R.id.details_hoursLabel);
		hours = (TextView) findViewById(R.id.hours);
		status = (TextView) findViewById(R.id.status);
		
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.LEFT);
	}
	
	public SearchDetailsView(Context context, AttributeSet attributeSet)
	{
		super(context, attributeSet);
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.search_detail_view, this);
		businessName = (TextView) findViewById(R.id.business_name);
		bussinessAddr = (TextView) findViewById(R.id.business_addr);
		ratingImg = (RatingBar) findViewById(R.id.MyRating2);
		ratingImg = (RatingBar) findViewById(R.id.MyRating2);
		distance = (TextView) findViewById(R.id.detail_distance);
		priceLabel = (TextView) findViewById(R.id.detail_priceLabel);
		price = (TextView) findViewById(R.id.detail_price);
		hoursLabel = (TextView) findViewById(R.id.details_hoursLabel);
		hours = (TextView) findViewById(R.id.hours);
		status = (TextView) findViewById(R.id.status);
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.LEFT);
	}

	public void loadPlaceDetails(Place place, Location currentLocation, Location businessLocation)
	{
		getBusinessName().setText(place.getName());
		getBussinessAddr().setText(place.getVicinity());
		getRatingImg().setRating((float) place.getRating());
		getDistance().setText(Utils.computeDistance(currentLocation, businessLocation));
		getPriceLabel().setVisibility(place.getPrice() == Price.NONE ? View.GONE : View.VISIBLE);
		getPrice().setText(GooglePlaceHelper.getFormattedPrice(place.getPrice()));
		getHoursLabel().setVisibility(place.getHours().getPeriods().size() == 0 ? View.GONE : View.VISIBLE);
		getHours().setText(GooglePlaceHelper.getFormattedHours(place.getHours()));
		getStatus().setText(place.getStatus().toString().equals("CLOSED") ? "CLOSED" : "");
	}
	
	public LinearLayout getmContainer()
	{
		return mContainer;
	}

	public TextView getBusinessName()
	{
		return businessName;
	}

	public TextView getBussinessAddr()
	{
		return bussinessAddr;
	}

	public RatingBar getRatingImg()
	{
		return ratingImg;
	}

	public TextView getDistance()
	{
		return distance;
	}

	public TextView getPrice()
	{
		return price;
	}

	public TextView getHours()
	{
		return hours;
	}

	public TextView getStatus()
	{
		return status;
	}


	public TextView getPriceLabel()
	{
		return priceLabel;
	}

	public TextView getHoursLabel()
	{
		return hoursLabel;
	}
}
