package com.kalafche.dao;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.model.schedule.EmployeeHours;
import com.kalafche.model.schedule.MonthlySchedule;

public interface MonthlyScheduleDao {

	Integer insertMonthlySchedule(MonthlySchedule monthlySchedule) throws SQLException;
	
	MonthlySchedule getMonthlySchedule(Integer storeId, Integer month, Integer year, Boolean isPresentForm);

	void updateMonthlySchedule(MonthlySchedule monthlySchedule);

	List<EmployeeHours> getEmployeeHoursByMonthlyScheduleId(Integer monthlyScheduleId);

	MonthlySchedule getMonthlyScheduleById(Integer monthlyScheduleId);

	List<MonthlySchedule> getMonthlySchedules(Integer month, Integer year, boolean isPresentForm, boolean isFinalized);

}
