package com.kalafche.model;

import java.math.BigDecimal;

public class DailyStoreReport {

	private Integer id;
	private Integer storeId;
	private String storeName;
	private Long createTimestamp;
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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public Long getCreateTimestamp() {
		return createTimestamp;
	}

	public void setCreateTimestamp(Long createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public Long getLastUpdateTimestamp() {
		return lastUpdateTimestamp;
	}

	public void setLastUpdateTimestamp(Long lastUpdateTimestamp) {
		this.lastUpdateTimestamp = lastUpdateTimestamp;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public Integer getUpdatedByEmployeeId() {
		return updatedByEmployeeId;
	}

	public void setUpdatedByEmployeeId(Integer updatedByEmployeeId) {
		this.updatedByEmployeeId = updatedByEmployeeId;
	}

	public String getUpdatedByEmployeeName() {
		return updatedByEmployeeName;
	}

	public void setUpdatedByEmployeeName(String updatedByEmployeeName) {
		this.updatedByEmployeeName = updatedByEmployeeName;
	}

	public Integer getSoldItemsCount() {
		return soldItemsCount;
	}

	public void setSoldItemsCount(Integer soldItemsCount) {
		this.soldItemsCount = soldItemsCount;
	}

	public Integer getRefundedItemsCount() {
		return refundedItemsCount;
	}

	public void setRefundedItemsCount(Integer refundedItemsCount) {
		this.refundedItemsCount = refundedItemsCount;
	}

	public BigDecimal getIncome() {
		return income;
	}

	public void setIncome(BigDecimal income) {
		this.income = income;
	}

	public BigDecimal getCollected() {
		return collected;
	}

	public void setCollected(BigDecimal collected) {
		this.collected = collected;
	}

	public BigDecimal getCardPayment() {
		return cardPayment;
	}

	public void setCardPayment(BigDecimal cardPayment) {
		this.cardPayment = cardPayment;
	}

	public BigDecimal getExpense() {
		return expense;
	}

	public void setExpense(BigDecimal expense) {
		this.expense = expense;
	}

	public BigDecimal getInitialBalance() {
		return initialBalance;
	}

	public void setInitialBalance(BigDecimal initialBalance) {
		this.initialBalance = initialBalance;
	}

	public BigDecimal getFinalBalance() {
		return finalBalance;
	}

	public void setFinalBalance(BigDecimal finalBalance) {
		this.finalBalance = finalBalance;
	}

}
