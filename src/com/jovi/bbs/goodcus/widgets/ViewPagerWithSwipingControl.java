package com.jovi.bbs.goodcus.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ViewPagerWithSwipingControl extends ViewPager
{

	private boolean swipingEnabled;
	
	public ViewPagerWithSwipingControl(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.swipingEnabled = true;
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.swipingEnabled) {
            return super.onTouchEvent(event);
        }
  
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.swipingEnabled) {
            return super.onInterceptTouchEvent(event);
        }
 
        return false;
    }
 
    public void setPageSwipingEnabled(boolean enabled) {
        this.swipingEnabled = enabled;
    }

}
