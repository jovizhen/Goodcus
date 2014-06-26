package com.jovi.bbs.goodcus.net;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.jovi.bbs.goodcus.model.YelpFilter;

public class Yelp
{
	OAuthService service;
	Token accessToken;
	private static String CONSUMER_KEY = "zN1QEdhNjM4EfiffMe2F8g";
	private static String CONSUMER_SECRET = "RMB2IV6kTcbHJC__Lx7wITdH1tg";
	private static String TOKEN = "0Z81hbDeLe6OqLgtmr2-WjDAQi7qrevU";
	private static String TOKEN_SECRET = "VwbjCfYuMhpD7PXXG77D1kvEAcA";
	public static final int NET_SUCCESS = 1;
	public static final int NET_TIMEOUT = 2;
	public static final int NET_FAILED  = 3;
	
	/**
	 * Setup the Yelp API OAuth credentials.
	 * 
	 * OAuth credentials are available from the developer site, under Manage API
	 * access (version 2 API).
	 * 
	 * @param consumerKey
	 *            Consumer key
	 * @param consumerSecret
	 *            Consumer secret
	 * @param token
	 *            Token
	 * @param tokenSecret
	 *            Token secret
	 */
	public Yelp(String consumerKey, String consumerSecret, String token, String tokenSecret)
	{
		this.service = new ServiceBuilder().provider(YelpApi2.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
		this.accessToken = new Token(token, tokenSecret);
	}

	/**
	 * Search with term and location.
	 * 
	 * @param term
	 *            Search term
	 * @param latitude
	 *            Latitude
	 * @param longitude
	 *            Longitude
	 * @return JSON string response
	 */
	public Response search(YelpFilter filter)
	{
		OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
		request.addQuerystringParameter("term", filter.getTerm());
		if (filter.getLatitude() != null && filter.getLongitude() != null)
		{
			request.addQuerystringParameter("ll", filter.getLatitude() + "," + filter.getLongitude());
		}
		if(filter.getLimit()!=null)
		{
			request.addQuerystringParameter("limit", filter.getLimit().toString());
		}
		this.service.signRequest(this.accessToken, request);
		Response response = request.send();
		return response;
	}

	  // CLI
//	public static void main(String[] args)
//	{
//		// Update tokens here from Yelp developers site, Manage API access.
//		String consumerKey = "";
//		String consumerSecret = "";
//		String token = "";
//		String tokenSecret = "";
//
//		Yelp yelp = new Yelp(consumerKey, consumerSecret, token, tokenSecret);
//		String response = yelp.search("burritos", 30.361471, -87.164326);
//
//		System.out.println(response);
//	}

	private static Yelp instance;

	public static Yelp getInstance()
	{
		if (instance == null)
		{
			instance = new Yelp(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
		}

		return instance;
	}

}
