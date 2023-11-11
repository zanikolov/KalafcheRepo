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
	public DailyStoreReport finalizeDailyStoreReport() throws SQLException {
		return dailyStoreReportService.finalizeDailyStoreReport();
	}
	
	@GetMapping("/{dailyStoreReportId}")
	public DailyStoreReport getDailyStoreReport(@PathVariable(value = "dailyStoreReportId") Integer dailyStoreReportId) {
		return dailyStoreReportService.getDailyStoreReportById(dailyStoreReportId);
	}
	
	@GetMapping("/current/{storeId}")
	public DailyStoreReport calculateDailyStoreReport(@PathVariable(value = "storeId") Integer storeId) {
		return dailyStoreReportService.calculateDailyStoreReport(storeId);
	}
	
	@GetMapping
	public List<DailyStoreReport> searchDailyStoreReports(@RequestParam(value = "startDateMilliseconds") Long startDateMilliseconds, 
			@RequestParam(value = "endDateMilliseconds") Long endDateMilliseconds, @RequestParam(value = "storeIds") String storeIds) {
		return dailyStoreReportService.searchDailyStoreReports(startDateMilliseconds, endDateMilliseconds, storeIds);
	}
	
	@GetMapping("/checkFinalization/{storeId}")
	public Boolean isDailyStoreReportCanBeFinalized(@PathVariable(value = "storeId") Integer storeId) {
		return dailyStoreReportService.isDailyStoreReportCanBeFinalized(storeId);
	}
	
}