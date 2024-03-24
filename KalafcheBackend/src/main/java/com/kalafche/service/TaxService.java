package com.kalafche.service;

import java.math.BigDecimal;
import java.util.List;

import com.kalafche.model.Tax;
import com.kalafche.model.expense.Expense;

public interface TaxService {

	public BigDecimal calculateBase(BigDecimal amount);
	
	public List<Tax> getTaxes(Boolean isApplicableOnExpenses);
	
	public BigDecimal calculateDueVAT(BigDecimal bigDecimal);
	
	public BigDecimal calculateRefundVAT(List<Expense> expenses);
	
	public BigDecimal calculateFlatTax(BigDecimal amount);
	
	public BigDecimal calculateDivident(BigDecimal amount);
	
}
