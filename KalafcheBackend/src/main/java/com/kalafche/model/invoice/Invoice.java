package com.kalafche.model.invoice;

import java.math.BigDecimal;
import java.util.List;

import com.kalafche.model.Company;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Invoice {

	private Integer id;
	private Long createTimestamp;
	private Integer createdByEmployeeId;
	private String createdByEmployeeName;
	private String number;
	private Company issuer;
	private Company recipient;
	private Long startDateTimestamp;
	private Long endDateTimestamp;
	private Long issueDate;
	private Integer typeId;
	private String typeCode;
	private String typeName;
	private List<InvoiceItem> invoiceItems;
	private BigDecimal base;
	private BigDecimal dueVAT;
	private BigDecimal totalAmount;
	private BigDecimal vatRate;
}
