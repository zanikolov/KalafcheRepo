package com.kalafche.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DayDto {

	private Integer id;
	private Integer day;
	private Integer month;
	private Integer year;
	private Integer dayOfTheWeek;
	private String date;
	private Boolean isHoliday;
	private String description;

	public DayDto() {
	}
	
	public DayDto(Integer day, Integer month, Integer year, Integer dayOfWeek, String date, Boolean isHoliday, String description) {
		this.day = day;
		this.month = month;
		this.year = year;
		this.dayOfTheWeek = dayOfWeek;
		this.date = date;
		this.isHoliday = isHoliday;
		this.description = description;
	}

}
