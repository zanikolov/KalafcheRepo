package com.kalafche.service;

import java.util.List;

import com.kalafche.model.DailyStoreReport;
import com.kalafche.model.DayInMillis;

public interface DailyStoreReportService {

	DailyStoreReport generateDailyStoreReport();

	DailyStoreReport getDailyStoreReportById(Integer dailyStoreReportId);

	DailyStoreReport getDailyStoreReportByDay(Integer storeId, DayInMillis day);

	DailyStoreReport calculateDailyStoreReport(Integer storeId);

	List<DailyStoreReport> searchDailyStoreReports(Long startDateMilliseconds, Long endDateMilliseconds,
			Integer storeId);

}
