package com.kalafche.model.protectplus;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProtectPlusUsageRecord {

	private Integer saleId;
	private Long saleTimestamp;
	private String storeName;
	private String employeeName;
	private BigDecimal totalAmount;
	private Integer protectPlusAppliedItemsCount;
}
