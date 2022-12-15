package com.kalafche.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.stereotype.Service;

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
	
}
