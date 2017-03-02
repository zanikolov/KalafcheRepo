package com.kalafche.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.dao.StockDao;
import com.kalafche.model.stock.Stock;
import com.kalafche.service.StockService;

@CrossOrigin
@RestController
@RequestMapping({ "/service/stock" })
public class StockController {
	@Autowired
	private StockDao stockDao;
	
	@Autowired
	private StockService stockService;

	@RequestMapping(value = { "/getAllStocks" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public List<Stock> getAllStocks() {
		List<Stock> stocks = this.stockDao.getAllStocks();

		return stocks;
	}
	
	@RequestMapping(value = { "/updateStocksForApproval" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public List<Stock>  updateStocksForApproval(@RequestBody Map<String, List<Stock>> stocks) {
		List<Stock> failedAttempts = this.stockService.updateStocksForApproval(stocks);
		
		return failedAttempts;
	}
	
	@RequestMapping(value = { "/getUnapprovedStocksByKalafcheStoreId" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"kalafcheStoreId"})
	public List<Stock> getUnapprovedStocksByKalafcheStoreId(@RequestParam (value = "kalafcheStoreId") int kalafcheStoreId) {
		List<Stock> stocks = this.stockDao.getUnapprovedStocksByKalafcheStoreId(kalafcheStoreId);

		return stocks;
	}
	
	@RequestMapping(value = { "/approveStocksForApproval" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public void approveStocksForApproval(@RequestBody List<Stock> stocks) {
		this.stockService.approveStocksForApproval(stocks);
	}
	
	@RequestMapping(value = { "/getAllApprovedStocks" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public List<Stock> getAllApprovedStocks() {
		List<Stock> stocks = this.stockDao.getAllApprovedStocks();

		return stocks;
	}

	@RequestMapping(value = { "/getQuantitiyOfStock" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"itemId", "deviceModelId", "kalafcheStoreId"})
	public Integer getQuantitiyOfStock(@RequestParam (value = "itemId") int itemId, @RequestParam (value = "deviceModelId") int deviceModelId, @RequestParam (value = "kalafcheStoreId") int kalafcheStoreId) {
		Integer quantityInStock = this.stockDao.getQuantitiyOfStock(itemId, deviceModelId, kalafcheStoreId);

		return quantityInStock;
	}
	
	@RequestMapping(value = { "/getAllStocksForReport" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public List<Stock> getAllStocksForReport() {
		List<Stock> stocks = stockService.generateStockReport();

		return stocks;
	}
}
