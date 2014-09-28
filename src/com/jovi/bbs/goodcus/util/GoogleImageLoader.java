package com.jovi.bbs.goodcus.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import static com.jovi.bbs.goodcus.net.googlePlacesApi.HttpUtil.*;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GoogleImageLoader extends ImageLoader
{
	public static final HttpClient CLIENT = new DefaultHttpClient();
	
	public GoogleImageLoader(Context context)
	{
		super(context);
	}
	
	protected  synchronized Bitmap getBitmap(String url)
	{
		File f = fileCache.getFile(url);
		
		// from SD cache
		Bitmap b = decodeFile(f);
		if (b != null)
			return b;

		// from web
		try
		{
			Bitmap bitmap = null;
			HttpGet get = new HttpGet(url);
			HttpResponse response = CLIENT.execute(get);
			InputStream is = response.getEntity().getContent();
			bitmap = BitmapFactory.decodeStream(is);
			response.getEntity().consumeContent();
			return bitmap;
		}
		catch (Throwable ex)
		{
			ex.printStackTrace();
			if (ex instanceof OutOfMemoryError)
				memoryCache.clear();
			return null;
		}
	}

}
