package com.kalafche.model.protectplus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProtectPlusCertificateSearchResult {

	private Integer id;
	private Integer certificateNumber;
	private ProtectPlusCertificateStatus status;
	private Long validUntilTimestamp;
	private Boolean freeProtectorUsed;
	private Boolean freeDisplayReplacementServiceUsed;
	private Boolean freeBatteryReplacementServiceUsed;
	private Boolean deviceModelChangeUsed;
	private Integer usageCount;
	private String gdprConsentFileId;
	private String loyalCustomerEmail;
	private String customerName;
	private String phoneNumber;
	private Integer deviceBrandId;
	private Integer deviceModelId;
	private String deviceModelName;
	private String soldStoreName;
	private String soldByEmployeeName;
	private Long createdTimestamp;
}
