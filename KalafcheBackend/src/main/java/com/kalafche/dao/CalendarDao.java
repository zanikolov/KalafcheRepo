package com.kalafche.dao;

import java.util.List;

import com.kalafche.model.DayDto;

public interface CalendarDao {
	
	public abstract void insertDays(List<DayDto> days);
	
	public abstract List<Integer> getDayIdsByMonthAndYear(Integer month, Integer year);

	public abstract List<DayDto> getDaysByMonthAndYear(Integer month, Integer year);

	public abstract void updateHoliday(DayDto dayDto);

	public abstract List<DayDto> getPublicHolidays(Integer currentYear);

	public abstract Integer getWorkingHoursForMonthInMinutes(Integer month, Integer year);

}
