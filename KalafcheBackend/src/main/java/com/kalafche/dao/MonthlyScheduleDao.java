package com.kalafche.dao;

import java.sql.SQLException;

import com.kalafche.model.MonthlySchedule;

public interface MonthlyScheduleDao {

	Integer insertMonthlySchedule(MonthlySchedule monthlySchedule) throws SQLException;
	
	MonthlySchedule getMonthlySchedule(Integer storeId, Integer month, Integer year);

	void updateDailyStoreReport(MonthlySchedule monthlySchedule);

}
