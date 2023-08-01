package com.kalafche.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.kalafche.dao.DailyStoreReportDao;
import com.kalafche.model.DailyReportData;
import com.kalafche.model.DailyStoreReport;
import com.kalafche.model.Employee;
import com.kalafche.model.DayInMillis;
import com.kalafche.service.DailyStoreReportService;
import com.kalafche.service.DateService;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.EntityService;
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
	
	@Autowired
	EntityService storeService;
	
	@Autowired
	DailyStoreReportDao dailyStoreReportDao;
	
	@Override
	public DailyStoreReport generateDailyStoreReport() {
		DailyStoreReport report = calculateDailyStoreReport(null);
		dailyStoreReportDao.insertDailyStoreReport(report);
		
		return report;
	}

	@Override
	public DailyStoreReport getDailyStoreReportById(Integer dailyStoreReportId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DailyStoreReport getDailyStoreReportByDay(Integer storeId, DayInMillis day) {
		return dailyStoreReportDao.getDailyStoreReport(storeId, day);
	}

	@Override
	public DailyStoreReport calculateDailyStoreReport(Integer storeId) {
		Employee loggedInEmployee = employeeService.getLoggedInEmployee();
		if (loggedInEmployee.getRoles().contains("ROLE_USER")) {
			storeId = loggedInEmployee.getStoreId();
		}
		DayInMillis todayInMillis = dateService.getTodayInMillis(0);
		DayInMillis yesterdayInMillis = dateService.getTodayInMillis(-1);
		DailyStoreReport yesterdayReport = getDailyStoreReportByDay(storeId, yesterdayInMillis);
		DailyReportData saleItemDailyReportData = saleService.getSaleItemDailyReportData(todayInMillis.getStartDateTime(), todayInMillis.getEndDateTime(), storeId);
		DailyReportData refundedSaleItemDailyReportData = saleService.getRefundedSaleItemDailyReportData(todayInMillis.getStartDateTime(), todayInMillis.getEndDateTime(), storeId);
		DailyReportData cardPaymentDailyReportData = saleService.getCardPaymentDailyReportData(todayInMillis.getStartDateTime(), todayInMillis.getEndDateTime(), storeId);
		DailyReportData expensesDailyReportData = expenseService.getExpenseDailyReportData(todayInMillis.getStartDateTime(), todayInMillis.getEndDateTime(), storeId);
		DailyReportData collectionDailyReportData = expenseService.getCollectionDailyReportData(todayInMillis.getStartDateTime(), todayInMillis.getEndDateTime(), storeId);
		
		DailyStoreReport report = new DailyStoreReport();
		if (yesterdayReport != null && yesterdayReport.getFinalBalance() != null) {
			report.setInitialBalance(yesterdayReport.getFinalBalance());
		} else {
			report.setInitialBalance(BigDecimal.ZERO);
		}
		report.setStoreId(storeId);
		report.setCreateTimestamp(dateService.getCurrentMillisBGTimezone());
		report.setEmployeeId(loggedInEmployee.getId());
		report.setLastUpdateTimestamp(dateService.getCurrentMillisBGTimezone());
		report.setUpdatedByEmployeeId(loggedInEmployee.getId());
		report.setStoreName(loggedInEmployee.getStoreName());
		report.setEmployeeName(loggedInEmployee.getName());
		report.setIncome(saleItemDailyReportData.getTotalAmount());
		report.setSoldItemsCount(saleItemDailyReportData.getCount());
		report.setExpense(expensesDailyReportData.getTotalAmount());
		report.setCollected(collectionDailyReportData.getTotalAmount());
		report.setCardPayment(cardPaymentDailyReportData.getTotalAmount());
		report.setRefundedItemsCount(refundedSaleItemDailyReportData.getCount());
		report.setFinalBalance(calculateFinalBalance(report));
				
		return report;
	}

	@Override
	public List<DailyStoreReport> searchDailyStoreReports(Long startDateMilliseconds, Long endDateMilliseconds,
			Integer storeId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private BigDecimal calculateFinalBalance(DailyStoreReport report) {
		return report.getInitialBalance().add(report.getIncome()).subtract(report.getExpense())
				.subtract(report.getCollected()).subtract(report.getCardPayment());
	}

}
