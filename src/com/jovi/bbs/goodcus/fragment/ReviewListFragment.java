package com.jovi.bbs.goodcus.fragment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.jovi.bbs.goodcus.R;
import com.jovi.bbs.goodcus.model.ReviewRecord;
import com.jovi.bbs.goodcus.model.ReviewRecordListResponse;
import com.jovi.bbs.goodcus.util.HttpUtil;
import com.jovi.bbs.goodcus.widgets.ImageViewWithCache;
import com.jovi.bbs.goodcus.widgets.XListView;
import com.jovi.bbs.goodcus.widgets.XListView.IXListViewListener;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ReviewListFragment extends Fragment implements IXListViewListener
{
	protected static final String TAG = "bussiness_review_page";
	ShowCommentAdapter m_adapter;
	private ProgressBar m_pBar;
	private XListView m_listView;
	private int m_currentPage = 1;
	private RequestQueue requestQueue;
	private String business_id;
	EditText replyTextView;
	private ArrayList<ReviewRecord> m_model = new ArrayList<ReviewRecord>();
	private ArrayList<ReviewRecord> dummy_model = new ArrayList<ReviewRecord>();
	TextView messageBoard;
	
	@SuppressLint("HandlerLeak") private Handler m_handler = new Handler()
	{
		public void handleMessage(Message msg) 
		{
			if(msg.what==-1)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage("网络连接超时，请稍候再试");
				builder.setPositiveButton("确定", null);
				builder.create().show();
			}
			else if(msg.what == 0)
			{
				if(m_model.size()==0)
				{
					messageBoard.setText("暂无评论");
				}
				
				else
				{
					messageBoard.setVisibility(View.GONE);
					notifyModelDataChanged();
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
				
			}
			
			m_pBar.setVisibility(View.GONE);

		}
	};
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_review_list, container, false);
		m_listView = (XListView) view.findViewById(R.id.review_list);
		m_listView.setPullLoadEnable(false);
		m_listView.setPullRefreshEnable(true);
		m_listView.setXListViewListener(this);
		m_adapter = new ShowCommentAdapter();
		m_listView.setAdapter(m_adapter);
		m_pBar = (ProgressBar) view.findViewById(R.id.reviewDisplayProgressBar);
		business_id =getArguments().getString("place_id");  
		requestQueue = HttpUtil.getInstance().getRequestQueue();
		
		try
		{
			loadModel(m_currentPage++);
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return view;
	}
	
	private void loadModel(final int page) throws JSONException
	{
		dummy_model.clear();
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
					dummy_model.addAll(response.getRecordList());
				}
				m_handler.sendEmptyMessage(0);
				
			}
		}, new ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError error)
			{
				Log.d(TAG, error.toString());
				if(error instanceof TimeoutError)
				{
					m_handler.sendEmptyMessage(-1);
				}
			}
		});
		requestQueue.add(jr);
	}
	
	public void notifyModelDataChanged()
	{
		m_model.addAll(dummy_model);
		m_adapter.notifyDataSetChanged();
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

	@Override
	public void onRefresh()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadMore()
	{
		// TODO Auto-generated method stub
		
	}
	
}
