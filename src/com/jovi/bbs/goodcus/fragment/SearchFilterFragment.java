package com.jovi.bbs.goodcus.fragment;

import com.jovi.bbs.goodcus.R;
import com.jovi.bbs.goodcus.net.googlePlacesApi.GooglePlaceFilter;
import com.jovi.bbs.goodcus.widgets.tableView.DropdownView;
import com.jovi.bbs.goodcus.widgets.tableView.DropdownView.ClickListener;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;


public class SearchFilterFragment extends Fragment
{
	private DropdownView priceDropdown;
	private DropdownView distanceDropdown;
	private SharedPreferences sharedPreferences;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		sharedPreferences = getActivity().getSharedPreferences("googleFilter", Context.MODE_PRIVATE);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view =  inflater.inflate(R.layout.fragment_search_filter, container, false);
		setupDropdownMenus(view);
		setupToggleMenu(view);
		return view;
	}
	
	private void setupDropdownMenus(View parentView)
	{
		priceDropdown = (DropdownView) parentView.findViewById(R.id.priceDropdown);	
		priceDropdown.addDropdownItem("$");
		priceDropdown.addDropdownItem("$$");
		priceDropdown.addDropdownItem("$$$");
		priceDropdown.addDropdownItem("$$$$");
		priceDropdown.commit();
		int maxPrice = sharedPreferences.getInt("max_price", GooglePlaceFilter.DEFAULT_MAX_PRICE);
		if(maxPrice == GooglePlaceFilter.DEFAULT_MAX_PRICE)
		{
			priceDropdown.selectItemByTitle("Auto");
		}
		else if(maxPrice==1)
		{
			priceDropdown.selectItemByTitle("$");
		}
		else if(maxPrice==2)
		{
			priceDropdown.selectItemByTitle("$$");
		}
		else if(maxPrice==3)
		{
			priceDropdown.selectItemByTitle("$$$");
		}
		else if(maxPrice==4)
		{
			priceDropdown.selectItemByTitle("$$$$");
		}
		priceDropdown.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(int index)
			{
				Editor editor = sharedPreferences.edit();
				String key = "max_price";
				if(priceDropdown.getSelectedString().equals("Auto"))
				{
					editor.putInt(key, GooglePlaceFilter.DEFAULT_MAX_PRICE);
				}
				else if(priceDropdown.getSelectedString().equals("$"))
				{
					editor.putInt(key, 1);
				}
				else if(priceDropdown.getSelectedString().equals("$$"))
				{
					editor.putInt(key, 2);
				}
				else if(priceDropdown.getSelectedString().equals("$$$"))
				{
					editor.putInt(key, 3);
				}
				else if(priceDropdown.getSelectedString().equals("$$$$"))
				{
					editor.putInt(key, 4);
				}
				editor.commit();
			}
		});

		distanceDropdown = (DropdownView) parentView.findViewById(R.id.distanceDropdown);
		distanceDropdown.addDropdownItem("5 miles");
		distanceDropdown.addDropdownItem("15 miles");
		distanceDropdown.addDropdownItem("30 miles");
		distanceDropdown.commit();
		long radius = sharedPreferences.getLong("radius", GooglePlaceFilter.DEFAULT_SEARCH_RADIUS);
		if (radius == GooglePlaceFilter.DEFAULT_SEARCH_RADIUS)
		{
			distanceDropdown.selectItemByTitle("Auto");
		}
		else if(radius==5*1.6*1000)
		{
			distanceDropdown.selectItemByTitle("5 miles");
		}
		else if(radius==15*1.6*1000)
		{
			distanceDropdown.selectItemByTitle("15 miles");
		}
		else if(radius==30*1.6*1000)
		{
			distanceDropdown.selectItemByTitle("30 miles");
		}
		
		distanceDropdown.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(int index)
			{
				Editor editor = sharedPreferences.edit();
				String key = "radius";
				if(distanceDropdown.getSelectedString().equals("Auto"))
				{
					editor.putLong(key, GooglePlaceFilter.DEFAULT_SEARCH_RADIUS);
				}
				if(distanceDropdown.getSelectedString().equals("5 miles"))
				{
					editor.putLong(key, 8000);
				}
				else if(distanceDropdown.getSelectedString().equals("15 miles"))
				{
					editor.putLong(key, 24000);
				}
				else if(distanceDropdown.getSelectedString().equals("30 miles"))
				{
					editor.putLong(key, 48000);
				}
				editor.commit();
			}
		});
	}
	
	private void setupToggleMenu(View parentView)
	{
		ToggleButton tgBtn = (ToggleButton) parentView.findViewById(R.id.toggleButton);
		tgBtn.setChecked(sharedPreferences.getBoolean("open_now", GooglePlaceFilter.DEFAULT_OPEN_NOW));
		tgBtn.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
			{
				Editor editor = sharedPreferences.edit();
				String key = "open_now";
				editor.putBoolean(key, paramBoolean);
				editor.commit();
			}
		});
	}
	
}
