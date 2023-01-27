package com.kalafche.model;

import java.math.BigDecimal;

public class SalesForPastPeriodsByStore {

	private Integer storeId;
	private String storeName;
	private String storeCode;
	private BigDecimal currentMonthAmount;
	private BigDecimal previousMonthAmount;
	private BigDecimal previousYearAmount;

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

	public String getStoreCode() {
		return storeCode;
	}

	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}

	public BigDecimal getCurrentMonthAmount() {
		return currentMonthAmount;
	}

	public void setCurrentMonthAmount(BigDecimal currentMonthAmount) {
		this.currentMonthAmount = currentMonthAmount;
	}

	public BigDecimal getPreviousMonthAmount() {
		return previousMonthAmount;
	}

	public void setPreviousMonthAmount(BigDecimal previousMonthAmount) {
		this.previousMonthAmount = previousMonthAmount;
	}

	public BigDecimal getPreviousYearAmount() {
		return previousYearAmount;
	}

	public void setPreviousYearAmount(BigDecimal previousYearAmount) {
		this.previousYearAmount = previousYearAmount;
	}

}
