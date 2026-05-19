package com.kalafche.service;

import java.util.Date;

import com.kalafche.model.PeriodInMillis;

public interface DateService {

	public long getCurrentMillisBGTimezone();
	
	public long getSameDayPrevYearInMillisBGTimezone(long millis);

	public long addMonthsInMillisBGTimezone(long millis, int months);

	public String convertMillisToDateTimeString(Long millis, String dateFormat, Boolean withTime);

	public PeriodInMillis getTodayInMillis(Integer dayShift);

	public Date getCurrentTimeBGTimezone();

	public String generateDisplayDate(Integer day, Integer month, Integer year, Integer dayOfTheWeekNumber);

	public String convertMinutesToTime(Integer minutes);

	public Integer getCurrentYear();

	public Integer getNextYear();

	public PeriodInMillis getMonthInMillis(Integer monthShift);

	PeriodInMillis getPeriodInMillis(Integer monthShift, Integer startDay, Integer endDay);
}
