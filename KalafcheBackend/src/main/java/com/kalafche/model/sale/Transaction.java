package com.kalafche.model.sale;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transaction {

	private Integer id;
	private long createTimestamp;
	private long lastUpdateTimestamp;
	private Integer createdByEmployeeId;
	private String createdByEmployeeName;
	private Integer updatedByEmployeeId;
	private String updatedByEmployeeName;
	private Integer storeId;
	private String storeName;
	private String comment;

	public Transaction() {}
	
	public Transaction(long createTimestamp, Integer createdByEmployeeId, Integer storeId) {
		this.createTimestamp = createTimestamp;
		this.createdByEmployeeId = createdByEmployeeId;
		this.storeId = storeId;
	}
}
