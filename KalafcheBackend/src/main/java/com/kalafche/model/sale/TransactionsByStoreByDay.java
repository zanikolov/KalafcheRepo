package com.kalafche.model.sale;

import java.math.BigDecimal;

public class TransactionsByStoreByDay {

	private Integer storeId;
	private String storeName;
	private String day;
	private Integer soldItemsCount;
	private Integer soldItemsCountPrevYear;
	private Integer transactionsCount;
	private Integer transactionsCountPrevYear;
	private BigDecimal turnover;
	private BigDecimal turnoverPrevYear;

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

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public Integer getSoldItemsCount() {
		return soldItemsCount;
	}

	public void setSoldItemsCount(Integer soldItemsCount) {
		this.soldItemsCount = soldItemsCount;
	}

	public Integer getSoldItemsCountPrevYear() {
		return soldItemsCountPrevYear;
	}

	public void setSoldItemsCountPrevYear(Integer soldItemsCountPrevYear) {
		this.soldItemsCountPrevYear = soldItemsCountPrevYear;
	}

	public Integer getTransactionsCount() {
		return transactionsCount;
	}

	public void setTransactionsCount(Integer transactionsCount) {
		this.transactionsCount = transactionsCount;
	}

	public Integer getTransactionsCountPrevYear() {
		return transactionsCountPrevYear;
	}

	public void setTransactionsCountPrevYear(Integer transactionsCountPrevYear) {
		this.transactionsCountPrevYear = transactionsCountPrevYear;
	}

	public BigDecimal getTurnover() {
		return turnover;
	}

	public void setTurnover(BigDecimal turnover) {
		this.turnover = turnover;
	}

	public BigDecimal getTurnoverPrevYear() {
		return turnoverPrevYear;
	}

	public void setTurnoverPrevYear(BigDecimal turnoverPrevYear) {
		this.turnoverPrevYear = turnoverPrevYear;
	}

}
