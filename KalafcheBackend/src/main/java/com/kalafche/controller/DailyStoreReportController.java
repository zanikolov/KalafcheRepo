package com.kalafche.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.model.DailyStoreReport;
import com.kalafche.service.DailyStoreReportService;

@CrossOrigin
@RestController
@RequestMapping({ "/dailyStoreReport" })
public class DailyStoreReportController {
	
	@Autowired
	private DailyStoreReportService dailyStoreReportService;
	
	@PutMapping
	public DailyStoreReport generateDailyStoreReport() throws SQLException {
		return dailyStoreReportService.generateDailyStoreReport();
	}
	
	@GetMapping("/{dailyStoreReportId}")
	public DailyStoreReport getRevision(@PathVariable(value = "dailyStoreReportId") Integer dailyStoreReportId) {
		return dailyStoreReportService.getDailyStoreReportById(dailyStoreReportId);
	}
	
	@GetMapping("/{day}")
	public DailyStoreReport getRevision(@PathVariable(value = "day") Long day) {
		return dailyStoreReportService.getDailyStoreReportByDay(day);
	}
	
	@GetMapping("/current/{storeId}")
	public DailyStoreReport calculateDailyStoreReport(@PathVariable(value = "storeId") Integer storeId) {
		return dailyStoreReportService.calculateDailyStoreReport(storeId);
	}
	
	@GetMapping
	public List<DailyStoreReport> searchDailyStoreReports(@RequestParam(value = "startDateMilliseconds") Long startDateMilliseconds, 
			@RequestParam(value = "endDateMilliseconds") Long endDateMilliseconds, @RequestParam(value = "storeId") Integer storeId) {
		return dailyStoreReportService.searchDailyStoreReports(startDateMilliseconds, endDateMilliseconds, storeId);
	}
	
}