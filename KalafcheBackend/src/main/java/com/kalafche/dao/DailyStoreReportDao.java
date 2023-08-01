package com.kalafche.dao;

import com.kalafche.model.DailyStoreReport;
import com.kalafche.model.DayInMillis;

public interface DailyStoreReportDao {

	void insertDailyStoreReport(DailyStoreReport dailyStoreReport);
	
	DailyStoreReport getDailyStoreReport(Integer storeId, DayInMillis day);

}
