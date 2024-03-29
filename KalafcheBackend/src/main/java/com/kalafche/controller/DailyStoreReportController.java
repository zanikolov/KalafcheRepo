package com.kalafche.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.exceptions.CommonException;
import com.kalafche.model.DailyStoreReport;
import com.kalafche.service.DailyStoreReportService;

@CrossOrigin
@RestController
@RequestMapping({ "/dailyStoreReport" })
public class DailyStoreReportController {
	
	@Autowired
	private DailyStoreReportService dailyStoreReportService;
	
	@PutMapping("/{storeId}")
	public DailyStoreReport finalizeDailyStoreReport(@PathVariable(value = "storeId") Integer storeId) throws SQLException {
		return dailyStoreReportService.finalizeDailyStoreReport(storeId);
	}
	
	@GetMapping("/{dailyStoreReportId}")
	public DailyStoreReport getDailyStoreReport(@PathVariable(value = "dailyStoreReportId") Integer dailyStoreReportId) {
		return dailyStoreReportService.getDailyStoreReportById(dailyStoreReportId);
	}
	
	@GetMapping("/current/{storeId}")
	public DailyStoreReport calculateDailyStoreReport(@PathVariable(value = "storeId") Integer storeId) {
		return dailyStoreReportService.calculateDailyStoreReport(storeId, false);
	}
	
	@GetMapping
	public List<DailyStoreReport> searchDailyStoreReports(@RequestParam(value = "startDateMilliseconds") Long startDateMilliseconds, 
			@RequestParam(value = "endDateMilliseconds") Long endDateMilliseconds, @RequestParam(value = "storeIds") String storeIds) {
		return dailyStoreReportService.searchDailyStoreReports(startDateMilliseconds, endDateMilliseconds, storeIds);
	}
	
	@GetMapping("/correction")
	public List<DailyStoreReport> searchDailyStoreReportsForCorrection(@RequestParam(value = "startDateMilliseconds") Long startDateMilliseconds, 
			@RequestParam(value = "endDateMilliseconds") Long endDateMilliseconds, @RequestParam(value = "storeIds") String storeIds) throws CommonException {
		return dailyStoreReportService.searchDailyStoreReportsForCorrection(startDateMilliseconds, endDateMilliseconds, storeIds);
	}
	
	@GetMapping("/companies")
	public List<DailyStoreReport> searchDailyCompanyReports(@RequestParam(value = "startDateMilliseconds") Long startDateMilliseconds, 
			@RequestParam(value = "endDateMilliseconds") Long endDateMilliseconds, @RequestParam(value = "companyId") Integer companyId) {
		return dailyStoreReportService.searchDailyCompanyReports(startDateMilliseconds, endDateMilliseconds, companyId);
	}
	
	@GetMapping("/checkFinalization/{storeId}")
	public Boolean canDailyStoreReportBeFinalized(@PathVariable(value = "storeId") Integer storeId) {
		return dailyStoreReportService.canDailyStoreReportBeFinalized(storeId, false);
	}
	
	@PostMapping
	public void updateDailyStoreReport(@RequestBody DailyStoreReport dailyStoreReport) throws CommonException {
		dailyStoreReportService.updateDailyStoreReport(dailyStoreReport);
	}
	
}