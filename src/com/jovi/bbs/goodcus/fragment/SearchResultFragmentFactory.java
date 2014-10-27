package com.jovi.bbs.goodcus.fragment;


import com.jovi.bbs.goodcus.net.googlePlacesApi.GooglePlaceFilter;

import android.support.v4.app.Fragment;

public class SearchResultFragmentFactory
{
	public static final SearchType RESTAURANT_CN                = new SearchType(0, "Chinese", new GooglePlaceFilter().setTypes("restaurant").setKeyword("chinese").setLanguage("en"));
	public static final SearchType RESTAURANT_JP                = new SearchType(1, "Japanese", new GooglePlaceFilter().setTypes("restaurant").setKeyword("japanese").setLanguage("en"));
	public static final SearchType RESTAURANT_KR                = new SearchType(2, "Korean", new GooglePlaceFilter().setTypes("restaurant").setKeyword("korean").setLanguage("en"));
	public static final SearchType BAKERY_AND_CAFE              = new SearchType(3, "Coffe & Tea", new GooglePlaceFilter().setKeyword("cafe+bakery").setLanguage("en"));
	public static final SearchType GROCERY_STORE_MARKET         = new SearchType(4, "Grocery", new GooglePlaceFilter().setKeyword("supermarket").setLanguage("en"));
	public static final SearchType GYM_AND_SPA                  = new SearchType(5, "Gym/SPA",new GooglePlaceFilter().setKeyword("gym+spa").setLanguage("en")); 
	public static final SearchType NIGHT_CLUB                   = new SearchType(6, "Nightlife", new GooglePlaceFilter().setTypes("bar").setKeyword("night").setLanguage("en")); 
	public static final SearchType PUBLIC_UTIL                  = new SearchType(7, "Others", new GooglePlaceFilter().setLanguage("en"));
	
	public static final SearchType[] navigation_menu =
	{ RESTAURANT_CN,RESTAURANT_JP, RESTAURANT_KR,  BAKERY_AND_CAFE, GROCERY_STORE_MARKET, GYM_AND_SPA, NIGHT_CLUB, PUBLIC_UTIL};
	
	public static Fragment buildFragment(SearchType searchType)
	{
		return ClassfiedSearchResultFragment.newInstance(searchType);
	}

	public static class SearchType
	{
		Integer index;
		String name;
		String key;
		GooglePlaceFilter placeFilter;

		public SearchType(Integer index, String name, String key)
		{
			this.index = index;
			this.name = name;
			this.key = key;
		}

		public SearchType(Integer index, String name, GooglePlaceFilter placeFilter)
		{
			this.index = index;
			this.name = name;
			this.placeFilter = placeFilter;
		}

		public Integer getIndex()
		{
			return index;
		}

		public String getName()
		{
			return name;
		}

		public String getKey()
		{
			return key;
		}

		public GooglePlaceFilter getPlaceFilter()
		{
			return placeFilter;
		}
	}
}
