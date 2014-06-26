package com.jovi.bbs.goodcus.model;

import android.R.integer;



public class SearchResult 
{
	String name;
	String display_phone;
	String id;
	String image_url;
	String rating_img_url;
	String rating_img_url_small;
	Location location;

	
	public SearchResult()
	{
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDisplay_phone()
	{
		return display_phone;
	}

	public void setDisplay_phone(String display_phone)
	{
		this.display_phone = display_phone;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getImage_url()
	{
		return image_url;
	}

	public void setImage_url(String image_url)
	{
		this.image_url = image_url;
	}
	
	public String getRating_img_url()
	{
		return rating_img_url;
	}

	public void setRating_img_url(String rating_img_url)
	{
		this.rating_img_url = rating_img_url;
	}

	public String getRating_img_url_small()
	{
		return rating_img_url_small;
	}

	public void setRating_img_url_small(String rating_img_url_small)
	{
		this.rating_img_url_small = rating_img_url_small;
	}


	public Location getLocation()
	{
		return location;
	}

	public void setLocation(Location location)
	{
		this.location = location;
	}
	
	public String getLocationLabel()
	{
		String outputLabel = "";
		String[] address = location.getAddress();
		for(int i = 0 ; i<address.length;i++)
		{
			outputLabel += address[i]+" ";
		}
		outputLabel+=location.city;
		return outputLabel;
	}
	
}
