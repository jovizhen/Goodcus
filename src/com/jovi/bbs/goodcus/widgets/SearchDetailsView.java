package com.jovi.bbs.goodcus.widgets;

import java.util.zip.Inflater;

import com.jovi.bbs.goodcus.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchDetailsView extends LinearLayout
{
	LinearLayout mContainer;
	ImageViewWithCache headImgDetail;
	TextView businessName;
	TextView bussinessAddr;
	ImageView ratingImg;
	TextView snippetText;

	public SearchDetailsView(Context context)
	{
		super(context);
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.search_detail_view, this);
		headImgDetail = (ImageViewWithCache) findViewById(R.id.headImgDetail);
		businessName = (TextView) findViewById(R.id.business_name);
		bussinessAddr = (TextView) findViewById(R.id.business_addr);
		ratingImg = (ImageView) findViewById(R.id.rating_image);
		snippetText = (TextView) findViewById(R.id.snippet_text);
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.LEFT);
	}
	
	public SearchDetailsView(Context context, AttributeSet attributeSet)
	{
		super(context, attributeSet);
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.search_detail_view, this);
		
		headImgDetail = (ImageViewWithCache) findViewById(R.id.headImgDetail);
		businessName = (TextView) findViewById(R.id.business_name);
		bussinessAddr = (TextView) findViewById(R.id.business_addr);
		ratingImg = (ImageView) findViewById(R.id.rating_image);
		snippetText = (TextView) findViewById(R.id.snippet_text);
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.LEFT);
	}

	public LinearLayout getmContainer()
	{
		return mContainer;
	}

	public ImageViewWithCache getHeadImgDetail()
	{
		return headImgDetail;
	}

	public TextView getBusinessName()
	{
		return businessName;
	}

	public TextView getBussinessAddr()
	{
		return bussinessAddr;
	}

	public ImageView getRatingImg()
	{
		return ratingImg;
	}

	public TextView getSnippetText()
	{
		return snippetText;
	}
	
	

}