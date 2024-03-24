package com.kalafche.model.formula;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Attribute {

	private Integer id;
	private String name;
	private Integer typeId;
	private String typeName;
	private String typeCode;
	private Integer contextId;
	private String contextName;
	private String contextCode;
	private Long createTimestamp;
	private Long lastUpdateTimestamp;
	private Integer createdByEmployeeId;
	private String createdByEmployeeName;
	private Integer updatedByEmployeeId;
	private String updatedByEmployeeName;
	private Long fromTimestamp;
	private Long toTimestamp;
	private Integer offset;
	private Integer offsetStartDay;
	private Integer offsetEndDay;
	private Double value;
	
}
