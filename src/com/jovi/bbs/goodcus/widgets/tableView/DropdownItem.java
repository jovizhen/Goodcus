package com.jovi.bbs.goodcus.widgets.tableView;


public class DropdownItem implements IListItem
{
	private boolean mClickable = true;
	private int mDrawable = -1;
	private String mTitle;
	private int mColor = -1;
	
	
	public DropdownItem(String _title)
	{
		this.mTitle = _title;
	}

	public DropdownItem(String _title, int _color)
	{
		this.mTitle = _title;
		this.mColor = _color;
	}

	public DropdownItem(String _title, boolean _clickable)
	{
		this.mTitle = _title;
		this.mClickable = _clickable;
	}

	public DropdownItem(int _drawable, String _title)
	{
		this.mDrawable = _drawable;
		this.mTitle = _title;
	}

	public DropdownItem(int _drawable, String _title,  int _color)
	{
		this.mDrawable = _drawable;
		this.mTitle = _title;
		this.mColor = _color;
	}
	
	public boolean ismClickable()
	{
		return mClickable;
	}

	public void setmClickable(boolean mClickable)
	{
		this.mClickable = mClickable;
	}

	public int getmDrawable()
	{
		return mDrawable;
	}

	public void setmDrawable(int mDrawable)
	{
		this.mDrawable = mDrawable;
	}

	public String getmTitle()
	{
		return mTitle;
	}

	public void setmTitle(String mTitle)
	{
		this.mTitle = mTitle;
	}

	public int getmColor()
	{
		return mColor;
	}

	public void setmColor(int mColor)
	{
		this.mColor = mColor;
	}

	@Override
	public boolean isClickable()
	{
		return mClickable;
	}

	@Override
	public void setClickable(boolean clickable)
	{
		mClickable = clickable;
	}

}
