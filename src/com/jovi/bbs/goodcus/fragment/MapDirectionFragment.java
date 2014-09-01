package com.jovi.bbs.goodcus.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.jovi.bbs.goodcus.R;
import com.jovi.bbs.goodcus.model.SearchResult;
import com.jovi.bbs.goodcus.net.GMapV2Direction;
import com.jovi.bbs.goodcus.net.GetDirectionsAsyncTask;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MapDirectionFragment extends Fragment
{
	private MapFragment mMap;
	private SearchResult result;
	private Location currentLocation;
	private Location destination;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		
		View view = inflater.inflate(R.layout.fragment_map_direction, container, false);
		
		Gson gson = new Gson();
		String jsonResult =getArguments().getString("searchResult");  
		result =  gson.fromJson(jsonResult, SearchResult.class);
		currentLocation = getCurrentLocation();
		destination = parseLocation(result);
		setupMapIfNeeded();
		findDirections(currentLocation.getLatitude(), currentLocation.getLongitude(),
				destination.getLatitude(), destination.getLongitude(), GMapV2Direction.MODE_DRIVING);
		return view;
	}
	
	private void setupMapIfNeeded()
	{
		if (mMap == null)
		{
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.direction_map_frag));

		}
		
		Marker currMarker = mMap.getMap().addMarker(
				new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).
				icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

		Marker destMarker = mMap.getMap().addMarker(
				new MarkerOptions().position(new LatLng(destination.getLatitude(), destination.getLongitude())).
				icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
		
		LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
		boundsBuilder.include(currMarker.getPosition()).include(destMarker.getPosition());
		final LatLngBounds bounds = boundsBuilder.build();
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		mMap.getMap().moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, size.x, size.y,  80));
	}
	

	
	public Fragment getMap()
	{
		return mMap;
	}
	
	public Location getCurrentLocation()
	{
		getActivity();
		LocationManager mlocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String bestProvider = mlocManager.getBestProvider(criteria, false);
		return mlocManager.getLastKnownLocation(bestProvider);
	}
	
	public Location parseLocation(SearchResult result)
	{
		Location location = new Location("");
		try
		{
			if (result.getLocation().getCoordinate() != null)
			{
				double lat = result.getLocation().getCoordinate().getLatitude();
				double lont = result.getLocation().getCoordinate().getLongitude();

				location.setLatitude(lat);
				location.setLongitude(lont);
				return location;
			}
			else
			{
				Geocoder geocoder = new Geocoder(getActivity());
				List<Address> resultAddresses = geocoder.getFromLocationName(result.getLocationLabel(), 1);
				if (resultAddresses.size() != 0)
				{
					double lat = resultAddresses.get(0).getLatitude();
					double lont = resultAddresses.get(0).getLongitude();
					location.setLatitude(lat);
					location.setLongitude(lont);
					return location;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return location;
	}
	
	@SuppressWarnings("unchecked")
	public void findDirections(double fromPositionDoubleLat, double fromPositionDoubleLong, 
			double toPositionDoubleLat, double toPositionDoubleLong, String mode)
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionDoubleLat));
		map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionDoubleLong));
		map.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(toPositionDoubleLat));
		map.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(toPositionDoubleLong));
		map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);

		GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
		asyncTask.execute(map);
	}

	public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints)
	{
	    PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.BLUE);
	    for(int i = 0 ; i < directionPoints.size() ; i++)
	    {
	        rectLine.add(directionPoints.get(i));
	    }
	    mMap.getMap().addPolyline(rectLine);
	}
}
