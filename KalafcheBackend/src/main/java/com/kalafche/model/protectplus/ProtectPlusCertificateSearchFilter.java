package com.kalafche.model.protectplus;

public class ProtectPlusCertificateSearchFilter {

	private Integer certificateNumber;
	private String phoneNumber;
	private Integer storeId;
	private Integer deviceBrandId;
	private Integer deviceModelId;
	private Long activationFromTimestamp;
	private Long activationToTimestamp;
	private Long usageFromTimestamp;
	private Long usageToTimestamp;
	private Integer usageCount;
	private Long callFromTimestamp;
	private Long callToTimestamp;
	private Integer callCount;
	private Long validUntilFromTimestamp;
	private Long validUntilToTimestamp;
	private Boolean freeProtectorUsed;
	private Boolean freeDisplayReplacementServiceUsed;
	private Boolean freeBatteryReplacementServiceUsed;
	private Boolean deviceModelChangeUsed;

	public Integer getCertificateNumber() {
		return certificateNumber;
	}

	public void setCertificateNumber(Integer certificateNumber) {
		this.certificateNumber = certificateNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public Integer getDeviceBrandId() {
		return deviceBrandId;
	}

	public void setDeviceBrandId(Integer deviceBrandId) {
		this.deviceBrandId = deviceBrandId;
	}

	public Integer getDeviceModelId() {
		return deviceModelId;
	}

	public void setDeviceModelId(Integer deviceModelId) {
		this.deviceModelId = deviceModelId;
	}

	public Long getActivationFromTimestamp() {
		return activationFromTimestamp;
	}

	public void setActivationFromTimestamp(Long activationFromTimestamp) {
		this.activationFromTimestamp = activationFromTimestamp;
	}

	public Long getActivationToTimestamp() {
		return activationToTimestamp;
	}

	public void setActivationToTimestamp(Long activationToTimestamp) {
		this.activationToTimestamp = activationToTimestamp;
	}

	public Long getUsageFromTimestamp() {
		return usageFromTimestamp;
	}

	public void setUsageFromTimestamp(Long usageFromTimestamp) {
		this.usageFromTimestamp = usageFromTimestamp;
	}

	public Long getUsageToTimestamp() {
		return usageToTimestamp;
	}

	public void setUsageToTimestamp(Long usageToTimestamp) {
		this.usageToTimestamp = usageToTimestamp;
	}

	public Integer getUsageCount() {
		return usageCount;
	}

	public void setUsageCount(Integer usageCount) {
		this.usageCount = usageCount;
	}

	public Long getCallFromTimestamp() {
		return callFromTimestamp;
	}

	public void setCallFromTimestamp(Long callFromTimestamp) {
		this.callFromTimestamp = callFromTimestamp;
	}

	public Long getCallToTimestamp() {
		return callToTimestamp;
	}

	public void setCallToTimestamp(Long callToTimestamp) {
		this.callToTimestamp = callToTimestamp;
	}

	public Integer getCallCount() {
		return callCount;
	}

	public void setCallCount(Integer callCount) {
		this.callCount = callCount;
	}

	public Long getValidUntilFromTimestamp() {
		return validUntilFromTimestamp;
	}

	public void setValidUntilFromTimestamp(Long validUntilFromTimestamp) {
		this.validUntilFromTimestamp = validUntilFromTimestamp;
	}

	public Long getValidUntilToTimestamp() {
		return validUntilToTimestamp;
	}

	public void setValidUntilToTimestamp(Long validUntilToTimestamp) {
		this.validUntilToTimestamp = validUntilToTimestamp;
	}

	public Boolean getFreeProtectorUsed() {
		return freeProtectorUsed;
	}

	public void setFreeProtectorUsed(Boolean freeProtectorUsed) {
		this.freeProtectorUsed = freeProtectorUsed;
	}

	public Boolean getFreeDisplayReplacementServiceUsed() {
		return freeDisplayReplacementServiceUsed;
	}

	public void setFreeDisplayReplacementServiceUsed(Boolean freeDisplayReplacementServiceUsed) {
		this.freeDisplayReplacementServiceUsed = freeDisplayReplacementServiceUsed;
	}

	public Boolean getFreeBatteryReplacementServiceUsed() {
		return freeBatteryReplacementServiceUsed;
	}

	public void setFreeBatteryReplacementServiceUsed(Boolean freeBatteryReplacementServiceUsed) {
		this.freeBatteryReplacementServiceUsed = freeBatteryReplacementServiceUsed;
	}

	public Boolean getDeviceModelChangeUsed() {
		return deviceModelChangeUsed;
	}

	public void setDeviceModelChangeUsed(Boolean deviceModelChangeUsed) {
		this.deviceModelChangeUsed = deviceModelChangeUsed;
	}
}
