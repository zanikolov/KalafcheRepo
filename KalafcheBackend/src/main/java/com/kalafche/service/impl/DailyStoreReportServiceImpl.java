package com.kalafche.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.kalafche.dao.DailyStoreReportDao;
import com.kalafche.model.DailyReportData;
import com.kalafche.model.DailyStoreReport;
import com.kalafche.model.DayInMillis;
import com.kalafche.model.Employee;
import com.kalafche.model.StoreDto;
import com.kalafche.service.CompanyService;
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
	CompanyService companyService;
	
	@Autowired
	DailyStoreReportDao dailyStoreReportDao;
	
	@Override
	public DailyStoreReport finalizeDailyStoreReport(Integer storeId) {
		Employee loggedInEmployee = employeeService.getLoggedInEmployee();
		DailyStoreReport report = null;
		
		if (loggedInEmployee.getRoles().contains("ROLE_USER")) {
			storeId = loggedInEmployee.getStoreId();
		}
		
		if (canDailyStoreReportBeFinalized(storeId, false)) {
			report = calculateDailyStoreReport(storeId, false);
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
	public DailyStoreReport calculateDailyStoreReport(Integer storeId, Boolean scheduled) {
		Employee loggedInEmployee = null;
		if (!scheduled) {
			loggedInEmployee = employeeService.getLoggedInEmployee();
			if (loggedInEmployee.getRoles().contains("ROLE_USER")) {
				storeId = loggedInEmployee.getStoreId();
			}
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
		report.setLastUpdateTimestamp(dateService.getCurrentMillisBGTimezone());
		if (scheduled) {
			report.setEmployeeName("System generated");
		} else {
			report.setEmployeeId(loggedInEmployee.getId());
			report.setUpdatedByEmployeeId(loggedInEmployee.getId());
			report.setEmployeeName(loggedInEmployee.getName());
		}
		StoreDto store = storeService.getStoreById(storeId);
		report.setStoreName(store.getCity() + ", " + store.getName());
		report.setSoldItemsCount(saleItemDailyReportData.getCount());
		report.setRefundedItemsCount(refundedSaleItemDailyReportData.getCount());
		report.setIncome(saleItemDailyReportData.getTotalAmount() != null ? saleItemDailyReportData.getTotalAmount().setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
		report.setExpense(expensesDailyReportData.getTotalAmount() != null ? expensesDailyReportData.getTotalAmount().setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
		report.setCollected(collectionDailyReportData.getTotalAmount() != null ? collectionDailyReportData.getTotalAmount().setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
		report.setCardPayment(cardPaymentDailyReportData.getTotalAmount() != null ? cardPaymentDailyReportData.getTotalAmount().setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);		
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
				.subtract(report.getCollected()).subtract(report.getCardPayment()).setScale(2, RoundingMode.HALF_UP);
	}

	@Override
	public Boolean canDailyStoreReportBeFinalized(Integer storeId, Boolean scheduled) {
		if (!scheduled) {
			Employee loggedInEmployee = employeeService.getLoggedInEmployee();
			if (!loggedInEmployee.getRoles().contains("ROLE_USER")) {
				return false;
			}
		}
		
		if (getDailyStoreReportByDay(storeId, dateService.getTodayInMillis(0)) != null) {
			return false;
		}
		
		return true;
	}

	@Override
	public List<DailyStoreReport> searchDailyCompanyReports(Long startDateMilliseconds, Long endDateMilliseconds, Integer companyId) {
		Employee loggedInEmployee = employeeService.getLoggedInEmployee();
		DailyStoreReport companyReport = new DailyStoreReport();
		companyReport.setCreateTimestamp(dateService.getCurrentMillisBGTimezone());
		companyReport.setEmployeeName(loggedInEmployee.getName());
		companyReport.setCompanyName(companyService.getCompanyById(companyId).getName());
		List<Integer> storeIds = storeService.getStoreIdsByCompanyId(companyId);
		
		for (Integer storeId : storeIds) {
			DailyStoreReport storeReport = calculateDailyStoreReport(storeId, false);
			companyReport.setSoldItemsCount(companyReport.getSoldItemsCount() + storeReport.getSoldItemsCount());
			companyReport.setRefundedItemsCount(companyReport.getRefundedItemsCount() + storeReport.getRefundedItemsCount());
			companyReport.setIncome(companyReport.getIncome().add(storeReport.getIncome()));
			companyReport.setCollected(companyReport.getCollected().add(storeReport.getCollected()));
			companyReport.setCardPayment(companyReport.getCardPayment().add(storeReport.getCardPayment()));
			companyReport.setExpense(companyReport.getExpense().add(storeReport.getExpense()));
			companyReport.setInitialBalance(companyReport.getInitialBalance().add(storeReport.getInitialBalance()));
			companyReport.setFinalBalance(companyReport.getFinalBalance().add(storeReport.getFinalBalance()));
		}
		
		return Arrays.asList(companyReport);
	}
	
	@Scheduled(cron = "0 0 22 * * *", zone = "EET")
	public void scheduledFinalizeDailyStoreReports() {
	    List<StoreDto> stores = storeService.getStores();
	    for (StoreDto store : stores) {
	    	Integer storeId = store.getId();
	    	if (canDailyStoreReportBeFinalized(storeId, true)) {
	    		DailyStoreReport report = calculateDailyStoreReport(storeId, true);
				dailyStoreReportDao.insertDailyStoreReport(report);
	    	}
	    }
	}

}
