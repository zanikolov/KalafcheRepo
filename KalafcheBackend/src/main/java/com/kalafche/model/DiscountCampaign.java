package com.kalafche.model;

public class DiscountCampaign {

	private Integer id;
	private Long createTimestamp;
	private String code;
	private String name;
	private String description;
	private Integer discountTypeId;
	private String discountTypeCode;
	private String discountTypeName;
	private String discountTypeSign;
	private String discountValue;
	private Integer createdByEmployeeId;
	private String createdByEmployeeName;
	private Integer updatedByEmployeeId;
	private String updatedByEmployeeName;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getCreateTimestamp() {
		return createTimestamp;
	}

	public void setCreateTimestamp(Long createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getDiscountTypeId() {
		return discountTypeId;
	}

	public void setDiscountTypeId(Integer discountTypeId) {
		this.discountTypeId = discountTypeId;
	}

	public String getDiscountTypeCode() {
		return discountTypeCode;
	}

	public void setDiscountTypeCode(String discountTypeCode) {
		this.discountTypeCode = discountTypeCode;
	}

	public String getDiscountTypeName() {
		return discountTypeName;
	}

	public void setDiscountTypeName(String discountTypeName) {
		this.discountTypeName = discountTypeName;
	}

	public String getDiscountTypeSign() {
		return discountTypeSign;
	}

	public void setDiscountTypeSign(String discountTypeSign) {
		this.discountTypeSign = discountTypeSign;
	}

	public String getDiscountValue() {
		return discountValue;
	}

	public void setDiscountValue(String discountValue) {
		this.discountValue = discountValue;
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

}
