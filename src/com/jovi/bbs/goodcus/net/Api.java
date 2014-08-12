package com.jovi.bbs.goodcus.net;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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

public class Api  implements ConnectionCallbacks, OnConnectionFailedListener, DisconnectCallbacks
{
	private static final int INVALID_REQUEST_CODE = -1;
	private PlusClient googlePlusClient;
	Activity mActivity;
	ConnectionResult mLastConnectionResult;
	int mRequestCode;
	private boolean isConnecting = false;
	private ProgressDialog pd = null;
	
	public void setActivity(Activity activity)
	{
		mActivity = activity;
		Builder clientBuilder = new PlusClient.Builder(mActivity, this, this);
		clientBuilder.setScopes(new String[]{Scopes.PLUS_PROFILE});
		googlePlusClient = clientBuilder.build();
	}
	
	public PlusClient getGooglePlusClient()
	{
		return googlePlusClient;
	}

	@Override
	public void onConnectionFailed(ConnectionResult result)
	{
		isConnecting = true;
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
        pd.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setMessage("登陆失败， 再试一次？");
		builder.setPositiveButton("确定", new OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				connectToGooglePlus();
			}
		}).setNegativeButton("取消", null);
		builder.create().show();
	}

	private void resolveLastResult()
	{
		if (GooglePlayServicesUtil.isUserRecoverableError(mLastConnectionResult.getErrorCode()))
		{
			// Show a dialog to install or enable Google Play services.
			return;
		}

		if (mLastConnectionResult.hasResolution())
		{
			startResolution();
		}

		Intent intent = new Intent(App.LOGIN_STATE_CHANGE_ACTION);
		Bundle data = new Bundle();
		data.putBoolean("isConnecting", isConnecting);
		intent.putExtras(data);
		mActivity.sendBroadcast(intent);
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
		isConnecting = false;
		Intent intent = new Intent(App.LOGIN_STATE_CHANGE_ACTION);
		Bundle data = new Bundle();
		data.putBoolean("isConnecting", isConnecting);
		intent.putExtras(data);
		mActivity.sendBroadcast(intent);
		if(pd != null)
		{
			pd.dismiss();
		}
	}

	public void connectToGooglePlus()
	{
		pd = ProgressDialog.show(mActivity, null, "登录中，请稍后……", true, true);
		googlePlusClient.connect();
	}
	
	public void disconnectFromGooglePlus()
	{
		pd = ProgressDialog.show(mActivity, null, "登出中，请稍后……", true, true);
		googlePlusClient.disconnect();
		onServiceDisconnected();
	}
	
	@Override
	public void onDisconnected()
	{
		
	}

	@Override
	public void onServiceDisconnected()
	{
		isConnecting = false;
		Intent intent = new Intent(App.LOGIN_STATE_CHANGE_ACTION);
		Bundle data = new Bundle();
		data.putBoolean("isConnecting", isConnecting);
		intent.putExtras(data);
		mActivity.sendBroadcast(intent);
		pd.dismiss();
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
}
