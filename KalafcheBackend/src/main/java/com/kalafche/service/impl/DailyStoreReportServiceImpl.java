package com.kalafche.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.kalafche.model.DailyReportData;
import com.kalafche.model.DailyStoreReport;
import com.kalafche.model.Employee;
import com.kalafche.model.TodayInMillis;
import com.kalafche.service.DailyStoreReportService;
import com.kalafche.service.DateService;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.ExpenseService;
import com.kalafche.service.SaleService;

public class DailyStoreReportServiceImpl implements DailyStoreReportService {

	@Autowired
	DateService dateService;
	
	@Autowired
	SaleService saleService;
	
	@Autowired
	ExpenseService expenseService;
	
	@Autowired
	EmployeeService employeeService;
	
	@Override
	public DailyStoreReport generateDailyStoreReport() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DailyStoreReport getDailyStoreReportById(Integer dailyStoreReportId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DailyStoreReport getDailyStoreReportByDay(Long day) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DailyStoreReport calculateDailyStoreReport(Integer storeId) {
		Employee loggedInEmployee = employeeService.getLoggedInEmployee();
		if (loggedInEmployee.getRoles().contains("ROLE_USER")) {
			storeId = loggedInEmployee.getStoreId();
		}
		TodayInMillis todayInMillis = dateService.getTodayInMillis();
		DailyReportData saleItemDailyReportData = saleService.getSaleItemDailyReportData(todayInMillis.getStartDateTime(), todayInMillis.getEndDateTime(), storeId);
		DailyReportData expensesDailyReportData = expenseService.getExpenseDailyReportData(todayInMillis.getStartDateTime(), todayInMillis.getEndDateTime(), storeId);
		DailyReportData collectionDailyReportData = expenseService.getCollectionDailyReportData(todayInMillis.getStartDateTime(), todayInMillis.getEndDateTime(), storeId);
		
		return null;
	}

	@Override
	public List<DailyStoreReport> searchDailyStoreReports(Long startDateMilliseconds, Long endDateMilliseconds,
			Integer storeId) {
		// TODO Auto-generated method stub
		return null;
	}

}
