package com.jovi.bbs.goodcus.net;

import android.R.integer;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.Builder;
import com.jovi.bbs.goodcus.App;
import com.jovi.bbs.goodcus.SettingPage;

public class Api  implements ConnectionCallbacks, OnConnectionFailedListener
{
	private static final int INVALID_REQUEST_CODE = -1;
	private PlusClient googlePlusClient;
	Activity mActivity;
	ConnectionResult mLastConnectionResult;
	int mRequestCode;
	
	
	public void setActivity(Activity activity)
	{
		mActivity = activity;
		Builder clientBuilder = new PlusClient.Builder(mActivity, this, this);
		clientBuilder.setScopes(new String[]{Scopes.PLUS_PROFILE});
		googlePlusClient = clientBuilder.build();
	}
	
	private static Api instance;

	public static Api getInstance()
	{
		if (instance == null)
		{
			instance = new Api();
		}
		return instance;
	}
	
	public PlusClient getGooglePlusClient()
	{
		return googlePlusClient;
	}


	@Override
	public void onConnectionFailed(ConnectionResult result)
	{
		mLastConnectionResult = result;
		mRequestCode = result.getErrorCode();

        // On a failed connection try again.
        if (mRequestCode != INVALID_REQUEST_CODE) 
        {
            resolveLastResult();
        } 
        else 
        {
        	Toast.makeText(mActivity, "Invalid request",
					Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(mActivity, "please try again",
				Toast.LENGTH_SHORT).show();
        
        mActivity.sendBroadcast(new Intent(App.LOGIN_STATE_CHANGE_ACTION));
		
	}

	private void resolveLastResult()
	{
		 if (GooglePlayServicesUtil.isUserRecoverableError(mLastConnectionResult.getErrorCode())) {
	        	// Show a dialog to install or enable Google Play services.
	            return;
	        }

	        if (mLastConnectionResult.hasResolution()) 
	        {
	            startResolution();
	        }
	}
	
	private void startResolution() {
        try 
        {
            mLastConnectionResult.startResolutionForResult(mActivity, mRequestCode);
        } 
        catch (SendIntentException e)
        {
            // The intent we had is not valid right now, perhaps the remote process died.
            // Try to reconnect to get a new resolution intent.
            mLastConnectionResult = null;
            googlePlusClient.connect();
        }
    }

	@Override
	public void onConnected(Bundle connectionHint)
	{
		mActivity.sendBroadcast(new Intent(App.LOGIN_STATE_CHANGE_ACTION));
		
	}

	@Override
	public void onDisconnected()
	{
		
	}
	
}
