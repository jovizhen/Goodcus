package com.jovi.bbs.goodcus;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

public class App extends Application
{
	public static final String LOGIN_STATE_CHANGE_ACTION = "com.jovi.bbs.goodcus.LOGIN_STATE_CHANGE_ACTION";
	private static String LOG_FILE_NAME = "goodcus.log";
	private int m_versionCode = 0;
	
	public void onCreate()
	{
		super.onCreate();

		PackageInfo pinfo = null;
		try
		{
			pinfo = getPackageManager().getPackageInfo(App.this.getPackageName(), PackageManager.GET_CONFIGURATIONS);
			m_versionCode = pinfo.versionCode;
		}
		catch (NameNotFoundException e)
		{
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

		enableRecordLog();
	}

	private void enableRecordLog()
	{
		String path = LOG_FILE_NAME;
		FileOutputStream fos = null;
		try
		{
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			{
				path = Environment.getExternalStorageDirectory() + "/goodcus/" + path;
				fos = new FileOutputStream(path, true);
			} else
			{
				fos = openFileOutput(path, MODE_APPEND | MODE_PRIVATE);
			}
			System.setErr(new PrintStream(fos));
			fos.close();
		}
		catch (IOException e)
		{
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

	}

	
	public int getVersionCode()
	{
		return m_versionCode;
	}
}
