package com.jovi.bbs.goodcus;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.plus.PlusClient.OnPeopleLoadedListener;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.google.gson.Gson;
import com.jovi.bbs.goodcus.model.ReviewRecord;
import com.jovi.bbs.goodcus.model.ReviewRecordListResponse;
import com.jovi.bbs.goodcus.model.SearchResult;
import com.jovi.bbs.goodcus.net.Api;
import com.jovi.bbs.goodcus.util.HttpUtil;
import com.jovi.bbs.goodcus.widgets.ImageViewWithCache;
import com.jovi.bbs.goodcus.widgets.SearchDetailsView;
import com.jovi.bbs.goodcus.widgets.XListView;
import com.jovi.bbs.goodcus.widgets.XListView.IXListViewListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SearchDetailsPage extends Activity implements IXListViewListener, OnItemClickListener, OnPeopleLoadedListener
{
	protected static final String TAG = "Search Details Page";
	private XListView m_listView;
	SearchDetailsView detailsView;
	TextView warnMsg;
	private ArrayList<ReviewRecord> m_model = new ArrayList<ReviewRecord>();
	ShowCommentAdapter m_adapter;
	private ProgressDialog m_pd;
	private ProgressBar m_pBar;
	private String business_id;
	private String user_id;
	private int m_currentPage = 1;
	private RequestQueue requestQueue;
	private HashMap<String, Person> hPersons = new HashMap<String, Person>();
	
	private Handler m_handler = new Handler()
	{
		public void handleMessage(Message msg) 
		{
			if(m_model.size()==0)
			{
				warnMsg.setText("暂无评论");
			}
			
			else
			{
				warnMsg.setText("");
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_details_page);
		
		requestQueue = HttpUtil.getInstance().getRequestQueue();
		detailsView = (SearchDetailsView) findViewById(R.id.searchDetailsFrag);
		Gson gson = new Gson();
		String jsonResult =  this.getIntent().getExtras().getString("searchResult");
		SearchResult result =  gson.fromJson(jsonResult, SearchResult.class);
		detailsView.getBusinessName().setText(result.getName());
		detailsView.getBussinessAddr().setText(result.getLocationLabel());
		try
		{
			detailsView.getHeadImgDetail().setImageUrl(new URL(result.getImage_url()));
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		
		warnMsg = (TextView) findViewById(R.id.warnMsg);
		
		m_listView = (XListView) findViewById(R.id.commentsList);
		m_listView.setPullLoadEnable(false);
		m_listView.setPullRefreshEnable(true);
		m_listView.setXListViewListener(this);
		m_listView.setOnItemClickListener(this);
		m_adapter = new ShowCommentAdapter();
		m_listView.setAdapter(m_adapter);
		m_listView.requestFocus();

		m_pBar = (ProgressBar)this.findViewById(R.id.showThreadProgressBar);

		Bundle data = this.getIntent().getExtras();
		business_id = data.getString("id");
		user_id = Api.getInstance().getGooglePlusClient().getCurrentPerson().getId();
		loadModel(m_currentPage++);
	}
	
	public void onReplyBtnClick(View v)
	{
		if (!Api.getInstance().getGooglePlusClient().isConnected())
		{
			Api.getInstance().getGooglePlusClient().connect();
		}
		final EditText replyTextView = (EditText) this.findViewById(R.id.showThreadReplyText);
		if (replyTextView.length() == 0)
		{
			Toast.makeText(SearchDetailsPage.this, "回帖内容不能为空", Toast.LENGTH_SHORT).show();
			replyTextView.requestFocus();
			return;
		} 
		else if (replyTextView.length() < 6)
		{
			Toast.makeText(SearchDetailsPage.this, "回帖内容长度不能小于" + 6 + "个字符", Toast.LENGTH_SHORT).show();
			replyTextView.requestFocus();
			return;
		}

		this.m_pd = ProgressDialog.show(this, "提示", "回帖中，请稍后……", true, true);
		
		ReviewRecord record = new ReviewRecord();
		record.setReview(replyTextView.getText().toString());
		record.setBusinessId(business_id);
		record.setGooglePlusUserId(user_id);
		try
		{
			postReview(record);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	public void postReview(ReviewRecord record) throws JSONException
	{
		Gson gson = new Gson();
		String jsonString = gson.toJson(record);
		JSONObject j = new JSONObject(jsonString);

		JsonObjectRequest jr = new JsonObjectRequest(Method.POST, "http://24.24.219.46:8888/postReview", j, new Listener<JSONObject>()
		{
			@Override
			public void onResponse(JSONObject json)
			{
				Gson gson = new Gson();
				try
				{
					String code = json.getString("code");
					if ("1".equals(code))
					{
						m_pd.dismiss();
						String recordString = json.getString("review_record");
						ReviewRecord aRecord = gson.fromJson(recordString, ReviewRecord.class);
						m_model.add(aRecord);
						m_adapter.notifyDataSetChanged();
					}
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
	}

	@Override
	public void onRefresh()
	{
		
	}

	@Override
	public void onLoadMore()
	{
	}
	
	private void loadModel(final int page) 
	{
		JSONObject j = new JSONObject();
		try
		{
			j.put("business_id", business_id);
		}
		catch (JSONException e1)
		{
			e1.printStackTrace();
		}

		JsonObjectRequest jr = new JsonObjectRequest(Method.GET, "http://24.24.219.46:8888/queryReview", j, new Listener<JSONObject>()
		{
			@Override
			public void onResponse(JSONObject json)
			{
				Gson gson = new Gson();
				ReviewRecordListResponse response = gson.fromJson(json.toString(), ReviewRecordListResponse.class);
				m_model.addAll(response.getRecordList());
				m_adapter.notifyDataSetChanged();
				ArrayList<String> idList = new ArrayList<String>();
				for (ReviewRecord record : m_model)
				{
					idList.add(record.getGooglePlusUserId());
				}
				Api.getInstance().getGooglePlusClient().loadPeople(SearchDetailsPage.this, idList);
				
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

		public ShowCommentAdapter()
		{
			super();
		}

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
			Person person = hPersons.get(record.getGooglePlusUserId());
			username.setText(person.getDisplayName());
			posttime.setText("not imp");
			if (person.getImage() != null)
			{
				try
				{
					img.setImageUrl(new URL(person.getImage().getUrl()));
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
	public void onPeopleLoaded(ConnectionResult status, PersonBuffer personBuffer, String nextPageToken)
	{
		if (status.isSuccess())
		{
			Iterator<Person> itP = personBuffer.iterator();
			while (itP.hasNext())
			{
				Person person = itP.next();
				Log.i("", person.getDisplayName());
				hPersons.put(person.getId(), person);
				// put some you actions here
			}
		}
	}
}
