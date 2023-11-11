package com.kalafche.service;

import java.util.Date;

import com.kalafche.model.DayInMillis;

public interface DateService {

	public long getCurrentMillisBGTimezone();

	public String convertMillisToDateTimeString(Long millis, String dateFormat, Boolean withTime);

	DayInMillis getTodayInMillis(Integer dayShift);

	Date getCurrentTimeBGTimezone();
}
