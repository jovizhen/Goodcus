package com.jovi.bbs.goodcus.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import com.jovi.bbs.goodcus.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

public class GoogleImageLoader
{

	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	protected Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	protected ExecutorService executorService;
	protected Handler handler = new Handler();// handler to display images in UI
												// thread
	public static final HttpClient CLIENT = new DefaultHttpClient();

	public GoogleImageLoader(Context context)
	{
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(10);
	}

	final int stub_id = R.drawable.default_user_head_img;

	public void DisplayImage(String url, String placeId, ImageView imageView)
	{
		Bitmap bitmap = memoryCache.get(placeId);
		if (bitmap != null)
			imageView.setImageBitmap(bitmap);
		else
		{
			
			queuePhoto(new PhotoToLoad(url, placeId, imageView));
			imageView.setImageResource(stub_id);
		}
	}

	private void queuePhoto(PhotoToLoad photoToLoad)
	{
		executorService.submit(new PhotosLoader(photoToLoad));
	}

	protected synchronized Bitmap getBitmap(String url, String placeId)
	{
		File f = fileCache.getFile(placeId);
		Bitmap b = null;
		if (f != null)
		{
			if (f.exists())
			{
				b = decodeFile(f);
			}
		}
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
			cacheBitmapFromPlaceID(placeId, bitmap);
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

	protected Bitmap decodeFile(File f)
	{
		try
		{
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream stream1 = new FileInputStream(f);
			BitmapFactory.decodeStream(stream1, null, o);
			stream1.close();

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 70;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true)
			{
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			FileInputStream stream2 = new FileInputStream(f);
			Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
			stream2.close();
			return bitmap;
		}
		catch (FileNotFoundException e)
		{
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private class PhotoToLoad
	{
		public String url;
		public String placeId;
		public ImageView imageView;

		public PhotoToLoad(String u, String placeId, ImageView i)
		{
			this.url = u;
			this.placeId = placeId;
			this.imageView = i;
		}
	}

	class PhotosLoader implements Runnable
	{
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad)
		{
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run()
		{
			try
			{
				Bitmap bmp = getBitmap(photoToLoad.url, photoToLoad.placeId);
				memoryCache.put(photoToLoad.placeId, bmp);
				BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
				handler.post(bd);
			}
			catch (Throwable th)
			{
				th.printStackTrace();
			}
		}
	}

	public void cacheBitmapFromPlaceID(String placeId, Bitmap bitmap)
	{
		if (placeId == null || bitmap == null)
			return;
		File tFile = fileCache.getFile(placeId);
		try
		{
			if (tFile != null)
			{
				if (!tFile.exists())
				{
					tFile.createNewFile();
				}
				FileOutputStream fos = new FileOutputStream(tFile);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	class BitmapDisplayer implements Runnable
	{
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p)
		{
			bitmap = b;
			photoToLoad = p;
		}

		public void run()
		{
			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);
			else
				photoToLoad.imageView.setImageResource(stub_id);
		}
	}

	public void clearCache()
	{
		memoryCache.clear();
		fileCache.clear();
	}
}
