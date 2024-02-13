package com.kalafche.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FormulaVariable {
	
	private String name;
	private Long startDateMilliseconds;
	private Long endDateMilliseconds;

}
