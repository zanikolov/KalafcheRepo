package com.kalafche.model;

import java.math.BigDecimal;
import java.util.List;

public class Sale {
	
	private Integer id;
	private Integer itemId;
	private Integer stockId;
	private String productName;
	private String productCode;
	private Integer storeId;
	private String storeName;
	private Integer deviceModelId;
	private String deviceModelName;
	private Integer deviceBrandId;
	private long saleTimestamp;
	private Integer partnerId;
	private Integer employeeId;
	private String employeeName;
	private BigDecimal amount;
	private String partnerCode;
	private List<SaleItem> saleItems;
	private Boolean isCashPayment;
	private Integer discountCodeId;
	private Integer discountCodeCode;

	public Sale() {
	}
	
	public Sale(Integer itemId, Integer employeeId, Integer storeId, Integer saleTimestamp, Integer partnerId) {
		this.itemId = itemId;
		this.employeeId = employeeId;
		this.storeId = storeId;
		this.saleTimestamp = saleTimestamp;
		this.partnerId = partnerId;
	}
	
	public Integer getId() {
		return this.id;
	}

	public Integer setId(Integer id) {
		return this.id = id;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public long getSaleTimestamp() {
		return this.saleTimestamp;
	}

	public void setSaleTimestamp(long saleTimestamp) {
		this.saleTimestamp = saleTimestamp;
	}

	public Integer getPartnerId() {
		return this.partnerId;
	}

	public void setPartnerId(Integer partnerId) {
		this.partnerId = partnerId;
	}

	public String getPartnerCode() {
		return partnerCode;
	}

	public void setPartnerCode(String partnerCode) {
		this.partnerCode = partnerCode;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getStockId() {
		return stockId;
	}

	public void setStockId(Integer stockId) {
		this.stockId = stockId;
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

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public int getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public List<SaleItem> getSaleItems() {
		return saleItems;
	}

	public void setSaleItems(List<SaleItem> saleItems) {
		this.saleItems = saleItems;
	}

	public Boolean getIsCashPayment() {
		return isCashPayment;
	}

	public void setIsCashPayment(Boolean isCashPayment) {
		this.isCashPayment = isCashPayment;
	}

	public Integer getDiscountCodeId() {
		return discountCodeId;
	}

	public void setDiscountCodeId(Integer discountCodeId) {
		this.discountCodeId = discountCodeId;
	}

	public Integer getDiscountCodeCode() {
		return discountCodeCode;
	}

	public void setDiscountCodeCode(Integer discountCodeCode) {
		this.discountCodeCode = discountCodeCode;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
