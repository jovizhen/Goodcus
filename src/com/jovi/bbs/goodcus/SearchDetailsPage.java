package com.jovi.bbs.goodcus;

import java.io.InputStream;
import java.util.ArrayList;

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
import com.jovi.bbs.goodcus.fragment.MapDirectionFragment;
import com.jovi.bbs.goodcus.fragment.PostReviewFragment;
import com.jovi.bbs.goodcus.fragment.ReviewListFragment;
import com.jovi.bbs.goodcus.model.ApplicationUser;
import com.jovi.bbs.goodcus.model.ResponseMessage;
import com.jovi.bbs.goodcus.model.ReviewRecord;
import com.jovi.bbs.goodcus.model.ReviewRecordListResponse;
import com.jovi.bbs.goodcus.net.Api;
import com.jovi.bbs.goodcus.net.googlePlacesApi.CustomGooglePlaces;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Photo;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Place;
import com.jovi.bbs.goodcus.util.HttpUtil;
import com.jovi.bbs.goodcus.widgets.SearchDetailsView;
import com.jovi.bbs.goodcus.widgets.XListView;
import com.jovi.bbs.goodcus.widgets.XListView.IXListViewListener;
import com.jovi.bbs.goodcus.widgets.tableView.UITableView;
import com.jovi.bbs.goodcus.widgets.tableView.UITableView.ClickListener;
import com.jovi.bbs.goodcus.widgets.touchGallery.CirclePageIndicator;
import com.jovi.bbs.goodcus.widgets.touchGallery.ReferencePagerAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SearchDetailsPage extends Activity implements IXListViewListener, OnItemClickListener
{
	protected static final String TAG = "Search Details Page";
	private   static final String POST_REVIEW_FRAGMENT    = "post_review_fragment";
	private   static final String REVIEW_LIST_FRAGMENT    = "review_list_fragment";
	private   static final String MAP_DIRECTION_FRAGMENT  = "map_direction_fragment";
	
	private RelativeLayout    pageHeader;
	private XListView         m_listView;
	private SearchDetailsView detailsView;
	private TextView          warnMsg;
	private EditText          replyTextView;
	private ProgressDialog    m_pd;
	private ProgressBar       m_pBar;
	private ViewPager         mViewPager;
	private CirclePageIndicator mIndicator;
	private ArrayList<ReviewRecord> m_model  = new ArrayList<ReviewRecord>();
	private ArrayList<String>       m_photos = new ArrayList<String>();
	private UITableView  tableView;
	private String business_id;
	private String user_id;
	private String placeId;
	
	private RequestQueue requestQueue;
	private CustomGooglePlaces googlePalcesClient;
	private ReferencePagerAdapter pagerAdapter;
	
	private GoogleMap mMap;
	private Location location;
	private Place place;
	
	
	
	
	@SuppressLint("HandlerLeak") private Handler m_handler = new Handler()
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
		googlePalcesClient = new CustomGooglePlaces();
		requestQueue = HttpUtil.getInstance().getRequestQueue();
		detailsView = (SearchDetailsView) findViewById(R.id.searchDetailsFrag);
		Gson gson = new Gson();
		placeId =  this.getIntent().getExtras().getString("placeId");
		String jsonLocation = this.getIntent().getExtras().getString("location");
		location = gson.fromJson(jsonLocation, Location.class);
		
		PlaceDetailTask detailTask = new PlaceDetailTask();
		detailTask.execute("");
		setUpMapIfNeeded();
		pagerAdapter = new ReferencePagerAdapter(this, m_photos);
		mViewPager = (ViewPager) findViewById(R.id.viewer);
		mViewPager.setAdapter(pagerAdapter);
		pageHeader = (RelativeLayout) findViewById(R.id.pagedetail_header);
		mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
		mIndicator.setViewPager(mViewPager);
		
		tableView = (UITableView) findViewById(R.id.tableView);
		createList();
	}
	
	private void createList() 
	{
		CustomClickListener listener = new CustomClickListener();
		tableView.setClickListener(listener);
		tableView.addBasicItem(R.drawable.ic_action_directions, "地图导航", null);
		tableView.addBasicItem(R.drawable.ic_action_copy, "阅读评论", null);
		tableView.addBasicItem(R.drawable.ic_action_call, "电话", null);
		tableView.commit();
	}
	
	public void toggleReplyPanel(View v)
	{
		Fragment f = getFragmentManager().findFragmentByTag(POST_REVIEW_FRAGMENT);
        if (f != null) 
        {
            getFragmentManager().popBackStack();
            pageHeader.setVisibility(View.VISIBLE);
        } 
        else 
        {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.review_fragment_container, PostReviewFragment
                                    .instantiate(this, PostReviewFragment.class.getName()),
                                    POST_REVIEW_FRAGMENT
                    ).addToBackStack(null).commit();
            pageHeader.setVisibility(View.GONE);
        }
	}
	
	public void onCancelCommentBtnClick(View v)
	{
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		toggleReplyPanel(v);
	}
	
	public void onDirectionClick()
	{
		Fragment f = getFragmentManager().findFragmentByTag(MAP_DIRECTION_FRAGMENT);
		if(f!=null)
		{
			getFragmentManager().popBackStack();
            pageHeader.setVisibility(View.VISIBLE);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.remove(((MapDirectionFragment)f).getMap());
            fragmentTransaction.commit();
            
		}
		else
		{
			Location location = new Location("");
			location.setLatitude(place.getLatitude());
			location.setLongitude(place.getLongitude());
			Gson gson = new Gson();
			String jsonLocation = gson.toJson(location);
			
			Bundle data = new Bundle();
			data.putSerializable("bussiness_location", jsonLocation);
			getFragmentManager().beginTransaction().
			setCustomAnimations(R.animator.slide_from_right, 
					R.animator.slide_to_right, 
					R.animator.slide_from_right, 
					R.animator.slide_to_right)
				.add(R.id.review_fragment_container, MapDirectionFragment
						.instantiate(this, MapDirectionFragment.class.getName(), data)
						,MAP_DIRECTION_FRAGMENT).addToBackStack(null).commit();
			pageHeader.setVisibility(View.GONE);
		}
	}
	
	public void onDirectionBackBtnClick(View v)
	{
		onDirectionClick(); 
	}
	
	public void onBrowseReviewClick()
	{
		Fragment f = getFragmentManager().findFragmentByTag(MAP_DIRECTION_FRAGMENT);
		if(f!=null)
		{
			getFragmentManager().popBackStack();
            pageHeader.setVisibility(View.VISIBLE);
		}
		else 
		{
			Bundle data = new Bundle();
			data.putString("place_id", place.getId());
			getFragmentManager().beginTransaction().
			setCustomAnimations(R.animator.slide_from_right, 
					R.animator.slide_to_right, 
					R.animator.slide_from_right, 
					R.animator.slide_to_right)
				.add(R.id.review_fragment_container, ReviewListFragment
						.instantiate(this, ReviewListFragment.class.getName(), data)
						,MAP_DIRECTION_FRAGMENT).addToBackStack(null).commit();
			pageHeader.setVisibility(View.GONE);
		}
	}
	
	public void onPhoneClick()
	{
		
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
				double lat = location.getLatitude();
				double lont = location.getLongitude();
				Marker mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lont)).title("You are here!"));
				mMarker.setDraggable(true);
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lont), 14.00f));
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		// TODO Auto-generated method stub
	}
	
	
	class PlaceDetailTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... arg0)
		{
			place = googlePalcesClient.getPlace(placeId);
			return null;
		}
		
		protected void onPostExecute(String result)
		{
			Thread aThread = new Thread()
			{
				@Override
				public void run()
				{
					Bitmap bitmap = null;
					if(place.getPhotos().size()>0)
					{
						InputStream is = place.getPhotos().get(0).download().getInputStream();
						bitmap = BitmapFactory.decodeStream(is);
					}
					DetailViewDisplayer displayer = new DetailViewDisplayer(place, bitmap);
					business_id = place.getId();
					m_handler.post(displayer);
				}
			};
			
			aThread.start();
			m_handler.post(new Runnable()
			{
				
				@Override
				public void run()
				{
					ArrayList<String> refList = new ArrayList<String>();
					for(Photo photo :place.getPhotos())
					{
						refList.add(photo.getReference());
					}
					m_photos.addAll(refList);
					pagerAdapter.notifyDataSetChanged();
				}
			});
		}
	}
	
	class DetailViewDisplayer implements Runnable
	{
		Place detailPlace;
		Bitmap bitmap;
		
		public DetailViewDisplayer(Place detailPlace, Bitmap bitmap)
		{
			this.detailPlace = detailPlace;
			this.bitmap = bitmap;
		}
		
		public void run()
		{
			detailsView.getBusinessName().setText(place.getName());
			detailsView.getBussinessAddr().setText(place.getAddress());
			detailsView.getRatingImg().setRating((float) place.getRating());
			if(bitmap != null)
			{
				detailsView.getHeadImgDetail().setImageBitmap(bitmap);
			}
		}
	}
	
	private class CustomClickListener implements ClickListener
	{
		@Override
		public void onClick(int index)
		{
			if(index == 0)
			{
				onDirectionClick();;
			}
			else if(index == 1)
			{
				onBrowseReviewClick();
			}
			else if(index == 2)
			{
				onPhoneClick();
			}
		}
	}
}
