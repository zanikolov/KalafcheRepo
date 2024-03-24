package com.kalafche.service;

import java.util.List;

import com.kalafche.model.ProfitReport;

public interface ProfitReportService {

	List<ProfitReport> generateProfitReport(Long startDateMilliseconds, Long endDateMilliseconds);

}
