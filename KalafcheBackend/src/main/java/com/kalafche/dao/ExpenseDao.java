package com.kalafche.dao;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.model.Expense;
import com.kalafche.model.ExpenseType;

public interface ExpenseDao {

	public void insertExpense(Expense expense);
	
	public List<ExpenseType> selectExpenseTypes(Boolean isAdmin);

	public List<Expense> searchExpenses(Long startDateMilliseconds, Long endDateMilliseconds, String storeIds);

	public Integer insertExpenseType(ExpenseType expenseType) throws SQLException;

	public void updateExpenseType(ExpenseType expenseType);

	public boolean checkIfExpenseTypeExists(ExpenseType expenseType);
	
}
