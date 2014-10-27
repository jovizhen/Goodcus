package com.jovi.bbs.goodcus.util;

import com.jovi.bbs.goodcus.App;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.provider.Settings;

public class LocationChangeHandler implements LocationListener
{

	private Location currentLocation; 
	private Context context;
	
	public LocationChangeHandler()
	{
	}
	
	public LocationChangeHandler(Context context)
	{
		this.context = context;
	}
	
	public LocationChangeHandler(Location currentLocation)
	{
		this.currentLocation = currentLocation;
	}
	
	@Override
	public void onLocationChanged(Location paramLocation)
	{
		currentLocation = paramLocation;
		Intent intent = new Intent(App.GEO_LOCATION_UPDATE_ACTION);
		Bundle data = new Bundle();
		data.putDouble("Latitude", currentLocation.getLatitude());
		data.putDouble("Longtitude", currentLocation.getLongitude());
		intent.putExtras(data);
		context.sendBroadcast(intent);
	}

	@Override
	public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle)
	{
	}

	@Override
	public void onProviderEnabled(String paramString)
	{
	}

	@Override
	public void onProviderDisabled(String paramString)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Location Services Not Active");
		builder.setMessage("Please enable Location Services and GPS");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialogInterface, int i)
			{
				// Show location settings when the user acknowledges the alert
				// dialog
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				context.startActivity(intent);
			}
		});
		Dialog alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.show();
	}

	public Location getCurrentLocation()
	{
		return currentLocation;
	}

	public void setCurrentLocation(Location currentLocation)
	{
		this.currentLocation = currentLocation;
	}
}
