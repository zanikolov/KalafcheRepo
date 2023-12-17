package com.kalafche.service;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.model.EmployeeHours;
import com.kalafche.model.MonthlySchedule;

public interface MonthlyScheduleService {

	MonthlySchedule getMonthlySchedule(Integer storeId, Integer month, Integer year, Boolean isPresentForm);

	MonthlySchedule generateMonthlySchedule(MonthlySchedule monthlySchedule) throws SQLException;

	void finalizeMonthlySchedule(MonthlySchedule monthlySchedule, Boolean isPresentForm) throws SQLException;

	MonthlySchedule getMonthlyScheduleById(Integer monthlyScheduleId);

	List<EmployeeHours> getEmployeeHours(Integer monthlyScheduleId, Integer monthlyScheduleWorkingHoursInMinutes);

}
