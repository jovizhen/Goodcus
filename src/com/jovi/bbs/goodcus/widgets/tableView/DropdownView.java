package com.jovi.bbs.goodcus.widgets.tableView;

import java.util.ArrayList;
import java.util.List;

import com.jovi.bbs.goodcus.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DropdownView extends LinearLayout
{
	private int mIndexController = 0;
	
	private LayoutInflater mInflater;
	private RelativeLayout mMainContainer;
	private LinearLayout mDropdownFoldOutMenu;
	private TextView mDropdownTitle;
	
	private List<DropdownItem> mItemList;
	private List<TextView> mTextViewList;
	private DropdownItem defaultItem = new DropdownItem("Auto");
	
	private ClickListener mClickListener;
	
	public DropdownView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mItemList = new ArrayList<DropdownItem>();
		mItemList.add(defaultItem);
		mTextViewList = new ArrayList<TextView>();
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMainContainer = (RelativeLayout) mInflater.inflate(R.layout.dropdown_list_container, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mMainContainer, params);
		mDropdownFoldOutMenu = (LinearLayout) mMainContainer.findViewById(R.id.dropdown_foldout_menu);
		mDropdownTitle = (TextView) mMainContainer.findViewById(R.id.dropdown_textview);
		mDropdownTitle.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (mDropdownFoldOutMenu.getVisibility() == View.GONE)
				{
					openDropdown();
				} 
				else
				{
					closeDropdown();
				}
			}
		});
	}
	
	public void addDropdownItem(String _title)
	{
		mItemList.add(new DropdownItem(_title));
	}
	
	public void addDropdownItem(String _title, int _color)
	{
		mItemList.add(new DropdownItem(_title, _color));
	}
	
	public void addDropdownItem(String _title, boolean _clickable)
	{
		mItemList.add(new DropdownItem(_title, _clickable));
	}
	
	public void addDropdownItem(int _drawable, String _title)
	{
		mItemList.add(new DropdownItem(_drawable, _title));
	}
	
	public void addDropdownItem(int _drawable, String _title,  int _color)
	{
		mItemList.add(new DropdownItem(_drawable, _title, _color));
	}
	
	public void addDropdownItem(DropdownItem item)
	{
		mItemList.add(item);
	}
	
	public void setDefaultItem(DropdownItem defaultItem)
	{
		this.defaultItem = defaultItem;
	}

	public void commit()
	{
		mIndexController = 0;
		if(mItemList.size() > 0)
		{
			for(DropdownItem item : mItemList)
			{
				View temItemView = mInflater.inflate(R.layout.dropdown_list_item, null);
				setUpItem(temItemView, item, mIndexController);
				mDropdownFoldOutMenu.addView(temItemView);
				mIndexController++;
			}
		}
		for(TextView textView : mTextViewList)
		{
			if(textView.isClickable())
			{
				final int index = (Integer) textView.getTag();
				textView.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						String selectedText = "";
						for(TextView aTextView : mTextViewList)
						{
							if(aTextView.getTag().equals(index))
							{
								aTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
										R.drawable.icn_dropdown_checked, 0);
								selectedText = (String) aTextView.getText();
								
							}
							else 
							{
								aTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
							}
						}
						mDropdownTitle.setText(selectedText);
						closeDropdown();
						if (mClickListener != null)
							mClickListener.onClick((Integer) view.getTag());
					}
				});
			}
		}
	}
	
	public void setUpItem(View view, DropdownItem item, int index)
	{
		TextView textView = (TextView) view.findViewById(R.id.dropdown_item_text);
		if(index == 0)
		{
			textView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.drawable.icn_dropdown_checked, 0);
		}
		textView.setClickable(item.isClickable());
		textView.setTag(index);
		mTextViewList.add(textView);
		textView.setText(item.getmTitle());
	}
	
	public interface ClickListener
	{
		void onClick(int index);
	}
	
	private void openDropdown()
	{
		if (mDropdownFoldOutMenu.getVisibility() != View.VISIBLE)
		{
			ScaleAnimation anim = new ScaleAnimation(1, 1, 0, 1);
			anim.setDuration(200);
			mDropdownFoldOutMenu.startAnimation(anim);
			mDropdownTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_dropdown_close, 0);
			mDropdownFoldOutMenu.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Animates out the dropdown list
	 */
	private void closeDropdown()
	{
		if (mDropdownFoldOutMenu.getVisibility() == View.VISIBLE)
		{
			ScaleAnimation anim = new ScaleAnimation(1, 1, 1, 0);
			anim.setDuration(200);
			anim.setAnimationListener(new AnimationListener()
			{
				@Override
				public void onAnimationStart(Animation animation)
				{
				}

				@Override
				public void onAnimationRepeat(Animation animation)
				{
				}

				@Override
				public void onAnimationEnd(Animation animation)
				{
					mDropdownFoldOutMenu.setVisibility(View.GONE);
				}
			});
			mDropdownTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_dropdown_open, 0);
			mDropdownFoldOutMenu.startAnimation(anim);
		}
	}
	
	public void selectItem(DropdownItem item)
	{
		if(item.getmTitle()==null)
			return;
		for(TextView textView: mTextViewList)
		{
			if(textView.getText().equals(item.getmTitle()))
			{
				textView.performClick();
			}
		}
	}
	
	public void selectItemByTitle(String title)
	{
		if(title==null)
			return;
		for(TextView textView: mTextViewList)
		{
			if(textView.getText().equals(title))
			{
				textView.performClick();
			}
		}
	}

	public int getCount()
	{
		return mItemList.size();
	}

	public void clear()
	{
		mItemList.clear();
		mDropdownFoldOutMenu.removeAllViews();
	}

	public void setClickListener(ClickListener listener)
	{
		this.mClickListener = listener;
	}

	public void removeClickListener()
	{
		this.mClickListener = null;
	}
	
	public TextView getDropDownTitle()
	{
		return mDropdownTitle;
	}
	
	public String getSelectedString()
	{
		return (String) mDropdownTitle.getText();
	}
}
