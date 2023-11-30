package com.kalafche.service.impl;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.model.WorkingShift;

public interface WorkingShiftDao {

	Integer insertWorkingShift(WorkingShift workingShift) throws SQLException;

	List<WorkingShift> selectAllWorkingShifts();

	void updateWorkingShift(WorkingShift workingShift);

}
