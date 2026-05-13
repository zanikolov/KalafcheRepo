package com.kalafche.service.impl;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalSumReport {
	
	private BigDecimal totalSum = BigDecimal.ZERO;
	private BigDecimal discount = BigDecimal.ZERO;
	private BigDecimal totalSumAfterDiscount = BigDecimal.ZERO;
	private BigDecimal change = BigDecimal.ZERO;

}
