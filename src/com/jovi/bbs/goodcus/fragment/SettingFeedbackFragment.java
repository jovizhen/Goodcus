package com.jovi.bbs.goodcus.fragment;

import com.jovi.bbs.goodcus.R;
import com.jovi.bbs.goodcus.SettingPage;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class SettingFeedbackFragment extends Fragment
{
	private ImageButton feedbackBackBtn;
	private FragmentNavigationListener navigationListener;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = initView(inflater, container);
		configure();
		return view;
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		setNavigateListener((FragmentNavigationListener) activity);
	}
	
	private View initView(LayoutInflater inflater, ViewGroup container)
	{
		View view = inflater.inflate(R.layout.fragment_feedback, container, false);
		feedbackBackBtn = (ImageButton) view.findViewById(R.id.feedbackBackBtn);
		feedbackBackBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				navigateTo(SettingPage.FRAGMENT_SETTING_HOME);
			}
		});
		return view;
	}
	
	private void configure()
	{
	}
	
	private void setNavigateListener(FragmentNavigationListener listener)
	{
		this.navigationListener = listener;
	}
	
	private void navigateTo(int fragmentTag)
	{
		if (navigationListener != null)
		{
			navigationListener.onNavigateInvoked(fragmentTag);
		}
	}
}
