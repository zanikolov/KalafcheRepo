package com.kalafche.model;

import java.sql.Date;

public class DayDto {

	private Integer id;
	private Integer day;
	private Integer month;
	private Integer year;
	private Integer dayOfWeek;
	private Date date;
	private Boolean isHoliday;

	public DayDto() {
	}	
	
	public DayDto(Integer day, Integer month, Integer year, Integer dayOfWeek, Date date, Boolean isHoliday) {
		this.day = day;
		this.month = month;
		this.year = year;
		this.dayOfWeek = dayOfWeek;
		this.date = date;
		this.isHoliday = isHoliday;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(Integer dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Boolean getIsHoliday() {
		return isHoliday;
	}

	public void setIsHoliday(Boolean isHoliday) {
		this.isHoliday = isHoliday;
	}

}
