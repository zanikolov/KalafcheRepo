package com.kalafche.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Stock extends BaseStock {

	private String productDescription;
	private Integer storeId;
	private String storeName;
	private int orderedQuantity;
	private boolean approved;
	private Integer approver;
	private int quantityInStock;
	private int extraQuantity;
	private BigDecimal specificPrice;

}
