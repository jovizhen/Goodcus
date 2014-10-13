package com.jovi.bbs.goodcus.fragment;

import com.jovi.bbs.goodcus.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SearchFilterFragment extends Fragment
{
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view =  inflater.inflate(R.layout.search_filter_fragment, container, false);
		return view;
	}
	
	public void saveFilter()
	{
		
	}

}
