package com.kalafche.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kalafche.dao.MonthlyScheduleDao;
import com.kalafche.exceptions.DuplicationException;
import com.kalafche.model.DailyShift;
import com.kalafche.model.DayDto;
import com.kalafche.model.EmployeeHours;
import com.kalafche.model.MonthlySchedule;
import com.kalafche.service.CalendarService;
import com.kalafche.service.DailyShiftService;
import com.kalafche.service.DateService;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.MonthlyScheduleService;

@Service
public class MonthlyScheduleServiceImpl implements MonthlyScheduleService {

	@Autowired
	MonthlyScheduleDao monthlyScheduleDao;

	@Autowired
	EmployeeService employeeService;

	@Autowired
	CalendarService calendarService;

	@Autowired
	DateService dateService;

	@Autowired
	DailyShiftService dailyShiftService;

	@Override
	public MonthlySchedule generateMonthlySchedule(MonthlySchedule monthlySchedule) throws SQLException {
		if (!checkIfMonthlyScheduleCanBeGenerated(monthlySchedule)) {
			throw new DuplicationException("month", "Montlhy schedule already generated.");
		}
		insertMonthlySchedule(monthlySchedule);

		List<DayDto> days = calendarService.getDaysByMonthAndYear(monthlySchedule.getMonth(),
				monthlySchedule.getYear());
		Collections.sort(monthlySchedule.getEmployeesHours());
		TreeMap<String, List<DailyShift>> dailyShiftsGroupedByDay = new TreeMap<>();
		Integer loggedInEmployeeId = employeeService.getLoggedInEmployee().getId();
		for (DayDto day : days) {
			List<DailyShift> dailyShifts = new ArrayList<DailyShift>();
			for (EmployeeHours employee : monthlySchedule.getEmployeesHours()) {
				DailyShift dailyShift = new DailyShift();
				dailyShift.setCalendarId(day.getId());
				dailyShift.setCreatedByEmployeeId(loggedInEmployeeId);
				dailyShift.setCreateTimestamp(dateService.getCurrentMillisBGTimezone());
				dailyShift.setEmployeeId(employee.getEmployee().getId());
				dailyShift.setMonthlyScheduleId(monthlySchedule.getId());
				dailyShift.setId(dailyShiftService.createDailyShift(dailyShift));
				dailyShifts.add(dailyShift);
			}
			Collections.sort(dailyShifts);
			dailyShiftsGroupedByDay.put(day.getDate(), dailyShifts);
		}
		monthlySchedule.setDailyShiftsGroupedByDay(dailyShiftsGroupedByDay);

		return monthlySchedule;
	}

	private boolean checkIfMonthlyScheduleCanBeGenerated(MonthlySchedule monthlySchedule) {
		monthlySchedule = monthlyScheduleDao.getMonthlySchedule(monthlySchedule.getStoreId(),
				monthlySchedule.getMonth(), monthlySchedule.getYear(), false);
		return monthlySchedule == null;
	}

	private void insertMonthlySchedule(MonthlySchedule monthlySchedule) throws SQLException {
		Integer workingHourInMinutesForMonth = calendarService
				.getWorkingHoursInMinutesForMonth(monthlySchedule.getMonth(), monthlySchedule.getYear());
		monthlySchedule.setCreatedByEmployeeId(employeeService.getLoggedInEmployee().getId());
		monthlySchedule.setCreateTimestamp(dateService.getCurrentMillisBGTimezone());
		monthlySchedule.setIsFinalized(false);
		monthlySchedule.setIsPresentForm(false);
		monthlySchedule.setWorkingHoursInMinutes(workingHourInMinutesForMonth);
		monthlySchedule.setWorkingHours(dateService.convertMinutesToTime(workingHourInMinutesForMonth));
		monthlySchedule.setId(monthlyScheduleDao.insertMonthlySchedule(monthlySchedule));
	}

	@Override
	public MonthlySchedule getMonthlySchedule(Integer storeId, Integer month, Integer year, Boolean isPresentForm) {
		MonthlySchedule monthlySchedule = monthlyScheduleDao.getMonthlySchedule(storeId, month, year, isPresentForm);
		addDataToMonthlySchedule(monthlySchedule);

		return monthlySchedule;
	}

	private void addDataToMonthlySchedule(MonthlySchedule monthlySchedule) {
		if (monthlySchedule != null) {
			List<DailyShift> dailyShifts = dailyShiftService.getDailyShiftByMonthlyScheduleId(monthlySchedule.getId());
			monthlySchedule.setDailyShiftsGroupedByDay(getDailyShiftsGroupedByDay(dailyShifts));
			monthlySchedule.setEmployeesHours(
					getEmployeeHours(monthlySchedule.getId(), monthlySchedule.getWorkingHoursInMinutes()));
		}
	}

//	private List<EmployeeHours> getEmployeeHours(List<DailyShift> dailyShifts) {
//		Map<Integer, List<DailyShift>> dailyShiftsGroupedByEmployee = dailyShifts.stream()
//				.collect(Collectors.groupingBy(DailyShift::getEmployeeId, TreeMap::new, Collectors.toList()));
//		List<EmployeeHours> employeeHoursList = new ArrayList<>();
//		for (Integer employeeId : dailyShiftsGroupedByEmployee.keySet()) {
//			EmployeeHours employeeHours = new EmployeeHours();
//			Integer minutes = dailyShiftsGroupedByEmployee.get(employeeId).stream()
//					.filter(dailyShift -> dailyShift.getWorkingShiftDurationMinutes() != null)
//					.mapToInt(DailyShift::getWorkingShiftDurationMinutes).sum();
//			String hours = dateService.convertMinutesToTime(minutes);
//			Employee employee = employeeService.getEmployeesById(employeeId);
//			employeeHours.setEmployee(employee);
//			employeeHours.setHours(hours);
//			employeeHoursList.add(employeeHours);
//		}
//		Collections.sort(employeeHoursList);
//
//		return employeeHoursList;
//	}

	private TreeMap<String, List<DailyShift>> getDailyShiftsGroupedByDay(List<DailyShift> dailyShifts) {
		TreeMap<String, List<DailyShift>> dailyShiftsGroupedByDay = dailyShifts.stream()
				.collect(Collectors.groupingBy(DailyShift::getCalendarDate, TreeMap::new, Collectors.toList()));
		for (String key : dailyShiftsGroupedByDay.keySet()) {
			Collections.sort(dailyShiftsGroupedByDay.get(key));
		}
		return dailyShiftsGroupedByDay;
	}

	@Override
	public List<EmployeeHours> getEmployeeHours(Integer monthlyScheduleId,
			Integer monthlyScheduleWorkingHoursInMinutes) {
		List<EmployeeHours> employeeHoursList = monthlyScheduleDao
				.getEmployeeHoursByMonthlyScheduleId(monthlyScheduleId);
		for (EmployeeHours employeeHours : employeeHoursList) {
			employeeHours.setHours(dateService.convertMinutesToTime(employeeHours.getMinutes()));
			if (employeeHours.getMinutes() > monthlyScheduleWorkingHoursInMinutes) {
				employeeHours.setOvertimeInMinutes(employeeHours.getMinutes() - monthlyScheduleWorkingHoursInMinutes);
				employeeHours.setOvertime(dateService
						.convertMinutesToTime(employeeHours.getMinutes() - monthlyScheduleWorkingHoursInMinutes));
			}
		}
		Collections.sort(employeeHoursList);

		return employeeHoursList;
	}

	@Override
	public void finalizeMonthlySchedule(MonthlySchedule monthlySchedule, Boolean isPresentForm) throws SQLException {
		monthlySchedule = monthlyScheduleDao.getMonthlyScheduleById(monthlySchedule.getId());
		if (!monthlySchedule.getIsFinalized()) {
			List<EmployeeHours> employeeHours = getEmployeeHours(monthlySchedule.getId(),
					monthlySchedule.getWorkingHoursInMinutes());
			if ((isPresentForm && checkOvertimeCorrectness(employeeHours, 1800)
					|| !isPresentForm && checkOvertimeCorrectness(employeeHours, 0))) {
				monthlySchedule.setIsFinalized(true);
				monthlySchedule.setLastUpdateTimestamp(dateService.getCurrentMillisBGTimezone());
				monthlySchedule.setUpdatedByEmployeeId(employeeService.getLoggedInEmployee().getId());
				monthlyScheduleDao.updateMonthlySchedule(monthlySchedule);
				if (!isPresentForm) {
					createPresentForm(monthlySchedule);
				}
			}
		}
	}

	private boolean checkOvertimeCorrectness(List<EmployeeHours> employeeHoursList, Integer allowedOvertimeInMinutes) {
		for (EmployeeHours employeeHours : employeeHoursList) {
			if (employeeHours.getOvertimeInMinutes() != null
					&& employeeHours.getOvertimeInMinutes() > allowedOvertimeInMinutes) {
				return false;
			}
		}

		return true;
	}

	private void createPresentForm(MonthlySchedule monthlySchedule) throws SQLException {
		MonthlySchedule presentForm = new MonthlySchedule();
		presentForm.setCreateTimestamp(dateService.getCurrentMillisBGTimezone());
		presentForm.setCreatedByEmployeeId(employeeService.getLoggedInEmployee().getId());
		presentForm.setStoreId(monthlySchedule.getStoreId());
		presentForm.setMonth(monthlySchedule.getMonth());
		presentForm.setYear(monthlySchedule.getYear());
		presentForm.setIsFinalized(false);
		presentForm.setIsPresentForm(true);
		presentForm.setWorkingHoursInMinutes(monthlySchedule.getWorkingHoursInMinutes());
		presentForm.setWorkingHours(monthlySchedule.getWorkingHours());
		presentForm.setId(monthlyScheduleDao.insertMonthlySchedule(presentForm));
		createPresentFormDailyShifts(monthlySchedule.getId(), presentForm.getId());
	}

	private void createPresentFormDailyShifts(Integer monthlyScheduleId, Integer presentFormId) {
		List<DailyShift> dailyShifts = dailyShiftService.getDailyShiftByMonthlyScheduleId(monthlyScheduleId);
		for (DailyShift dailyShift : dailyShifts) {
			dailyShift.setCreatedByEmployeeId(employeeService.getLoggedInEmployee().getId());
			dailyShift.setCreateTimestamp(dateService.getCurrentMillisBGTimezone());
			dailyShift.setMonthlyScheduleId(presentFormId);
		}
		dailyShiftService.createDailyShiftBatch(dailyShifts);
	}

	@Override
	public MonthlySchedule getMonthlyScheduleById(Integer monthlyScheduleId) {
		return monthlyScheduleDao.getMonthlyScheduleById(monthlyScheduleId);
	}

}
