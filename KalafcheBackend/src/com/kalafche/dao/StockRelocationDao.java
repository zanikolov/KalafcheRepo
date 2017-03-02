package com.kalafche.dao;

import java.util.List;

import com.kalafche.model.stock.StockRelocation;

public abstract interface StockRelocationDao {
	public abstract List<StockRelocation> getAllStockRelocations();
	
	public abstract List<StockRelocation> getIncomingStockRelocations(int kalafcheStoreId);
	
	public abstract List<StockRelocation> getOutgoingStockRelocations(int kalafcheStoreId);

	public abstract void insertStockRelocation(StockRelocation stockRelocation);

	public abstract void updateStockRelocationArrived(StockRelocation stockRelocation);

	public abstract void rejectStockRelocation(StockRelocation stockRelocation);

	public abstract void approveStockRelocation(Integer stockRelocationId);

	public abstract void archiveStockRelocation(Integer stockRelocationId);

	List<StockRelocation> getAllStockRelocationsTest();
}
