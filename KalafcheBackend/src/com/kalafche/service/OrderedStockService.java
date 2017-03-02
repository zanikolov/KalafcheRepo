package com.kalafche.service;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kalafche.dao.OrderedStockDao;
import com.kalafche.dao.StockOrderDao;
import com.kalafche.exceptions.CommonException;
import com.kalafche.model.stock.OrderedStock;

@Service
public class OrderedStockService {

	@Autowired
	OrderedStockDao orderedStockDao;
	
	@Autowired
	StockOrderDao stockOrderDao;
	
	@Autowired
	AuthenticationService authenticationService;
	
	@Transactional
	public int addStockToOrder(OrderedStock orderedStock) throws CommonException, SQLException {
		int currentUserId = authenticationService.getPrincipal().getId();
		long currentTime = System.currentTimeMillis();
		int stockOrderId = orderedStock.getStockOrderId();
		
		orderedStock.setCreatedBy(currentUserId);
		orderedStock.setCreateTimestamp(currentTime);
		
		int orderedStockId = orderedStockDao.insertOrderedStock(orderedStock);
		
		stockOrderDao.updateStockOrderUpdateTimestamp(stockOrderId, currentUserId, currentTime);
		
		return orderedStockId;
	}
	
}
