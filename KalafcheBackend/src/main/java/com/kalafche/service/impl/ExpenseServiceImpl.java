package com.kalafche.service.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kalafche.dao.ExpenseDao;
import com.kalafche.dao.StoreDao;
import com.kalafche.exceptions.DuplicationException;
import com.kalafche.model.DataReport;
import com.kalafche.model.employee.Employee;
import com.kalafche.model.expense.Expense;
import com.kalafche.model.expense.ExpensePriceByType;
import com.kalafche.model.expense.ExpenseReport;
import com.kalafche.model.expense.ExpenseType;
import com.kalafche.service.DateService;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.EntityService;
import com.kalafche.service.ExpenseService;
import com.kalafche.service.fileutil.ImageUploadService;

@Service
public class ExpenseServiceImpl implements ExpenseService {

	@Autowired
	ExpenseDao expenseDao;

	@Autowired
	EmployeeService employeeService;
	
	@Autowired
	EntityService entityService;

	@Autowired
	DateService dateService;

	@Autowired
	ImageUploadService imageUploadService;

	@Autowired
	StoreDao storeDao;

	@Transactional
	@Override
	public void createExpense(String typeCode, String description, BigDecimal price, Integer storeId,
			MultipartFile expenseImage) {
		Boolean isAdmin = employeeService.isLoggedInEmployeeAdmin();
		Expense expense = new Expense(typeCode, description, price);
		Employee loggedInEmployee = employeeService.getLoggedInEmployee();
		expense.setEmployeeId(loggedInEmployee.getId());
		
		if (!isAdmin) {
			expense.setStoreId(loggedInEmployee.getStoreId());
		} else {
			expense.setStoreId(storeId);
		}
		
		expense.setTimestamp(dateService.getCurrentMillisBGTimezone());
		if (expenseImage != null) {
			expense.setFileId(imageUploadService.uploadExpenseImage(expenseImage));
		}

		expenseDao.insertExpense(expense);
	}

	@Override
	public List<ExpenseType> getExpenseTypes() {
		Boolean isAdmin = employeeService.isLoggedInEmployeeAdmin();
		return expenseDao.selectExpenseTypes(isAdmin);
	}

	@Override
	public ExpenseReport getExpenseReport(Long startDateMilliseconds, Long endDateMilliseconds, String storeIds,
			Integer typeId, List<String> typeCodesForExclusion) {
		Boolean isAdmin = employeeService.isLoggedInEmployeeAdmin();

		ExpenseReport report = new ExpenseReport();
		List<Expense> expenses = expenseDao.searchExpenses(startDateMilliseconds, endDateMilliseconds,
				entityService.getConcatenatedStoreIdsForFiltering(storeIds), typeId, typeCodesForExclusion, isAdmin);
		calculateTotalAmountAndCount(expenses, report);
		report.setExpenses(expenses);

		return report;
	}
	
	@Override
	public List<Expense> searchExpenses(Long startDateMilliseconds, Long endDateMilliseconds, String storeIds,
			Integer typeId, List<String> typeCodesForExclusion) {
		Boolean isAdmin = employeeService.isLoggedInEmployeeAdmin();

		List<Expense> expenses = expenseDao.searchExpenses(startDateMilliseconds, endDateMilliseconds,
				entityService.getConcatenatedStoreIdsForFiltering(storeIds), typeId, typeCodesForExclusion, isAdmin);

		return expenses;
	}

	private void calculateTotalAmountAndCount(List<Expense> expenses, ExpenseReport expenseReport) {
		BigDecimal totalAmount = expenses.stream()
		        .map(expense -> expense.getPrice())
		        .reduce(BigDecimal.ZERO, BigDecimal::add);
		
		expenseReport.setCount(expenses.size());
		expenseReport.setTotalAmount(totalAmount);
	}

	@Override
	public ExpenseType createExpenseType(ExpenseType expenseType) throws SQLException {
		validateExpenseType(expenseType);
		Integer expenseTypeId = expenseDao.insertExpenseType(expenseType);
		expenseType.setId(expenseTypeId);
		
		return expenseType;
	}

	@Override
	public void updateExpenseType(ExpenseType expenseType) {
		expenseDao.updateExpenseType(expenseType);	
	}
	
	private void validateExpenseType(ExpenseType expenseType) {
		if (expenseDao.checkIfExpenseTypeExists(expenseType)) {
			throw new DuplicationException("code", "Code duplication.");
		}
	}

	@Override
	public DataReport getExpenseDailyReportData(Long startDateTime, Long endDateTime, Integer storeId) {
		return expenseDao.selectExpenseTotalAndCountByStore(startDateTime, endDateTime, storeId);
	}

	@Override
	public DataReport getCollectionDailyReportData(Long startDateTime, Long endDateTime, Integer storeId) {
		return expenseDao.selectCollectionTotalAndCountByStore(startDateTime, endDateTime, storeId);
	}

	@Override
	public DataReport getExpenseTotalAndCountWithoutRefundByStore(Long startDateTime, Long endDateTime,
			Integer storeId) {
		return expenseDao.selectExpenseTotalAndCountWithoutRefundByStore(startDateTime, endDateTime, storeId);
	}
	
	@Override
	public List<ExpensePriceByType> getExpensePriceGroupdByType(Long startDateTime, Long endDateTime,
			Integer storeId) {
		return expenseDao.selectExpensePriceGroupByType(startDateTime, endDateTime, storeId);
	}
}
