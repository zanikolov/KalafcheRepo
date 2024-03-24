package com.kalafche.model.expense;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Expense {

	private Integer id;
	private Integer storeId;
	private String storeName;
	private long timestamp;
	private Integer employeeId;
	private String employeeName;
	private BigDecimal price;
	private String description;
	private Integer typeId;
	private String typeName;
	private String typeCode;
	private String fileId;
	private Integer taxId;
	private String taxCode;
	private String taxName;
	private Integer taxRate;
	private String invoice;

	public Expense() {}
	
	public Expense(String typeCode, String description, BigDecimal price) {
		this.typeCode = typeCode;
		this.description = description;
		this.price = price;
	}

}
