package com.jovi.bbs.goodcus.widgets.touchGallery;

import java.util.List;
import android.content.Context;
import android.view.ViewGroup;

public class UrlPagerAdapter extends BasePagerAdapter
{
	public UrlPagerAdapter(Context context, List<String> resources)
	{
		super(context, resources);
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object)
	{
		super.setPrimaryItem(container, position, object);
		((GalleryViewPager) container).mCurrentView = ((UrlTouchImageView) object).getImageView();
	}

	@Override
	public Object instantiateItem(ViewGroup collection, final int position)
	{
		final UrlTouchImageView iv = new UrlTouchImageView(mContext);
		iv.setUrl(mResources.get(position));
		iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		collection.addView(iv, 0);
		return iv;
	}
	
}
