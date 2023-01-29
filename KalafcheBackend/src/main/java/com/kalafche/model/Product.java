package com.kalafche.model;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.kalafche.BaseModel;

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
	private BigDecimal purchasePrice;
	private List<ProductSpecificPrice> specificPrices;
	private MultipartFile pic;

	public String getCode() {
		return code;
	}

	public void setCode(String productCode) {
		this.code = productCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFabric() {
		return fabric;
	}

	public void setFabric(String fabric) {
		this.fabric = fabric;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getId() {
		return this.id;
	}

	public Integer setId(Integer id) {
		return this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(BigDecimal purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public MultipartFile getPic() {
		return pic;
	}

	public void setPic(MultipartFile pic) {
		this.pic = pic;
	}

	public String getOnlineName() {
		return onlineName;
	}

	public void setOnlineName(String onlineName) {
		this.onlineName = onlineName;
	}

	public List<ProductSpecificPrice> getSpecificPrices() {
		return specificPrices;
	}

	public void setSpecificPrices(List<ProductSpecificPrice> specificPrices) {
		this.specificPrices = specificPrices;
	}

	public Integer getMasterTypeId() {
		return masterTypeId;
	}

	public void setMasterTypeId(Integer masterTypeId) {
		this.masterTypeId = masterTypeId;
	}

	public String getMasterTypeName() {
		return masterTypeName;
	}

	public void setMasterTypeName(String masterTypeName) {
		this.masterTypeName = masterTypeName;
	}
}
