package com.jovi.bbs.goodcus.util;

import java.util.ArrayList;

import com.jovi.bbs.goodcus.net.googlePlacesApi.Place;

public class CollecttionHelper
{

	public static Place findPlaceById(ArrayList<Place> placeList, String placeId)
	{
		if (placeList.size() == 0 || placeId == null)
			return null;
		for (Place place : placeList)
		{
			if (placeId.equals(place.getPlaceId()))
				return place;
		}
		return null;
	}
	
	public static ArrayList<String> getPlaceIdList(ArrayList<Place> originList)
	{
		if (originList == null || originList.size() == 0)
			return null;
		ArrayList<String> placeIdList = new ArrayList<String>();
		for(Place aPlace:originList)
		{
			placeIdList.add(aPlace.getPlaceId());
		}
		return placeIdList;
	}
}
