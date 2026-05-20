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
	private String customerName;
	private String phoneNumber;
	private String deviceModelName;
	private String soldStoreName;
	private String soldByEmployeeName;
	private Long createdTimestamp;
}
