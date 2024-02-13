package com.kalafche.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CalculationResponse {

	private Integer storeId;
	private String storeName;
	private Double formulaOutcome;
	private List<FormulaVariable> variables;
	
}
