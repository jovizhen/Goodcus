package com.jovi.bbs.goodcus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SplashActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		final View view = View.inflate(this, R.layout.splash, null);
		setContentView(view);
		new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				gotoMainPage();
			}
		}.start();
	}

	private void gotoMainPage()
	{
		Intent intent = (new Intent(this, MainActivity.class));
		startActivity(intent);
		finish();
	}
}



