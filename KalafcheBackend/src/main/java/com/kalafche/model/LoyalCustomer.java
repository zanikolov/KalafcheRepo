package com.kalafche.model;

public class LoyalCustomer {

	private Integer id;
	private String name;
	private String phoneNumber;
	private String email;
	private Integer discountCodeId;
	private Integer discountCodeCode;
	private Integer createdById;
	private String createdByName;
	private Long createdTimestamp;
	private Integer updatedById;
	private String updatedByName;
	private Long lastUpdateTimestamp;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public Integer getCreatedById() {
		return createdById;
	}

	public void setCreatedById(Integer createdById) {
		this.createdById = createdById;
	}

	public String getCreatedByName() {
		return createdByName;
	}

	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}

	public Long getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Long createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public Integer getUpdatedById() {
		return updatedById;
	}

	public void setUpdatedById(Integer updatedById) {
		this.updatedById = updatedById;
	}

	public String getUpdatedByName() {
		return updatedByName;
	}

	public void setUpdatedByName(String updatedByName) {
		this.updatedByName = updatedByName;
	}

	public Long getLastUpdateTimestamp() {
		return lastUpdateTimestamp;
	}

	public void setLastUpdateTimestamp(Long lastUpdateTimestamp) {
		this.lastUpdateTimestamp = lastUpdateTimestamp;
	}

}
