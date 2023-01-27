package com.kalafche.service;

public interface DateService {

	public long getCurrentMillisBGTimezone();

	public String convertMillisToDateTimeString(Long millis, String dateFormat, Boolean withTime);
}
