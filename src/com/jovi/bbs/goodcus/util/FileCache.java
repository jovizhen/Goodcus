package com.jovi.bbs.goodcus.util;

import java.io.File;

import android.content.Context;

public class FileCache
{

	private File cacheDir;

	public FileCache(Context context)
	{
		// Find the dir to save cached images
		cacheDir = new File(context.getFilesDir(), "goodcus/img");
		if (!cacheDir.exists())
			cacheDir.mkdirs();
	}

	public File getFile(String url)
	{
		String filename = String.valueOf(url+".png");
		File f = new File(cacheDir, filename);
		return f;
	}

	public void clear()
	{
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		for (File f : files)
			f.delete();
	}
}
