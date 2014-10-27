package com.jovi.bbs.goodcus.widgets.tableView;

import com.jovi.bbs.goodcus.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class CustomUITableView extends UITableView
{
	private CustomItemClickListener mCustomListener;
	
	public CustomUITableView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public void addBasicItem(String title, boolean showChevron)
	{
		mItemList.add(new BasicItem(title, showChevron));
	}
	
	public void addBasicItem(int drawable, String title, String summary, boolean showChevron)
	{
		mItemList.add(new BasicItem(drawable, title, summary, showChevron));
	}

	@Override
	protected void setupBasicItem(View view, BasicItem item, int index)
	{
		super.setupBasicItem(view, item, index);
		if(item.isShowChevron() == false)
		{
			ImageView chevron = (ImageView) view.findViewById(R.id.chevron);
			chevron.setVisibility(View.GONE);
		}
		
		if (item.isClickable())
		{
			view.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					if (mClickListener != null)
						mClickListener.onClick((Integer) view.getTag());
					if(mCustomListener != null)
						mCustomListener.onClick(view);
				}
			});
		} else
		{
			((ImageView) view.findViewById(R.id.chevron)).setVisibility(View.GONE);
		}
	}
	
	public interface CustomItemClickListener
	{
		public void onClick(View view);
	}
	
	public void setCustomListener(CustomItemClickListener listener)
	{
		mCustomListener = listener;
	}
	
	public void removeCustomListener()
	{
		mCustomListener = null; 
	}
}
