package com.kalafche.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.model.ProfitReport;
import com.kalafche.service.ProfitReportService;

@CrossOrigin
@RestController
@RequestMapping({ "/profit" })
public class ProfitReportController {

	@Autowired
	private ProfitReportService profitReportService;

	@GetMapping
	public List<ProfitReport> generateProfitReport(@RequestParam(value = "startDateMilliseconds") Long startDateMilliseconds,
			@RequestParam(value = "endDateMilliseconds") Long endDateMilliseconds) {
		return profitReportService.generateProfitReport(startDateMilliseconds, endDateMilliseconds);
	}
	
}
