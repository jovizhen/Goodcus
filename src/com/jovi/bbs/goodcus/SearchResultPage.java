package com.jovi.bbs.goodcus;



import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.Response;

import com.google.gson.Gson;
import com.jovi.bbs.goodcus.fragment.SearchFilterFragment;
import com.jovi.bbs.goodcus.model.SearchResult;
import com.jovi.bbs.goodcus.model.YelpFilter;
import com.jovi.bbs.goodcus.net.Yelp;
import com.jovi.bbs.goodcus.widgets.ImageViewWithCache;
import com.jovi.bbs.goodcus.widgets.RefreshActionBtn;
import com.jovi.bbs.goodcus.widgets.XListView;
import com.jovi.bbs.goodcus.widgets.XListView.IXListViewListener;

import android.app.Activity;
import android.app.Fragment;
import android.content.IntentFilter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class SearchResultPage extends Activity implements IXListViewListener,
OnItemClickListener
{
	private static final String SEARCH_FILTER_FRAGMENT_TAG = "search_filter_fragment";
	private int m_currentPage = 1;
	private XListView m_listView;
	private SearchResultListAdapter m_adapter;
	private ProgressBar m_pBar;
	private RefreshActionBtn m_refreshBtn;
	private ArrayList<SearchResult> m_model = new ArrayList<SearchResult>();
	private YelpFilter yelpFilter;
	private int status;

	private Handler m_handler = new Handler()
	{
		public void handleMessage(Message msg) 
		{
			switch (msg.what) 
			{
				case Yelp.NET_SUCCESS:
					m_adapter.notifyDataSetChanged();
					// 只在 listview 中有数据之后才启用pull load
					break;
				case Yelp.NET_TIMEOUT:
					Toast.makeText(SearchResultPage.this, R.string.net_timeout,
							Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
			}
			
			if (m_refreshBtn.isRefreshing())
			{
				m_listView.setSelection(1);
				m_refreshBtn.endRefresh();
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
		setContentView(R.layout.search_result_page);
		
		m_listView = (XListView) this.findViewById(R.id.forumDisplayListView);
		m_listView.setPullLoadEnable(false);
		m_listView.setPullRefreshEnable(true);
		m_listView.setXListViewListener(this);
		m_listView.setOnItemClickListener(this);
		m_adapter = new SearchResultListAdapter();
		m_listView.setAdapter(m_adapter);

		m_refreshBtn = (RefreshActionBtn) this
				.findViewById(R.id.forumDisplayRefreshBtn);
		m_pBar = (ProgressBar) this.findViewById(R.id.forumDisplayProgressBar);

		Bundle data = this.getIntent().getExtras();
		TextView tv = (TextView) this.findViewById(R.id.forumDisplayPageTitle);
		tv.setText(data.getString("title"));
		
		yelpFilter =  new YelpFilter.FilterBuilder("Resturant").build();
		yelpFilter.setLatitude(30.361471);
		yelpFilter.setLongitude(-87.164326);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(App.LOGIN_STATE_CHANGE_ACTION);
		loadModel(m_currentPage++);
	}
	
	

	public void loadModel(int numOfPages)
	{
		SearchResultTask searchTask = new SearchResultTask();
		searchTask.execute("");
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
		Intent intent = new Intent(this, SearchDetailsPage.class);
		intent.putExtras(data);
		this.startActivity(intent);
	}
	
	public void forceRefresh()
	{
		m_currentPage = 1;
		loadModel(m_currentPage++);
	}

	@Override
	public void onRefresh()
	{
		m_refreshBtn.startRefresh();
		forceRefresh();
	}

	public void onBackBtnClick(View v)
	{
		this.finish();
	}
	
	public void onPageTitleClick(View v)
	{
		// 滑动listView到顶端
		m_listView.setSelection(0);
	}
	
	public void toggleFilterPanel(View v)
	{
		Fragment f = getFragmentManager().findFragmentByTag(SEARCH_FILTER_FRAGMENT_TAG);
        if (f != null) {
            getFragmentManager().popBackStack();
        } else {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.slide_up,
                            R.animator.slide_down,
                            R.animator.slide_up,
                            R.animator.slide_down)
                    .add(R.id.list_fragment_container, SearchFilterFragment
                                    .instantiate(this, SearchFilterFragment.class.getName()),
                                    SEARCH_FILTER_FRAGMENT_TAG
                    ).addToBackStack(null).commit();
        }
	}
	
	public void onRefreshBtnClick(View v)
	{
		if (m_refreshBtn.isRefreshing())
		{
			return;
		}
		m_refreshBtn.startRefresh();
		m_listView.setSelection(0);
		m_listView.pullRefreshing();
		forceRefresh();
	}
	
	@Override
	public void onLoadMore()
	{
		loadModel(m_currentPage++);
	}
	
	
	class SearchResultListAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			return (m_model == null) ? 0 : m_model.size();
		}

		@Override
		public Object getItem(int arg0)
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
				convertView = getLayoutInflater().inflate(
						R.layout.search_result_item, null);
			}
			
			TextView name = (TextView) convertView.findViewById(R.id.business_name);
			ImageViewWithCache rating_image  = (ImageViewWithCache) convertView.findViewById(R.id.rating_image);
			ImageViewWithCache  img = (ImageViewWithCache ) convertView.findViewById(R.id.headImgDetail);
			
			name.setText(m_model.get(position).getName());
			if(m_model.get(position).getImage_url()!=null)
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
			}
			else 
				img.setImageResource(R.drawable.default_user_head_img);
			
			return convertView;
		}
	}

	class SearchResultTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... urls)
		{
			Response response = Yelp.getInstance().search(yelpFilter);
			String responseString = response.getBody();
			int responseCode = response.getCode();
			m_model.clear();
			
			try
			{
				if(responseCode == HttpStatus.SC_OK)
				{
					JSONObject obj = new JSONObject(responseString);
					JSONArray arr  = obj.getJSONArray("businesses");
					for(int i=0; i<arr.length();i++)
					{
						m_model.add(parseToSearchResult(arr.getJSONObject(i)));
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
	
	
}
