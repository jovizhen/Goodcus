package com.jovi.bbs.goodcus;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.jovi.bbs.goodcus.model.ReviewRecord;
import com.jovi.bbs.goodcus.model.ReviewRecordListResponse;
import com.jovi.bbs.goodcus.model.SearchResult;
import com.jovi.bbs.goodcus.util.HttpUtil;
import com.jovi.bbs.goodcus.widgets.ImageViewWithCache;
import com.jovi.bbs.goodcus.widgets.XListView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BusinessReviewPage extends Activity
{
	protected static final String TAG = "bussiness_review_page";
	ShowCommentAdapter m_adapter;
	private ProgressBar m_pBar;
	private XListView m_listView;
	private int m_currentPage = 1;
	private RequestQueue requestQueue;
	private SearchResult result;
	private String business_id;
	EditText replyTextView;
	private ArrayList<ReviewRecord> m_model = new ArrayList<ReviewRecord>();
	private ArrayList<ReviewRecord> dummy_model = new ArrayList<ReviewRecord>();
	TextView messageBoard;
	@SuppressLint("HandlerLeak") private Handler m_handler = new Handler()
	{
		public void handleMessage(Message msg) 
		{
			if(m_model.size()==0)
			{
				messageBoard.setText("暂无评论");
			}
			
			else
			{
				messageBoard.setVisibility(View.GONE);
				m_adapter.notifyDataSetChanged();
			}

			if (m_listView.getPullLoading())
			{
				// 加载到最后一页时禁用pull load
				m_listView.stopLoadMore();
			}

			if (m_listView.getPullRefreshing())
			{
				m_listView.stopRefresh();
			}
			
			m_pBar.setVisibility(View.GONE);

		}
	};
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_details_page);
		Gson gson = new Gson();
		String jsonResult =  this.getIntent().getExtras().getString("searchResult");
		result =  gson.fromJson(jsonResult, SearchResult.class);
		requestQueue = HttpUtil.getInstance().getRequestQueue();
		business_id = result.getId();
		try
		{
			loadModel(m_currentPage++);
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void loadModel(final int page) throws JSONException
	{
		m_model.clear();
		ReviewRecord record = new ReviewRecord();
		record.setBusinessId(business_id);
		Gson gson =new Gson();
		String jsonString = gson.toJson(record);
		JSONObject j = new JSONObject(jsonString);

		JsonObjectRequest jr = new JsonObjectRequest(Method.POST, "http://24.24.219.46:8888/queryReview", j, new Listener<JSONObject>()
		{
			@Override
			public void onResponse(JSONObject json)
			{
				Gson gson = new Gson();
				ReviewRecordListResponse response = gson.fromJson(json.toString(), ReviewRecordListResponse.class);
				if (response.getRecordList() != null)
				{
					m_model.addAll(response.getRecordList());
				}
				m_handler.sendEmptyMessage(0);
				
			}
		}, new ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError error)
			{
				Log.d(TAG, error.toString());
			}
		});
		requestQueue.add(jr);
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
				convertView = getLayoutInflater().inflate(R.layout.show_thread_item, null);
			}

			TextView username = (TextView) convertView.findViewById(R.id.showthreadUsername);
			TextView floorNum = (TextView) convertView.findViewById(R.id.showThreadFloorNum);
			floorNum.setText((position + 1) + "#");
			TextView posttime = (TextView) convertView.findViewById(R.id.showthreadPosttime);
			final TextView msg = (TextView) convertView.findViewById(R.id.showthreadMsg);
			ImageViewWithCache img = (ImageViewWithCache) convertView.findViewById(R.id.showthreadHeadImg);

			final ReviewRecord record = m_model.get(position);
			username.setText(record.getUser().getDisplayName());
			posttime.setText("not imp");
			if (record.getUser().getImgUrl() != null)
			{
				try
				{
					img.setImageUrl(new URL(record.getUser().getImgUrl()));
				}
				catch (MalformedURLException e)
				{
					e.printStackTrace();
				}
			} 
			else
			{
				img.setImageResource(R.drawable.default_user_head_img);
			}
			msg.setText(record.getReview());

			return convertView;
		}
	}

}
