package com.jovi.bbs.goodcus.model;

public class ApplicationUser
{
	private String userID;
	private String displayName;
	private String email;
	private String imgUrl;
	
	public ApplicationUser()
	{
	}
	
	

	public ApplicationUser(String userID, String displayName, String email, String imgUrl)
	{
		this.userID = userID;
		this.displayName = displayName;
		this.email = email;
		this.imgUrl = imgUrl;
	}



	public String getUserID()
	{
		return userID;
	}

	public void setUserID(String userID)
	{
		this.userID = userID;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getImgUrl()
	{
		return imgUrl;
	}

	public void setImgUrl(String imgUrl)
	{
		this.imgUrl = imgUrl;
	}
	


}
