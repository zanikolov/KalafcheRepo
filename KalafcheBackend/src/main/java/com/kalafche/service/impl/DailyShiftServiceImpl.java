package com.kalafche.service.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kalafche.dao.DailyShiftDao;
import com.kalafche.model.DailyShift;
import com.kalafche.model.EmployeeHours;
import com.kalafche.model.MonthlySchedule;
import com.kalafche.service.DailyShiftService;
import com.kalafche.service.DateService;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.MonthlyScheduleService;

@Service
public class DailyShiftServiceImpl implements DailyShiftService {

	@Autowired
	DailyShiftDao dailyShiftDao;
	
	@Autowired
	EmployeeService employeeService;
	
	@Autowired
	DateService dateService;
	
	@Autowired
	MonthlyScheduleService monthlyScheduleService;
	
	@Override
	public Integer createDailyShift(DailyShift dailyShift) throws SQLException {
		return dailyShiftDao.insertDailyShift(dailyShift);
	}
	
	@Override
	public List<DailyShift> getDailyShiftByMonthlyScheduleId(Integer monthlyScheduleId) {
		return dailyShiftDao.getDailyShiftByMonthlyScheduleId(monthlyScheduleId);
	}

	@Override
	public List<EmployeeHours> updateDailyShift(DailyShift dailyShift) {
		MonthlySchedule monthlySchedule = monthlyScheduleService.getMonthlyScheduleById(dailyShift.getMonthlyScheduleId());
		if (monthlySchedule != null && !monthlySchedule.getIsFinalized()) {
			dailyShift.setLastUpdateTimestamp(dateService.getCurrentMillisBGTimezone());
			dailyShift.setUpdatedByEmployeeId(employeeService.getLoggedInEmployee().getId());
			dailyShiftDao.updateDailyShift(dailyShift);
		}
		
		return monthlyScheduleService.getEmployeeHours(dailyShift.getMonthlyScheduleId(), monthlySchedule.getWorkingHoursInMinutes());
	}

	@Override
	public void createDailyShiftBatch(List<DailyShift> dailyShifts) {
		dailyShiftDao.createDailyShiftBatch(dailyShifts);
	}

}
