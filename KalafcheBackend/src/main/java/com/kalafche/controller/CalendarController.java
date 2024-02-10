package com.kalafche.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.model.DayDto;
import com.kalafche.service.CalendarService;

@CrossOrigin
@RestController
@RequestMapping({ "/calendar" })
public class CalendarController {
	
	@Autowired
	CalendarService calendarService;

	@GetMapping("/publicHoliday")
	public List<DayDto> getPublicHolidays() {
		return calendarService.getPublicHolidays();
	}
	
}
