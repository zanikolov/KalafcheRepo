package com.kalafche.dao;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.model.schedule.WorkingShift;

public interface WorkingShiftDao {

	Integer insertWorkingShift(WorkingShift workingShift) throws SQLException;

	List<WorkingShift> selectAllWorkingShifts();

	void updateWorkingShift(WorkingShift workingShift);

	List<WorkingShift> selectWorkingShiftsWithoutLeaves();

}
