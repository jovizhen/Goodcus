package com.jovi.bbs.goodcus;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;
import com.jovi.bbs.goodcus.model.ApplicationUser;
import com.jovi.bbs.goodcus.model.Coordinate;
import com.jovi.bbs.goodcus.model.ResponseMessage;
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
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.location.Address;
import android.location.Geocoder;
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

public class SearchDetailsPage extends Activity implements IXListViewListener, OnItemClickListener
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
	EditText replyTextView;
	private GoogleMap mMap;
	private SearchResult result;
	
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
				warnMsg.setVisibility(View.GONE);
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_details_page);
		
		requestQueue = HttpUtil.getInstance().getRequestQueue();
		detailsView = (SearchDetailsView) findViewById(R.id.searchDetailsFrag);
		Gson gson = new Gson();
		String jsonResult =  this.getIntent().getExtras().getString("searchResult");
		result =  gson.fromJson(jsonResult, SearchResult.class);
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
		setUpMapIfNeeded();
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
	
	public void onBackBtnClick(View v)
	{
		this.finish();
	}
	
	public void onReplyBtnClick(View v)
	{
		if (!Api.getInstance().getGooglePlusClient().isConnected())
		{
			Builder builder = new Builder(this);
			builder.setMessage("需要登陆，才能回贴");
			builder.setPositiveButton("确定", new OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					Api.getInstance().connectToGooglePlus();
				}
			}).setNegativeButton("取消", null);
			builder.create().show();
		}
		else 
		{
			user_id = Api.getInstance().getGooglePlusClient().getCurrentPerson().getId();
			replyTextView = (EditText) this.findViewById(R.id.showThreadReplyText);
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
			Person currentUser = Api.getInstance().getGooglePlusClient().getCurrentPerson();
			ApplicationUser user = new ApplicationUser(currentUser.getId(), currentUser.getDisplayName(), 
					currentUser.getUrl(), currentUser.getImage().getUrl());
			record.setUser(user);
			try
			{
				postReview(record);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	private void setUpMapIfNeeded()
	{
		if (mMap == null)
		{
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.business_map)).getMap();
			mMap.setPadding(0, 10, 0, 10);
			try
			{
				Coordinate coordinate = result.getLocation().getCoordinate();
				//if yelp response contains the coordinate
				if(coordinate!=null)
				{
					double lat = coordinate.getLatitude();
					double lont = coordinate.getLongitude();
					Marker mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lont)).title("You are here!"));
					mMarker.setDraggable(true);
					mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lont), 14.00f));
				}
				// otherwise we will try google build in geocoder_api to parse from its address 
				else 
				{
					Geocoder geocoder = new Geocoder(this);
					List<Address> resultAddresses = geocoder.getFromLocationName(result.getLocationLabel(), 1);
					if(resultAddresses.size()!= 0)
					{
						double lat = resultAddresses.get(0).getLatitude();
						double lont = resultAddresses.get(0).getLongitude();
						Marker mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lont)).title("You are here!"));
						mMarker.setDraggable(true);
						mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lont), 14.00f));
						
					}
					
				}
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		
		
	} 
	
	public void postReview(ReviewRecord record) throws JSONException
	{
		Gson gson = new Gson();
		String jsonString = gson.toJson(record);
		JSONObject j = new JSONObject(jsonString);
		final ReviewRecord aRecord = record;

		JsonObjectRequest jr = new JsonObjectRequest(Method.POST, "http://24.24.219.46:8888/postReview", j, new Listener<JSONObject>()
		{
			@Override
			public void onResponse(JSONObject json)
			{
				Gson gson = new Gson();
				ResponseMessage message = gson.fromJson(json.toString(), ResponseMessage.class);
				if ("1".equals(message.getCode()))
				{
					m_pd.dismiss();
					Toast.makeText(SearchDetailsPage.this, "成功回帖", Toast.LENGTH_SHORT).show();
					m_model.add(aRecord);
					replyTextView.setText("");
					m_handler.sendEmptyMessage(0);
				}
				else 
				{
					m_pd.dismiss();
					Toast.makeText(SearchDetailsPage.this, "网络连接超时", Toast.LENGTH_SHORT).show();
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
