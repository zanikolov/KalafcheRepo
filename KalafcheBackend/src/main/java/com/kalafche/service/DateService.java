package com.kalafche.service;

import com.kalafche.model.TodayInMillis;

public interface DateService {

	public long getCurrentMillisBGTimezone();

	public String convertMillisToDateTimeString(Long millis, String dateFormat, Boolean withTime);

	TodayInMillis getTodayInMillis();
}
