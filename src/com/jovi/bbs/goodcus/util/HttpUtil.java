package com.jovi.bbs.goodcus.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class HttpUtil
{
	RequestQueue requestQueue;
	
	public void setContext(Context context)
	{
		requestQueue = Volley.newRequestQueue(context);
	}
	
	public RequestQueue getRequestQueue()
	{
		return requestQueue;
	}
	
	private static HttpUtil instance;
	public static HttpUtil getInstance()
	{
		if(instance == null)
		{
			instance = new HttpUtil();
		}
		return instance;
	}

}
