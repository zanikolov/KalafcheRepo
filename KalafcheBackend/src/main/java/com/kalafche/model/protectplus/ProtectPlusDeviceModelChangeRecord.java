package com.kalafche.model.protectplus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProtectPlusDeviceModelChangeRecord {

	private Integer id;
	private String storeName;
	private String employeeName;
	private String oldDeviceModelName;
	private String newDeviceModelName;
	private Boolean adminOverride;
	private Long createdTimestamp;
}
