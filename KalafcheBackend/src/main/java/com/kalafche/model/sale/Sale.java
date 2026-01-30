package com.kalafche.model.sale;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Sale {
	
	private Integer id;
	private String uniqueSaleId;
	private Integer itemId;
	private Integer stockId;
	private String productName;
	private String productCode;
	private Integer storeId;
	private String storeName;
	private Integer deviceModelId;
	private String deviceModelName;
	private Integer deviceBrandId;
	private long saleTimestamp;
	private Integer partnerId;
	private Integer employeeId;
	private String employeeName;
	private BigDecimal amount;
	private BigDecimal amountEuro;
	private String partnerCode;
	private List<SaleItem> saleItems;
	private Boolean isCashPayment;
	private int bonusPts;
	private Boolean isInitial;
	private Integer transactionId;
	private String replacementSaleUSI;

	public Sale() {
	}
	
	public Sale(Integer itemId, Integer employeeId, Integer storeId, Integer saleTimestamp, Integer partnerId) {
		this.itemId = itemId;
		this.employeeId = employeeId;
		this.storeId = storeId;
		this.saleTimestamp = saleTimestamp;
		this.partnerId = partnerId;
	}
	
}
