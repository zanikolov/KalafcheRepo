package com.kalafche.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FormulaVariable {
	
	private String name;
	private Long startDateMilliseconds;
	private Long endDateMilliseconds;
	private Double variableValue;
	
	public FormulaVariable(FormulaVariable variable) {
		this.name = variable.getName();
		this.startDateMilliseconds = variable.getStartDateMilliseconds();
		this.endDateMilliseconds = variable.getEndDateMilliseconds();
	}

}
