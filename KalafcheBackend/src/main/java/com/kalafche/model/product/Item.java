package com.kalafche.model.product;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {

	private int id;
	private int deviceBrandId;
	private int deviceModelId;
	private String deviceModelName;
	private int productId;
	private String productCode;
	private String productName;
	private BigDecimal productPrice;
	private BigDecimal productPurchasePrice;
	private int productBonusPts;
	private Integer productTypeId;
	private String productTypeName;
	private String barcode;

}
