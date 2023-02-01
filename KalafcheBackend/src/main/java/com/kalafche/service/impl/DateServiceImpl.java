package com.kalafche.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.stereotype.Service;

import com.kalafche.model.TodayInMillis;
import com.kalafche.service.DateService;

@Service
public class DateServiceImpl implements DateService {

	@Override
	public long getCurrentMillisBGTimezone() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Sofia"));
		return cal.getTimeInMillis();
	}

	@Override
	public String convertMillisToDateTimeString(Long millis, String dateFormat, Boolean withTime) {
		Date date = new java.util.Date(millis);
		
		SimpleDateFormat sdf = null;
		if (withTime) {
			sdf = new java.text.SimpleDateFormat(dateFormat + " HH:mm");
		} else {
			sdf = new java.text.SimpleDateFormat(dateFormat);
		}
		
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/Sofia"));
		String formattedDate = sdf.format(date);
		
		return formattedDate;
	}
	
	@Override
	public TodayInMillis getTodayInMillis() {
		TodayInMillis todayInMillis = new TodayInMillis();
		Calendar today = Calendar.getInstance(TimeZone.getTimeZone("Europe/Sofia"));
		
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		todayInMillis.setStartDateTime(today.getTimeInMillis());
		
		today.set(Calendar.HOUR_OF_DAY, 23);
		today.set(Calendar.MINUTE, 59);
		today.set(Calendar.SECOND, 59);
		today.set(Calendar.MILLISECOND, 999);
		todayInMillis.setEndDateTime(today.getTimeInMillis());
		
		return todayInMillis;
	}
	
}
