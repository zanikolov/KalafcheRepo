package com.kalafche.model;

import java.math.BigDecimal;

public class RevisionItem {

	private Integer id;
	private Integer revisionId;
	private Integer itemId;
	private Integer productId;
	private String productCode;
	private String productName;
	private String barcode;
	private Integer deviceModelId;
	private String deviceModelName;
	private Integer deviceBrandId;
	private BigDecimal productPrice;
	private Integer balance;
	private Integer expected;
	private Integer actual;
	private Boolean partOfTheCurrentRevision = true;
	private Boolean synced;

	public RevisionItem() {
	}
	
	public RevisionItem(Integer revisionId, Item item, Integer expected, Integer actual) {
		this(revisionId, item.getId(), item.getProductId(), item.getProductCode(), item.getProductName(),
				item.getBarcode(), item.getDeviceModelId(), item.getDeviceModelName(), item.getDeviceBrandId(),
				item.getProductPrice(), 0, 0, false);
	}
	
	public RevisionItem(Integer revisionId, Integer itemId, Integer productId, String productCode, String productName,
			String barcode, Integer deviceModelId, String deviceModelName, Integer deviceBrandId,
			BigDecimal productPrice, Integer expected, Integer actual, Boolean synced) {
		this.revisionId = revisionId;
		this.itemId = itemId;
		this.productId = productId;
		this.productCode = productCode;
		this.productName = productName;
		this.barcode = barcode;
		this.deviceModelId = deviceModelId;
		this.deviceModelName = deviceModelName;
		this.deviceBrandId = deviceBrandId;
		this.productPrice = productPrice;
		this.expected = expected;
		this.actual = actual;
		this.synced = synced;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRevisionId() {
		return revisionId;
	}

	public void setRevisionId(Integer revisionId) {
		this.revisionId = revisionId;
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

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
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

	public BigDecimal getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(BigDecimal productPrice) {
		this.productPrice = productPrice;
	}

	public Integer getBalance() {
		return balance;
	}

	public void setBalance(Integer balance) {
		this.balance = balance;
	}

	public Integer getExpected() {
		return expected;
	}

	public void setExpected(Integer expected) {
		this.expected = expected;
	}

	public Integer getActual() {
		return actual;
	}

	public void setActual(Integer actual) {
		this.actual = actual;
	}

	public Boolean getPartOfTheCurrentRevision() {
		return partOfTheCurrentRevision;
	}

	public void setPartOfTheCurrentRevision(Boolean partOfTheCurrentRevision) {
		this.partOfTheCurrentRevision = partOfTheCurrentRevision;
	}

	public Boolean getSynced() {
		return synced;
	}

	public void setSynced(Boolean synced) {
		this.synced = synced;
	}

}
