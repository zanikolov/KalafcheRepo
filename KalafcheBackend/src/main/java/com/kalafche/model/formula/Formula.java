package com.kalafche.model.formula;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Formula {

	private Integer id;
	private String name;
	private Long createTimestamp;
	private Long lastUpdateTimestamp;
	private Integer createdByEmployeeId;
	private String createdByEmployeeName;
	private Integer updatedByEmployeeId;
	private String updatedByEmployeeName;
	private String expression;
	private String description;
	private List<Attribute> attributes;
	
}
