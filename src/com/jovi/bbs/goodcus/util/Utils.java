package com.jovi.bbs.goodcus.util;

import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class Utils
{
	public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
	
	public static Location getCurrentLocation(final Context context, LocationListener locationListener)
	{
		LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String bestProvider = locationManager.getBestProvider(criteria, false);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5 * 60 * 1000, 20, locationListener);
		return locationManager.getLastKnownLocation(bestProvider);
	}
}
