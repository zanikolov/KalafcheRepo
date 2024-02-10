package com.kalafche.service;

import java.util.List;

import com.kalafche.model.DayDto;

public interface CalendarService {

	List<Integer> getDayIdsByMonthAndYear(Integer month, Integer year);
	
	List<DayDto> getDaysByMonthAndYear(Integer month, Integer year);

	List<DayDto> getPublicHolidays();

	Integer getWorkingHoursInMinutesForMonth(Integer month, Integer year);
}
