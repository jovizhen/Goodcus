package com.jovi.bbs.goodcus.net.googlePlacesApi;

import static com.jovi.bbs.goodcus.net.googlePlacesApi.HttpUtil.get;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.jovi.bbs.goodcus.net.googlePlacesApi.exception.GooglePlacesException;


public class CustomGooglePlaces extends GooglePlaces
{
	public static final int STATUS_CODE_OVER_QUERY_LIMIT  = -1;
	public static final int STATUS_CODE_REQUEST_DENIED    = -2;
	public static final int STATUS_CODE_INVALIDE_REQUEST  = -3;
	public static final int STATUS_CODE_UNKNOWN_ERROR     = -4;
	public static final int STATUS_CODE_OK                = 1;
	public static final int STATUS_CODE_ZERO_RESULTS      = 0;
	public  static final String apiKey = "AIzaSyAdxpP8KZZnmJgDC_gKOFIpI5od_AZmDfw";
	
	private Integer statusCode;
	private String pagetoken;

	public CustomGooglePlaces()
	{
		this(apiKey);
	}
	
	public CustomGooglePlaces(String apiKey)
	{
		super(apiKey);
	}

	public CustomGooglePlaces(String apiKey, HttpClient client)
	{
		super(apiKey, client);
	}

	public Integer getStatusCode()
	{
		return statusCode;
	}

	public void setStatusCode(Integer statusCode)
	{
		this.statusCode = statusCode;
	}

	/*
	 * Nearby Search lets you search for places within a specified area. You can refine your search request by supplying keywords
	 * or specifying the type of place you are searching for.
	 */
	
	public List<Place> getNearbyPlaces(double lat, double lng, double radius, GooglePlaceFilter filter)
	{
		List<Param> params = filter.getParamList();
		Param[] paramArray = params.toArray(new Param[params.size()]);
		setSensorEnabled(true);
		return getNearbyPlaces(lat, lng, radius, MAXIMUM_RESULTS, true, paramArray);
	}
	
	private List<Place> getNearbyPlaces(double lat, double lng, double radius, int limit, boolean pagewise, Param... extraParams) 
	{
		try
		{
			statusCode = null;
			if (pagewise)
			{
				String uri = buildUrl(METHOD_NEARBY_SEARCH, String.format("key=%s&location=%f,%f&radius=%f&sensor=%b", apiKey, lat, lng, radius, 
						sensor), extraParams);
				return getPagewisePlaces(uri, METHOD_NEARBY_SEARCH, limit);
			} 
			else
			{
				return getNearbyPlaces(lat, lng, radius, MAXIMUM_RESULTS, extraParams);
			}
		}
		catch (Exception e)
		{
			throw new GooglePlacesException(e);
		}
	}

	public List<Place> getPlacesByQuery(String query, GooglePlaceFilter filter)
	{
		List<Param> params = filter.getParamList();
		Param[] paramArray = params.toArray(new Param[params.size()]);
		setSensorEnabled(true);
		return getPlacesByQuery(query, MAXIMUM_RESULTS, true, paramArray);
	}
	
	public List<Place> getPlacesByQuery(String query, int limit, Boolean pagewise, Param... extraParams)
	{
		try
		{
			if(pagewise)
			{
				String uri = buildUrl(METHOD_TEXT_SEARCH, String.format("query=%s&key=%s&sensor=%b", query, apiKey, sensor), extraParams);
				return getPagewisePlaces(uri, METHOD_TEXT_SEARCH, limit);
			}
			else 
			{
				return getPlacesByQuery(query, extraParams);
			}
		}
		catch (Exception e)
		{
			throw new GooglePlacesException(e);
		}
	}
	
	
	public List<Place> getPagewisePlaces(String uri, String method, int limit) throws IOException, JSONException
	{
		limit = Math.min(limit, MAXIMUM_RESULTS); // max of 60 results possible
		List<Place> places = new ArrayList<Place>();
		String raw = get(client, uri);
		JSONObject json = new JSONObject(raw);
		String status = json.getString(STRING_STATUS);
		statusCode = parseStatus(status);
		pagetoken = parse(this, places, raw, limit);
//		sleep(3000);
		return places;
	}
	
	/*
	 override change query by placeId instead of by reference because reference is deprecated
	 this method for getting place details
	*/
	
	public Place getPlace(String placeId, Param... extraParams)
	{
		try
		{
			String uri = buildUrl(METHOD_DETAILS, String.format("key=%s&placeid=%s&sensor=%b", apiKey, placeId, sensor), extraParams);
			return Place.parseDetails(this, get(client, uri));
		}
		catch (Exception e)
		{
			throw new GooglePlacesException(e);
		}
	}
	
	public String buildPhotoDownloadUrl(Photo photo, int maxWidth, int maxHeight)
	{
		return String.format("%sphoto?photoreference=%s&sensor=%b&key=%s&maxwidth=%d&maxheight=%d", API_URL, 
				photo.getReference(), sensor, apiKey, maxWidth, maxHeight);
	}
	
	public String buildPhotoUrl(Photo photo)
	{
		return buildPhotoDownloadUrl(photo, 500, 500);
	}
	
	public Integer parseStatus(String status)
	{
		if(status.equals(STATUS_OVER_QUERY_LIMIT))
		{
			statusCode = STATUS_CODE_OVER_QUERY_LIMIT;
		}
		else if(status.equals(STATUS_REQUEST_DENIED))
		{
			statusCode = STATUS_CODE_REQUEST_DENIED;
		}
		else if (status.equals(STATUS_INVALID_REQUEST))
		{
			statusCode = STATUS_CODE_INVALIDE_REQUEST;
		}
		else if(status.equals(STATUS_UNKNOWN_ERROR))
		{
			statusCode = STATUS_CODE_UNKNOWN_ERROR;
		}
		else if(status.equals(STATUS_OK))
		{
			statusCode = STATUS_CODE_OK;
		}
		else if(status.equals(STATUS_ZERO_RESULTS))
		{
			statusCode = STATUS_CODE_ZERO_RESULTS;
		}
		return statusCode;
	}
	
	public String getPageToken()
	{
		return pagetoken;
	}
	
}
