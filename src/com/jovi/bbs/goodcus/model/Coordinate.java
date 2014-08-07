package com.jovi.bbs.goodcus.model;

public class Coordinate
{
	double latitude;
	double longitude;
	
	public Coordinate()
	{

	}

	public Coordinate(double latitude, double longtitude)
	{
		this.latitude = latitude;
		this.longitude = longtitude;
	}
	
	public double getLatitude()
	{
		return latitude;
	}
	
	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}
	
	public double getLongitude()
	{
		return longitude;
	}
	
	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}
}
