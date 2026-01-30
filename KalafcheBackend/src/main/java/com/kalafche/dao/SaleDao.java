package com.kalafche.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import com.kalafche.model.DataReport;
import com.kalafche.model.PeriodInMillis;
import com.kalafche.model.sale.Sale;
import com.kalafche.model.sale.SaleItem;
import com.kalafche.model.sale.SalesByStore;
import com.kalafche.model.sale.SalesByStoreByDayByProductType;
import com.kalafche.model.sale.Transaction;
import com.kalafche.model.sale.TransactionsByStoreByDay;

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

	public abstract List<SalesByStore> searchSaleByStore(Long startDateMilliseconds, Long endDateMilliseconds, String storeIds, boolean monthlyReportGeneration);

	public abstract List<SalesByStoreByDayByProductType> generateProductTypeSplitReport(Long startDateMilliseconds, Long endDateMilliseconds,
			String storeId);

	public abstract List<TransactionsByStoreByDay> generateTransactionSplitReport(Long startDateMilliseconds, Long endDateMilliseconds,
			String storeId);

	public abstract DataReport selectSaleItemTotalAndCountByStoreId(Long startDateTime, Long endDateTime, Integer storeId);

	public abstract DataReport selectSaleItemWithCardPaymentTotalAndCount(Long startDateTime, Long endDateTime, Integer storeId);

	public abstract DataReport selectRefundedSaleItemTotalAndCount(Long startDateTime, Long endDateTime, Integer storeId);

	public abstract DataReport selectSaleItemTotalAndCountWithoutRefundByStoreId(Long startDateTime, Long endDateTime, Integer storeId);

	public abstract String selectStoreIdsForAllSalesInThePeriods(PeriodInMillis previousYearPeriodInMillis,
			PeriodInMillis previousMonthPeriodInMillis, PeriodInMillis selectedMonthPeriodInMillis);

	public abstract List<SalesByStore> searchSaleTurnoverForCompany(Long startDateMilliseconds, Long endDateMilliseconds,
			String storeIds);

	public abstract Integer insertTransaction(Transaction transaction) throws SQLException;

	public abstract void udpateTransaction(Integer transactionId, long updateTimestamp, Integer updateEmployeeId);

	public abstract Integer getSaleTransactionId(String referenceSaleUniqueSaleId);

	public abstract void updateSaleUSI(Integer saleId, String usi);
	
	public abstract Sale selectSaleByUniqueSaleId(String uniqueSaleId);

}
