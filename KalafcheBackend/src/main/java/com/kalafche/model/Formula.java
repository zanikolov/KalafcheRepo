package com.kalafche.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Formula {
	
	private String storeId;
	private String expression;
	private List<FormulaVariable> variables;

}
