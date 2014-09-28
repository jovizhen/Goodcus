package com.jovi.bbs.goodcus.net.googlePlacesApi;

import java.util.ArrayList;

import com.jovi.bbs.goodcus.net.googlePlacesApi.GooglePlaces.Param;

public class GooglePlaceFilter
{

	ArrayList<Param> paramList = new ArrayList<Param>();
	
	//This must be specified as comma delimited "latitude,longitude"
	String location;
	Double lat;
	Double lng;
	
	//	Defines the distance (in meters) within which to return place results. 
	//	The maximum allowed radius is 50 000 meters. Note that radius must not 
	//	be included if rankby=distance (described under Optional parameters below) is specified.
	Double radius;
	

	Boolean opennow;
	//distance (When distance is specified, one or more of keyword, name, or types is required.)
	//prominence(default) rankby importance 
	String rankby;
	
	//Types should be separated with a pipe symbol (type1|type2|etc)
	String types;
	String keyword;
	
	// en = English, zh-CN = Chinese(Simplified), zh-TW = Chinese (Tranditional)
	String language;
	
	String pagetoken;
	
	//Restricts results to only those places within the specified range. Valid values range between 0
	//	(most affordable) to 4 (most expensive), inclusive. The exact amount indicated by a specific value 
	//	will vary from region to region.
	Integer minprice;
	Integer maxprice;
	
	public ArrayList<Param> getParamList()
	{
		return paramList;
	}
	
	public void setParamList(ArrayList<Param> paramList)
	{
		this.paramList = paramList;
	}
	
	public String getLocation()
	{
		return location;
	}
	
	public void setLocation(String location)
	{
		this.location = location;
	}
	
	public Double getLat()
	{
		return lat;
	}

	public void setLat(Double lat)
	{
		this.lat = lat;
	}

	public Double getLng()
	{
		return lng;
	}

	public void setLng(Double lng)
	{
		this.lng = lng;
	}

	public Double getRadius()
	{
		return radius;
	}
	
	public void setRadius(Double radius)
	{
		this.radius = radius;
	}
	
	
	public Boolean getOpennow()
	{
		return opennow;
	}

	public GooglePlaceFilter setOpennow(Boolean opennow)
	{
		this.opennow = opennow;
		Param param = searchParamByName("opennow");
		if(param == null)
		{
			paramList.add(Param.name("opennow").value(opennow));
		}
		else
		{
			param.setValue(opennow.toString());
		}
		return this;
	}

	public String getRankby()
	{
		return rankby;
	}
	
	public GooglePlaceFilter setRankby(String rankby)
	{
		this.rankby = rankby;
		Param param = searchParamByName("rankby");
		if(param == null)
		{
			paramList.add(Param.name("rankby").value(rankby));
		}
		else
		{
			param.setValue(rankby);
		}
		if(rankby.equals("distance"))
		{
			Param radiusParam = searchParamByName("radius");
			if(radiusParam!=null)
			{
				paramList.remove(radiusParam);
			}
		}
		return this;
	}
	
	public String getTypes()
	{
		return types;
	}
	
	public GooglePlaceFilter setTypes(String types)
	{
		this.types = types;
		Param param = searchParamByName("types");
		if(param == null)
		{
			paramList.add(Param.name("types").value(types));
		}
		else
		{
			param.setValue(types);
		}
		return this;
	}
	
	public String getKeyword()
	{
		return keyword;
	}
	
	public GooglePlaceFilter setKeyword(String keyword)
	{
		this.keyword = keyword;
		Param param = searchParamByName("keyword");
		if(param == null)
		{
			paramList.add(Param.name("keyword").value(keyword));
		}
		else
		{
			param.setValue(keyword);
		}
		return this;
	}
	
	public String getLanguage()
	{
		return language;
	}
	
	public GooglePlaceFilter setLanguage(String language)
	{
		this.language = language;
		Param param = searchParamByName("language");
		if(param == null)
		{
			paramList.add(Param.name("language").value(language));
		}
		else
		{
			param.setValue(language);
		}
		return this;
	}
	
	public String getPagetoken()
	{
		return pagetoken;
	}
	
	public GooglePlaceFilter setPagetoken(String pagetoken)
	{
		this.pagetoken = pagetoken;
		Param param = searchParamByName("pagetoken");
		if(param == null)
		{
			paramList.add(Param.name("pagetoken").value(pagetoken));
		}
		else
		{
			param.setValue(pagetoken);
		}
		return this;
	}
	
	public Integer getMinprice()
	{
		return minprice;
	}
	
	public GooglePlaceFilter setMinprice(Integer minprice)
	{
		this.minprice = minprice;
		Param param = searchParamByName("minprice");
		if(param == null)
		{
			paramList.add(Param.name("minprice").value(minprice));
		}
		else
		{
			param.setValue(minprice.toString());
		}
		return this;
	}
	
	public Integer getMaxprice()
	{
		return maxprice;
	}
	
	public GooglePlaceFilter setMaxprice(Integer maxprice)
	{
		this.maxprice = maxprice;
		Param param = searchParamByName("maxprice");
		if(param == null)
		{
			paramList.add(Param.name("maxprice").value(maxprice));
		}
		else
		{
			param.setValue(maxprice.toString());
		}
		return this;
	}
	
	public Param searchParamByName(String name)
	{
		if(paramList.size()!=0)
		{
			for(Param param : paramList)
			{
				if(param.getName().equals(name))
				{
					return param;
				}
			}
		}
		return null;
	}

}
