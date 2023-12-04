package com.kalafche.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DayDto {

	private Integer id;
	private Integer day;
	private Integer month;
	private Integer year;
	private Integer dayOfWeek;
	private String date;
	private Boolean isHoliday;

	public DayDto() {
	}	
	
	public DayDto(Integer day, Integer month, Integer year, Integer dayOfWeek, String date, Boolean isHoliday) {
		this.day = day;
		this.month = month;
		this.year = year;
		this.dayOfWeek = dayOfWeek;
		this.date = date;
		this.isHoliday = isHoliday;
	}

}
