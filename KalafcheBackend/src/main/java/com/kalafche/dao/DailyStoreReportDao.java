package com.kalafche.dao;

import java.util.List;

import com.kalafche.model.DailyStoreReport;
import com.kalafche.model.DayInMillis;

public interface DailyStoreReportDao {

	void insertDailyStoreReport(DailyStoreReport dailyStoreReport);
	
	DailyStoreReport getDailyStoreReport(Integer storeId, DayInMillis day);

	List<DailyStoreReport> searchDailyStoreReports(Long startDateMilliseconds, Long endDateMilliseconds,
			String storeIds);

	List<DailyStoreReport> searchDailyCompanyReports(Long startDateMilliseconds, Long endDateMilliseconds,
			Integer companyId);

	void updateDailyStoreReport(DailyStoreReport dailyStoreReport);

}
