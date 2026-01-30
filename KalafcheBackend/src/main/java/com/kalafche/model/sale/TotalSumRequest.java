package com.kalafche.model.sale;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalSumRequest {

	private List<SaleItem> selectedSaleItems;
	private BigDecimal paid;
	private String currency;
	
}
