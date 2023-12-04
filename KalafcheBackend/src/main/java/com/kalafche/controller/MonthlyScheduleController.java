package com.kalafche.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.model.DailyShift;
import com.kalafche.model.EmployeeHours;
import com.kalafche.model.MonthlySchedule;
import com.kalafche.service.DailyShiftService;
import com.kalafche.service.MonthlyScheduleService;

@CrossOrigin
@RestController
@RequestMapping({ "/monthlySchedule" })
public class MonthlyScheduleController {

	@Autowired
	private MonthlyScheduleService monthlyScheduleService;

	@Autowired
	private DailyShiftService dailyShiftService;

	@PutMapping
	public MonthlySchedule generateMonthlySchedule(@RequestBody MonthlySchedule monthlySchedule)
			throws SQLException {
		return monthlyScheduleService.generateMonthlySchedule(monthlySchedule);
	}
	
	@PostMapping
	public void finalizeMonthlySchedule(@RequestBody MonthlySchedule monthlySchedule) throws SQLException {
		monthlyScheduleService.finalizeMonthlySchedule(monthlySchedule, false);
	}
	
	@PostMapping("/presentForm")
	public void finalizePresentForm(@RequestBody MonthlySchedule monthlySchedule) throws SQLException {
		monthlyScheduleService.finalizeMonthlySchedule(monthlySchedule, true);
	}
	
	@GetMapping
	public MonthlySchedule getMonthlySchedule(@RequestParam(value = "storeId") Integer storeId,
			@RequestParam(value = "month") Integer month, @RequestParam(value = "year") Integer year) {
		return monthlyScheduleService.getMonthlySchedule(storeId, month, year, false);
	}
	
	@GetMapping("/presentForm")
	public MonthlySchedule getPresentForm(@RequestParam(value = "storeId") Integer storeId,
			@RequestParam(value = "month") Integer month, @RequestParam(value = "year") Integer year) {
		return monthlyScheduleService.getMonthlySchedule(storeId, month, year, true);
	}
	
	@PostMapping("/dailyShift")
	public List<EmployeeHours> updateDailyShift(@RequestBody DailyShift dailyShift) {
		return dailyShiftService.updateDailyShift(dailyShift);
	}

}