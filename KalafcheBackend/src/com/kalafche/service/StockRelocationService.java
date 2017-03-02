package com.kalafche.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kalafche.dao.StockDao;
import com.kalafche.dao.StockRelocationDao;
import com.kalafche.model.stock.Stock;
import com.kalafche.model.stock.StockRelocation;

@Service
public class StockRelocationService {

	@Autowired
	StockDao stockDao;
	
	@Autowired
	StockRelocationDao stockRelocationDao;
	
	public void processArrivedRelocatedStock(StockRelocation stockRelocation) {
		this.stockRelocationDao.updateStockRelocationArrived(stockRelocation);
		
		Stock relocatedStock = this.stockDao.getStockById(stockRelocation.getStockId());
		relocatedStock.setQuantity(stockRelocation.getQuantity());
		relocatedStock.setKalafcheStoreId(stockRelocation.getToKalafcheStoreId());
		
		this.stockDao.approveStockForApproval(relocatedStock);
	}
}
