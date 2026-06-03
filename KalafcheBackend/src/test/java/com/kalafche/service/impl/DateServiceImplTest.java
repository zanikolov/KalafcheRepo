package com.kalafche.service.impl;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.Test;

public class DateServiceImplTest {

	private DateServiceImpl dateService = new DateServiceImpl();

	@Test
	public void testEndOfDayInMillisBGTimezoneReturnsLastMillisecondOfTheSameDay() {
		long inputTimestamp = LocalDateTime.of(2026, 5, 26, 14, 30, 12, 123000000)
				.atZone(ZoneId.of("Europe/Sofia"))
				.toInstant()
				.toEpochMilli();
		long expectedTimestamp = LocalDateTime.of(2026, 5, 26, 23, 59, 59, 999000000)
				.atZone(ZoneId.of("Europe/Sofia"))
				.toInstant()
				.toEpochMilli();

		assertEquals(expectedTimestamp, dateService.endOfDayInMillisBGTimezone(inputTimestamp));
	}
}
