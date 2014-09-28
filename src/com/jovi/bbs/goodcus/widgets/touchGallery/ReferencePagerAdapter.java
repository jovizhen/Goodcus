package com.jovi.bbs.goodcus.widgets.touchGallery;

import java.util.List;
import android.content.Context;
import android.view.ViewGroup;

public class ReferencePagerAdapter extends BasePagerAdapter
{
	public ReferencePagerAdapter(Context context, List<String> resources)
	{
		super(context, resources);
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object)
	{
		super.setPrimaryItem(container, position, object);
		if (getCount() > 0)
		{
			((GalleryViewPager) container).mCurrentView = ((ReferenceTouchImageView) object).getImageView();
		}
	}

	@Override
	public Object instantiateItem(ViewGroup collection, final int position)
	{
		final ReferenceTouchImageView iv = new ReferenceTouchImageView(mContext);
		iv.setReference(mResources.get(position));
		iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		collection.addView(iv, 0);
		return iv;
	}
}
