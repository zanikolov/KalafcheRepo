package com.kalafche.model.invoice;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceItem {
	
	private String name;
	private Integer quantity;
	private BigDecimal itemPrice;
	private BigDecimal amount;
}
