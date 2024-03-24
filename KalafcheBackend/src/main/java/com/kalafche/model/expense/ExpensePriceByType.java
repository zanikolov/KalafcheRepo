package com.kalafche.model.expense;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpensePriceByType extends ExpenseType {

	private BigDecimal price;

}
