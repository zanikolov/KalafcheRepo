package com.kalafche.model.sale;

import java.math.BigDecimal;
import java.util.List;

import com.kalafche.model.DataReport;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaleReport extends DataReport {

	private List<Sale> sales;
	private List<SaleItem> saleItems;
	private List<SalesByStore> salesByStores;
	private List<SalesForPastPeriodsByStore> salesForPastPeriodsByStore;
	private Integer warehouseQuantity;
	private Integer companyQuantity;
	private Integer itemCount;
	private Integer transactionCount;
	private Integer bonusPts;
	private BigDecimal spt;
	private BigDecimal sqs;
	private BigDecimal attachRate;
	private String storeName;
	private Long startDate;
	private Long endDate;
	private String deviceModelName;
	private String deviceBrandName;
	private String productCode;

	public SaleReport() {
	}
	
	public SaleReport(List<Sale> sales, List<SaleItem> saleItems, List<SalesByStore> saleByStore,
			Integer warehouseQuantity, Integer companyQuantity,
			List<SalesForPastPeriodsByStore> salesForPastPeriodsByStore) {
		this.sales = sales;
		this.setSaleItems(saleItems);
		this.salesByStores = saleByStore;
		this.salesForPastPeriodsByStore = salesForPastPeriodsByStore;
		this.warehouseQuantity = warehouseQuantity;
		this.companyQuantity = companyQuantity;
	}

}
