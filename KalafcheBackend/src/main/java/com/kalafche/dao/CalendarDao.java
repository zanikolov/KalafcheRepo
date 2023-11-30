package com.kalafche.dao;

import java.util.List;

import com.kalafche.model.DayDto;

public interface CalendarDao {
	
	public abstract void insertDays(List<DayDto> days);

}
