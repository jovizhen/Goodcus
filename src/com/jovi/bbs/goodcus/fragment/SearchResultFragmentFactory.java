package com.jovi.bbs.goodcus.fragment;


import android.support.v4.app.Fragment;

public class SearchResultFragmentFactory
{
	public static final SearchType TAIWAN_FOOD    = new SearchType(0, "台湾", "taiwanese");
	public static final SearchType SICHUAN_FOOD   = new SearchType(2, "川菜", "Szechuan");
	public static final SearchType CANTONESE_FOOD = new SearchType(3, "广东菜", "cantonese");
	public static final SearchType KOREAN_FOOD    = new SearchType(4, "韩餐", "korean");
	public static final SearchType JAPANESE_FOOD  = new SearchType(5, "日料", "japanese");
	public static final SearchType SPANISH_FOOD   = new SearchType(6, "西班牙", "spanish"); 
	
	public static final SearchType[] navigation_menu =
	{ TAIWAN_FOOD, SICHUAN_FOOD, CANTONESE_FOOD, KOREAN_FOOD, JAPANESE_FOOD, SPANISH_FOOD };
	
	public static Fragment buildFragment(String searchTerm)
	{
		return ClassfiedSearchResultFragment.newInstance(searchTerm);
	}
	
	public static class SearchType 
	{
		Integer index;
		String name;
		String key;
		
		public SearchType(Integer index, String name, String key)
		{
			this.index = index;
			this.name = name;
			this.key = key;
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
	}

}
