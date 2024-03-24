package com.kalafche.model.sale;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PastPeriodTurnover {

	private Integer storeId;
	private String storeCode;
	private String storeName;
	private BigDecimal prevYearAmount;
	private BigDecimal prevYearAmountDelta;
	private BigDecimal prevYearTransactionCount;
	private BigDecimal prevYearTransactionCountDelta;
	private BigDecimal prevYearItemCount;
	private BigDecimal prevYearItemCountDelta;
	private BigDecimal prevYearSpt;
	private BigDecimal prevYearSptDelta;
	private BigDecimal prevMonthAmount;
	private BigDecimal prevMonthAmountDelta;
	private BigDecimal prevMonthTransactionCount;
	private BigDecimal prevMonthTransactionCountDelta;
	private BigDecimal prevMonthItemCount;
	private BigDecimal prevMonthItemCountDelta;
	private BigDecimal prevMonthSpt;
	private BigDecimal prevMonthSptDelta;
	private BigDecimal selectedMonthAmount;
	private BigDecimal selectedMonthTransactionCount;
	private BigDecimal selectedMonthItemCount;
	private BigDecimal selectedMonthSpt;

}
