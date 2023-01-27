package com.kalafche.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.dao.OrderedStockDao;
import com.kalafche.exceptions.CommonException;
import com.kalafche.model.OrderedStock;
import com.kalafche.service.OrderedStockService;

@CrossOrigin
@RestController
@RequestMapping({ "/service/orderedStock" })
public class OrderedStockController {

//	@Autowired
//	private OrderedStockDao orderedStockDao;
//	
//	@Autowired
//	private OrderedStockService orderedStockService;
//
//	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.GET })
//	public List<OrderedStock> getAllOrderedStockForStockOrder(@RequestParam(value = "stockOrderId") int stockOrderId) {
//		List<OrderedStock> orderedStockList = orderedStockDao.getAllOrderedStockForStockOrder(stockOrderId);
//
//		return orderedStockList;
//	}
//	
//	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.DELETE })
//	public void deleteOrderedStock(@RequestParam(value = "orderedStockId") int orderedStockId, @RequestParam(value = "stockOrderId") int stockOrderId) {
//		orderedStockDao.deleteOrderedStock(orderedStockId, stockOrderId);
//	}
//	
//	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.PUT })
//	public int insertOrderedStock(@RequestBody OrderedStock orderedStock) {
//		int id = 0;
//		try {
//			id = orderedStockService.addStockToOrder(orderedStock);
//		} catch (CommonException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return id;
//	}
}