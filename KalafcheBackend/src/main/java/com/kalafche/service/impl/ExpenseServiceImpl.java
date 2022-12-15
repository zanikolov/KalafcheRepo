package com.kalafche.service.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.kalafche.dao.ExpenseDao;
import com.kalafche.dao.StoreDao;
import com.kalafche.exceptions.DuplicationException;
import com.kalafche.model.DiscountCampaign;
import com.kalafche.model.DiscountCode;
import com.kalafche.model.Employee;
import com.kalafche.model.Expense;
import com.kalafche.model.ExpenseReport;
import com.kalafche.model.ExpenseType;
import com.kalafche.service.DateService;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.ExpenseService;
import com.kalafche.service.fileutil.ImageUploadService;

@Service
public class ExpenseServiceImpl implements ExpenseService {

	@Autowired
	ExpenseDao expenseDao;

	@Autowired
	EmployeeService employeeService;

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
	public ExpenseReport searchExpenses(Long startDateMilliseconds, Long endDateMilliseconds, String storeIds) {
		ExpenseReport report = new ExpenseReport();
		
		if (storeIds.equals("0") || storeIds.equals("ANIKO") || storeIds.equals("AZARD")) {
			storeIds = storeDao.selectStoreIdsByOwner(storeIds);
		}
		
		List<Expense> expenses = expenseDao.searchExpenses(startDateMilliseconds, endDateMilliseconds, storeIds);
		calculateTotalAmountAndCount(expenses, report);
		report.setExpenses(expenses);
		
		return report;
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
			throw new DuplicationException("code", "Съществуващ код.");
		}
	}
	
}
