package com.kalafche.model;

import java.math.BigDecimal;
import java.util.List;

public class Revision {

	private Integer id;
	private Integer storeId;
	private String storeName;
	private long createTimestamp;
	private long submitTimestamp;
	private List<RevisionItem> revisionItems;
	private List<Employee> revisers;
	private List<DeviceModel> deviceModels;
	private Integer typeId;
	private String typeCode;
	private String typeName;
	private BigDecimal balance;
	private Integer totalActual;
	private Integer totalExpected;
	private String comment;
	private Integer createdByEmployeeId;
	private String createdByEmployeeName;
	private Integer updatedByEmployeeId;
	private String updatedByEmployeeName;
	private Boolean actualSynced;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public long getCreateTimestamp() {
		return createTimestamp;
	}

	public void setCreateTimestamp(long createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public long getSubmitTimestamp() {
		return submitTimestamp;
	}

	public void setSubmitTimestamp(long submitTimestamp) {
		this.submitTimestamp = submitTimestamp;
	}

	public List<RevisionItem> getRevisionItems() {
		return revisionItems;
	}

	public void setRevisionItems(List<RevisionItem> revisionItems) {
		this.revisionItems = revisionItems;
	}

	public List<Employee> getRevisers() {
		return revisers;
	}

	public void setRevisers(List<Employee> revisers) {
		this.revisers = revisers;
	}

	public List<DeviceModel> getDeviceModels() {
		return deviceModels;
	}

	public void setDeviceModels(List<DeviceModel> deviceModels) {
		this.deviceModels = deviceModels;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Integer getTotalActual() {
		return totalActual;
	}

	public void setTotalActual(Integer totalActual) {
		this.totalActual = totalActual;
	}

	public Integer getTotalExpected() {
		return totalExpected;
	}

	public void setTotalExpected(Integer totalExpected) {
		this.totalExpected = totalExpected;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getCreatedByEmployeeId() {
		return createdByEmployeeId;
	}

	public void setCreatedByEmployeeId(Integer createdByEmployeeId) {
		this.createdByEmployeeId = createdByEmployeeId;
	}

	public String getCreatedByEmployeeName() {
		return createdByEmployeeName;
	}

	public void setCreatedByEmployeeName(String createdByEmployeeName) {
		this.createdByEmployeeName = createdByEmployeeName;
	}

	public Integer getUpdatedByEmployeeId() {
		return updatedByEmployeeId;
	}

	public void setUpdatedByEmployeeId(Integer updatedByEmployeeId) {
		this.updatedByEmployeeId = updatedByEmployeeId;
	}

	public String getUpdatedByEmployeeName() {
		return updatedByEmployeeName;
	}

	public void setUpdatedByEmployeeName(String updatedByEmployeeName) {
		this.updatedByEmployeeName = updatedByEmployeeName;
	}

	public Boolean getActualSynced() {
		return actualSynced;
	}

	public void setActualSynced(Boolean actualSynced) {
		this.actualSynced = actualSynced;
	}

}
