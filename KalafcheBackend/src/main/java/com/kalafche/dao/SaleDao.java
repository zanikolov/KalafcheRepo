package com.kalafche.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import com.kalafche.model.DailyReportData;
import com.kalafche.model.Sale;
import com.kalafche.model.SaleItem;
import com.kalafche.model.SalesByStore;
import com.kalafche.model.SalesByStoreByDayByProductType;
import com.kalafche.model.TransactionsByStoreByDay;

public abstract interface SaleDao {

	public abstract Integer insertSale(Sale sale) throws SQLException;

	public abstract List<Sale> searchSales(Long startDateMilliseconds, Long endDateMilliseconds,
			String storeIds);

	public abstract List<SaleItem> getSaleItemsBySaleId(Integer saleId);

	public abstract void insertSaleItem(SaleItem saleItem);

	public abstract List<SaleItem> searchSaleItems(Long startDateMilliseconds, Long endDateMilliseconds,
			String storeIds, String productCode, Integer deviceBrandId, Integer deviceModelId,
			Integer productTypeId, Integer masterProductTypeId, Float priceFrom, Float priceTo, String discountCampaignCode);

	public abstract void updateRefundedSaleItem(Integer saleItemId);

	public abstract BigDecimal getSaleItemPrice(Integer saleItemId);

	public abstract List<SalesByStore> searchSaleByStore(Long startDateMilliseconds, Long endDateMilliseconds);

	public abstract List<SalesByStoreByDayByProductType> generateProductTypeSplitReport(Long startDateMilliseconds, Long endDateMilliseconds,
			String storeId);

	public abstract List<TransactionsByStoreByDay> generateTransactionSplitReport(Long startDateMilliseconds, Long endDateMilliseconds,
			String storeId);

	public abstract DailyReportData selectSaleItemTotalAndCount(Long startDateTime, Long endDateTime, Integer storeId);

	public abstract DailyReportData selectSaleItemWithCardPaymentTotalAndCount(Long startDateTime, Long endDateTime, Integer storeId);

	public abstract DailyReportData selectRefundedSaleItemTotalAndCount(Long startDateTime, Long endDateTime, Integer storeId);

}
