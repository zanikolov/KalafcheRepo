package com.kalafche.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tax {

	private int id;
	private String code;
	private String name;
	private int rate;
	private boolean isApplicableOnExpense;
}
