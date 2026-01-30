package com.kalafche.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.util.Lists;
import com.kalafche.dao.DeviceBrandDao;
import com.kalafche.dao.DeviceModelDao;
import com.kalafche.dao.DiscountDao;
import com.kalafche.dao.ItemDao;
import com.kalafche.dao.SaleDao;
import com.kalafche.dao.StoreDao;
import com.kalafche.exceptions.DomainObjectNotFoundException;
import com.kalafche.exceptions.NoRefundedItemException;
import com.kalafche.model.DataReport;
import com.kalafche.model.PeriodInMillis;
import com.kalafche.model.StoreDto;
import com.kalafche.model.comparator.SalesByStoreByStoreIdComparator;
import com.kalafche.model.discount.DiscountCode;
import com.kalafche.model.employee.Employee;
import com.kalafche.model.product.Item;
import com.kalafche.model.sale.PastPeriodSaleReport;
import com.kalafche.model.sale.PastPeriodTurnover;
import com.kalafche.model.sale.Sale;
import com.kalafche.model.sale.SaleItem;
import com.kalafche.model.sale.SaleReport;
import com.kalafche.model.sale.SaleSplitReportRequest;
import com.kalafche.model.sale.SalesByStore;
import com.kalafche.model.sale.SalesByStoreByDayByProductType;
import com.kalafche.model.sale.TotalSumRequest;
import com.kalafche.model.sale.Transaction;
import com.kalafche.model.sale.TransactionsByStoreByDay;
import com.kalafche.service.CurrencyService;
import com.kalafche.service.DateService;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.EntityService;
import com.kalafche.service.SaleService;
import com.kalafche.service.StockService;
import com.kalafche.service.fileutil.SplitReportExcelWriterService;

@Service
public class SaleServiceImpl implements SaleService {

	@Autowired
	EmployeeService employeeService;
	
	@Autowired
	EntityService entityService;
	
	@Autowired
	StockService stockService;
	
	@Autowired
	DateService dateService;
	
	@Autowired
	SplitReportExcelWriterService splitReportExcelWriterService;
	
	@Autowired
	CurrencyService currencyService;
	
	@Autowired
	SaleDao saleDao;
	
	@Autowired
	ItemDao itemDao;
	
	@Autowired
	StoreDao storeDao;
	
	@Autowired
	DeviceBrandDao deviceBrandDao;
	
	@Autowired
	DeviceModelDao deviceModelDao;
	
	@Autowired
	DiscountDao discountDao;
	
	private static final TimeZone timeZone = TimeZone.getTimeZone("Europe/Sofia");

	private static final BigDecimal ZERO = BigDecimal.ZERO;
	private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
	
	@Override
	public Sale submitSale(Sale sale) throws SQLException {
		Integer saleEmployeeId;
		if (sale.getEmployeeId() != null) {
			saleEmployeeId = sale.getEmployeeId();
		} else {
			Employee loggedInEmployee = employeeService.getLoggedInEmployee();
			saleEmployeeId = loggedInEmployee.getId();
		}
		long currentMillis = dateService.getCurrentMillisBGTimezone();
		
		if (sale.getReplacementSaleUSI() == null) {
			Transaction transaction = new Transaction(currentMillis, saleEmployeeId, sale.getStoreId());
			Integer transactionId = saleDao.insertTransaction(transaction);
			sale.setTransactionId(transactionId);
			sale.setIsInitial(true);		
		} else {
			Integer transactionId = saleDao.getSaleTransactionId(sale.getReplacementSaleUSI());
			saleDao.udpateTransaction(transactionId, currentMillis, saleEmployeeId);
			sale.setTransactionId(transactionId);
			sale.setIsInitial(false);
		}
		
		sale.setEmployeeId(saleEmployeeId);
		sale.setStoreId(sale.getStoreId());
		sale.setSaleTimestamp(dateService.getCurrentMillisBGTimezone());
		
		Integer saleId = saleDao.insertSale(sale);
		StoreDto store = entityService.getStoreById(sale.getStoreId());
		String usi = generateUSI(store.getFdSerialNo(), saleEmployeeId, saleId);
		saleDao.updateSaleUSI(saleId, usi);
		
		saveSaleItems(sale, saleId);
		
		Sale insertedSale = saleDao.selectSaleByUniqueSaleId(usi);
		insertedSale.setSaleItems(saleDao.getSaleItemsBySaleId(insertedSale.getId()));
		
		return insertedSale;
	}

	private String generateUSI(String fdSerialNo, Integer saleEmployeeId, Integer saleId) {
		if (fdSerialNo == null) {
			throw new IllegalArgumentException("The fiscal device serial number should be 8 symbols.");
		}

		String paddedEmployeeId = String.format("%04d", saleEmployeeId);
		String paddedSaleId = String.format("%06d", saleId);

        StringBuilder usiBuilder = new StringBuilder();
        usiBuilder.append(fdSerialNo)
                  .append("-")
                  .append(paddedEmployeeId)
                  .append("-")
                  .append(paddedSaleId);
        
        return usiBuilder.toString();
	}

//	private void saveSaleItems(Sale sale, Employee loggedInEmployee, DiscountCode discountCode, Integer saleId) {
//		if (discountCode == null) {
//			for (SaleItem saleItem : sale.getSaleItems()) {
//				saleItem.setSaleId(saleId);
//				BigDecimal itemPrice = itemDao.getItemPriceByStoreId(saleItem.getItemId(), sale.getStoreId());
//				saleItem.setItemPrice(itemPrice);
//				saleItem.setSalePrice(itemPrice);
//
//				saleDao.insertSaleItem(saleItem);
//				stockService.updateTheQuantitiyOfSoldStock(saleItem.getItemId(), loggedInEmployee.getStoreId());
//			}
//		} else if ("PERCENTAGE".equals(discountCode.getDiscountTypeCode())
//				|| "AMOUNT".equals(discountCode.getDiscountTypeCode())) {
//			BigDecimal discountValueAmount = new BigDecimal(discountCode.getDiscountValue());
//			for (SaleItem saleItem : sale.getSaleItems()) {
//				saleItem.setSaleId(saleId);
//				BigDecimal itemPrice = itemDao.getItemPriceByStoreId(saleItem.getItemId(), sale.getStoreId());
//				saleItem.setItemPrice(itemPrice);
//
//				String discountTypeCode = discountCode.getDiscountTypeCode();
//				if ("PERCENTAGE".equals(discountTypeCode)) {
//					BigDecimal salePrice = calculcatePercentageDiscountValuePrice(itemPrice, discountValueAmount);
//					saleItem.setSalePrice(salePrice);
//				} else if ("AMOUNT".equals(discountTypeCode)) {
//					BigDecimal salePrice = calculcateAmountDiscountValuePrice(itemPrice, discountValueAmount);
//					discountValueAmount = discountValueAmount.subtract(itemPrice);
//					if (discountValueAmount.compareTo(BigDecimal.ZERO) < 0) {
//						discountValueAmount = BigDecimal.ZERO;
//					}
//					saleItem.setSalePrice(salePrice);
//				}
//				saleDao.insertSaleItem(saleItem);
//				stockService.updateTheQuantitiyOfSoldStock(saleItem.getItemId(), loggedInEmployee.getStoreId());
//			}
//		} else if ("BUNDLE".equals(discountCode.getDiscountTypeCode())) {
//			for (SaleItem saleItem : sale.getSaleItems()) {
//				saleItem.setSaleId(saleId);
//				BigDecimal itemPrice = itemDao.getItemPriceByStoreId(saleItem.getItemId(), sale.getStoreId());
//				saleItem.setItemPrice(itemPrice);
//			}
//			
//			String discountValueAmount = discountCode.getDiscountValue();
//			List<String> bundleDiscount = Arrays.asList(discountValueAmount.split(";"));
//			
//			List<SaleItem> sortedSaleItems = sale.getSaleItems();
//			sortedSaleItems.sort(new SaleItemByItemPriceComparator());
//
//			int bundleDiscountCounter = 0;
//
//			for (int i = 0; i < sortedSaleItems.size(); i++) {
//				SaleItem saleItem = sortedSaleItems.get(i);
//				if (sortedSaleItems.size() - (i + 1) < bundleDiscount.size()) {
//					BigDecimal discountValue = new BigDecimal(bundleDiscount.get(bundleDiscountCounter++));
//					BigDecimal salePrice = calculcatePercentageDiscountValuePrice(saleItem.getItemPrice(), discountValue);
//					saleItem.setSalePrice(salePrice);
//				} else {
//					saleItem.setSalePrice(saleItem.getItemPrice());
//				}
//				
//				saleDao.insertSaleItem(saleItem);
//				stockService.updateTheQuantitiyOfSoldStock(saleItem.getItemId(), loggedInEmployee.getStoreId());
//			}
//		}
//	}

	private void saveSaleItems(Sale sale, Integer saleId) {
		List<SaleItem> saleItems = sale.getSaleItems();
		saveSaleItemsWithoutDiscount(sale, saleId, saleItems);
		
		List<SaleItem> percentageDiscounTypeItems = saleItems.stream()
				.filter(item -> "PERCENTAGE".equals(item.getDiscountType())).collect(Collectors.toList());
		saveSaleItemsWithPercentageDiscount(sale, saleId, percentageDiscounTypeItems);
				
		List<SaleItem> amountDiscounTypeItems = saleItems.stream()
				.filter(item -> "AMOUNT".equals(item.getDiscountType())).collect(Collectors.toList());
		saveSaleItemsWithAmountDiscount(sale, saleId, amountDiscounTypeItems);
		
		List<SaleItem> bundleDiscounTypeItems = saleItems.stream()
				.filter(item -> "BUNDLE".equals(item.getDiscountType())).collect(Collectors.toList());
		saveSaleItemsWithBundleDiscount(sale, saleId, bundleDiscounTypeItems);
	}

	private void saveSaleItemsWithBundleDiscount(Sale sale, Integer saleId,
			List<SaleItem> bundleDiscounTypeItems) {
		if (!bundleDiscounTypeItems.isEmpty()) {
			LinkedHashMap<Integer, List<SaleItem>> bundledGroupedByDiscountCode = bundleDiscounTypeItems.stream()
					.collect(Collectors.groupingBy(SaleItem::getDiscountCode, LinkedHashMap::new, Collectors.toList()));
			
			for (Integer discountCode : bundledGroupedByDiscountCode.keySet()) {
				List<SaleItem> bundle = bundledGroupedByDiscountCode.get(discountCode);
				
				for (SaleItem saleItem : bundle) {
					saleItem.setSaleId(saleId);
					BigDecimal itemPrice = itemDao.getItemPriceByStoreId(saleItem.getItemId(), sale.getStoreId());
					saleItem.setItemPrice(itemPrice);
				}
				
		        List<SaleItem> bundleSortedByPrice = bundle.stream()
		    			.sorted(Comparator.comparing(SaleItem::getItemPrice, Comparator.reverseOrder()))
		    			.collect(Collectors.toList());
				
				String discountValueAmount = bundleSortedByPrice.get(0).getDiscountValue();
				List<String> bundleDiscountValues = Arrays.asList(discountValueAmount.split(";"));
				
				int bundleDiscountCounter = 0;
				
				for (int i = 0; i < bundleSortedByPrice.size(); i++) {
					SaleItem saleItem = bundleSortedByPrice.get(i);
					if (bundleSortedByPrice.size() - (i + 1) < bundleDiscountValues.size()) {
						BigDecimal discountValue = new BigDecimal(bundleDiscountValues.get(bundleDiscountCounter++));
						BigDecimal salePrice = calculcatePercentageDiscountValuePrice(saleItem.getItemPrice(), discountValue);
						saleItem.setSalePrice(salePrice);
					} else {
						saleItem.setSalePrice(saleItem.getItemPrice());
					}
					
					Item item = itemDao.getItem(saleItem.getItemId());
					saleItem.setBonusPts(item.getProductBonusPts());
					saleDao.insertSaleItem(saleItem);
					stockService.updateTheQuantitiyOfSoldStock(saleItem.getItemId(), sale.getStoreId());
				}
			}
		}
	}

	private void saveSaleItemsWithAmountDiscount(Sale sale, Integer saleId,
			List<SaleItem> amountDiscounTypeItems) {
		for (SaleItem saleItem : amountDiscounTypeItems) {
			saleItem.setSaleId(saleId);
			BigDecimal itemPrice = itemDao.getItemPriceByStoreId(saleItem.getItemId(), sale.getStoreId());
			saleItem.setItemPrice(itemPrice);
			Item item = itemDao.getItem(saleItem.getItemId());
			saleItem.setBonusPts(item.getProductBonusPts());

			BigDecimal salePrice = calculcateAmountDiscountValuePrice(itemPrice, new BigDecimal(saleItem.getDiscountValue()));
			saleItem.setSalePrice(salePrice);

			saleDao.insertSaleItem(saleItem);
			stockService.updateTheQuantitiyOfSoldStock(saleItem.getItemId(), sale.getStoreId());
		}
	}

	private void saveSaleItemsWithPercentageDiscount(Sale sale, Integer saleId,
			List<SaleItem> percentageDiscounTypeItems) {
		for (SaleItem saleItem : percentageDiscounTypeItems) {
			saleItem.setSaleId(saleId);
			BigDecimal itemPrice = itemDao.getItemPriceByStoreId(saleItem.getItemId(), sale.getStoreId());
			saleItem.setItemPrice(itemPrice);
			Item item = itemDao.getItem(saleItem.getItemId());
			saleItem.setBonusPts(item.getProductBonusPts());

			BigDecimal salePrice = calculcatePercentageDiscountValuePrice(itemPrice, new BigDecimal(saleItem.getDiscountValue()));
			saleItem.setSalePrice(salePrice);

			saleDao.insertSaleItem(saleItem);
			stockService.updateTheQuantitiyOfSoldStock(saleItem.getItemId(), sale.getStoreId());
		}
	}

	private void saveSaleItemsWithoutDiscount(Sale sale, Integer saleId,
			List<SaleItem> saleItems) {
		for (SaleItem saleItem : saleItems) {
			if (saleItem.getDiscountCode() == null) {
				saleItem.setSaleId(saleId);
				BigDecimal itemPrice = itemDao.getItemPriceByStoreId(saleItem.getItemId(), sale.getStoreId());
				Item item = itemDao.getItem(saleItem.getItemId());
				saleItem.setBonusPts(item.getProductBonusPts());
				saleItem.setItemPrice(itemPrice);
				saleItem.setSalePrice(itemPrice);
				
				saleDao.insertSaleItem(saleItem);
				stockService.updateTheQuantitiyOfSoldStock(saleItem.getItemId(), sale.getStoreId());
			} else {
				DiscountCode discountCode = discountDao.selectDiscountCode(saleItem.getDiscountCode());
				if (discountCode == null) {
					throw new DomainObjectNotFoundException("discountCodeCode", "Несъществуващ код за намаление.");
				} else {
					saleItem.setDiscountCodeId(discountCode.getId());
					saleItem.setDiscountValue(discountCode.getDiscountValue());
					saleItem.setDiscountType(discountCode.getDiscountTypeCode());
				}

			}
		}
	}
	
	private BigDecimal calculcateAmountDiscountValuePrice(BigDecimal priceBeforeDiscount, BigDecimal discountValueAmount) {
		BigDecimal salePrice = priceBeforeDiscount.subtract(discountValueAmount);
		if (salePrice.compareTo(ZERO) > 0) {
			return salePrice;
		}
		
		return ZERO;
	}

	private BigDecimal calculcatePercentageDiscountValuePrice(BigDecimal priceBeforeDiscount, BigDecimal discountValueAmount) {
		return priceBeforeDiscount.multiply(ONE_HUNDRED.subtract(discountValueAmount))
				.divide(ONE_HUNDRED).setScale(2, RoundingMode.HALF_UP);
	}
	
	private BigDecimal calculcatePercentageDiscount(BigDecimal priceBeforeDiscount, BigDecimal discountValueAmount) {
		return priceBeforeDiscount.multiply(discountValueAmount).divide(ONE_HUNDRED).setScale(2, RoundingMode.HALF_UP);
	}

	@Override
	public SaleReport searchSales(Long startDateMilliseconds, Long endDateMilliseconds, String storeIds) {

		SaleReport saleReport = generateReport(storeIds, startDateMilliseconds, endDateMilliseconds, null, null, null);

		List<Sale> sales = saleDao.searchSales(startDateMilliseconds, endDateMilliseconds,
				entityService.getConcatenatedStoreIdsForFiltering(storeIds));

		calculateTotalAmountAndCountForSales(sales, saleReport);
		saleReport.setSales(sales);

		return saleReport;
	}

	private SaleReport generateReport(String storeId, Long startDateMilliseconds,
			Long endDateMilliseconds, String productCode, Integer deviceBrandId, Integer deviceModelId) {
		SaleReport report = new SaleReport();
		
		report.setStartDate(startDateMilliseconds);
		report.setEndDate(endDateMilliseconds);	
		
		if (storeId != null) {
			setSaleReportStoreName(storeId, report);
		}
		
		if (deviceBrandId != null) {
			report.setDeviceBrandName(deviceBrandDao.selectDeviceBrand(deviceBrandId).getName());
		}
		
		if (deviceModelId != null) {
			report.setDeviceModelName(deviceModelDao.selectDeviceModel(deviceModelId).getName());
		}
		
		if (productCode != null) {
			report.setProductCode(productCode);
		}
		
		return report;
		
	}

	private void setSaleReportStoreName(String storeId, SaleReport report) {
		if ("0".equals(storeId)) {
			report.setStoreName("Всички магазини");
		} else {
			StoreDto store = storeDao.selectStore(storeId);
			report.setStoreName(store.getCity() + ", " + store.getName());
		}
	}
	

	@Override
	public List<SaleItem> getSaleItems(Integer saleId) {
		return saleDao.getSaleItemsBySaleId(saleId);
	}

	@Override
	public SaleReport searchSaleItems(Long startDateMilliseconds, Long endDateMilliseconds, String storeIds,
			String productCode, Integer deviceBrandId, Integer deviceModelId, Integer masterProductTypeId,
			Integer productTypeId, Float priceFrom, Float priceTo, String discountCampaignCode) {
		SaleReport saleReport = generateReport(storeIds, startDateMilliseconds, endDateMilliseconds, productCode,
				deviceBrandId, deviceModelId);

		List<SaleItem> saleItems = saleDao.searchSaleItems(startDateMilliseconds, endDateMilliseconds,
				entityService.getConcatenatedStoreIdsForFiltering(storeIds), productCode, deviceBrandId, deviceModelId,
				masterProductTypeId, productTypeId, priceFrom, priceTo, discountCampaignCode);

		if (deviceModelId != null && productCode != null && productCode != "") {
			saleReport.setWarehouseQuantity(stockService.getQuantitiyOfStockInWH(productCode, deviceModelId));
			saleReport.setCompanyQuantity(stockService.getCompanyQuantityOfStock(productCode, deviceModelId));
		}

		calculateTotalAmountAndCountSaleItems(saleItems, saleReport);
		saleReport.setSaleItems(saleItems);

		return saleReport;
	}

	private void calculateTotalAmountAndCountSaleItems(List<SaleItem> saleItems, SaleReport saleReport) {
		BigDecimal totalAmount = ZERO;	
		Integer count = 0;

		if (saleItems != null && !saleItems.isEmpty()) {
			totalAmount = saleItems.stream()
			        .map(saleItem -> saleItem.getSalePrice())
			        .reduce(ZERO, BigDecimal::add);	
			count = saleItems.size();
		}
		
		saleReport.setItemCount(count);
		saleReport.setTotalAmount(totalAmount);	
	}
	
	private void calculateTotalAmountAndCountSaleByStore(List<SalesByStore> saleByStores, SaleReport saleReport) {
		BigDecimal totalAmount = ZERO;
		BigDecimal totalTransactionCount = ZERO;
		BigDecimal totalSaleCount = ZERO;
		BigDecimal totalItemCount = ZERO;
		BigDecimal totalBonusPts = ZERO;
		BigDecimal totalProtectorCount = ZERO;
		BigDecimal totalProtectorPlusCount = ZERO;
		BigDecimal totalSPT = ZERO;
		BigDecimal totalSQS = ZERO;
		BigDecimal totalAttachRate = ZERO;

		if (saleByStores != null && !saleByStores.isEmpty()) {
			totalAmount = saleByStores.stream()
					.filter(saleByStore -> Optional.ofNullable(saleByStore)
							.map(SalesByStore::getAmount)
							.map(amount -> amount != null)
							.orElse(false))
			        .map(saleByStore -> saleByStore.getAmount())	        
			        .reduce(ZERO, BigDecimal::add);
			
			totalTransactionCount = saleByStores.stream()
					.filter(saleByStore -> Optional.ofNullable(saleByStore)
							.map(SalesByStore::getTransactionCount)
							.map(count -> count != null)
							.orElse(false))
					.map(saleByStore -> saleByStore.getTransactionCount())	        
					.reduce(ZERO, BigDecimal::add);
			
			totalSaleCount = saleByStores.stream()
					.filter(saleByStore -> Optional.ofNullable(saleByStore)
							.map(SalesByStore::getSaleCount)
							.map(count -> count != null)
							.orElse(false))
					.map(saleByStore -> saleByStore.getTransactionCount())	        
					.reduce(ZERO, BigDecimal::add);
			
			totalItemCount = saleByStores.stream()
					.filter(saleByStore -> Optional.ofNullable(saleByStore)
							.map(SalesByStore::getItemCount)
							.map(count -> count != null)
							.orElse(false))
					.map(saleByStore -> saleByStore.getItemCount())	        
					.reduce(ZERO, BigDecimal::add);
			
			totalBonusPts = saleByStores.stream()
					.filter(saleByStore -> Optional.ofNullable(saleByStore)
							.map(SalesByStore::getBonusPts)
							.map(count -> count != null)
							.orElse(false))
					.map(saleByStore -> saleByStore.getBonusPts())	        
					.reduce(ZERO, BigDecimal::add);
			
			totalProtectorCount = saleByStores.stream()
					.filter(saleByStore -> Optional.ofNullable(saleByStore)
							.map(SalesByStore::getProtectorCount)
							.map(count -> count != null)
							.orElse(false))
					.map(saleByStore -> saleByStore.getProtectorCount())	        
					.reduce(ZERO, BigDecimal::add);
			
			totalProtectorPlusCount = saleByStores.stream()
					.filter(saleByStore -> Optional.ofNullable(saleByStore)
							.map(SalesByStore::getProtectorPlusCount)
							.map(count -> count != null)
							.orElse(false))
					.map(saleByStore -> saleByStore.getProtectorPlusCount())	        
					.reduce(ZERO, BigDecimal::add);
			
	    	if (ZERO.compareTo(totalItemCount) < 0 && ZERO.compareTo(totalTransactionCount) < 0) {
	    		totalSPT = totalItemCount.divide(totalTransactionCount, 2, RoundingMode.HALF_UP);
	    	} else {
	    		totalSPT = ZERO;
	    	}
	    	if (ZERO.compareTo(totalBonusPts) < 0 && ZERO.compareTo(totalItemCount) < 0) {
	    		totalSQS = totalBonusPts.divide(totalItemCount, 2, RoundingMode.HALF_UP);
	    	} else {
	    		totalSQS = ZERO;
	    	}
	    	if (ZERO.compareTo(totalProtectorCount) < 0 && ZERO.compareTo(totalProtectorPlusCount) < 0) {
	    		totalAttachRate = totalProtectorPlusCount.divide(totalProtectorCount, 2, RoundingMode.HALF_UP).multiply(ONE_HUNDRED);
	    	} else {
	    		totalAttachRate = ZERO;
	    	}
		}
		
		saleReport.setTotalAmount(totalAmount);
		saleReport.setTransactionCount(totalTransactionCount.intValue());
		saleReport.setItemCount(totalItemCount.intValue());
		saleReport.setBonusPts(totalBonusPts.intValue());
		saleReport.setSpt(totalSPT);
		saleReport.setSqs(totalSQS);
		saleReport.setAttachRate(totalAttachRate);
	}
	
	private void calculateTotalAmountAndCountForSales(List<Sale> sales, SaleReport saleReport) {
		BigDecimal totalAmount = ZERO;
		Integer count = 0;
		
		if (sales != null && !sales.isEmpty()) {
			totalAmount = sales.stream()
			        .map(sale -> sale.getAmount())
			        .reduce(ZERO, BigDecimal::add);
			count = sales.size();
		}
		
		saleReport.setTransactionCount(count);
		saleReport.setTotalAmount(totalAmount);
		saleReport.setTotalAmountEuro(currencyService.convertToEuro(totalAmount));
	}

	@Override
	public TotalSumReport calculateTotalSum(TotalSumRequest request) {
		TotalSumReport totalSumReport = new TotalSumReport();
		List<SaleItem> selectedSaleItems = request.getSelectedSaleItems();
		
		BigDecimal totalSum = selectedSaleItems.stream().map(SaleItem::getItemPrice)
				.reduce(ZERO, BigDecimal::add);
		totalSumReport.setTotalSum(totalSum);		
		BigDecimal discount = ZERO;
		
		List<SaleItem> percentageDiscounTypeItems = selectedSaleItems.stream()
				.filter(item -> "PERCENTAGE".equals(item.getDiscountType())).collect(Collectors.toList());
		if (!percentageDiscounTypeItems.isEmpty()) {
			BigDecimal percentageDiscount = percentageDiscounTypeItems.stream()
					.map(item -> calculcatePercentageDiscount(item.getItemPrice(), new BigDecimal(item.getDiscountValue())))
					.reduce(ZERO, BigDecimal::add);
			discount = discount.add(percentageDiscount);
		}
 		
		List<SaleItem> amountDiscounTypeItems = selectedSaleItems.stream()
				.filter(item -> "AMOUNT".equals(item.getDiscountType())).collect(Collectors.toList());
		if (!amountDiscounTypeItems.isEmpty()) {
			BigDecimal amountTotalSum = amountDiscounTypeItems.stream().map(SaleItem::getItemPrice)
					.reduce(ZERO, BigDecimal::add);
			BigDecimal amountDiscount = amountDiscounTypeItems.stream().map(item -> new BigDecimal(item.getDiscountValue()))
					.reduce(ZERO, BigDecimal::add);
			if (amountTotalSum.compareTo(amountDiscount) < 0) {
				discount = discount.add(amountTotalSum);
			} else {
				discount = discount.add(amountDiscount);
			}
		}
		
		List<SaleItem> bundleDiscounTypeItems = selectedSaleItems.stream()
				.filter(item -> "BUNDLE".equals(item.getDiscountType())).collect(Collectors.toList());
		if (!bundleDiscounTypeItems.isEmpty()) {
			BigDecimal totalBundleDiscount = ZERO;
			LinkedHashMap<Integer, List<SaleItem>> bundledGroupedByDiscountCode = bundleDiscounTypeItems.stream()
					.collect(Collectors.groupingBy(SaleItem::getDiscountCode, LinkedHashMap::new, Collectors.toList()));
			
			for (Integer discountCode : bundledGroupedByDiscountCode.keySet()) {
				List<SaleItem> bundle = bundledGroupedByDiscountCode.get(discountCode);
				
		        List<SaleItem> bundleSortedByPrice = bundle.stream()
		    			.sorted(Comparator.comparing(SaleItem::getItemPrice, Comparator.reverseOrder()))
		    			.collect(Collectors.toList());
				
				String discountValueAmount = bundleSortedByPrice.get(0).getDiscountValue();
				List<String> bundleDiscountValues = Arrays.asList(discountValueAmount.split(";"));
				
				int bundleDiscountCounter = 0;
				
				for (int i = 0; i < bundleSortedByPrice.size(); i++) {
					if (bundleSortedByPrice.size() - (i + 1) < bundleDiscountValues.size()) {
						BigDecimal discountValue = new BigDecimal(bundleDiscountValues.get(bundleDiscountCounter++));
						totalBundleDiscount = totalBundleDiscount.add(calculcatePercentageDiscount(bundleSortedByPrice.get(i).getItemPrice(), discountValue));
					}
				}
			}
			
			discount = discount.add(totalBundleDiscount);
		}
		
		totalSumReport.setDiscount(discount);
		totalSumReport.setTotalSumAfterDiscount(totalSum.subtract(discount));
		
		calculateChange(request.getPaid(), request.getCurrency(), totalSumReport);
		
		return totalSumReport;
	}
	

//	@Override
//	public TotalSumReport calculateTotalSum(TotalSumRequest totalSumRequest) {
//		TotalSumReport totalSumReport = new TotalSumReport();
//		
//		BigDecimal totalSum = totalSumRequest.getPrices().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
//		totalSumReport.setTotalSum(totalSum);
//		totalSumReport.setTotalSumAfterDiscount(totalSum);
//		
//		DiscountCode discountCode = null;
//		if (totalSumRequest.getDiscountCode() != null) {
//			discountCode = discountDao.selectDiscountCode(totalSumRequest.getDiscountCode());
//		}
//		if (discountCode != null) {					
//			BigDecimal totalSumAfterDiscount = BigDecimal.ZERO;
//			if ("PERCENTAGE".equals(discountCode.getDiscountTypeCode())) {
//				totalSumAfterDiscount = calculcatePercentageDiscountValuePrice(totalSum, new BigDecimal(discountCode.getDiscountValue())); 
//			} else if ("AMOUNT".equals(discountCode.getDiscountTypeCode())) {
//				totalSumAfterDiscount = calculcateAmountDiscountValuePrice(totalSum, new BigDecimal(discountCode.getDiscountValue()));
//			} else if ("BUNDLE".equals(discountCode.getDiscountTypeCode())) {
//				List<BigDecimal> sortedPrices = totalSumRequest.getPrices();
//				Collections.sort(sortedPrices, Collections.reverseOrder());
//				
//				String discountValueAmount = discountCode.getDiscountValue();
//				List<String> bundleDiscount = Arrays.asList(discountValueAmount.split(";"));
//				
//				int bundleDiscountCounter = 0;
//				
//				for (int i = 0; i < sortedPrices.size(); i++) {
//					BigDecimal price = sortedPrices.get(i);
//					if (sortedPrices.size() - (i + 1) < bundleDiscount.size()) {
//						BigDecimal discountValue = new BigDecimal(bundleDiscount.get(bundleDiscountCounter++));
//						BigDecimal salePrice = calculcatePercentageDiscountValuePrice(price, discountValue);
//						totalSumAfterDiscount = totalSumAfterDiscount.add(salePrice);
//						//bundleDiscountCounter = bundleDiscountCounter + 1;
//					} else {
//						totalSumAfterDiscount = totalSumAfterDiscount.add(price);
//					}
//				}
//			}
//
//			totalSumReport.setDiscount(totalSum.subtract(totalSumAfterDiscount));
//			totalSumReport.setTotalSumAfterDiscount(totalSumAfterDiscount);
//		}
//		
//		return totalSumReport;
//	}

	private void calculateChange(BigDecimal paid, String currency, TotalSumReport totalSumReport) {
		BigDecimal change = ZERO;
		if (paid != null && paid.compareTo(ZERO) > 0) {
			if ("BGN".equals(currency) && paid.compareTo(totalSumReport.getTotalSumAfterDiscount()) > 0) {
				change = paid.subtract(totalSumReport.getTotalSumAfterDiscount());
				totalSumReport.setChange(change);
				totalSumReport.setChangeEuro(currencyService.convertToEuro(change));
			}
			if ("EUR".equals(currency) && paid.compareTo(totalSumReport.getTotalSumAfterDiscountEuro()) > 0) {
				change = paid.subtract(totalSumReport.getTotalSumAfterDiscountEuro());
				totalSumReport.setChangeEuro(change);
				totalSumReport.setChange(currencyService.convertToBgn(change));
			}
		}
	}

	@Override
	public SaleReport searchSalesByStores(Long startDateMilliseconds, Long endDateMilliseconds, String productCode,
			Integer deviceBrandId, Integer deviceModelId, Integer productTypeId) {
		SaleReport saleReport = generateReport(null, startDateMilliseconds, endDateMilliseconds, productCode, deviceBrandId, deviceModelId);
		
		String storeIds = entityService.getConcatenatedStoreIdsForFiltering("0");
		List<SalesByStore> salesByStores = saleDao.searchSaleByStore(startDateMilliseconds, endDateMilliseconds, storeIds, true);
		
		if (deviceModelId != null && productCode != null && productCode != "") {
			saleReport.setWarehouseQuantity(stockService.getQuantitiyOfStockInWH(productCode, deviceModelId));
			saleReport.setCompanyQuantity(stockService.getCompanyQuantityOfStock(productCode, deviceModelId));
		}		

		calculateTotalAmountAndCountSaleByStore(salesByStores, saleReport);
		saleReport.setSalesByStores(salesByStores);
		
		return saleReport;
	}

	@Override
	public PastPeriodSaleReport searchSalesForPastPeriodsByStores(String month) {
		PastPeriodSaleReport report = new PastPeriodSaleReport();
		int selectedMonth = Integer.valueOf(month.split("-")[0]);

		int currentYear = Calendar.getInstance(timeZone).get(Calendar.YEAR);
		int currentMonth = Calendar.getInstance(timeZone).get(Calendar.MONTH);
		int currentDay = Calendar.getInstance(timeZone).get(Calendar.DAY_OF_MONTH);

		List<SalesByStore> selectedMonthTurnover = Lists.newArrayList();
		List<SalesByStore> previousMonthTurnover = Lists.newArrayList();
		List<SalesByStore> previousYearTurnover = Lists.newArrayList();
		PeriodInMillis previousYearPeriodInMillis = new PeriodInMillis();
		PeriodInMillis previousMonthPeriodInMillis = new PeriodInMillis();
		PeriodInMillis selectedMonthPeriodInMillis = new PeriodInMillis();
		if (selectedMonth == currentMonth) {
			int day;
			if (Calendar.getInstance(timeZone).get(Calendar.HOUR_OF_DAY) < 21 && currentDay > 1) {
				day = currentDay - 1;
			} else {
				day = currentDay;
			}

			int previousMonthMonth;
			int previousMonthYear;
			if (selectedMonth == 0) {
				previousMonthMonth = 11;
				previousMonthYear = currentYear - 1;
			} else {
				previousMonthMonth = selectedMonth - 1;
				previousMonthYear = currentYear;
			}

			report.setPrevYearDay(day);
			report.setPrevYearMonth(currentMonth);
			report.setPrevYearYear(currentYear - 1);
			report.setPrevMonthDay(day);
			report.setPrevMonthMonth(previousMonthMonth);
			report.setPrevMonthYear(previousMonthYear);
			report.setSelectedMonthDay(day);
			report.setSelectedMonthMonth(currentMonth);
			report.setSelectedMonthYear(currentYear);
			previousYearPeriodInMillis = getMonthInMillis(currentYear - 1, currentMonth, day);
			previousMonthPeriodInMillis = getMonthInMillis(previousMonthYear, previousMonthMonth, day);
			selectedMonthPeriodInMillis = getMonthInMillis(currentYear, currentMonth, day);
		} else if (selectedMonth > currentMonth) {
			int selectedMonthDay = YearMonth.of(currentYear - 1, selectedMonth + 1).atEndOfMonth().getDayOfMonth();
			int previousMonthDay = YearMonth.of(currentYear - 1, selectedMonth).atEndOfMonth().getDayOfMonth();

			report.setPrevYearDay(selectedMonthDay);
			report.setPrevYearMonth(selectedMonth);
			report.setPrevYearYear(currentYear - 2);
			report.setPrevMonthDay(previousMonthDay);
			report.setPrevMonthMonth(selectedMonth - 1);
			report.setPrevMonthYear(currentYear - 1);
			report.setSelectedMonthDay(selectedMonthDay);
			report.setSelectedMonthMonth(selectedMonth);
			report.setSelectedMonthYear(currentYear - 1);

			previousYearPeriodInMillis = getMonthInMillis(currentYear - 2, selectedMonth, selectedMonthDay);
			previousMonthPeriodInMillis = getMonthInMillis(currentYear - 1, selectedMonth - 1, previousMonthDay);
			selectedMonthPeriodInMillis = getMonthInMillis(currentYear - 1, selectedMonth, selectedMonthDay);
		} else if (selectedMonth < currentMonth) {
			int previousMonthDay;
			int previousMonthMonth;
			int previousMonthYear;
			if (selectedMonth == 0) {
				previousMonthYear = currentYear - 1;
				previousMonthDay = YearMonth.of(previousMonthYear, 12).atEndOfMonth().getDayOfMonth();
				previousMonthMonth = 11;
			} else {
				previousMonthYear = currentYear;
				previousMonthDay = YearMonth.of(currentYear, selectedMonth).atEndOfMonth().getDayOfMonth();
				previousMonthMonth = selectedMonth - 1;
			}

			int selectedMonthDay = YearMonth.of(currentYear, selectedMonth + 1).atEndOfMonth().getDayOfMonth();

			report.setPrevYearDay(selectedMonthDay);
			report.setPrevYearMonth(selectedMonth);
			report.setPrevYearYear(currentYear - 1);
			report.setPrevMonthDay(previousMonthDay);
			report.setPrevMonthMonth(previousMonthMonth);
			report.setPrevMonthYear(previousMonthYear);
			report.setSelectedMonthDay(selectedMonthDay);
			report.setSelectedMonthMonth(selectedMonth);
			report.setSelectedMonthYear(currentYear);

			previousYearPeriodInMillis = getMonthInMillis(currentYear - 1, selectedMonth, selectedMonthDay);
			previousMonthPeriodInMillis = getMonthInMillis(previousMonthYear, previousMonthMonth, previousMonthDay);
			selectedMonthPeriodInMillis = getMonthInMillis(currentYear, selectedMonth, selectedMonthDay);
		}

		previousYearTurnover = saleDao.searchSaleByStore(previousYearPeriodInMillis.getStartDateTime(),
				previousYearPeriodInMillis.getEndDateTime(), null, false);
		previousMonthTurnover = saleDao.searchSaleByStore(previousMonthPeriodInMillis.getStartDateTime(),
				previousMonthPeriodInMillis.getEndDateTime(), null, false);
		selectedMonthTurnover = saleDao.searchSaleByStore(selectedMonthPeriodInMillis.getStartDateTime(),
				selectedMonthPeriodInMillis.getEndDateTime(), null, false);

		if (employeeService.isLoggedInEmployeeAdmin()) {
			String activeStoreIds = saleDao.selectStoreIdsForAllSalesInThePeriods(previousYearPeriodInMillis,
					previousMonthPeriodInMillis, selectedMonthPeriodInMillis);
			previousYearTurnover
					.addAll(saleDao.searchSaleTurnoverForCompany(previousYearPeriodInMillis.getStartDateTime(),
							previousYearPeriodInMillis.getEndDateTime(), activeStoreIds));
			previousMonthTurnover
					.addAll(saleDao.searchSaleTurnoverForCompany(previousMonthPeriodInMillis.getStartDateTime(),
							previousMonthPeriodInMillis.getEndDateTime(), activeStoreIds));
			selectedMonthTurnover
					.addAll(saleDao.searchSaleTurnoverForCompany(selectedMonthPeriodInMillis.getStartDateTime(),
							selectedMonthPeriodInMillis.getEndDateTime(), activeStoreIds));
		}

		report.setPastPeriodTurnovers(
				mergeThePastPeriodTurnovers(previousYearTurnover, previousMonthTurnover, selectedMonthTurnover));

		return report;
	}

	private List<PastPeriodTurnover> mergeThePastPeriodTurnovers(List<SalesByStore> previousYearTurnoverList,
			List<SalesByStore> previousMonthTurnoverList, List<SalesByStore> selectedMonthTurnoverList) {
		List<PastPeriodTurnover> pastPeriodReportList = Lists.newArrayList();
		selectedMonthTurnoverList.sort(new SalesByStoreByStoreIdComparator());		
		Map<String, SalesByStore> previousYearTurnoverMap = previousYearTurnoverList.stream()
			      .collect(Collectors.toMap(SalesByStore::getStoreCode, Function.identity()));	
		Map<String, SalesByStore> previousMonthTurnoverMap = previousMonthTurnoverList.stream()
				.collect(Collectors.toMap(SalesByStore::getStoreCode, Function.identity()));
		
		for (int i = 0; i < selectedMonthTurnoverList.size(); i++) {
			SalesByStore selectedMonth = selectedMonthTurnoverList.get(i);
			SalesByStore prevYear = previousYearTurnoverMap.get(selectedMonth.getStoreCode()) != null ? previousYearTurnoverMap.get(selectedMonth.getStoreCode()) : SalesByStore.createEmptySalesByStore();
			SalesByStore prevMonth = previousMonthTurnoverMap.get(selectedMonth.getStoreCode()) != null ? previousMonthTurnoverMap.get(selectedMonth.getStoreCode()) : SalesByStore.createEmptySalesByStore();
			PastPeriodTurnover pastPeriodReport = new PastPeriodTurnover();
			pastPeriodReport.setStoreId(selectedMonth.getStoreId());
			pastPeriodReport.setStoreCode(selectedMonth.getStoreCode());
			pastPeriodReport.setStoreName(selectedMonth.getStoreName());
			
			BigDecimal prevYearAmount = prevYear.getAmount();
			pastPeriodReport.setPrevYearAmount(prevYearAmount);					
			BigDecimal prevMonthAmount = prevMonth.getAmount();
			pastPeriodReport.setPrevMonthAmount(prevMonthAmount);
			BigDecimal selectedMonthAmount = selectedMonth.getAmount();
			pastPeriodReport.setSelectedMonthAmount(selectedMonthAmount);
			
			BigDecimal prevYearTransactionCount = prevYear.getTransactionCount();
			pastPeriodReport.setPrevYearTransactionCount(prevYearTransactionCount);
			BigDecimal prevMonthTransactionCount = prevMonth.getTransactionCount();
			pastPeriodReport.setPrevMonthTransactionCount(prevMonthTransactionCount);
			BigDecimal selectedMonthTransactionCount = selectedMonth.getTransactionCount();
			pastPeriodReport.setSelectedMonthTransactionCount(selectedMonthTransactionCount);
			
			BigDecimal prevYearSaleCount = prevYear.getSaleCount();
			pastPeriodReport.setPrevYearSaleCount(prevYearSaleCount);
			BigDecimal prevMonthSaleCount = prevMonth.getSaleCount();
			pastPeriodReport.setPrevMonthSaleCount(prevMonthSaleCount);
			BigDecimal selectedMonthSaleCount = selectedMonth.getSaleCount();
			pastPeriodReport.setSelectedMonthSaleCount(selectedMonthSaleCount);
			
			BigDecimal prevYearItemCount = prevYear.getItemCount();
			pastPeriodReport.setPrevYearItemCount(prevYearItemCount);
			BigDecimal prevMonthItemCount = prevMonth.getItemCount();
			pastPeriodReport.setPrevMonthItemCount(prevMonthItemCount);
			BigDecimal selectedMonthItemCount = selectedMonth.getItemCount();
			pastPeriodReport.setSelectedMonthItemCount(selectedMonthItemCount);
			
			BigDecimal prevYearSpt = prevYear.getSpt();
			pastPeriodReport.setPrevYearSpt(prevYearSpt);
			BigDecimal prevMonthSpt = prevMonth.getSpt();
			pastPeriodReport.setPrevMonthSpt(prevMonthSpt);
			BigDecimal selectedMonthSpt = selectedMonth.getSpt();
			pastPeriodReport.setSelectedMonthSpt(selectedMonthSpt);
			
			if (ZERO.compareTo(prevYear.getAmount()) < 0) {
				BigDecimal prevYearDelta = selectedMonthAmount.multiply(ONE_HUNDRED)
						.divide(prevYear.getAmount(), RoundingMode.HALF_UP).subtract(ONE_HUNDRED);
				pastPeriodReport.setPrevYearAmountDelta(prevYearDelta);
			}
			if (ZERO.compareTo(prevMonthAmount) < 0) {
				BigDecimal prevMonthDelta = selectedMonthAmount.multiply(ONE_HUNDRED)
						.divide(prevMonthAmount, RoundingMode.HALF_UP).subtract(ONE_HUNDRED);
				pastPeriodReport.setPrevMonthAmountDelta(prevMonthDelta);
			}
			
			if (ZERO.compareTo(prevYear.getTransactionCount()) < 0) {
				BigDecimal prevYearTransactionCountDelta = selectedMonthTransactionCount.multiply(ONE_HUNDRED)
						.divide(prevYear.getTransactionCount(), RoundingMode.HALF_UP).subtract(ONE_HUNDRED);
				pastPeriodReport.setPrevYearTransactionCountDelta(prevYearTransactionCountDelta);
			}
			if (ZERO.compareTo(prevMonthTransactionCount) < 0) {
				BigDecimal prevMonthTransactionCountDelta = selectedMonthTransactionCount.multiply(ONE_HUNDRED)
						.divide(prevMonthTransactionCount, RoundingMode.HALF_UP).subtract(ONE_HUNDRED);
				pastPeriodReport.setPrevMonthTransactionCountDelta(prevMonthTransactionCountDelta);
			}
			
			if (ZERO.compareTo(prevYear.getSaleCount()) < 0) {
				BigDecimal prevYearSaleCountDelta = selectedMonthSaleCount.multiply(ONE_HUNDRED)
						.divide(prevYear.getSaleCount(), RoundingMode.HALF_UP).subtract(ONE_HUNDRED);
				pastPeriodReport.setPrevYearSaleCountDelta(prevYearSaleCountDelta);
			}
			if (ZERO.compareTo(prevMonthSaleCount) < 0) {
				BigDecimal prevMonthSaleCountDelta = selectedMonthSaleCount.multiply(ONE_HUNDRED)
						.divide(prevMonthSaleCount, RoundingMode.HALF_UP).subtract(ONE_HUNDRED);
				pastPeriodReport.setPrevMonthSaleCountDelta(prevMonthSaleCountDelta);
			}
			
			if (ZERO.compareTo(prevYear.getItemCount()) < 0) {
				BigDecimal prevYearItemCountDelta = selectedMonthItemCount.multiply(ONE_HUNDRED)
						.divide(prevYear.getItemCount(), RoundingMode.HALF_UP).subtract(ONE_HUNDRED);
				pastPeriodReport.setPrevYearItemCountDelta(prevYearItemCountDelta);
			}
			if (ZERO.compareTo(prevMonthItemCount) < 0) {
				BigDecimal prevMonthItemCountDelta = selectedMonthItemCount.multiply(ONE_HUNDRED)
						.divide(prevMonthItemCount, RoundingMode.HALF_UP).subtract(ONE_HUNDRED);
				pastPeriodReport.setPrevMonthItemCountDelta(prevMonthItemCountDelta);
			}
			
			if (ZERO.compareTo(prevYear.getSpt()) < 0) {
				BigDecimal prevYearSptDelta = selectedMonthSpt.multiply(ONE_HUNDRED)
						.divide(prevYear.getSpt(), RoundingMode.HALF_UP).subtract(ONE_HUNDRED);
				pastPeriodReport.setPrevYearSptDelta(prevYearSptDelta);
			}
			if (ZERO.compareTo(prevMonthSpt) < 0) {
				BigDecimal prevMonthSptDelta = selectedMonthSpt.multiply(ONE_HUNDRED)
						.divide(prevMonthSpt, RoundingMode.HALF_UP).subtract(ONE_HUNDRED);
				pastPeriodReport.setPrevMonthSptDelta(prevMonthSptDelta);
			}
			
			pastPeriodReportList.add(pastPeriodReport);
		}
		
		return pastPeriodReportList;
		
	}

	private PeriodInMillis getMonthInMillis(int year, int month, int day) {
//		List<SalesByStore> salesByStore = new ArrayList<>();
//		if (day == 1) {
//			return salesByStore;
//		} else {
			Calendar starDateCal = Calendar.getInstance(timeZone);
			starDateCal.set(Calendar.YEAR, year);
			starDateCal.set(Calendar.MONTH, month);
			starDateCal.set(Calendar.DAY_OF_MONTH, 1);
			starDateCal.set(Calendar.HOUR_OF_DAY, 0);
			starDateCal.set(Calendar.MINUTE, 0);
			starDateCal.set(Calendar.SECOND, 0);
			starDateCal.set(Calendar.MILLISECOND, 000);
			long startDateMilliseconds = starDateCal.getTimeInMillis();

			Calendar endDateCal = Calendar.getInstance(timeZone);
			endDateCal.set(Calendar.YEAR, year);
			endDateCal.set(Calendar.MONTH, month);
			if (endDateCal.getActualMaximum(Calendar.DAY_OF_MONTH) < day) {
				endDateCal.set(Calendar.DAY_OF_MONTH, endDateCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			} else {
				endDateCal.set(Calendar.DAY_OF_MONTH, day);
			}
			endDateCal.set(Calendar.HOUR_OF_DAY, 23);
			endDateCal.set(Calendar.MINUTE, 59);
			endDateCal.set(Calendar.SECOND, 59);
			endDateCal.set(Calendar.MILLISECOND, 999);
			long endDateMilliseconds = endDateCal.getTimeInMillis();
			
			return new PeriodInMillis(startDateMilliseconds, endDateMilliseconds);

//			return saleDao.searchSaleByStore(startDateMilliseconds, endDateMilliseconds, null, false);
//		}

	}

	@Override
	public byte[] getProductTypeSplitReport(SaleSplitReportRequest saleSplitReportRequest) {
		List<SalesByStoreByDayByProductType> report = saleDao.generateProductTypeSplitReport(saleSplitReportRequest.getStartDate(),
				saleSplitReportRequest.getEndDate(), saleSplitReportRequest.getStoreId() != null ? saleSplitReportRequest.getStoreId().toString() : null);

		Map<String, LinkedHashMap<String, List<SalesByStoreByDayByProductType>>> groupedReport = report.stream()
				.collect(Collectors.groupingBy(SalesByStoreByDayByProductType::getStoreName,
						Collectors.groupingBy(SalesByStoreByDayByProductType::getDay, LinkedHashMap::new, Collectors.toList())));
		
		try {
			return splitReportExcelWriterService.createProductTypeSplitReportExcel(groupedReport);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public byte[] getTransactionSplitReport(SaleSplitReportRequest saleSplitReportRequest) {
		List<TransactionsByStoreByDay> report = saleDao.generateTransactionSplitReport(saleSplitReportRequest.getStartDate(),
				saleSplitReportRequest.getEndDate(), saleSplitReportRequest.getStoreId() != null ? saleSplitReportRequest.getStoreId().toString() : null);

		Map<String, LinkedHashMap<String, List<TransactionsByStoreByDay>>> groupedReport = report.stream()
				.collect(Collectors.groupingBy(TransactionsByStoreByDay::getStoreName,
						Collectors.groupingBy(TransactionsByStoreByDay::getDay, LinkedHashMap::new, Collectors.toList())));
		
		try {
			return splitReportExcelWriterService.createTransactionSplitReportExcel(groupedReport);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public DataReport getSaleItemDailyReportData(Long startDateTime, Long endDateTime, Integer storeId) {
		return saleDao.selectSaleItemTotalAndCountByStoreId(startDateTime, endDateTime, storeId);
	}
	
	@Override
	public DataReport getRefundedSaleItemDailyReportData(Long startDateTime, Long endDateTime, Integer storeId) {
		return saleDao.selectRefundedSaleItemTotalAndCount(startDateTime, endDateTime, storeId);
	}

	@Override
	public DataReport getCardPaymentDailyReportData(Long startDateTime, Long endDateTime, Integer storeId) {
		return saleDao.selectSaleItemWithCardPaymentTotalAndCount(startDateTime, endDateTime, storeId);
	}

	@Override
	public DataReport getSaleItemTotalAndCountWithoutRefundByStoreId(Long startDateTime, Long endDateTime, Integer storeId) {
		return saleDao.selectSaleItemTotalAndCountWithoutRefundByStoreId(startDateTime, endDateTime, storeId);
	}

	@Override
	public Sale getSaleByUSI(String uniqueSaleId) {
		Sale sale = saleDao.selectSaleByUniqueSaleId(uniqueSaleId);
		if (sale != null) {
			List<SaleItem> saleItems = getSaleItems(sale.getId());
			validateReplacementSaleItems(saleItems);
			
			sale.setSaleItems(saleItems);
			
			return sale;
		} else {
			throw new DomainObjectNotFoundException("replacementSaleUSI", "Non-existing sale!");
		}

	}

	private void validateReplacementSaleItems(List<SaleItem> saleItems) {
		for (SaleItem saleItem : saleItems) {
			if (saleItem.getIsRefunded()) {
				return;
			}
		}
		
		throw new NoRefundedItemException("replacementSaleUSI", "This sale have not refunded items!");
		
	}
}
