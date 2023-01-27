package com.kalafche.model;

public class DiscountCode {

	private Integer id;
	private Integer code;
	private Integer discountCampaignId;
	private String discountCampaignName;
	private String discountCampaignCode;
	private Integer discountTypeId;
	private String discountTypeCode;
	private String discountTypeSign;
	private String discountValue;
	private Integer partnerId;
	private String partnerName;
	private Integer partnerStoreId;
	private String partnerStoreName;
	private Integer loyalCustomerId;
	private String loyalCustomerName;
	private boolean active;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDiscountCampaignName() {
		return discountCampaignName;
	}

	public void setDiscountCampaignName(String discountCampaignName) {
		this.discountCampaignName = discountCampaignName;
	}

	public String getDiscountCampaignCode() {
		return discountCampaignCode;
	}

	public void setDiscountCampaignCode(String discountCampaignCode) {
		this.discountCampaignCode = discountCampaignCode;
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

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Integer getDiscountCampaignId() {
		return discountCampaignId;
	}

	public void setDiscountCampaignId(Integer discountCampaignId) {
		this.discountCampaignId = discountCampaignId;
	}

	public Integer getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(Integer partnerId) {
		this.partnerId = partnerId;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
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

	public Integer getLoyalCustomerId() {
		return loyalCustomerId;
	}

	public void setLoyalCustomerId(Integer loyalCustomerId) {
		this.loyalCustomerId = loyalCustomerId;
	}

	public String getLoyalCustomerName() {
		return loyalCustomerName;
	}

	public void setLoyalCustomerName(String loyalCustomerName) {
		this.loyalCustomerName = loyalCustomerName;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
