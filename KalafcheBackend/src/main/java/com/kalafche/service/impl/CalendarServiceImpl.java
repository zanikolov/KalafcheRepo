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

	@Scheduled(cron = "0 0 22 * * *", zone = "EET")
	public void insertCalendarDays() {
		calendarDao.insertDays(generateDaysForDBCalendar(1, 2));
	}	

	
	public List<DayDto> generateDaysForDBCalendar(Integer offsetInMonths, Integer periodInMonths) {
		ZoneId zoneId = ZoneId.of("Europe/Sofia");
		ZonedDateTime now = ZonedDateTime.of(LocalDateTime.now(), zoneId);
		ZonedDateTime dateOfFirstDayOfMonth;

		List<DayDto> days = new ArrayList<DayDto>();
		for (int i = 0; i < periodInMonths; i++) {
			dateOfFirstDayOfMonth = now.plusMonths(offsetInMonths + i).withDayOfMonth(1);
			YearMonth yearMonth = YearMonth.of(dateOfFirstDayOfMonth.getYear(), dateOfFirstDayOfMonth.getMonth());
			int daysInMonth = yearMonth.lengthOfMonth();

			for (int y = 1; y <= daysInMonth; y++) {
				DayDto day = new DayDto(y, dateOfFirstDayOfMonth.getMonthValue(), dateOfFirstDayOfMonth.getYear(),
						dateOfFirstDayOfMonth.getDayOfWeek().getValue(), null, false);
				days.add(day);
			}
		}
		
		return days;
	}
	
}
