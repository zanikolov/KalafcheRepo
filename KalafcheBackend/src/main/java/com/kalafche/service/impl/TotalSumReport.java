package com.kalafche.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalSumReport {
	
	private static final BigDecimal BGN_TO_EUR_RATE = new BigDecimal("1.95583");

	private BigDecimal totalSum = BigDecimal.ZERO;
	private BigDecimal totalSumEuro = BigDecimal.ZERO;
	private BigDecimal discount = BigDecimal.ZERO;
	private BigDecimal discountEuro = BigDecimal.ZERO;
	private BigDecimal totalSumAfterDiscount = BigDecimal.ZERO;
	private BigDecimal totalSumAfterDiscountEuro = BigDecimal.ZERO;
	private BigDecimal change = BigDecimal.ZERO;
	private BigDecimal changeEuro = BigDecimal.ZERO;

	public void setTotalSum(BigDecimal totalSum) {
		this.totalSum = totalSum;

		if (totalSum != null) {
			this.totalSumEuro = convertToEuro(totalSum);
		}
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
		
		if (discount != null) {
			this.discountEuro = convertToEuro(discount);
		}
	}

	public void setTotalSumAfterDiscount(BigDecimal totalSumAfterDiscount) {
		this.totalSumAfterDiscount = totalSumAfterDiscount;
		
		if (totalSumAfterDiscount != null) {
			this.totalSumAfterDiscountEuro = convertToEuro(totalSumAfterDiscount);
		}
	}
	
	private BigDecimal convertToEuro(BigDecimal totalSum) {
		return totalSum.divide(BGN_TO_EUR_RATE, 2, RoundingMode.HALF_UP);
	}

}
