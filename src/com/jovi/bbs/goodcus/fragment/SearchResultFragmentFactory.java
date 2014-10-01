package com.jovi.bbs.goodcus.fragment;


import com.jovi.bbs.goodcus.net.googlePlacesApi.GooglePlaceFilter;
import com.jovi.bbs.goodcus.util.GoogleImageLoader;

import android.support.v4.app.Fragment;

public class SearchResultFragmentFactory
{
	public static final SearchType RESTAURANT_CN                = new SearchType(0, "中餐馆", new GooglePlaceFilter().setTypes("restaurant").setKeyword("china").setLanguage("zh-TW"));
	public static final SearchType RESTAURANT_JP                = new SearchType(0, "日料", new GooglePlaceFilter().setTypes("restaurant").setKeyword("japan").setLanguage("zh-TW"));
	public static final SearchType RESTAURANT_KR                = new SearchType(0, "韩料", new GooglePlaceFilter().setTypes("restaurant").setKeyword("korea").setLanguage("zh-TW"));
	public static final SearchType BAKERY_AND_CAFE              = new SearchType(1, "咖啡", new GooglePlaceFilter().setKeyword("cafe+bakery").setLanguage("zh-TW"));
	public static final SearchType GROCERY_STORE_MARKET         = new SearchType(2, "超市", new GooglePlaceFilter().setKeyword("supermarket").setLanguage("zh-TW"));
	public static final SearchType GYM_AND_SPA                  = new SearchType(6, "健身/SPA",new GooglePlaceFilter().setKeyword("gym+spa").setLanguage("zh-TW")); 
	public static final SearchType NIGHT_CLUB                   = new SearchType(7, "夜店", new GooglePlaceFilter().setTypes("bar").setKeyword("night").setLanguage("zh-TW")); 
	public static final SearchType PUBLIC_UTIL                  = new SearchType(8, "市府/公用", new GooglePlaceFilter().setLanguage("zh-TW"));
	
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
		
		public SearchType(Integer index, String name,  String key)
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
