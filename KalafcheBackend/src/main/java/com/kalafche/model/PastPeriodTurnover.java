package com.kalafche.model;

import java.math.BigDecimal;

public class PastPeriodTurnover {

	private Integer storeId;
	private String storeCode;
	private String storeName;
	private BigDecimal prevYearTurnover;
	private BigDecimal prevYearDelta;
	private BigDecimal prevMonthTurnover;
	private BigDecimal prevMonthDelta;
	private BigDecimal selectedMonthTurnover;

	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public String getStoreCode() {
		return storeCode;
	}

	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public BigDecimal getPrevYearTurnover() {
		return prevYearTurnover;
	}

	public void setPrevYearTurnover(BigDecimal prevYearTurnover) {
		this.prevYearTurnover = prevYearTurnover;
	}

	public BigDecimal getPrevYearDelta() {
		return prevYearDelta;
	}

	public void setPrevYearDelta(BigDecimal prevYearDelta) {
		this.prevYearDelta = prevYearDelta;
	}

	public BigDecimal getPrevMonthTurnover() {
		return prevMonthTurnover;
	}

	public void setPrevMonthTurnover(BigDecimal prevMonthTurnover) {
		this.prevMonthTurnover = prevMonthTurnover;
	}

	public BigDecimal getPrevMonthDelta() {
		return prevMonthDelta;
	}

	public void setPrevMonthDelta(BigDecimal prevMonthDelta) {
		this.prevMonthDelta = prevMonthDelta;
	}

	public BigDecimal getSelectedMonthTurnover() {
		return selectedMonthTurnover;
	}

	public void setSelectedMonthTurnover(BigDecimal selectedMonthTurnover) {
		this.selectedMonthTurnover = selectedMonthTurnover;
	}

}
