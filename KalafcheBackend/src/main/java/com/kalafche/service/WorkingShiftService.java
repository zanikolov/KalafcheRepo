package com.kalafche.service;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.model.WorkingShift;

public interface WorkingShiftService {

	WorkingShift createWorkingShift(WorkingShift workingShift) throws SQLException;

	void updateWorkingShift(WorkingShift workingShift) throws SQLException;

	List<WorkingShift> getWorkingShifts();
	
	List<WorkingShift> getWorkingShiftsForLegendTableInExcelReport();

}
