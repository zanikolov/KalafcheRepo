package com.kalafche.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.model.PastPeriodSaleReport;
import com.kalafche.model.Sale;
import com.kalafche.model.SaleItem;
import com.kalafche.model.SaleItemExcelReportRequest;
import com.kalafche.model.SaleReport;
import com.kalafche.model.SaleSplitReportRequest;
import com.kalafche.model.TotalSumReport;
import com.kalafche.model.TotalSumRequest;
import com.kalafche.service.SaleService;
import com.kalafche.service.fileutil.SaleItemExcelReportService;

@CrossOrigin
@RestController
@RequestMapping({ "/sale" })
public class SaleController {
	
	@Autowired
	private SaleService saleService;
	
	@Autowired 
	private SaleItemExcelReportService saleItemExcelReportService;
	
	@GetMapping
	public SaleReport searchSales(@RequestParam(value = "startDateMilliseconds") Long startDateMilliseconds, 
			@RequestParam(value = "endDateMilliseconds") Long endDateMilliseconds, @RequestParam(value = "storeIds") String storeIds) {
		return saleService.searchSales(startDateMilliseconds, endDateMilliseconds, storeIds);
	}
	
	@GetMapping("/saleItem")
	public SaleReport searchSaleItems(@RequestParam(value = "startDateMilliseconds") Long startDateMilliseconds,
			@RequestParam(value = "endDateMilliseconds") Long endDateMilliseconds,
			@RequestParam(value = "storeIds") String storeIds,
			@RequestParam(value = "productCode", required = false) String productCode,
			@RequestParam(value = "deviceBrandId", required = false) Integer deviceBrandId,
			@RequestParam(value = "deviceModelId", required = false) Integer deviceModelId,
			@RequestParam(value = "masterProductTypeId", required = false) Integer masterProductTypeId,
			@RequestParam(value = "productTypeId", required = false) Integer productTypeId,
			@RequestParam(value = "priceFrom", required = false) Float priceFrom,	
			@RequestParam(value = "priceTo", required = false) Float priceTo,	
			@RequestParam(value = "discountCampaignCode", required = false) String discountCampaignCode) {
		return saleService.searchSaleItems(startDateMilliseconds, endDateMilliseconds, storeIds, productCode,
				deviceBrandId, deviceModelId, masterProductTypeId, productTypeId, priceFrom, priceTo, discountCampaignCode);
	}
	
	@GetMapping("/store")
	public SaleReport searchSalesByStores(@RequestParam(value = "startDateMilliseconds") Long startDateMilliseconds, 
			@RequestParam(value = "endDateMilliseconds") Long endDateMilliseconds,
			@RequestParam(value = "productCode", required = false) String productCode, @RequestParam(value = "deviceBrandId", required = false) Integer deviceBrandId,
			@RequestParam(value = "deviceModelId", required = false) Integer deviceModelId, @RequestParam(value = "productTypeId", required = false) Integer productTypeId) {
		return saleService.searchSalesByStores(startDateMilliseconds, endDateMilliseconds, productCode, deviceBrandId, deviceModelId, productTypeId);
	}
	
	@GetMapping("/pastPeriods")
	public PastPeriodSaleReport searchSalesByStores(@RequestParam(value = "month") String month) {
		return saleService.searchSalesForPastPeriodsByStores(month);
	}
	
	@PutMapping
	public void insertSale(@RequestBody Sale sale) throws SQLException, InterruptedException {
		//TimeUnit.SECONDS.sleep(5);
		saleService.submitSale(sale);
	}
	
	@GetMapping("/{saleId}")
	public List<SaleItem> getSaleItems(@PathVariable(value = "saleId") Integer saleId) {
		return saleService.getSaleItems(saleId);
	}
	
	@PostMapping("/totalSum")
	public TotalSumReport getTotalSum(@RequestBody TotalSumRequest totalSumRequest) {
		return saleService.calculateTotalSum(totalSumRequest);
	}
	
	@PostMapping("/productTypeSplit")
	public ResponseEntity<byte[]> printProductTypeSplitReport(@RequestBody SaleSplitReportRequest saleSplitReportRequest) {
	
		byte[] excelBytes = saleService.getProductTypeSplitReport(saleSplitReportRequest);
	
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
	    String filename = "split-report.xlsx";
	    headers.setContentDispositionFormData(filename, filename);
	    headers.set("Content-Transfer-Encoding", "binary");
	    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
	    
	    ResponseEntity<byte[]> response = new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);	    
	    
	    return response;
	}
	
	@PostMapping("/transactionSplit")
	public ResponseEntity<byte[]> printTransactionSplitReport(@RequestBody SaleSplitReportRequest saleSplitReportRequest) {
		
		byte[] excelBytes = saleService.getTransactionSplitReport(saleSplitReportRequest);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
		String filename = "split-report.xlsx";
		headers.setContentDispositionFormData(filename, filename);
		headers.set("Content-Transfer-Encoding", "binary");
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		
		ResponseEntity<byte[]> response = new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);	    
		
		return response;
	}
	
	@PostMapping("/excel")
	public ResponseEntity<byte[]> generateExcel(@RequestBody SaleItemExcelReportRequest saleItemExcelReportRequest) {
		byte[] contents = saleItemExcelReportService.generateExcel(saleItemExcelReportRequest);
		
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
	    String filename = "sale-item-report.xlsx";
	    headers.setContentDispositionFormData(filename, filename);
	    headers.set("Content-Transfer-Encoding", "binary");
	    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
	    ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
	    return response;
		
	}
	
}
