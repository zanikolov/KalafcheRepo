package com.kalafche.model;

public class Partner {
	private Integer id;
	private String name;
	private Integer partnerStoreId;
	private String partnerStoreName;
	private String phoneNumber;
	private Integer discountCodeId;
	private Integer discountCodeCode;

	public Integer getId() {
		return this.id;
	}
	
	public Integer setId(Integer id) {
		return this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPartnerStoreId() {
		return partnerStoreId;
	}

	public void setPartnerStoreId(Integer partnerStoreId) {
		this.partnerStoreId = partnerStoreId;
	}

	public String getPartnerStoreName() {
		return partnerStoreName;
	}

	public void setPartnerStoreName(String partnerStoreName) {
		this.partnerStoreName = partnerStoreName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
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

}
