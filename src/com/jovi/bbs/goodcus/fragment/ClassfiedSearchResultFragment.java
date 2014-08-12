package com.jovi.bbs.goodcus.fragment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.Response;
import com.google.gson.Gson;
import com.jovi.bbs.goodcus.R;
import com.jovi.bbs.goodcus.SearchDetailsPage;
import com.jovi.bbs.goodcus.model.SearchResult;
import com.jovi.bbs.goodcus.model.YelpFilter;
import com.jovi.bbs.goodcus.net.Yelp;
import com.jovi.bbs.goodcus.widgets.ImageViewWithCache;
import com.jovi.bbs.goodcus.widgets.XListView;
import com.jovi.bbs.goodcus.widgets.XListView.IXListViewListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ClassfiedSearchResultFragment extends Fragment implements IXListViewListener, OnItemClickListener
{
	private int m_currentPage = 1;
	private XListView m_listView;
	private static int previousIndex;
	private int listOffset;
	private SearchResultListAdapter m_adapter;
	private ProgressBar m_pBar;
	private ArrayList<SearchResult> m_model = new ArrayList<SearchResult>();
	//dummy_model loads up data from external yelp API from AynTask, then have mHandler to call notifyModelDataChanged
    //post list data changes in UI thread to avoid illegalstatesException caused by list adapter change and listView 
	//change in different threads
	private ArrayList<SearchResult> dummy_model = new ArrayList<SearchResult>();
	private YelpFilter yelpFilter;
	private int status;
	private String searchTerm;
	
	@SuppressLint("HandlerLeak") private Handler m_handler = new Handler()
	{
		public void handleMessage(Message msg) 
		{
			switch (msg.what) 
			{
				case Yelp.NET_SUCCESS:
					notifyModelDataChanged();
					// 只在 listview 中有数据之后才启用pull load
					break;
				case Yelp.NET_TIMEOUT:
					Toast.makeText(getActivity(), R.string.net_timeout,
							Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
			}
			//toggle off the refresh btn animation on host activity action bar
			
//			if (m_refreshBtn.isRefreshing())
//			{
//				m_listView.setSelection(1);
//				m_refreshBtn.endRefresh();
//			}

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
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		loadModel(m_currentPage++);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.classfied_result_fragment, container, false);
		m_listView = (XListView) view.findViewById(R.id.fragment_result_list);
		m_listView.setPullLoadEnable(false);
		m_listView.setPullRefreshEnable(true);
		m_listView.setXListViewListener(this);
		m_listView.setOnItemClickListener(this);
		m_adapter = new SearchResultListAdapter();
		m_listView.setAdapter(m_adapter);
		m_pBar = (ProgressBar) view.findViewById(R.id.forumDisplayProgressBar);
		return view;
	}

	
	
	@Override
	public void onPause()
	{
		super.onPause();
		previousIndex = m_listView.getLastVisiblePosition();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if(m_listView != null)
		{
		    if(m_listView.getCount() > previousIndex)
		    	m_listView.setSelectionFromTop(previousIndex, 0);
		    else
		    	m_listView.setSelectionFromTop(0, 0);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
	{
		if (position < 1)
			return;
		SearchResult result = m_model.get(position - 1);
		Gson gson = new Gson();
		String jsonResult = gson.toJson(result);
		Bundle data = new Bundle();
		data.putSerializable("searchResult", jsonResult);
		Intent intent = new Intent(getActivity(), SearchDetailsPage.class);
		intent.putExtras(data);
		this.startActivity(intent);
		
	}
	
	public Location getCurrentLocation()
	{

		getActivity();
		LocationManager mlocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String bestProvider = mlocManager.getBestProvider(criteria, false);
		return mlocManager.getLastKnownLocation(bestProvider);
	}

	public void loadModel(int numOfPages)
	{
		Location location = getCurrentLocation();
		if (location != null)
		{
			yelpFilter = new YelpFilter.FilterBuilder(searchTerm).build();
			yelpFilter.setLatitude(location.getLatitude());
			yelpFilter.setLongitude(location.getLongitude());
			SearchResultTask searchTask = new SearchResultTask();
			searchTask.execute("");
		} 
	}
	
	public void notifyModelDataChanged()
	{
		m_model.addAll(dummy_model);
		m_adapter.notifyDataSetChanged();
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
	
	class SearchResultTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{
			Response response = Yelp.getInstance().search(yelpFilter);
			String responseString = response.getBody();
			int responseCode = response.getCode();
			dummy_model.clear();
			
			try
			{
				if(responseCode == HttpStatus.SC_OK)
				{
					JSONObject obj = new JSONObject(responseString);
					JSONArray arr  = obj.getJSONArray("businesses");
					for(int i=0; i<arr.length();i++)
					{
						dummy_model.add(parseToSearchResult(arr.getJSONObject(i)));
					}
					status=Yelp.NET_SUCCESS;
				}
				
				if(responseCode == HttpStatus.SC_GATEWAY_TIMEOUT)
				{
					status = Yelp.NET_TIMEOUT;
				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
			return responseString;
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			m_handler.sendEmptyMessage(status);
		}

		public SearchResult parseToSearchResult(JSONObject jsonObject)
		{
			Gson gson = new Gson();
			SearchResult result = gson.fromJson(jsonObject.toString(), SearchResult.class);
			return result;
		}
	}
	
	class SearchResultListAdapter extends BaseAdapter
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
		public long getItemId(int arg0)
		{
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if (convertView == null)
			{
				LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.search_result_item, null);
			}

			TextView name = (TextView) convertView.findViewById(R.id.business_name);
			ImageViewWithCache rating_image = (ImageViewWithCache) convertView.findViewById(R.id.rating_image);
			ImageViewWithCache img = (ImageViewWithCache) convertView.findViewById(R.id.headImgDetail);

			name.setText(m_model.get(position).getName());
			if (m_model.get(position).getImage_url() != null)
			{
				try
				{
					img.setImageUrl(new URL(m_model.get(position).getImage_url()));
					rating_image.setImageUrl(new URL(m_model.get(position).getRating_img_url_small()));
				}
				catch (MalformedURLException e)
				{
					e.printStackTrace();
				}
			} else
				img.setImageResource(R.drawable.default_user_head_img);

			return convertView;
		}
	}
	
	public static ClassfiedSearchResultFragment newInstance(String searchTerm)
	{
		ClassfiedSearchResultFragment instance = new ClassfiedSearchResultFragment();
		instance.searchTerm = searchTerm;
		return instance;
	}
}

