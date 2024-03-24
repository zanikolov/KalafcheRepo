package com.kalafche.model.expense;

import java.util.List;

import com.kalafche.model.DataReport;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseReport extends DataReport {

	private List<Expense> expenses;
	
}
