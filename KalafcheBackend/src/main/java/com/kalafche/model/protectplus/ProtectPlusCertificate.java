package com.kalafche.model.protectplus;

import com.kalafche.model.LoyalCustomer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProtectPlusCertificate {

	private Integer id;
	private Integer certificateNumber;
	private Integer loyalCustomerId;
	private LoyalCustomer loyalCustomer;
	private Integer deviceModelId;
	private String deviceModelName;
	private Integer soldStoreId;
	private String soldStoreName;
	private Integer soldByEmployeeId;
	private String soldByEmployeeName;
	private Integer soldSaleId;
	private ProtectPlusCertificateStatus status;
	private Long validFromTimestamp;
	private Long validUntilTimestamp;
	private Integer activatedById;
	private String activatedByName;
	private Long activatedTimestamp;
	private Long confirmationEmailSentTimestamp;
	private String gdprConsentFileId;
	private String callRecordingFileId;
	private Boolean freeProtectorUsed;
	private Integer createdById;
	private String createdByName;
	private Long createdTimestamp;
	private Integer updatedById;
	private String updatedByName;
	private Long lastUpdateTimestamp;
}
