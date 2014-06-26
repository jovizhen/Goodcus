package com.jovi.bbs.goodcus.model;

public class YelpFilter
{
	String term;
	Double latitude;
	Double longitude;
	Integer limit;

	public static class FilterBuilder
	{
		String term;
		Double latitude;
		Double longitude;
		Integer limit;

		public FilterBuilder(String term)
		{
			this.term = term;
		}

		public FilterBuilder setLatitude(double latitude)
		{
			this.latitude = latitude;
			return this;
		}

		public FilterBuilder setLongitude(double longitude)
		{
			this.longitude = longitude;
			return this;
		}
		
		public FilterBuilder setLimit(Integer limit)
		{
			this.limit = limit;
			return this;
		}

		public YelpFilter build()
		{
			return new YelpFilter(this);
		}
	}

	private YelpFilter(FilterBuilder builder)
	{
		this.term = builder.term;
		this.latitude = builder.latitude;
		this.longitude = builder.longitude;
	}

	public String getTerm()
	{
		return term;
	}

	public void setTerm(String term)
	{
		this.term = term;
	}

	public Double getLatitude()
	{
		return latitude;
	}

	public void setLatitude(Double latitude)
	{
		this.latitude = latitude;
	}

	public Double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(Double longitude)
	{
		this.longitude = longitude;
	}

	public Integer getLimit()
	{
		return limit;
	}

	public void setLimit(Integer limit)
	{
		this.limit = limit;
	}
}
