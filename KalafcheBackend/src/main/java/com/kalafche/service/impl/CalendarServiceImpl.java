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
import com.kalafche.service.DateService;

@Service
public class CalendarServiceImpl implements CalendarService {

	@Autowired
	CalendarDao calendarDao;

	@Autowired
	DateService dateService;

	@Scheduled(cron = "0 30 22 25 * *", zone = "EET")
	private void insertCalendarDays() {
		calendarDao.insertDays(generateDaysForDBCalendar(13, 1));
	}	

	private List<DayDto> generateDaysForDBCalendar(Integer offsetInMonths, Integer periodInMonths) {
		ZoneId zoneId = ZoneId.of("Europe/Sofia");
		ZonedDateTime now = ZonedDateTime.of(LocalDateTime.now(), zoneId);
		ZonedDateTime dateOfFirstDayOfMonth;

		List<DayDto> days = new ArrayList<DayDto>();
		for (int month = 0; month < periodInMonths; month++) {
			dateOfFirstDayOfMonth = now.plusMonths(offsetInMonths + month).withDayOfMonth(1);
			YearMonth yearMonth = YearMonth.of(dateOfFirstDayOfMonth.getYear(), dateOfFirstDayOfMonth.getMonth());
			int daysInMonth = yearMonth.lengthOfMonth();

			for (int day = 1; day <= daysInMonth; day++) {
				Integer dayOfTheWeek = dateOfFirstDayOfMonth.withDayOfMonth(day).getDayOfWeek().getValue();
				String displayDate = dateService.generateDisplayDate(day, dateOfFirstDayOfMonth.getMonthValue(),
						dateOfFirstDayOfMonth.getYear(), dayOfTheWeek);
				DayDto dayDto = new DayDto(day, dateOfFirstDayOfMonth.getMonthValue(), dateOfFirstDayOfMonth.getYear(),
						dayOfTheWeek, displayDate, false);
				days.add(dayDto);
			}
		}

		return days;
	}
	
	@Override
	public List<Integer> getDayIdsByMonthAndYear(Integer month, Integer year) {
		return calendarDao.getDayIdsByMonthAndYear(month, year);
	}

	@Override
	public List<DayDto> getDaysByMonthAndYear(Integer month, Integer year) {
		return calendarDao.getDaysByMonthAndYear(month, year);
	}
	
}
