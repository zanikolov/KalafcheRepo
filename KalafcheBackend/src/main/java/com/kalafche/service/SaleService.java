package com.kalafche.service;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.model.DataReport;
import com.kalafche.model.sale.PastPeriodSaleReport;
import com.kalafche.model.sale.Sale;
import com.kalafche.model.sale.SaleItem;
import com.kalafche.model.sale.SaleReport;
import com.kalafche.model.sale.SaleSplitReportRequest;
import com.kalafche.model.sale.TotalSumReport;
import com.kalafche.model.sale.TotalSumRequest;

public interface SaleService {

	public void submitSale(Sale sale) throws SQLException;

	public SaleReport searchSales(Long startDateMilliseconds, Long endDateMilliseconds, String storeIds);

	public List<SaleItem> getSaleItems(Integer saleId);

	public SaleReport searchSaleItems(Long startDateMilliseconds, Long endDateMilliseconds, String storeIds,
			String productCode, Integer deviceBrandId, Integer deviceModelId, Integer productTypeId, Integer masterProductTypeId, Float priceFrom, Float priceTo, String discountCampaignCode);

	public TotalSumReport calculateTotalSum(TotalSumRequest totalSumRequest);

	public SaleReport searchSalesByStores(Long startDateMilliseconds, Long endDateMilliseconds, String productCode,
			Integer deviceBrandId, Integer deviceModelId, Integer productTypeId);

	public PastPeriodSaleReport searchSalesForPastPeriodsByStores(String month);

	public byte[] getProductTypeSplitReport(SaleSplitReportRequest saleSplitReportRequest);

	public byte[] getTransactionSplitReport(SaleSplitReportRequest saleSplitReportRequest);

	public TotalSumReport calculateTotalSum(List<SaleItem> selectedSaleItems);

	public DataReport getSaleItemDailyReportData(Long startDateTime, Long endDateTime, Integer storeId);

	public DataReport getCardPaymentDailyReportData(Long startDateTime, Long endDateTime, Integer storeId);

	public DataReport getRefundedSaleItemDailyReportData(Long startDateTime, Long endDateTime, Integer storeId);

	public DataReport getSaleItemTotalAndCountWithoutRefundByStoreId(Long startDateTime, Long endDateTime, Integer storeId);
	
}
