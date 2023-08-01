package com.kalafche.service;

import com.kalafche.model.DayInMillis;

public interface DateService {

	public long getCurrentMillisBGTimezone();

	public String convertMillisToDateTimeString(Long millis, String dateFormat, Boolean withTime);

	DayInMillis getTodayInMillis(Integer dayShift);
}
