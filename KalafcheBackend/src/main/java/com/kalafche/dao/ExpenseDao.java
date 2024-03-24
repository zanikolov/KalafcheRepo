package com.kalafche.dao;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.model.DataReport;
import com.kalafche.model.expense.Expense;
import com.kalafche.model.expense.ExpensePriceByType;
import com.kalafche.model.expense.ExpenseType;

public interface ExpenseDao {

	public void insertExpense(Expense expense);
	
	public List<ExpenseType> selectExpenseTypes(Boolean isAdmin);

	public List<Expense> searchExpenses(Long startDateMilliseconds, Long endDateMilliseconds, String storeIds,
			Integer typeId, List<String> typeCodesForExclusion, Boolean isAdmin);

	public Integer insertExpenseType(ExpenseType expenseType) throws SQLException;

	public void updateExpenseType(ExpenseType expenseType);

	public boolean checkIfExpenseTypeExists(ExpenseType expenseType);

	public DataReport selectExpenseTotalAndCountByStore(Long startDateTime, Long endDateTime, Integer storeId);

	public DataReport selectCollectionTotalAndCountByStore(Long startDateTime, Long endDateTime, Integer storeId);

	public DataReport selectExpenseTotalAndCountWithoutRefundByStore(Long startDateTime, Long endDateTime, Integer storeId);

	public List<ExpensePriceByType> selectExpensePriceGroupByType(Long startDateTime, Long endDateTime, Integer storeId);
	
}
