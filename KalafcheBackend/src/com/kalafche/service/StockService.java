package com.kalafche.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.kalafche.dao.StockDao;
import com.kalafche.dao.StockOrderDao;
import com.kalafche.exceptions.CommonException;
import com.kalafche.model.stock.Stock;
import com.kalafche.model.stock.StockOrder;

@Service
public class StockService {
	
	@Autowired
	StockDao stockDao;
	
	@Autowired
	StockOrderDao stockOrderDao;
	
	public List<Stock> updateStocksForApproval(Map<String, List<Stock>> stocks) {				
		List<Stock> newItemsForApproval = stocks.get("addedStocksForApproval");
		List<Stock> deletedItemsForApproval = stocks.get("deletedStocksForApproval");
		
		List<Stock> failedAttempts = insertStocksForApproval(newItemsForApproval);
		stockDao.deleteStocksForApproval(deletedItemsForApproval);
		
		return failedAttempts;
	}
	
	public void approveStocksForApproval(List<Stock> stocks) {		
		for (Stock stock : stocks) {
			stockDao.approveStockForApproval(stock);
		}	
		stockDao.deleteStocksForApproval(stocks);
	}
	
	public void updateTheQuantitiyOfSoldStock(int stockId) {				
		stockDao.updateTheQuantitiyOfSoldStock(stockId);
	}
	
	private List<Stock> insertStocksForApproval(List<Stock> stocks) {
		List<Stock> failedAttempts = new ArrayList<Stock>();
		for (Stock stock : stocks) {
			try {
				stockDao.insertStockForApproval(stock);
			} catch (DuplicateKeyException e) {
				failedAttempts.add(stock);
				System.out.println(failedAttempts.size());
			}
		}
		
		return failedAttempts;
	}
	
	public List<Stock> generateStockReport() {
		StockOrder stockOrder;
		int stockOrderId = 0;
		try {
			stockOrder = stockOrderDao.getCurrentStockOrder();
			stockOrderId = stockOrder.getId();
		} catch (CommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Stock> stockList = stockDao.getAllStocksForReport(stockOrderId);
		
		return stockList;
	}
}
