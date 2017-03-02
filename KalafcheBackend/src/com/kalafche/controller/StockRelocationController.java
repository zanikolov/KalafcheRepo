package com.kalafche.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.dao.StockDao;
import com.kalafche.dao.StockRelocationDao;
import com.kalafche.model.stock.Stock;
import com.kalafche.model.stock.StockRelocation;
import com.kalafche.service.StockRelocationService;

@CrossOrigin
@RestController
@RequestMapping({ "/service/stockRelocation" })
public class StockRelocationController {

	@Autowired
	private StockRelocationDao stockRelocationDao;
	
	@Autowired
	private StockDao stockDao;
	
	@Autowired
	private StockRelocationService stockRelocationService;

	@RequestMapping(value = { "/getAllStockRelocations" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public List<StockRelocation> getAllStockRelocations() {
		List<StockRelocation> stockRelocations = this.stockRelocationDao.getAllStockRelocations();

		return stockRelocations;
	}
	
	@RequestMapping(value = { "/getOutgoingStockRelocations" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public List<StockRelocation> getOutgoingStockRelocations(@RequestParam(value = "kalafcheStoreId") Integer kalafcheStoreId) {
		List<StockRelocation> stockRelocations = this.stockRelocationDao.getOutgoingStockRelocations(kalafcheStoreId);

		return stockRelocations;
	}
	
	@RequestMapping(value = { "/getIncomingStockRelocations" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public List<StockRelocation> getIncomingStockRelocations(@RequestParam(value = "kalafcheStoreId") Integer kalafcheStoreId) {
		List<StockRelocation> stockRelocations = this.stockRelocationDao.getIncomingStockRelocations(kalafcheStoreId);

		return stockRelocations;
	}

	@RequestMapping(value = { "/insertStockRelocation" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public void insertStockRelocation(@RequestBody StockRelocation stockRelocation) {		
		this.stockRelocationDao.insertStockRelocation(stockRelocation);
		this.stockDao.updateTheQuantitiyOfRelocatedStock(stockRelocation.getQuantity() * (-1), stockRelocation.getStockId());
	}	

	@RequestMapping(value = { "/setStockRelocationArrived" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public void setStockRelocationArrived(@RequestBody StockRelocation stockRelocation) {
		this.stockRelocationService.processArrivedRelocatedStock(stockRelocation);	
	}
	
	@RequestMapping(value = { "/approveStockRelocation" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public void approveStockRelocation(@RequestBody Integer stockRelocationId) {
		this.stockRelocationDao.approveStockRelocation(stockRelocationId);
	}
	
	@RequestMapping(value = { "/rejectStockRelocation" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public void rejectStockRelocation(@RequestBody StockRelocation stockRelocation) {
		this.stockRelocationDao.rejectStockRelocation(stockRelocation);
		this.stockDao.updateTheQuantitiyOfRelocatedStock(stockRelocation.getQuantity(), stockRelocation.getStockId());
	}
	
	@RequestMapping(value = { "/archiveStockRelocation" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public void archiveStockRelocation(@RequestBody Integer stockRelocationId) {
		this.stockRelocationDao.archiveStockRelocation(stockRelocationId);
	}
	
	@RequestMapping(value = { "/insertStockRelocationBatch" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public void insertStockRelocationBatch(@RequestParam(value = "fromKalafcheStoreId") int fromKalafcheStoreId, @RequestParam(value = "toKalafcheStoreId") int toKalafcheStoreId) {
		
//		List<Stock> stocks = stockDao.getAllApprovedStocks();
//		for (Stock s : stocks) {
//			if (s.getKalafcheStoreId() == fromKalafcheStoreId) {
//				StockRelocation sr = new StockRelocation();
//				sr.setRelocationRequestTimestamp(System.currentTimeMillis());
//				sr.setEmployeeId(3);
//				sr.setStockId(s.getId());
//				sr.setQuantity(s.getQuantity());
//				sr.setFromKalafcheStoreId(fromKalafcheStoreId);
//				sr.setToKalafcheStoreId(toKalafcheStoreId);
//				sr.setApproved(true);
//				sr.setArrived(false);
//				sr.setArchived(false);
//				sr.setRelocationCompleteTimestamp(System.currentTimeMillis());
//				
//				this.stockRelocationDao.insertStockRelocation(sr);
//				this.stockDao.updateTheQuantitiyOfRelocatedStock(sr.getQuantity() * (-1), sr.getStockId());
//				this.stockRelocationService.processArrivedRelocatedStock(sr);	
//			}
//		}
		
		List<StockRelocation> srList =  stockRelocationDao.getAllStockRelocationsTest();
		for (StockRelocation relocation : srList) {
			Stock stock = stockDao.getStockByInfo(relocation.getToKalafcheStoreId(), relocation.getDeviceModelId(), relocation.getItemProductCode());
			if (stock.getQuantity() < relocation.getQuantity()) {
				System.out.println(">>>>>>>");
				System.out.println(relocation.getItemProductCode() + "  " + relocation.getDeviceModelId() + "  " + stock.getQuantity() + "  " + relocation.getQuantity());
			}
			this.stockDao.updateTheQuantitiyOfRelocatedStock(relocation.getQuantity() * (-1), stock.getId());
		}
	}
}
