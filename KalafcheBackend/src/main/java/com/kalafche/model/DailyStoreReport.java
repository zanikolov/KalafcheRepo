package com.kalafche.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyStoreReport {

	private Integer id;
	private Integer storeId;
	private String storeName;
	private Integer companyId;
	private String companyName;
	private Long createTimestamp;
	private Long createDate;
	private Long lastUpdateTimestamp;
	private Integer employeeId;
	private String employeeName;
	private Integer updatedByEmployeeId;
	private String updatedByEmployeeName;
	private Integer soldItemsCount;
	private Integer refundedItemsCount;
	private BigDecimal income;
	private BigDecimal collected;
	private BigDecimal cardPayment;
	private BigDecimal expense;
	private BigDecimal initialBalance;
	private BigDecimal finalBalance;
	private Boolean isFinalized;
	private String description;
	
	public DailyStoreReport() {
		this.soldItemsCount = 0;
		this.refundedItemsCount = 0;
		this.income = BigDecimal.ZERO;
		this.collected = BigDecimal.ZERO;
		this.cardPayment = BigDecimal.ZERO;
		this.expense = BigDecimal.ZERO;
		this.initialBalance = BigDecimal.ZERO;
		this.finalBalance = BigDecimal.ZERO;
	}
	
}
