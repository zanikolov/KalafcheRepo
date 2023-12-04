package com.kalafche.service.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.kalafche.dao.WorkingShiftDao;
import com.kalafche.model.WorkingShift;
import com.kalafche.service.DateService;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.WorkingShiftService;

@Service
public class WorkingShiftServiceImpl implements WorkingShiftService {

	@Autowired
	WorkingShiftDao workingShiftDao;

	@Autowired
	DateService dateService;

	@Autowired
	EmployeeService employeeService;

	@Override
	public WorkingShift createWorkingShift(WorkingShift workingShift) throws SQLException {
		workingShift.setCreateTimestamp(dateService.getCurrentMillisBGTimezone());
		workingShift.setCreatedByEmployeeId(employeeService.getLoggedInEmployee().getId());

		if (workingShift.getStartTimeMinutes() != null) {
			workingShift.setStartTime(dateService.convertMinutesToTime(workingShift.getStartTimeMinutes()));
		}

		if (workingShift.getEndTimeMinutes() != null) {
			workingShift.setEndTime(dateService.convertMinutesToTime(workingShift.getEndTimeMinutes()));
		}
		
		if (workingShift.getDurationMinutes() != null) {
			workingShift.setDuration(dateService.convertMinutesToTime(workingShift.getDurationMinutes()));
		}
		
		Integer workingShiftId = workingShiftDao.insertWorkingShift(workingShift);
		workingShift.setId(workingShiftId);

		return workingShift;
	}

	public List<WorkingShift> getWorkingShifts() {
		return workingShiftDao.selectAllWorkingShifts();
	}

	@Override
	public void updateWorkingShift(WorkingShift workingShift) {
		if (!StringUtils.isEmpty(workingShift.getCode()) 
				&& !StringUtils.isEmpty(workingShift.getStartTimeMinutes())
				&& !StringUtils.isEmpty(workingShift.getEndTimeMinutes())
				&& !StringUtils.isEmpty(workingShift.getDurationMinutes())) {
			workingShift.setStartTime(dateService.convertMinutesToTime(workingShift.getStartTimeMinutes()));
			workingShift.setEndTime(dateService.convertMinutesToTime(workingShift.getEndTimeMinutes()));
			workingShift.setDuration(dateService.convertMinutesToTime(workingShift.getDurationMinutes()));
			
			workingShiftDao.updateWorkingShift(workingShift);
		}
	}

}
