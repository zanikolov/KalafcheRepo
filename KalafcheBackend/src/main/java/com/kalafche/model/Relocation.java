package com.kalafche.model;

import java.math.BigDecimal;

import com.kalafche.BaseModel;
import com.kalafche.enums.RelocationStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Relocation extends BaseModel {
	private int id;
	private int stockId;
	private int itemId;
	private String productName;
	private String productCode;
	private float productPrice;
	private int sourceStoreId;
	private String sourceStoreName;
	private String sourceStoreCode;
	private int destStoreId;
	private String destStoreName;
	private String destStoreCode;
	private int deviceModelId;
	private String deviceModelName;
	private int deviceBrandId;
	private long relocationRequestTimestamp;
	private long relocationCompleteTimestamp;
	private int employeeId;
	private String employeeName;
	private boolean approved;
	private boolean rejected;
	private boolean arrived;
	private int quantity;
	private BigDecimal relocationAmount;
	private boolean archived;
	private RelocationStatus status;
	private BigDecimal itemPrice;

}
