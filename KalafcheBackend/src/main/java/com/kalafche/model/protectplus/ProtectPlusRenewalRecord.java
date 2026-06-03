package com.kalafche.model.protectplus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProtectPlusRenewalRecord {

	private Integer id;
	private Integer saleId;
	private String storeName;
	private String employeeName;
	private Long oldValidUntilTimestamp;
	private Long newValidUntilTimestamp;
	private String source;
	private Long createdTimestamp;
}
