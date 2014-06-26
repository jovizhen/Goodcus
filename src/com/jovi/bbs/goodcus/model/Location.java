package com.jovi.bbs.goodcus.model;

public class Location
{
	String[] address;
	String name;
	String city;
	String postal_code;
	String state_code;
	
	public Location()
	{
	}

	public String[] getAddress()
	{
		return address;
	}

	public void setAddress(String[] address)
	{
		this.address = address;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getPostal_code()
	{
		return postal_code;
	}

	public void setPostal_code(String postal_code)
	{
		this.postal_code = postal_code;
	}

	public String getState_code()
	{
		return state_code;
	}

	public void setState_code(String state_code)
	{
		this.state_code = state_code;
	}
	
	

}
