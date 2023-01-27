package com.kalafche.model;

import java.math.BigDecimal;

public class TotalSumReport {

	private BigDecimal totalSum = BigDecimal.ZERO;
	private BigDecimal discount = BigDecimal.ZERO;
	private BigDecimal totalSumAfterDiscount = BigDecimal.ZERO;

	public BigDecimal getTotalSum() {
		return totalSum;
	}

	public void setTotalSum(BigDecimal totalSum) {
		this.totalSum = totalSum;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public BigDecimal getTotalSumAfterDiscount() {
		return totalSumAfterDiscount;
	}

	public void setTotalSumAfterDiscount(BigDecimal totalSumAfterDiscount) {
		this.totalSumAfterDiscount = totalSumAfterDiscount;
	}
}
