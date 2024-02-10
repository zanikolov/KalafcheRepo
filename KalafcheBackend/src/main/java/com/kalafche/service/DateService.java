package com.kalafche.service;

import java.util.Date;

import com.kalafche.model.DayInMillis;

public interface DateService {

	public long getCurrentMillisBGTimezone();

	public String convertMillisToDateTimeString(Long millis, String dateFormat, Boolean withTime);

	DayInMillis getTodayInMillis(Integer dayShift);

	Date getCurrentTimeBGTimezone();

	String generateDisplayDate(Integer day, Integer month, Integer year, Integer dayOfTheWeekNumber);

	String convertMinutesToTime(Integer minutes);

	public Integer getCurrentYear();

	public Integer getNextYear();
}
