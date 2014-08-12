package com.jovi.bbs.goodcus.model;


public class YelpFilter
{
	public static final int SORT_BEST_MATCH = 0;
	public static final int SORT_DISTANCE = 1;
	public static final int SORT_HIGHEST_RATE = 3;
	
	
	String term;
	Double latitude;
	Double longitude;
	Integer limit;
	Integer offset;
	Double radius;
	Integer sort;
	
	public static class FilterBuilder
	{
		String term;
		Double latitude;
		Double longitude;
		Integer limit;
		Integer offset;
		Double radius;
		Integer sort;

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
		
		public FilterBuilder setOffset(Integer offset)
		{
			this.offset = offset;
			return this;
		}
		
		public FilterBuilder setRadius(Double radius)
		{
			this.radius = radius;
			return this;
		}
		
		public FilterBuilder setSort(Integer sort)
		{
			this.sort = sort;
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
		this.limit = builder.limit;
		this.radius = builder.radius;
		this.sort = builder.sort;
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

	public Integer getOffset()
	{
		return offset;
	}

	public void setOffset(Integer offset)
	{
		this.offset = offset;
	}

	public Double getRadius()
	{
		return radius;
	}

	public void setRadius(Double radius)
	{
		this.radius = radius;
	}

	public Integer getSort()
	{
		return sort;
	}

	public void setSort(Integer sort)
	{
		this.sort = sort;
	}
}
