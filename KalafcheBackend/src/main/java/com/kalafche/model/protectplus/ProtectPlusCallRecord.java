package com.kalafche.model.protectplus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProtectPlusCallRecord {

	private Integer id;
	private Integer protectPlusCertificateId;
	private Integer storeId;
	private String storeName;
	private Integer employeeId;
	private String employeeName;
	private String callRecordingFileId;
	private String callRecordingFileName;
	private String note;
	private Long createdTimestamp;
}
