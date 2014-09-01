package com.jovi.bbs.goodcus.net;

import java.util.ArrayList;
import java.util.Map;

import org.w3c.dom.Document;

import com.google.android.gms.maps.model.LatLng;
import com.jovi.bbs.goodcus.fragment.MapDirectionFragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

public class GetDirectionsAsyncTask extends AsyncTask<Map<String, String>, Object, ArrayList<LatLng>>
{
	public static final String USER_CURRENT_LAT = "user_current_lat";
    public static final String USER_CURRENT_LONG = "user_current_long";
    public static final String DESTINATION_LAT = "destination_lat";
    public static final String DESTINATION_LONG = "destination_long";
    public static final String DIRECTIONS_MODE = "directions_mode";
    private Exception exception;
    private ProgressDialog progressDialog;
    private MapDirectionFragment mapFragment;
	
    public GetDirectionsAsyncTask(MapDirectionFragment mapFragment)
    {
    	super();
    	this.mapFragment = mapFragment;
    }
    
    
    public void onPreExecute()
    {
        progressDialog = new ProgressDialog(mapFragment.getActivity());
        progressDialog.setMessage("Calculating directions");
        progressDialog.show();
    }
    
    public void onPostExecute(ArrayList<LatLng> result)
    {
        progressDialog.dismiss();
        if (exception == null)
        {
            mapFragment.handleGetDirectionsResult(result);
        }
        else
        {
            processException();
        }
    }
    
	@Override
	protected ArrayList<LatLng> doInBackground(Map<String, String>... params)
	{
		Map<String, String> paramMap = params[0];
        try
        {
            LatLng fromPosition = new LatLng(Double.valueOf(paramMap.get(USER_CURRENT_LAT)) , Double.valueOf(paramMap.get(USER_CURRENT_LONG)));
            LatLng toPosition = new LatLng(Double.valueOf(paramMap.get(DESTINATION_LAT)) , Double.valueOf(paramMap.get(DESTINATION_LONG)));
            GMapV2Direction md = new GMapV2Direction();
            Document doc = md.getDocument(fromPosition, toPosition, paramMap.get(DIRECTIONS_MODE));
            ArrayList directionPoints = md.getDirection(doc);
            return directionPoints;
        }
        catch (Exception e)
        {
            exception = e;
            return null;
        }
	}
	
	 @SuppressLint("ShowToast") private void processException()
	    {
	        Toast.makeText(mapFragment.getActivity(), "error when retriving data", 3000).show();
	    }
}
