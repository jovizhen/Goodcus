package com.jovi.bbs.goodcus.fragment;

import com.jovi.bbs.goodcus.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PhotoFragment extends Fragment
{

	public PhotoFragment()
	{
		
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_photo, container, false);
	}
	
}
