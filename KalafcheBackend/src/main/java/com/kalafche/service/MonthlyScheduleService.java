package com.kalafche.service;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.exceptions.CommonException;
import com.kalafche.model.schedule.EmployeeHours;
import com.kalafche.model.schedule.MonthlySchedule;

public interface MonthlyScheduleService {

	MonthlySchedule getMonthlySchedule(Integer storeId, Integer month, Integer year, Boolean isPresentForm);

	MonthlySchedule generateMonthlySchedule(MonthlySchedule monthlySchedule) throws SQLException;

	void finalizeMonthlySchedule(MonthlySchedule monthlySchedule, Boolean isPresentForm) throws SQLException;

	MonthlySchedule getMonthlyScheduleById(Integer monthlyScheduleId);

	List<EmployeeHours> getEmployeeHours(Integer monthlyScheduleId, Integer monthlyScheduleWorkingHoursInMinutes);

	byte[] getMonthlyScheduleReport(Integer month, Integer year, Integer storeId);

	void addEmployeeToPresentForm(Integer presentFormId, Integer employeeId) throws SQLException, CommonException;

}
