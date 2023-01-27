package com.kalafche.model;

public class SalesByStoreByDayByProductType {

	private Integer storeId;
	private String storeName;
	private String day;
	private Integer productTypeId;
	private String productTypeName;
	private Integer soldItemsCount;

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

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
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

	public Integer getSoldItemsCount() {
		return soldItemsCount;
	}

	public void setSoldItemsCount(Integer soldItemsCount) {
		this.soldItemsCount = soldItemsCount;
	}

}
