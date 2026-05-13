package com.kalafche.model.sale;

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
	private BigDecimal saleCount;
	private BigDecimal transactionCount;
	private BigDecimal protectorCount;
	private BigDecimal protectorPlusCount;
	private BigDecimal spt;
	private BigDecimal bonusPts;
	private BigDecimal sqs;
	private BigDecimal attachRate;
	
	public static SalesByStore createEmptySalesByStore() {
		SalesByStore empty = new SalesByStore();
		empty.setAmount(BigDecimal.ZERO);
		empty.setItemCount(BigDecimal.ZERO);
		empty.setSaleCount(BigDecimal.ZERO);
		empty.setTransactionCount(BigDecimal.ZERO);
		empty.setSpt(BigDecimal.ZERO);
		empty.setSqs(BigDecimal.ZERO);
		
		return empty;
	}
	
}
