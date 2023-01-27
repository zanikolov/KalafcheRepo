package com.kalafche.model;

import java.math.BigDecimal;
import java.util.List;

public class TotalSumRequest {

	private List<BigDecimal> prices;
	private Integer discountCode;

	public List<BigDecimal> getPrices() {
		return prices;
	}

	public void setPrices(List<BigDecimal> prices) {
		this.prices = prices;
	}

	public Integer getDiscountCode() {
		return discountCode;
	}

	public void setDiscountCode(Integer discountCode) {
		this.discountCode = discountCode;
	}

}
