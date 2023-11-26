package com.kalafche.service;

import java.util.List;

import com.kalafche.exceptions.CommonException;
import com.kalafche.model.DailyStoreReport;
import com.kalafche.model.DayInMillis;

public interface DailyStoreReportService {

	DailyStoreReport finalizeDailyStoreReport(Integer storeId);

	DailyStoreReport getDailyStoreReportById(Integer dailyStoreReportId);

	DailyStoreReport getDailyStoreReportByDay(Integer storeId, DayInMillis day);

	DailyStoreReport calculateDailyStoreReport(Integer storeId, Boolean scheduled);

	List<DailyStoreReport> searchDailyStoreReports(Long startDateMilliseconds, Long endDateMilliseconds,
			String storeIds);

	Boolean canDailyStoreReportBeFinalized(Integer storeId, Boolean scheduled);

	List<DailyStoreReport> searchDailyCompanyReports(Long startDateMilliseconds, Long endDateMilliseconds, Integer companyId);

	void updateDailyStoreReport(DailyStoreReport dailyStoreReport) throws CommonException;

	List<DailyStoreReport> searchDailyStoreReportsForCorrection(Long startDateMilliseconds, Long endDateMilliseconds, String storeIds) throws CommonException;

}
