package com.kalafche.model.expense;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseType {

	private Integer id;
	private String code;
	private String name;
	private Boolean isAdmin;
	private Integer taxId;
	private String taxCode;
	private String taxName;

}
