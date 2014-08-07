package com.jovi.bbs.goodcus.model;


public class ReviewRecord
{
	private String review;
	private String businessId;
	private String googlePlusUserId;
	private ApplicationUser user;
	
	public String getReview()
	{
		return review;
	}
	
	public void setReview(String review)
	{
		this.review = review;
	}
	
	public String getBusinessId()
	{
		return businessId;
	}
	
	public void setBusinessId(String businessId)
	{
		this.businessId = businessId;
	}
	
	public String getGooglePlusUserId()
	{
		return googlePlusUserId;
	}
	
	public void setGooglePlusUserId(String googlePlusUserId)
	{
		this.googlePlusUserId = googlePlusUserId;
	}

	public ApplicationUser getUser()
	{
		return user;
	}

	public void setUser(ApplicationUser user)
	{
		this.user = user;
	}

	
}
