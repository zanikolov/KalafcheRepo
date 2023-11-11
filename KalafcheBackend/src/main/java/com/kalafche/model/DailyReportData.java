package com.kalafche.model;

import java.math.BigDecimal;

public class DailyReportData {

	private Integer count;
	private BigDecimal totalAmount;

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

}
