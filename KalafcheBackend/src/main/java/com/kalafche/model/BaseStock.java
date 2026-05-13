package com.kalafche.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseStock {

	private Integer id;
	private Integer itemId;
	private Integer productId;
	private String productName;
	private String productCode;
	private Integer deviceBrandId;
	private String deviceBrandName;
	private Integer deviceModelId;
	private String deviceModelName;
	private Integer quantity;
	private BigDecimal productPrice;
	private String barcode;
	private String productFabric;
	private Integer productTypeId;
	private String productTypeName;

}
