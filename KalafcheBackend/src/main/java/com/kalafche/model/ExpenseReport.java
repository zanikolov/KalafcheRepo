package com.kalafche.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseReport extends DataReport {

	private List<Expense> expenses;
	
}
