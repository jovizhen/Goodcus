package com.jovi.bbs.goodcus.fragment;

import com.jovi.bbs.goodcus.R;
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
	private DropdownView sortDropdown;
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
		int maxPrice = sharedPreferences.getInt("max_price", 0);
		if(maxPrice == 0)
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
					editor.putInt(key, 0);
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
		distanceDropdown.addDropdownItem("10 miles");
		distanceDropdown.addDropdownItem("20 miles");
		distanceDropdown.commit();
		long radius = sharedPreferences.getLong("radius", 0);
		if(radius==0)
		{
			distanceDropdown.selectItemByTitle("Auto");
		}
		else if(radius==8000)
		{
			distanceDropdown.selectItemByTitle("5 miles");
		}
		else if(radius==16000)
		{
			distanceDropdown.selectItemByTitle("10 miles");
		}
		else if(radius==32000)
		{
			distanceDropdown.selectItemByTitle("20 miles");
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
					editor.putLong(key, 0);
				}
				if(distanceDropdown.getSelectedString().equals("5 miles"))
				{
					editor.putLong(key, 8000);
				}
				else if(distanceDropdown.getSelectedString().equals("10 miles"))
				{
					editor.putLong(key, 16000);
				}
				else if(distanceDropdown.getSelectedString().equals("20 miles"))
				{
					editor.putLong(key, 32000);
				}
				editor.commit();
			}
		});
		
		sortDropdown = (DropdownView) parentView.findViewById(R.id.sortDropdown);
		sortDropdown.addDropdownItem("Distance");
		sortDropdown.addDropdownItem("Best match");
		sortDropdown.commit();
		String rankBy = sharedPreferences.getString("rank_by", "Auto");
		if(rankBy.equals("Auto"))
		{
			sortDropdown.selectItemByTitle("Auto");
		}
		else if(rankBy.equals("distance"))
		{
			sortDropdown.selectItemByTitle("Distance");
		}
		else if(rankBy.equals("prominence"))
		{
			sortDropdown.selectItemByTitle("Best match");
		}
		sortDropdown.setClickListener(new ClickListener()
		{
			@Override
			public void onClick(int index)
			{
				Editor editor = sharedPreferences.edit();
				String key = "rank_by";
				if(sortDropdown.getSelectedString().equals("Auto"))
				{
					editor.putString(key, null);
				}
				else if(sortDropdown.getSelectedString().equals("Distance"))
				{
					editor.putString(key, "distance");
				}
				else if(sortDropdown.getSelectedString().equals("Best match"))
				{
					editor.putString(key, "prominence");
				}
				editor.commit();
			}
		});
	}
	
	private void setupToggleMenu(View parentView)
	{
		ToggleButton tgBtn = (ToggleButton) parentView.findViewById(R.id.toggleButton);
		tgBtn.setChecked(sharedPreferences.getBoolean("open_now", false));
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
