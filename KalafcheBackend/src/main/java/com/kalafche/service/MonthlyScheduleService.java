package com.kalafche.service;

import java.sql.SQLException;

import com.kalafche.model.MonthlySchedule;

public interface MonthlyScheduleService {

	MonthlySchedule generateMonthlySchedule(Integer storeId, Integer month, Integer year) throws SQLException;

	MonthlySchedule getMonthlySchedule(Integer storeId, Integer month, Integer year);

}
