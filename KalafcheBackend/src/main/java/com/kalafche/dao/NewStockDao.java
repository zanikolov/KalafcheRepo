package com.kalafche.dao;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.model.NewStock;

public interface NewStockDao {

	List<NewStock> getAllNewStocks();

	void deleteNewStock(Integer newStockId);

	void insertOrUpdateQuantityOfNewStock(Integer productId, Integer deviceModelId, Integer quantity);

	void updateQuantityOfNewStock(Integer productId, Integer deviceModelId, Integer quantity);

	void insertNewStockFromFile(String barcode, Integer quantity, Integer importId, Integer storeId);

	Integer insertNewStockImport(Long importTimestamp, Integer employeeId, String fileName) throws SQLException;

	void insertNewStock(Integer productId, Integer deviceModelId, Integer quantity, Integer storeId);

	List<NewStock> getNewStockByStoreId(Integer storeId);

}
