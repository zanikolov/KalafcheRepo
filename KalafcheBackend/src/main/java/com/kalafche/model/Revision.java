package com.kalafche.model;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Revision {

	private Integer id;
	private Integer storeId;
	private String storeName;
	private long createTimestamp;
	private long lastUpdateTimestamp;
	private long submitTimestamp;
	private List<RevisionItem> revisionItems;
	private List<Employee> revisers;
	private List<DeviceModel> deviceModels;
	private Integer typeId;
	private String typeCode;
	private String typeName;
	private BigDecimal absoluteAmountBalance;
	private Integer absoluteCountBalance;
	private Integer shortageCount;
	private BigDecimal shortageAmount;
	private Integer surplusCount;
	private BigDecimal surplusAmount;
	private BigDecimal totalAmount;
	private String comment;
	private Integer createdByEmployeeId;
	private String createdByEmployeeName;
	private Integer updatedByEmployeeId;
	private String updatedByEmployeeName;
	private Boolean actualSynced;
	private Boolean isFinalized;
	
}
