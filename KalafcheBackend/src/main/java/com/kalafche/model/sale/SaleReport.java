package com.kalafche.model.sale;

import java.math.BigDecimal;
import java.util.List;

import com.kalafche.model.DataReport;

public class SaleReport extends DataReport {

	private List<Sale> sales;
	private List<SaleItem> saleItems;
	private List<SalesByStore> salesByStores;
	private List<SalesForPastPeriodsByStore> salesForPastPeriodsByStore;
	private Integer warehouseQuantity;
	private Integer companyQuantity;
	private Integer itemCount;
	private Integer transactionCount;
	private BigDecimal spt;
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

	public List<Sale> getSales() {
		return sales;
	}

	public void setSales(List<Sale> sales) {
		this.sales = sales;
	}

	public List<SaleItem> getSaleItems() {
		return saleItems;
	}

	public void setSaleItems(List<SaleItem> saleItems) {
		this.saleItems = saleItems;
	}

	public List<SalesByStore> getSalesByStores() {
		return salesByStores;
	}

	public void setSalesByStores(List<SalesByStore> salesByStores) {
		this.salesByStores = salesByStores;
	}

	public List<SalesForPastPeriodsByStore> getSalesForPastPeriodsByStore() {
		return salesForPastPeriodsByStore;
	}

	public void setSalesForPastPeriodsByStore(List<SalesForPastPeriodsByStore> salesForPastPeriodsByStore) {
		this.salesForPastPeriodsByStore = salesForPastPeriodsByStore;
	}

	public Integer getWarehouseQuantity() {
		return warehouseQuantity;
	}

	public void setWarehouseQuantity(Integer warehouseQuantity) {
		this.warehouseQuantity = warehouseQuantity;
	}

	public Integer getCompanyQuantity() {
		return companyQuantity;
	}

	public void setCompanyQuantity(Integer companyQuantity) {
		this.companyQuantity = companyQuantity;
	}

	public Integer getItemCount() {
		return itemCount;
	}

	public void setItemCount(Integer itemCount) {
		this.itemCount = itemCount;
	}

	/**
	 * @return the transactionCount
	 */
	public Integer getTransactionCount() {
		return transactionCount;
	}

	/**
	 * @param transactionCount the transactionCount to set
	 */
	public void setTransactionCount(Integer transactionCount) {
		this.transactionCount = transactionCount;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}

	public String getDeviceModelName() {
		return deviceModelName;
	}

	public void setDeviceModelName(String deviceModelName) {
		this.deviceModelName = deviceModelName;
	}

	public String getDeviceBrandName() {
		return deviceBrandName;
	}

	public void setDeviceBrandName(String deviceBrandName) {
		this.deviceBrandName = deviceBrandName;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	/**
	 * @return the spt
	 */
	public BigDecimal getSpt() {
		return spt;
	}

	/**
	 * @param spt the spt to set
	 */
	public void setSpt(BigDecimal spt) {
		this.spt = spt;
	}

}
