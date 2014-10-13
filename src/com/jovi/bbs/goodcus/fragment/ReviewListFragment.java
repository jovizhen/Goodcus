package com.jovi.bbs.goodcus.fragment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.jovi.bbs.goodcus.R;
import com.jovi.bbs.goodcus.net.googlePlacesApi.CustomGooglePlaces;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Place;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Review;
import com.jovi.bbs.goodcus.util.GoogleImageLoader;
import com.jovi.bbs.goodcus.widgets.ImageViewWithCache;
import com.jovi.bbs.goodcus.widgets.RefreshActionBtn;
import com.jovi.bbs.goodcus.widgets.XListView;
import com.jovi.bbs.goodcus.widgets.XListView.IXListViewListener;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

public class ReviewListFragment extends Fragment implements IXListViewListener
{
	protected static final String TAG = "bussiness_review_page";
	private String placeId;
	
	private ProgressBar m_pBar;
	private XListView m_listView;

	private ArrayList<Review> m_model = new ArrayList<Review>();
	private ShowCommentAdapter m_adapter;
	public CustomGooglePlaces googlePalcesClient;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = initView(inflater, container);
		configure();
		loadModel();
		return view;
	}
	
	private View initView(LayoutInflater inflater, ViewGroup container)
	{
		View view = inflater.inflate(R.layout.fragment_review_list, container, false);
		m_listView = (XListView) view.findViewById(R.id.review_list);
		m_listView.setPullLoadEnable(false);
		m_listView.setPullRefreshEnable(true);
		m_listView.setXListViewListener(this);
		m_adapter = new ShowCommentAdapter();
		m_listView.setAdapter(m_adapter);
		RefreshActionBtn btn = (RefreshActionBtn) view.findViewById(R.id.reviewDisplayRefreshBtn);
		btn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onRefresh();
			}
		});
		m_pBar = (ProgressBar) view.findViewById(R.id.reviewDisplayProgressBar);
		return view;
	}
	
	private void configure()
	{
		googlePalcesClient = new CustomGooglePlaces();
		placeId = getArguments().getString("place_id");
	}
	
	private void loadModel() 
	{
		try
		{
			m_model.clear();
			PlaceDetailTask detailTask = new PlaceDetailTask();
			detailTask.execute("");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private class ShowCommentAdapter extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			return (m_model == null) ? 0 : m_model.size();
		}

		@Override
		public Object getItem(int position)
		{
			return null;
		}

		@Override
		public long getItemId(int position)
		{
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			if (convertView == null)
			{
				convertView = getActivity().getLayoutInflater().inflate(R.layout.show_thread_item, null);
			}

			TextView username = (TextView) convertView.findViewById(R.id.showthreadUsername);
			TextView floorNum = (TextView) convertView.findViewById(R.id.showThreadFloorNum);
			floorNum.setText((position + 1) + "#");
			RatingBar ratingbar = (RatingBar) convertView.findViewById(R.id.authorRating);
			TextView posttime = (TextView) convertView.findViewById(R.id.showthreadPosttime);
			final TextView msg = (TextView) convertView.findViewById(R.id.showthreadMsg);
			ImageViewWithCache img = (ImageViewWithCache) convertView.findViewById(R.id.showthreadHeadImg);

			final Review record = m_model.get(position);
			username.setText(record.getAuthor());
			ratingbar.setRating(record.getRating());
			Date date  = new Date(record.getTime()*1000); 
			posttime.setText(date.toString());
			if (record.getAuthorUrl() != null)
			{
				ImageUrlLoadTask urlLoadTask = new ImageUrlLoadTask(record.getAuthorUrl(), img);
				urlLoadTask.execute();
			} 
			msg.setText(record.getText());
			return convertView;
		}
	}

	@Override
	public void onRefresh()
	{
		loadModel();
	}

	@Override
	public void onLoadMore()
	{
	}
	
	class ImageUrlLoadTask extends AsyncTask<String, Void, String>
	{

		ImageViewWithCache imageView;
		String queryUrl;
		public ImageUrlLoadTask(String queryUrl, ImageViewWithCache imageView)
		{
			this.queryUrl = queryUrl;
			this.imageView = imageView;
		}
		@Override
		protected String doInBackground(String... params)
		{
			String imgUrl = "";
			try
			{
				String peopleId = queryUrl.trim().replace("https://plus.google.com/", "").trim();
				String url = String.format("%s/%s?fields=image&key=%s", "https://www.googleapis.com/plus/v1/people", peopleId, 
						"AIzaSyAdxpP8KZZnmJgDC_gKOFIpI5od_AZmDfw");
				String rawJson = com.jovi.bbs.goodcus.net.googlePlacesApi.HttpUtil.
						get(GoogleImageLoader.CLIENT, url);
				JSONObject json = new JSONObject(rawJson);
				JSONObject result = json.getJSONObject("image");
				imgUrl = result.getString("url");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return imgUrl;
		}
		
		protected void onPostExecute(String url)
		{
			if(url!=null)
			{
				try
				{
					imageView.setImageUrl(new URL(url));
				}
				catch (MalformedURLException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	class PlaceDetailTask extends AsyncTask<String, Void, List<Review>>
	{
		@Override
		protected List<Review> doInBackground(String... arg0)
		{
			Place place = googlePalcesClient.getPlace(placeId);
			return place.getReviews();
		}
		
		protected void onPostExecute(List<Review> result)
		{
			if(result.size()!=0)
			{
				m_model.addAll(result);
				m_adapter.notifyDataSetChanged();
			}
			if(m_listView.getPullRefreshing())
			{
				m_listView.stopRefresh();
			}
			m_pBar.setVisibility(View.GONE);
		}
	}
	
}
