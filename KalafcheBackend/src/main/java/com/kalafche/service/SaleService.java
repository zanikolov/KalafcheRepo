package com.kalafche.service;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.model.DailyReportData;
import com.kalafche.model.PastPeriodSaleReport;
import com.kalafche.model.Sale;
import com.kalafche.model.SaleItem;
import com.kalafche.model.SaleReport;
import com.kalafche.model.SaleSplitReportRequest;
import com.kalafche.model.TotalSumReport;
import com.kalafche.model.TotalSumRequest;

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

	public DailyReportData getSaleItemDailyReportData(Long startDateTime, Long endDateTime, Integer storeId);

	public DailyReportData getCardPaymentDailyReportData(Long startDateTime, Long endDateTime, Integer storeId);

	public DailyReportData getRefundedSaleItemDailyReportData(Long startDateTime, Long endDateTime, Integer storeId);
	
	

}
