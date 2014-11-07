package com.jovi.bbs.goodcus.widgets;

import com.jovi.bbs.goodcus.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;

public class ClearableAutocompleteTextView extends AutoCompleteTextView
{

	boolean justCleared = false;
	// if not set otherwise, the default clear listener clears the text in the
	// text view

	private OnClearListener defaultClearListener = new OnClearListener()
	{
		@Override
		public void onClear()
		{
			ClearableAutocompleteTextView et = ClearableAutocompleteTextView.this;
			et.setText("");
		}
	};

	private OnClearListener onClearListener = defaultClearListener;
	// The image we defined for the clear button
	public Drawable imgClearButton = getResources().getDrawable(R.drawable.ic_action_cancel_dark);
//	public Drawable imgSearchIcon  = getResources().getDrawable(R.drawable.ic_action_search_dark);

	public ClearableAutocompleteTextView(Context context)
	{
		super(context);
	}

	public ClearableAutocompleteTextView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	/* Required methods, not used in this implementation */
	public ClearableAutocompleteTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public void init()
	{
		this.setCompoundDrawablesWithIntrinsicBounds(null, null, imgClearButton, null);
		// if the clear button is pressed, fire up the handler. Otherwise do
		// nothing
		this.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				ClearableAutocompleteTextView et = ClearableAutocompleteTextView.this;
				
				if (et.getCompoundDrawables()[2] == null)
					return false;
				if (event.getAction() != MotionEvent.ACTION_UP)
					return false;
				if (event.getX() > et.getWidth() - et.getPaddingRight() - imgClearButton.getIntrinsicWidth())
				{
					onClearListener.onClear();
					justCleared = true;
				}
				return false;
			}
		});
		
		setOnFocusChangeListener(new OnFocusChangeListener()
		{
			
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if(hasFocus && getText().toString().length() > 0)
					showClearButton();
				else
					hideClearButton();
			}
		});
		
		addTextChangedListener(new TextWatcher()
		{
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				if(getText().toString().length() > 0)
					showClearButton();
				else
					hideClearButton();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
			}
			
			@Override
			public void afterTextChanged(Editable s)
			{
			}
		});
		
	}

	public interface OnClearListener
	{
		void onClear();
	}

	public void setImgClearButton(Drawable imgClearButton)
	{
		this.imgClearButton = imgClearButton;
	}

	public void setOnClearListener(final OnClearListener clearListener)
	{
		this.onClearListener = clearListener;
	}

	public void hideClearButton()
	{
		this.setCompoundDrawables(null, null, null, null);
	}

	public void showClearButton()
	{
		this.setCompoundDrawablesWithIntrinsicBounds(null, null, imgClearButton, null);
	}
}
