package com.kalafche.model.product;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.kalafche.BaseModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product extends BaseModel {
	private Integer id;
	private String name;
	private String onlineName;
	private String code;
	private String description;
	private String fabric;
	private Integer typeId;
	private String typeName;
	private Integer masterTypeId;
	private String masterTypeName;
	private BigDecimal price;
	private BigDecimal priceEuro;
	private BigDecimal purchasePrice;
	private int bonusPts;
	private List<ProductSpecificPrice> specificPrices;
	private MultipartFile pic;

}
