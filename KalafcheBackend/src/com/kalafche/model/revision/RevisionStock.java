package com.kalafche.model.revision;

public class RevisionStock {

	private int id;
	private int revisionId;
	private int itemId;
	private String itemName;
	private int itemProductCode;
	private int deviceModelId;
	private String deviceModelName;
	private int deviceBrandId;
	private String deviceBrandName;
	private int quantity;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRevisionId() {
		return revisionId;
	}

	public void setRevisionId(int revisionId) {
		this.revisionId = revisionId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getItemProductCode() {
		return itemProductCode;
	}

	public void setItemProductCode(int itemProductCode) {
		this.itemProductCode = itemProductCode;
	}

	public int getDeviceModelId() {
		return deviceModelId;
	}

	public void setDeviceModelId(int deviceModelId) {
		this.deviceModelId = deviceModelId;
	}

	public String getDeviceModelName() {
		return deviceModelName;
	}

	public void setDeviceModelName(String deviceModelName) {
		this.deviceModelName = deviceModelName;
	}

	public int getDeviceBrandId() {
		return deviceBrandId;
	}

	public void setDeviceBrandId(int deviceBrandId) {
		this.deviceBrandId = deviceBrandId;
	}

	public String getDeviceBrandName() {
		return deviceBrandName;
	}

	public void setDeviceBrandName(String deviceBrandName) {
		this.deviceBrandName = deviceBrandName;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
