package com.kalafche.model;

import com.kalafche.BaseModel;
import com.kalafche.enums.RelocationStatus;

public class Relocation extends BaseModel {
	private int id;
	private int stockId;
	private int itemId;
	private String productName;
	private String productCode;
	private float productPrice;
	private int sourceStoreId;
	private String sourceStoreName;
	private String sourceStoreCode;
	private int destStoreId;
	private String destStoreName;
	private String destStoreCode;
	private int deviceModelId;
	private String deviceModelName;
	private int deviceBrandId;
	private long relocationRequestTimestamp;
	private long relocationCompleteTimestamp;
	private int employeeId;
	private String employeeName;
	private boolean approved;
	private boolean rejected;
	private boolean arrived;
	private int quantity;
	private float relocationAmount;
	private boolean archived;
	private RelocationStatus status;

	public int getId() {
		return this.id;
	}

	public int setId(int id) {
		return this.id = id;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public int getStockId() {
		return stockId;
	}

	public void setStockId(int stockId) {
		this.stockId = stockId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
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

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public long getRelocationRequestTimestamp() {
		return relocationRequestTimestamp;
	}

	public void setRelocationRequestTimestamp(long relocationRequestTimestamp) {
		this.relocationRequestTimestamp = relocationRequestTimestamp;
	}

	public long getRelocationCompleteTimestamp() {
		return relocationCompleteTimestamp;
	}

	public void setRelocationCompleteTimestamp(long relocationCompleteTimestamp) {
		this.relocationCompleteTimestamp = relocationCompleteTimestamp;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public float getRelocationAmount() {
		return relocationAmount;
	}

	public void setRelocationAmount(float relocationAmount) {
		this.relocationAmount = relocationAmount;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public boolean isArrived() {
		return arrived;
	}

	public void setArrived(boolean arrived) {
		this.arrived = arrived;
	}

	public boolean isRejected() {
		return rejected;
	}

	public void setRejected(boolean rejected) {
		this.rejected = rejected;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public float getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(float productPrice) {
		this.productPrice = productPrice;
	}

	public RelocationStatus getStatus() {
		return status;
	}

	public void setStatus(RelocationStatus status) {
		this.status = status;
	}

	public int getSourceStoreId() {
		return sourceStoreId;
	}

	public void setSourceStoreId(int sourceStoreId) {
		this.sourceStoreId = sourceStoreId;
	}

	public String getSourceStoreName() {
		return sourceStoreName;
	}

	public void setSourceStoreName(String sourceStoreName) {
		this.sourceStoreName = sourceStoreName;
	}

	public int getDestStoreId() {
		return destStoreId;
	}

	public void setDestStoreId(int destStoreId) {
		this.destStoreId = destStoreId;
	}

	public String getDestStoreName() {
		return destStoreName;
	}

	public void setDestStoreName(String destStoreName) {
		this.destStoreName = destStoreName;
	}

	public String getSourceStoreCode() {
		return sourceStoreCode;
	}

	public void setSourceStoreCode(String sourceStoreCode) {
		this.sourceStoreCode = sourceStoreCode;
	}

	public String getDestStoreCode() {
		return destStoreCode;
	}

	public void setDestStoreCode(String destStoreCode) {
		this.destStoreCode = destStoreCode;
	}

}
