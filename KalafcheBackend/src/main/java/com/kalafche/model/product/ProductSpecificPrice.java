package com.kalafche.model.product;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSpecificPrice {

	private Integer id;
	private Integer productId;
	private Integer storeId;
	private String storeName;
	private BigDecimal price;
	
}
