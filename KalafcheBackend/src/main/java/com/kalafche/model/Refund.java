package com.kalafche.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Refund {

	private Integer id;
	private Integer saleItemId;
	private String productName;
	private String productCode;
	private Integer storeId;
	private String storeName;
	private Integer deviceModelId;
	private String deviceModelName;
	private Integer deviceBrandId;
	private long timestamp;
	private long saleTimestamp;
	private Integer employeeId;
	private String employeeName;
	private BigDecimal price;
	private Integer bonusPts;
	private String description;

	public Refund() {
	}
	
	public Refund(Integer saleItemId, String description) {
		this.saleItemId = saleItemId;
		this.description = description;
	}
	
}
