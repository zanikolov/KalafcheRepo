package com.kalafche.model;

import java.math.BigDecimal;

public class SaleItem {

	private Integer id;
	private Integer saleId;
	private Long saleTimestamp;
	private Integer itemId;
	private Integer productId;
	private String productCode;
	private String productName;
	private Integer productTypeId;
	private String productTypeName;
	private Integer productMasterTypeId;
	private String productMasterTypeName;
	private Integer deviceModelId;
	private String deviceModelName;
	private Integer deviceBrandId;
	private String deviceBrandName;
	private BigDecimal itemPrice;
	private BigDecimal salePrice;
	private Integer storeId;
	private String storeName;
	private Integer employeeId;
	private String employeeName;
	private Boolean isRefunded;
	private String discountCampaignCode;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSaleId() {
		return saleId;
	}

	public void setSaleId(Integer saleId) {
		this.saleId = saleId;
	}

	public Long getSaleTimestamp() {
		return saleTimestamp;
	}

	public void setSaleTimestamp(Long saleTimestamp) {
		this.saleTimestamp = saleTimestamp;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getProductTypeId() {
		return productTypeId;
	}

	public void setProductTypeId(Integer productTypeId) {
		this.productTypeId = productTypeId;
	}

	public String getProductTypeName() {
		return productTypeName;
	}

	public void setProductTypeName(String productTypeName) {
		this.productTypeName = productTypeName;
	}

	public Integer getProductMasterTypeId() {
		return productMasterTypeId;
	}

	public void setProductMasterTypeId(Integer productMasterTypeId) {
		this.productMasterTypeId = productMasterTypeId;
	}

	public String getProductMasterTypeName() {
		return productMasterTypeName;
	}

	public void setProductMasterTypeName(String productMasterTypeName) {
		this.productMasterTypeName = productMasterTypeName;
	}

	public Integer getDeviceModelId() {
		return deviceModelId;
	}

	public void setDeviceModelId(Integer deviceModelId) {
		this.deviceModelId = deviceModelId;
	}

	public String getDeviceModelName() {
		return deviceModelName;
	}

	public void setDeviceModelName(String deviceModelName) {
		this.deviceModelName = deviceModelName;
	}

	public Integer getDeviceBrandId() {
		return deviceBrandId;
	}

	public void setDeviceBrandId(Integer deviceBrandId) {
		this.deviceBrandId = deviceBrandId;
	}

	public String getDeviceBrandName() {
		return deviceBrandName;
	}

	public void setDeviceBrandName(String deviceBrandName) {
		this.deviceBrandName = deviceBrandName;
	}

	public BigDecimal getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(BigDecimal itemPrice) {
		this.itemPrice = itemPrice;
	}

	public BigDecimal getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(BigDecimal salePrice) {
		this.salePrice = salePrice;
	}

	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public Boolean getIsRefunded() {
		return isRefunded;
	}

	public void setIsRefunded(Boolean isRefunded) {
		this.isRefunded = isRefunded;
	}

	public String getDiscountCampaignCode() {
		return discountCampaignCode;
	}

	public void setDiscountCampaignCode(String discountCampaignCode) {
		this.discountCampaignCode = discountCampaignCode;
	}

}
