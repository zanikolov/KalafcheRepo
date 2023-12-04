package com.kalafche.service;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.model.DailyShift;
import com.kalafche.model.EmployeeHours;

public interface DailyShiftService {

	Integer createDailyShift(DailyShift dailyShift) throws SQLException;

	List<DailyShift> getDailyShiftByMonthlyScheduleId(Integer monthlyScheduleId);

	List<EmployeeHours> updateDailyShift(DailyShift dailyShift);

	void createDailyShiftBatch(List<DailyShift> dailyShifts);

}
