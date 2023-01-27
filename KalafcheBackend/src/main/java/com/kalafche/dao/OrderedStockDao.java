package com.kalafche.dao;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.exceptions.CommonException;
import com.kalafche.model.OrderedStock;

public interface OrderedStockDao {

	public abstract int insertOrderedStock(OrderedStock orderedStock) throws CommonException, SQLException;
	
	public abstract List<OrderedStock> getAllOrderedStockForStockOrder(int orderId);
	
	public abstract void deleteOrderedStock(int orderedStockId, int stockOrderId);
	
}
