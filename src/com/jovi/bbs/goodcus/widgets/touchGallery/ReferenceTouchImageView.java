package com.jovi.bbs.goodcus.widgets.touchGallery;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.jovi.bbs.goodcus.R;
import com.jovi.bbs.goodcus.net.googlePlacesApi.CustomGooglePlaces;
import com.jovi.bbs.goodcus.widgets.touchGallery.InputStreamWrapper.InputStreamProgressListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

public class ReferenceTouchImageView extends RelativeLayout
{
	protected ProgressBar mProgressBar;
	protected TouchImageView mImageView;
	protected Context mContext;

	public ReferenceTouchImageView(Context context)
	{
		super(context);
		mContext = context;
		init();
	}
	

	public ReferenceTouchImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
		init();
	}
	
	public TouchImageView getImageView()
	{
		return mImageView;
	}
	
	protected void init()
	{
		mImageView = new TouchImageView(mContext);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mImageView.setLayoutParams(params);
		this.addView(mImageView);
		mImageView.setVisibility(GONE);
		mProgressBar = new ProgressBar(mContext, null, android.R.attr.progressBarStyleHorizontal);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.setMargins(30, 0, 30, 0);
		mProgressBar.setLayoutParams(params);
		mProgressBar.setIndeterminate(false);
		mProgressBar.setMax(100);
		this.addView(mProgressBar);
	}
	
	public void setReference(String imageReference)
	{
		new ImageLoadTask().execute(imageReference);
	}
	
	public void setScaleType(ScaleType scaleType)
	{
		mImageView.setScaleType(scaleType);
	}
	
	@SuppressLint("DefaultLocale") public class ImageLoadTask extends AsyncTask<String, Integer, Bitmap>
	{
		@Override
		protected Bitmap doInBackground(String... strings)
		{
			HttpClient client = new DefaultHttpClient();
			String url = String.format("%sphoto?photoreference=%s&sensor=%b&key=%s&maxwidth=%d&maxheight=%d", CustomGooglePlaces.API_URL, 
					strings[0], true, CustomGooglePlaces.apiKey, 1000, 1000);
			
			Bitmap bm = null;
			try
			{
				
				HttpGet get = new HttpGet(url);
				HttpResponse response = client.execute(get);
				InputStream is = response.getEntity().getContent();
				long totalLen = response.getEntity().getContentLength();
				InputStreamWrapper bis = new InputStreamWrapper(is, 8192, totalLen);
				bis.setProgressListener(new InputStreamProgressListener()
				{
					@Override
					public void onProgress(float progressValue, long bytesLoaded, long bytesTotal)
					{
						publishProgress((int) (progressValue * 100));
					}
				});
				bm = BitmapFactory.decodeStream(bis);
				bis.close();
				is.close();
				response.getEntity().consumeContent();
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return bm;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap)
		{
			if (bitmap == null)
			{
				mImageView.setScaleType(ScaleType.CENTER_CROP);
				bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_user_head_img);
				mImageView.setImageBitmap(bitmap);
			} 
			else
			{
				mImageView.setScaleType(ScaleType.MATRIX);
				mImageView.setImageBitmap(bitmap);
			}
			mImageView.setVisibility(VISIBLE);
			mProgressBar.setVisibility(GONE);
		}

		@Override
		protected void onProgressUpdate(Integer... values)
		{
			mProgressBar.setProgress(values[0]);
		}
	}
}
