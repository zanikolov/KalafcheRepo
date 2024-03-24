package com.kalafche.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.kalafche.model.DataReport;
import com.kalafche.model.expense.Expense;
import com.kalafche.model.expense.ExpensePriceByType;
import com.kalafche.model.expense.ExpenseReport;
import com.kalafche.model.expense.ExpenseType;

public interface ExpenseService {

	public void createExpense(String typeCode, String description, BigDecimal price, Integer storeId,
			MultipartFile expenseImage);

	public List<ExpenseType> getExpenseTypes();

	public ExpenseReport getExpenseReport(Long startDateMilliseconds, Long endDateMilliseconds, String storeIds, Integer typeId, List<String> typeCodesForExclusion);

	public ExpenseType createExpenseType(ExpenseType expenseType) throws SQLException;
	
	public void updateExpenseType(ExpenseType expenseType);

	public DataReport getExpenseDailyReportData(Long startDateTime, Long endDateTime, Integer storeId);

	public DataReport getCollectionDailyReportData(Long startDateTime, Long endDateTime, Integer storeId);

	public DataReport getExpenseTotalAndCountWithoutRefundByStore(Long startDateTime, Long endDateTime, Integer storeId);

	public List<Expense> searchExpenses(Long startDateMilliseconds, Long endDateMilliseconds, String storeIds, Integer typeId,
			List<String> typeCodesForExclusion);

	public List<ExpensePriceByType> getExpensePriceGroupdByType(Long startDateTime, Long endDateTime, Integer storeId);

}
