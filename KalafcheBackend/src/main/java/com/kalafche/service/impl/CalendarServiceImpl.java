package com.kalafche.service.impl;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.kalafche.dao.CalendarDao;
import com.kalafche.model.DayDto;
import com.kalafche.service.CalendarService;

@Service
public class CalendarServiceImpl implements CalendarService {

	@Autowired
	CalendarDao calendarDao;

	@Scheduled(cron = "0 30 22 25 * *", zone = "EET")
	public void insertCalendarDays() {
		calendarDao.insertDays(generateDaysForDBCalendar(13, 1));
	}	

	public List<DayDto> generateDaysForDBCalendar(Integer offsetInMonths, Integer periodInMonths) {
		ZoneId zoneId = ZoneId.of("Europe/Sofia");
		ZonedDateTime now = ZonedDateTime.of(LocalDateTime.now(), zoneId);
		ZonedDateTime dateOfFirstDayOfMonth;

		List<DayDto> days = new ArrayList<DayDto>();
		for (int month = 0; month < periodInMonths; month++) {
			dateOfFirstDayOfMonth = now.plusMonths(offsetInMonths + month).withDayOfMonth(1);
			YearMonth yearMonth = YearMonth.of(dateOfFirstDayOfMonth.getYear(), dateOfFirstDayOfMonth.getMonth());
			int daysInMonth = yearMonth.lengthOfMonth();

			for (int day = 1; day <= daysInMonth; day++) {
				DayDto dayDto = new DayDto(day, dateOfFirstDayOfMonth.getMonthValue(), dateOfFirstDayOfMonth.getYear(),
						dateOfFirstDayOfMonth.withDayOfMonth(day).getDayOfWeek().getValue(), null, false);
				days.add(dayDto);
			}
		}
		
		return days;
	}
	
}
