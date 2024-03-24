package com.kalafche.model;

import java.math.BigDecimal;
import java.util.List;

import com.kalafche.model.expense.ExpensePriceByType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfitReport {

	private Integer storeId;
	private String storeName;
	private Integer companyId;
	private String companyName;
	private BigDecimal income;
	private BigDecimal base;
	private BigDecimal expense;
	private BigDecimal dueVAT;
	private BigDecimal refundVAT;
	private BigDecimal dividentTax;
	private BigDecimal flatTax;
	private BigDecimal profit;
	private List<ExpensePriceByType> expenses;
}
