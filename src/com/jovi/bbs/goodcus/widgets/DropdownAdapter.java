package com.jovi.bbs.goodcus.widgets;

import java.util.ArrayList;

import com.jovi.bbs.goodcus.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class DropdownAdapter extends BaseAdapter
{
	Context ctx;
	ArrayList<String> originalEntries = new ArrayList<String>();

	public DropdownAdapter(Context context, ArrayList<String> originalEntries)
	{
		this.ctx = context;
		this.originalEntries = originalEntries;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.dropdown_item, parent, false);
		}
		((TextView) convertView.findViewById(R.id.dropdown_item)).setText(getItem(position));
		return convertView;
	}


	@Override
	public int getCount()
	{
		return originalEntries.size();

	}

	@Override
	public String getItem(int position)
	{
		return originalEntries.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

}