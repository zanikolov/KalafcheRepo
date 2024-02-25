package com.kalafche.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesByStore {

	private Integer storeId;
	private String storeName;
	private String storeCode;
	private BigDecimal amount;
	private BigDecimal itemCount;
	private BigDecimal transactionCount;
	private BigDecimal spt;
	
}
