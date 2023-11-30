package com.kalafche.controller;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.model.DailyStoreReport;
import com.kalafche.service.MonthlyScheduleService;

@CrossOrigin
@RestController
@RequestMapping({ "/monthlySchedule" })
public class MonthlyScheduleController {

	@Autowired
	private MonthlyScheduleService monthlyScheduleService;

	@PutMapping
	public DailyStoreReport generateMonthlySchedule(@RequestParam(value = "storeId") Integer storeId,
			@RequestParam(value = "month") Integer month, @RequestParam(value = "year") Integer year)
			throws SQLException {
		return monthlyScheduleService.generateMonthlySchedule(storeId, month, year);
	}

}