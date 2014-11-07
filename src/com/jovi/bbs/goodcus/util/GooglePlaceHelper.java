package com.jovi.bbs.goodcus.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import java.util.Locale;


import com.jovi.bbs.goodcus.net.googlePlacesApi.Day;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Hours;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Hours.Period;
import com.jovi.bbs.goodcus.net.googlePlacesApi.Price;

public class GooglePlaceHelper
{

	public static String getFormattedPrice(Price price)
	{
		if(price == Price.FREE)
			return "Free";
		else if(price == Price.INEXPENSIVE)
			return "$";
		else if(price == Price.MODERATE)
			return "$$";
		else if(price == Price.EXPENSIVE)
			return "$$$";
		else if(price == Price.VERY_EXPENSIVE)
			return "$$$$";
		else
			return "";
	}
	
	public static String getFormattedHours(Hours hours)
	{
		Day today = Day.values()[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1];
		StringBuilder sb = new StringBuilder();
		
		for(Period period : hours.getPeriods())
		{
			if(period.getOpeningDay() == today)
				sb.append(convertTo12HourFormat(period.getOpeningTime())).append(" -- ")
				.append(convertTo12HourFormat(period.getClosingTime())).append("\n");
		}
		return sb.toString();
	}
	
	private static String convertTo12HourFormat(String rowTime)
	{
		String time24Format = String.format("%s:%s", rowTime.substring(0,2), rowTime.substring(2));
		final SimpleDateFormat sdf = new SimpleDateFormat("H:mm", Locale.US);
		Date dateObj;
		String resultString = "";
		try
		{
			dateObj = sdf.parse(time24Format);
			resultString = new SimpleDateFormat("h:mm a", Locale.US).format(dateObj);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  resultString;
	}
}
