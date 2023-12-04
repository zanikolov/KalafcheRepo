package com.kalafche.dao;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.model.DailyShift;

public interface DailyShiftDao {

	Integer insertDailyShift(DailyShift dailyShift) throws SQLException;

	List<DailyShift> getDailyShiftByMonthlyScheduleId(Integer monthlyScheduleId);

	void updateDailyShift(DailyShift dailyShift);

	void createDailyShiftBatch(List<DailyShift> dailyShifts);

}
