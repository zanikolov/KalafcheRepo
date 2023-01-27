package com.kalafche.dao;

import java.util.List;

import org.springframework.dao.DuplicateKeyException;

import com.kalafche.model.Stock;

public abstract interface StockDao {
	
	public abstract List<Stock> getAllStocks();

	public abstract List<Stock> getUnapprovedStocksByStoreId(int storeId);
	
	public abstract List<Stock> getAllApprovedStocks(int userStoreId, int selectedStoreId);
	
	public abstract void insertStockForApproval(Stock stock) throws DuplicateKeyException;
	
	public abstract void deleteStocksForApproval(List<Stock> stocks);

	public abstract void updateTheQuantitiyOfSoldStock(int itemId, int storeId);
	
	public abstract Integer getQuantitiyOfStock(int itemId, int storeId);
	
	public abstract Integer getCompanyQuantityOfStock(String productCode, int deviceModelId);

	public abstract Stock getStockById(int stockId);

	public abstract void approveStockForApproval(Stock stock);

	public abstract Stock getStockByInfo(int storeId, int deviceModelId, String productCode);
	
	public abstract List<Stock> getAllStocksForReport(int stockOrderId);

	public abstract Integer getQuantitiyOfStockInWH(String productCode, int deviceModelId);

	public abstract void insertOrUpdateQuantityOfInStock(Integer itemId, Integer storeId, Integer quantity);
	
	public abstract void updateQuantityOfApprovedStock(Integer itemId, Integer storeId, Integer quantity);

}
