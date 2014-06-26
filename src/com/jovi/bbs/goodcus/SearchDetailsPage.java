package com.jovi.bbs.goodcus;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;

import com.google.gson.Gson;
import com.jovi.bbs.goodcus.model.SearchResult;
import com.jovi.bbs.goodcus.widgets.ImageViewWithCache;
import com.jovi.bbs.goodcus.widgets.RefreshActionBtn;
import com.jovi.bbs.goodcus.widgets.SearchDetailsView;
import com.jovi.bbs.goodcus.widgets.ThreadItemFooter;
import com.jovi.bbs.goodcus.widgets.XListView;
import com.jovi.bbs.goodcus.widgets.XListView.IXListViewListener;

import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SearchDetailsPage extends Activity implements IXListViewListener, OnItemClickListener
{
	private XListView m_listView;
	SearchDetailsView detailsView;
	private JSONArray m_model = null;
	ShowCommentAdapter m_adapter;
	private ProgressDialog m_pd;
	private ProgressBar m_pBar;
	private int m_id;
	private int m_currentPage = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_details_page);
		
		
		detailsView = (SearchDetailsView) findViewById(R.id.searchDetailsFrag);
		Gson gson = new Gson();
		String jsonResult =  this.getIntent().getExtras().getString("searchResult");
		SearchResult result =  gson.fromJson(jsonResult, SearchResult.class);
		detailsView.getBusinessName().setText(result.getName());
		detailsView.getBussinessAddr().setText(result.getLocationLabel());
		TextView tv = (TextView) this.findViewById(R.id.forumDisplayPageTitle);
		tv.setText(result.getName());
		try
		{
			detailsView.getHeadImgDetail().setImageUrl(new URL(result.getImage_url()));
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		
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
		m_id = data.getInt("id");
		loadModel(m_currentPage++);
		
	}
	
//	public void onReplyBtnClick(View v)
//	{
//		if (!Api.getInstance().isLogin())
//		{
//			this.startActivity(new Intent(this, LoginPage.class));
//			return;
//		}
//		final EditText replyTextView = (EditText) this.findViewById(R.id.showThreadReplyText);
//		if (replyTextView.length() == 0)
//		{
//			Toast.makeText(SearchDetailsPage.this, "回帖内容不能为空", Toast.LENGTH_SHORT).show();
//			replyTextView.requestFocus();
//			return;
//		} 
//		else if (replyTextView.length() < Api.POST_CONTENT_SIZE_MIN)
//		{
//			Toast.makeText(SearchDetailsPage.this, "回帖内容长度不能小于" + Api.POST_CONTENT_SIZE_MIN + "个字符", Toast.LENGTH_SHORT).show();
//			replyTextView.requestFocus();
//			return;
//		}
//
//		this.m_pd = ProgressDialog.show(this, "提示", "回帖中，请稍后……", true, true);
//	}
//	

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		// TODO Auto-generated method stub
		
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
	
	private void loadModel(final int page) 
	{
		
	}

	
	
	private class ShowCommentAdapter extends BaseAdapter {		

		public ShowCommentAdapter() {
			super();
		}

		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int paramInt)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int paramInt)
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
		{
			// TODO Auto-generated method stub
			return null;
		}

//		@Override
//		public int getCount() {
//			return (m_model == null)?0:m_model.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return null;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return 0;
//		}
//
//		@Override
//		public View getView(final int position, View convertView, ViewGroup parent) {
//			if (convertView == null) {
//				convertView = getLayoutInflater().inflate(R.layout.show_thread_item, null);
//			}
//			
//			TextView username = (TextView)convertView.findViewById(R.id.showthreadUsername);
//			TextView floorNum = (TextView)convertView.findViewById(R.id.showThreadFloorNum);
//			floorNum.setText((position + 1) + "#");
//			TextView posttime = (TextView)convertView.findViewById(R.id.showthreadPosttime);
//			final TextView msg = (TextView)convertView.findViewById(R.id.showthreadMsg);
//			ImageViewWithCache img = (ImageViewWithCache)convertView.findViewById(R.id.showthreadHeadImg);
//			ThreadItemFooter itemFooter = (ThreadItemFooter)convertView.findViewById(R.id.showthreadLoadTip);
//			
//			final JSONObject item = m_model.getJSONObject(position);
//			username.setText(Html.fromHtml(item.get("username").toString()));
//			posttime.setText(item.get("postdate")+" "+item.get("posttime"));
//			if (item.getInteger("avatar") == 1) {
//				try {
//					img.setImageUrl(new URL(Api.getInstance().getUserHeadImageUrl(item.getInteger("userid"))));
//				} catch (MalformedURLException e) {
//					// TODO è‡ªåŠ¨ç”Ÿæˆ�çš„ catch å�—
//					e.printStackTrace();
//				}
//			}else {
//				img.setImageResource(R.drawable.default_user_head_img);
//			}
//			
//	        img.setOnClickListener(new View.OnClickListener() {
//	        	@Override
//	        	public void onClick(View v) {
//	        		
//	        		if (Api.getInstance().isLogin()) {
//		        		Bundle data = new Bundle();
//		        		data.putInt("user_id", item.getInteger("userid"));
//		        		Intent intent = new Intent(ShowThreadPage.this, UserInfoPage.class);
//		        		intent.putExtras(data);
//		        	    v.getContext().startActivity(intent);
//	        			return;
//	        		}
//	        		else {
//	        			Toast.makeText(ShowThreadPage.this, "æ��ç¤ºï¼šè¯·ç™»å…¥å�ŽæŸ¥çœ‹ç”¨æˆ·ä¿¡æ�¯ã€‚",
//	        					Toast.LENGTH_SHORT).show();
//	        		}
//	        	}
//	        }); 
//	        
//			//æ ¼å¼�åŒ–ç¼©ç•¥å¸–å­�ç¼©ç•¥ä¿¡æ�¯çš„Runnable
//			Runnable runFormatMessage = new Runnable() {
//
//				@Override
//				public void run() {
//					final Spanned spanned = Html.fromHtml(item.get("message").toString(), m_imgGetter, null);
//					item.put("thumbnailSpanned", spanned);
//					m_handler.post(new Runnable(){
//
//						@Override
//						public void run() {
//							msg.setText(spanned);
//						}
//						
//					});
//				}
//				
//			};
//			
//
//			//é•¿æ–‡ç« çš„ç‚¹å‡»æ‰©å±•
//			itemFooter.setVisibility((item.getInteger("thumbnail") == 1)?View.VISIBLE:View.GONE);
//			if (item.containsKey("isExpanded") && item.getInteger("isExpanded") == 1) {
//				//onItemClickä¸­æ‰©å±•æ–‡ç« ï¼Œç¼“å­˜æ ¼å¼�åŒ–å�Žçš„å¯¹è±¡
//				msg.setText((Spanned)item.get("expandSpanned"));
//				itemFooter.setExpanded();
//			}else {
//				if (item.containsKey("thumbnailSpanned")) {
//					msg.setText((Spanned)item.get("thumbnailSpanned"));
//				} else {
//					new Thread(runFormatMessage).start();
//				}
//				itemFooter.setCollapsed();
//			}
//			
//			//å›¾ç‰‡ä¸­å¸¦é™„ä»¶å¤„ç�†
//			View attachmentView = convertView.findViewById(R.id.showthreadAttachment);
//			if (!item.containsKey("thumbnailattachments") && !item.containsKey("otherattachments")) {
//				attachmentView.setVisibility(View.GONE);
//			}
//			
//			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//			lp.setMargins(0, 5, 0, 0);
//			LinearLayout attachmentList = (LinearLayout)convertView.findViewById(R.id.showThreadImgAttachmentList);
//			if (item.containsKey("thumbnailattachments")) {
//				JSONArray arr = item.getJSONArray("thumbnailattachments");
//				attachmentView.setVisibility(View.VISIBLE);
//				attachmentList.removeAllViews();
//				for (int i = 0; i < arr.size(); i++) {
//					
//					ImageViewWithCache imgWithCache = new ImageViewWithCache(ShowThreadPage.this);
//					imgWithCache.setLayoutParams(lp);
//					int attachmentId = arr.getJSONObject(i).getInteger("attachmentid");
//					try {
//						URL url = new URL(Api.getInstance().getAttachmentImgUrl(attachmentId));
//						imgWithCache.setImageUrl(url, Api.getInstance().getCookieStorage().getCookies());
//					} catch (MalformedURLException e) {
//						// TODO è‡ªåŠ¨ç”Ÿæˆ�çš„ catch å�—
//						e.printStackTrace();
//					}
//					attachmentList.addView(imgWithCache);
//				}
//			}
//			
//			//å…¶ä»–ç±»åž‹é™„ä»¶å¤„ç�†
//			attachmentList = (LinearLayout)convertView.findViewById(R.id.showThreadOtherAttachmentList);
//			if (item.containsKey("otherattachments")) {
//				JSONArray arr = item.getJSONArray("otherattachments");
//				attachmentView.setVisibility(View.VISIBLE);
//				attachmentList.removeAllViews();
//				
//				for (int i = 0; i < arr.size(); i++) {
//					TextView filename = new TextView(ShowThreadPage.this);
//					filename.setLayoutParams(lp);
//					filename.setText(arr.getJSONObject(i).getString("filename"));
//					attachmentList.addView(filename);
//				}
//			}
//			
//			return convertView;
//		}
	}
	

}
