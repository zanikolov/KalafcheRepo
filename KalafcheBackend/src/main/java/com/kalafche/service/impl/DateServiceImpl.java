package com.kalafche.service.impl;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.springframework.stereotype.Service;

import com.kalafche.model.PeriodInMillis;
import com.kalafche.service.DateService;

@Service
public class DateServiceImpl implements DateService {

	@Override
	public long getCurrentMillisBGTimezone() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Sofia"));
		return cal.getTimeInMillis();
	}
	
	@Override
	public Date getCurrentTimeBGTimezone() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Sofia"));
		return cal.getTime();
	}

	@Override
	public String convertMillisToDateTimeString(Long millis, String dateFormat, Boolean withTime) {
		Date date = new java.util.Date(millis);
		
		SimpleDateFormat sdf = null;
		if (withTime) {
			sdf = new java.text.SimpleDateFormat(dateFormat + " HH:mm");
		} else {
			sdf = new java.text.SimpleDateFormat(dateFormat);
		}
		
		sdf.setTimeZone(TimeZone.getTimeZone("Europe/Sofia"));
		String formattedDate = sdf.format(date);
		
		return formattedDate;
	}
	
	@Override
	public PeriodInMillis getTodayInMillis(Integer dayShift) {
		PeriodInMillis dayInMillis = new PeriodInMillis();
		Calendar day = Calendar.getInstance(TimeZone.getTimeZone("Europe/Sofia"));
		day.add(Calendar.DATE, dayShift);
		
		day.set(Calendar.HOUR_OF_DAY, 0);
		day.set(Calendar.MINUTE, 0);
		day.set(Calendar.SECOND, 0);
		day.set(Calendar.MILLISECOND, 0);
		dayInMillis.setStartDateTime(day.getTimeInMillis());
		
		day.set(Calendar.HOUR_OF_DAY, 23);
		day.set(Calendar.MINUTE, 59);
		day.set(Calendar.SECOND, 59);
		day.set(Calendar.MILLISECOND, 999);
		dayInMillis.setEndDateTime(day.getTimeInMillis());
		
		return dayInMillis;
	}
	
	@Override
	public PeriodInMillis getMonthInMillis(Integer monthShift) {
		PeriodInMillis monthInMillis = new PeriodInMillis();
		Calendar month = Calendar.getInstance(TimeZone.getTimeZone("Europe/Sofia"));
		month.add(Calendar.MONTH, monthShift);
		
		month.set(Calendar.DATE, 1);
		month.set(Calendar.HOUR_OF_DAY, 0);
		month.set(Calendar.MINUTE, 0);
		month.set(Calendar.SECOND, 0);
		month.set(Calendar.MILLISECOND, 0);
		monthInMillis.setStartDateTime(month.getTimeInMillis());
		
		month.set(Calendar.DATE, month.getActualMaximum(Calendar.DAY_OF_MONTH));
		month.set(Calendar.HOUR_OF_DAY, 23);
		month.set(Calendar.MINUTE, 59);
		month.set(Calendar.SECOND, 59);
		month.set(Calendar.MILLISECOND, 999);
		monthInMillis.setEndDateTime(month.getTimeInMillis());
		
		return monthInMillis;
	}
	
	@Override
	public PeriodInMillis getPeriodInMillis(Integer monthShift, Integer startDay, Integer endDay) {
		PeriodInMillis periodInMillis = new PeriodInMillis();
		Calendar month = Calendar.getInstance(TimeZone.getTimeZone("Europe/Sofia"));
		month.add(Calendar.MONTH, monthShift);
		
		if (startDay == null) {
			month.set(Calendar.DATE, 1);
		} else {
			month.set(Calendar.DATE, startDay);
		}
		month.set(Calendar.HOUR_OF_DAY, 0);
		month.set(Calendar.MINUTE, 0);
		month.set(Calendar.SECOND, 0);
		month.set(Calendar.MILLISECOND, 0);
		periodInMillis.setStartDateTime(month.getTimeInMillis());
		
		if (endDay == null || endDay > month.getActualMaximum(Calendar.DAY_OF_MONTH)) {			
			month.set(Calendar.DATE, month.getActualMaximum(Calendar.DAY_OF_MONTH));
		} else {
			month.set(Calendar.DATE, endDay);
		}
		month.set(Calendar.HOUR_OF_DAY, 23);
		month.set(Calendar.MINUTE, 59);
		month.set(Calendar.SECOND, 59);
		month.set(Calendar.MILLISECOND, 999);
		periodInMillis.setEndDateTime(month.getTimeInMillis());
		
		return periodInMillis;
	}
	
	@Override
	public String generateDisplayDate(Integer day, Integer month, Integer year, Integer dayOfTheWeek) {
		StringBuilder dateString = new StringBuilder()
				.append(String.format("%02d", day)).append('/')
				.append(String.format("%02d", month)).append('/')
				.append(year).append(' ')
				.append(DayOfWeek.of(dayOfTheWeek).getDisplayName(TextStyle.SHORT_STANDALONE, new Locale("bg", "BG")));

		return dateString.toString();
	}
	
	@Override
	public String convertMinutesToTime(Integer minutes) {
		return new StringBuilder().append(String.format("%02d", minutes / 60)).append(':').append(String.format("%02d", minutes % 60)).toString();
	}

	@Override
	public Integer getCurrentYear() {
		return LocalDateTime.now(ZoneId.of("Europe/Sofia")).getYear();
	}

	@Override
	public Integer getNextYear() {
		return getCurrentYear() + 1;
	}

	@Override
	public long getSameDayPrevYearInMillisBGTimezone(long millisTimestamp) {
        Instant instant = Instant.ofEpochMilli(millisTimestamp);      
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("Europe/Sofia"));     
        zonedDateTime = zonedDateTime.minusYears(1);
        
        return zonedDateTime.toEpochSecond() * 1000;
	}
	
}
