package com.kalafche.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kalafche.dao.DailyStoreReportDao;
import com.kalafche.model.DailyReportData;
import com.kalafche.model.DailyStoreReport;
import com.kalafche.model.DayInMillis;
import com.kalafche.model.Employee;
import com.kalafche.model.StoreDto;
import com.kalafche.service.DailyStoreReportService;
import com.kalafche.service.DateService;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.EntityService;
import com.kalafche.service.ExpenseService;
import com.kalafche.service.SaleService;

@Service
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
	public DailyStoreReport finalizeDailyStoreReport(Integer storeId) {
		Employee loggedInEmployee = employeeService.getLoggedInEmployee();
		DailyStoreReport report = null;
		
		if (loggedInEmployee.getRoles().contains("ROLE_USER")) {
			storeId = loggedInEmployee.getStoreId();
		}
		
		if (isDailyStoreReportCanBeFinalized(storeId)) {
			report = calculateDailyStoreReport(storeId);
			dailyStoreReportDao.insertDailyStoreReport(report);
		}
		
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
		report.setInitialBalance(yesterdayReport != null && yesterdayReport.getFinalBalance() != null ? yesterdayReport.getFinalBalance() : BigDecimal.ZERO);
		report.setStoreId(storeId);
		report.setCreateTimestamp(dateService.getCurrentMillisBGTimezone());
		report.setEmployeeId(loggedInEmployee.getId());
		report.setLastUpdateTimestamp(dateService.getCurrentMillisBGTimezone());
		report.setUpdatedByEmployeeId(loggedInEmployee.getId());
		StoreDto store = storeService.getStoreById(storeId);
		report.setStoreName(store.getCity() + ", " + store.getName());
		report.setEmployeeName(loggedInEmployee.getName());
		report.setSoldItemsCount(saleItemDailyReportData.getCount());
		report.setRefundedItemsCount(refundedSaleItemDailyReportData.getCount());
		report.setIncome(saleItemDailyReportData.getTotalAmount() != null ? saleItemDailyReportData.getTotalAmount() : BigDecimal.ZERO);
		report.setExpense(expensesDailyReportData.getTotalAmount() != null ? expensesDailyReportData.getTotalAmount() : BigDecimal.ZERO);
		report.setCollected(collectionDailyReportData.getTotalAmount() != null ? collectionDailyReportData.getTotalAmount() : BigDecimal.ZERO);
		report.setCardPayment(cardPaymentDailyReportData.getTotalAmount() != null ? cardPaymentDailyReportData.getTotalAmount() : BigDecimal.ZERO);		
		report.setFinalBalance(calculateFinalBalance(report));
				
		return report;
	}

	@Override
	public List<DailyStoreReport> searchDailyStoreReports(Long startDateMilliseconds, Long endDateMilliseconds,
			String storeIds) {
		List<DailyStoreReport> reports = dailyStoreReportDao.searchDailyStoreReports(startDateMilliseconds, endDateMilliseconds, storeService.getConcatenatedStoreIdsForFiltering(storeIds));
		
		return reports;
	}
	
	private BigDecimal calculateFinalBalance(DailyStoreReport report) {
		return report.getInitialBalance().add(report.getIncome()).subtract(report.getExpense())
				.subtract(report.getCollected()).subtract(report.getCardPayment());
	}

	@Override
	public Boolean isDailyStoreReportCanBeFinalized(Integer storeId) {
		Employee loggedInEmployee = employeeService.getLoggedInEmployee();		
//		if (!loggedInEmployee.getRoles().contains("ROLE_USER")) {
//			return false;
//		}
		
		if (getDailyStoreReportByDay(loggedInEmployee.getStoreId(), dateService.getTodayInMillis(0)) != null) {
			return false;
		}
		
		return true;
	}

}
