package com.kalafche.model;

import java.math.BigDecimal;

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

	public int getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public Integer getApprover() {
		return approver;
	}

	public void setApprover(Integer approver) {
		this.approver = approver;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public int getQuantityInStock() {
		return quantityInStock;
	}

	public void setQuantityInStock(int quantityInStock) {
		this.quantityInStock = quantityInStock;
	}

	public int getOrderedQuantity() {
		return orderedQuantity;
	}

	public void setOrderedQuantity(int orderedQuantity) {
		this.orderedQuantity = orderedQuantity;
	}

	public int getExtraQuantity() {
		return extraQuantity;
	}

	public void setExtraQuantity(int extraQuantity) {
		this.extraQuantity = extraQuantity;
	}

	public BigDecimal getSpecificPrice() {
		return specificPrice;
	}

	public void setSpecificPrice(BigDecimal specificPrice) {
		this.specificPrice = specificPrice;
	}

}
