package com.kalafche.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
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
import com.kalafche.model.DailyReportData;
import com.kalafche.model.DiscountCode;
import com.kalafche.model.Employee;
import com.kalafche.model.PastPeriodSaleReport;
import com.kalafche.model.PastPeriodTurnover;
import com.kalafche.model.Sale;
import com.kalafche.model.SaleItem;
import com.kalafche.model.SaleReport;
import com.kalafche.model.SaleSplitReportRequest;
import com.kalafche.model.SalesByStore;
import com.kalafche.model.SalesByStoreByDayByProductType;
import com.kalafche.model.StoreDto;
import com.kalafche.model.TotalSumReport;
import com.kalafche.model.TotalSumRequest;
import com.kalafche.model.TransactionsByStoreByDay;
import com.kalafche.model.comparator.SaleItemByItemPriceComparator;
import com.kalafche.model.comparator.SalesByStoreByStoreIdComparator;
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

	private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
	
	@Transactional
	@Override
	public void submitSale(Sale sale) throws SQLException {
		Employee loggedInEmployee = employeeService.getLoggedInEmployee();
		DiscountCode discountCode = null;
		if (sale.getDiscountCodeCode() != null) {
			discountCode = discountDao.selectDiscountCode(sale.getDiscountCodeCode());
			if (discountCode == null) {
				throw new DomainObjectNotFoundException("discountCodeCode", "Несъществуващ код за намаление.");
			} else {
				sale.setDiscountCodeId(discountCode.getId());
			}
		}
		
		sale.setEmployeeId(loggedInEmployee.getId());
		sale.setStoreId(loggedInEmployee.getStoreId());
		sale.setSaleTimestamp(dateService.getCurrentMillisBGTimezone());
		
		Integer saleId = saleDao.insertSale(sale);
		
		saveSaleItems(sale, loggedInEmployee, discountCode, saleId);
		
	}

	private void saveSaleItems(Sale sale, Employee loggedInEmployee, DiscountCode discountCode, Integer saleId) {
		if (discountCode == null) {
			for (SaleItem saleItem : sale.getSaleItems()) {
				saleItem.setSaleId(saleId);
				BigDecimal itemPrice = itemDao.getItemPriceByStoreId(saleItem.getItemId(), sale.getStoreId());
				saleItem.setItemPrice(itemPrice);
				saleItem.setSalePrice(itemPrice);

				saleDao.insertSaleItem(saleItem);
				stockService.updateTheQuantitiyOfSoldStock(saleItem.getItemId(), loggedInEmployee.getStoreId());
			}
		} else if ("PERCENTAGE".equals(discountCode.getDiscountTypeCode())
				|| "AMOUNT".equals(discountCode.getDiscountTypeCode())) {
			BigDecimal discountValueAmount = new BigDecimal(discountCode.getDiscountValue());
			for (SaleItem saleItem : sale.getSaleItems()) {
				saleItem.setSaleId(saleId);
				BigDecimal itemPrice = itemDao.getItemPriceByStoreId(saleItem.getItemId(), sale.getStoreId());
				saleItem.setItemPrice(itemPrice);

				String discountTypeCode = discountCode.getDiscountTypeCode();
				if ("PERCENTAGE".equals(discountTypeCode)) {
					BigDecimal salePrice = calculcatePercentageDiscountValuePrice(itemPrice, discountValueAmount);
					saleItem.setSalePrice(salePrice);
				} else if ("AMOUNT".equals(discountTypeCode)) {
					BigDecimal salePrice = calculcateAmountDiscountValuePrice(itemPrice, discountValueAmount);
					discountValueAmount = discountValueAmount.subtract(itemPrice);
					if (discountValueAmount.compareTo(BigDecimal.ZERO) < 0) {
						discountValueAmount = BigDecimal.ZERO;
					}
					saleItem.setSalePrice(salePrice);
				}
				saleDao.insertSaleItem(saleItem);
				stockService.updateTheQuantitiyOfSoldStock(saleItem.getItemId(), loggedInEmployee.getStoreId());
			}
		} else if ("BUNDLE".equals(discountCode.getDiscountTypeCode())) {
			for (SaleItem saleItem : sale.getSaleItems()) {
				saleItem.setSaleId(saleId);
				BigDecimal itemPrice = itemDao.getItemPriceByStoreId(saleItem.getItemId(), sale.getStoreId());
				saleItem.setItemPrice(itemPrice);
			}
			
			String discountValueAmount = discountCode.getDiscountValue();
			List<String> bundleDiscount = Arrays.asList(discountValueAmount.split(";"));
			
			List<SaleItem> sortedSaleItems = sale.getSaleItems();
			sortedSaleItems.sort(new SaleItemByItemPriceComparator());

			int bundleDiscountCounter = 0;

			for (int i = 0; i < sortedSaleItems.size(); i++) {
				SaleItem saleItem = sortedSaleItems.get(i);
				if (sortedSaleItems.size() - (i + 1) < bundleDiscount.size()) {
					BigDecimal discountValue = new BigDecimal(bundleDiscount.get(bundleDiscountCounter++));
					BigDecimal salePrice = calculcatePercentageDiscountValuePrice(saleItem.getItemPrice(), discountValue);
					saleItem.setSalePrice(salePrice);
				} else {
					saleItem.setSalePrice(saleItem.getItemPrice());
				}
				
				saleDao.insertSaleItem(saleItem);
				stockService.updateTheQuantitiyOfSoldStock(saleItem.getItemId(), loggedInEmployee.getStoreId());
			}
		}
	}

	private BigDecimal calculcateAmountDiscountValuePrice(BigDecimal priceBeforeDiscount, BigDecimal discountValueAmount) {
		BigDecimal salePrice;
		if (discountValueAmount.compareTo(BigDecimal.ZERO) > 0) {
			if (discountValueAmount.compareTo(priceBeforeDiscount) >= 0) {
				salePrice = BigDecimal.ZERO;			
			} else {
				salePrice = priceBeforeDiscount.subtract(discountValueAmount);
				discountValueAmount = BigDecimal.ZERO;
			}
		} else {
			salePrice = priceBeforeDiscount;
		}
		
		return salePrice;
	}

	private BigDecimal calculcatePercentageDiscountValuePrice(BigDecimal priceBeforeDiscount, BigDecimal discountValueAmount) {
		BigDecimal ONE_HUNDRED = new BigDecimal(100);
		BigDecimal salePrice = priceBeforeDiscount.multiply(ONE_HUNDRED.subtract(discountValueAmount))
				.divide(ONE_HUNDRED);
		salePrice = salePrice.setScale(2, RoundingMode.HALF_UP);
		return salePrice;
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
		BigDecimal totalAmount = BigDecimal.ZERO;	
		Integer count = 0;

		if (saleItems != null && !saleItems.isEmpty()) {
			totalAmount = saleItems.stream()
			        .map(saleItem -> saleItem.getSalePrice())
			        .reduce(BigDecimal.ZERO, BigDecimal::add);	
			count = saleItems.size();
		}
		
		saleReport.setCount(count);
		saleReport.setTotalAmount(totalAmount);	
	}
	
	private void calculateTotalAmountAndCountSaleByStore(List<SalesByStore> saleByStores, SaleReport saleReport) {
		BigDecimal totalAmount = BigDecimal.ZERO;	
		BigDecimal totalCount = BigDecimal.ZERO;	
		
		if (saleByStores != null && !saleByStores.isEmpty()) {
			totalAmount = saleByStores.stream()
					.filter(saleByStore -> Optional.ofNullable(saleByStore)
							.map(SalesByStore::getAmount)
							.map(amount -> amount != null)
							.orElse(false))
			        .map(saleByStore -> saleByStore.getAmount())	        
			        .reduce(BigDecimal.ZERO, BigDecimal::add);	
			
			totalCount = saleByStores.stream()
					.filter(saleByStore -> Optional.ofNullable(saleByStore)
							.map(SalesByStore::getCount)
							.map(count -> count != null)
							.orElse(false))
					.map(saleByStore -> saleByStore.getCount())	        
					.reduce(BigDecimal.ZERO, BigDecimal::add);	
		}
		
		saleReport.setTotalAmount(totalAmount);	
		saleReport.setCount(totalCount.intValue());
	}
	
	private void calculateTotalAmountAndCountForSales(List<Sale> sales, SaleReport saleReport) {
		BigDecimal totalAmount = BigDecimal.ZERO;
		Integer count = 0;
		
		if (sales != null && !sales.isEmpty()) {
			totalAmount = sales.stream()
			        .map(sale -> sale.getAmount())
			        .reduce(BigDecimal.ZERO, BigDecimal::add);
			count = sales.size();
		}
		
		saleReport.setCount(count);
		saleReport.setTotalAmount(totalAmount);
	}

	@Override
	public TotalSumReport calculateTotalSum(TotalSumRequest totalSumRequest) {
		TotalSumReport totalSumReport = new TotalSumReport();
		
		BigDecimal totalSum = totalSumRequest.getPrices().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
		totalSumReport.setTotalSum(totalSum);
		totalSumReport.setTotalSumAfterDiscount(totalSum);
		
		if (totalSumRequest.getDiscountCode() != null) {
			DiscountCode discountCode = discountDao.selectDiscountCode(totalSumRequest.getDiscountCode());
			
			BigDecimal totalSumAfterDiscount = BigDecimal.ZERO;
			if ("PERCENTAGE".equals(discountCode.getDiscountTypeCode())) {
				totalSumAfterDiscount = calculcatePercentageDiscountValuePrice(totalSum, new BigDecimal(discountCode.getDiscountValue())); 
			} else if ("AMOUNT".equals(discountCode.getDiscountTypeCode())) {
				totalSumAfterDiscount = calculcateAmountDiscountValuePrice(totalSum, new BigDecimal(discountCode.getDiscountValue()));
			} else if ("BUNDLE".equals(discountCode.getDiscountTypeCode())) {
				List<BigDecimal> sortedPrices = totalSumRequest.getPrices();
				Collections.sort(sortedPrices, Collections.reverseOrder());
				
				String discountValueAmount = discountCode.getDiscountValue();
				List<String> bundleDiscount = Arrays.asList(discountValueAmount.split(";"));
				
				int bundleDiscountCounter = 0;
				
				for (int i = 0; i < sortedPrices.size(); i++) {
					BigDecimal price = sortedPrices.get(i);
					if (sortedPrices.size() - (i + 1) < bundleDiscount.size()) {
						BigDecimal discountValue = new BigDecimal(bundleDiscount.get(bundleDiscountCounter++));
						BigDecimal salePrice = calculcatePercentageDiscountValuePrice(price, discountValue);
						totalSumAfterDiscount = totalSumAfterDiscount.add(salePrice);
						//bundleDiscountCounter = bundleDiscountCounter + 1;
					} else {
						totalSumAfterDiscount = totalSumAfterDiscount.add(price);
					}
				}
			}

			totalSumReport.setDiscount(totalSum.subtract(totalSumAfterDiscount));
			totalSumReport.setTotalSumAfterDiscount(totalSumAfterDiscount);
		}
		
		return totalSumReport;
	}

	@Override
	public SaleReport searchSalesByStores(Long startDateMilliseconds, Long endDateMilliseconds, String productCode,
			Integer deviceBrandId, Integer deviceModelId, Integer productTypeId) {
		SaleReport saleReport = generateReport(null, startDateMilliseconds, endDateMilliseconds, productCode, deviceBrandId, deviceModelId);
		
		List<SalesByStore> salesByStores = saleDao.searchSaleByStore(startDateMilliseconds, endDateMilliseconds);
		
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
		int selectedMonth = new Integer(month.split("-")[0]);
		
		int currentYear = Calendar.getInstance(timeZone).get(Calendar.YEAR);
		int currentMonth = Calendar.getInstance(timeZone).get(Calendar.MONTH);
		int currentDay = Calendar.getInstance(timeZone).get(Calendar.DAY_OF_MONTH);
		
		List<SalesByStore> selectedMonthTurnover = Lists.newArrayList();
		List<SalesByStore> previousMonthTurnover = Lists.newArrayList();
		List<SalesByStore> previousYearTurnover = Lists.newArrayList();
		if (selectedMonth == currentMonth) {
			int day;
			if (Calendar.getInstance(timeZone).get(Calendar.HOUR_OF_DAY) < 21 && currentDay < 1) {
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
			previousYearTurnover = getMonthlyTurnover(currentYear - 1, currentMonth, day);
			previousMonthTurnover = getMonthlyTurnover(previousMonthYear, previousMonthMonth, day);
			selectedMonthTurnover = getMonthlyTurnover(currentYear, currentMonth, day);
		} else if (selectedMonth > currentMonth) {
			int selectedMonthDay = YearMonth.of(currentYear - 1,selectedMonth + 1).atEndOfMonth().getDayOfMonth();
			int previousMonthDay = YearMonth.of(currentYear - 1,selectedMonth).atEndOfMonth().getDayOfMonth();
			
			report.setPrevYearDay(selectedMonthDay);
			report.setPrevYearMonth(selectedMonth);
			report.setPrevYearYear(currentYear - 2);
			report.setPrevMonthDay(previousMonthDay);
			report.setPrevMonthMonth(selectedMonth - 1);
			report.setPrevMonthYear(currentYear - 1);
			report.setSelectedMonthDay(selectedMonthDay);
			report.setSelectedMonthMonth(selectedMonth);
			report.setSelectedMonthYear(currentYear - 1);
			
			previousYearTurnover = getMonthlyTurnover(currentYear - 2, selectedMonth , selectedMonthDay);
			previousMonthTurnover = getMonthlyTurnover(currentYear - 1, selectedMonth - 1, previousMonthDay);
			selectedMonthTurnover = getMonthlyTurnover(currentYear - 1, selectedMonth, selectedMonthDay);
		} else if (selectedMonth < currentMonth) {
			int previousMonthDay;
			int previousMonthMonth;
			int previousMonthYear;
			if (selectedMonth == 0) {
				previousMonthDay = YearMonth.of(currentYear - 1, 12).atEndOfMonth().getDayOfMonth();
				previousMonthMonth = 11;
				previousMonthYear = currentYear - 1;
			} else {
				previousMonthDay = YearMonth.of(currentYear, selectedMonth).atEndOfMonth().getDayOfMonth();
				previousMonthMonth = selectedMonth - 1;
				previousMonthYear = currentYear;
			}
				
			int selectedMonthDay = YearMonth.of(currentYear ,selectedMonth + 1).atEndOfMonth().getDayOfMonth();
			
			report.setPrevYearDay(selectedMonthDay);
			report.setPrevYearMonth(selectedMonth);
			report.setPrevYearYear(currentYear - 1);
			report.setPrevMonthDay(previousMonthDay);
			report.setPrevMonthMonth(previousMonthMonth);
			report.setPrevMonthYear(previousMonthYear);
			report.setSelectedMonthDay(selectedMonthDay);
			report.setSelectedMonthMonth(selectedMonth);
			report.setSelectedMonthYear(currentYear);
			previousYearTurnover = getMonthlyTurnover(currentYear - 1, selectedMonth, selectedMonthDay);
			previousMonthTurnover = getMonthlyTurnover(previousMonthYear, previousMonthMonth, previousMonthDay);
			selectedMonthTurnover = getMonthlyTurnover(currentYear, selectedMonth, selectedMonthDay);	
		}
		
		report.setPastPeriodTurnovers(mergeThePastPeriodTurnovers(previousYearTurnover, previousMonthTurnover, selectedMonthTurnover));
		
		return report;
	}

	private List<PastPeriodTurnover> mergeThePastPeriodTurnovers(List<SalesByStore> previousYearTurnover,
			List<SalesByStore> previousMonthTurnover, List<SalesByStore> selectedMonthTurnover) {
		List<PastPeriodTurnover> pastPeriodReportList = Lists.newArrayList();
		SalesByStoreByStoreIdComparator comparator = new SalesByStoreByStoreIdComparator();
		previousYearTurnover.sort(comparator);
		previousMonthTurnover.sort(comparator);
		selectedMonthTurnover.sort(comparator);
		for (int i = 0; i < previousYearTurnover.size(); i++) {
			SalesByStore prevYear = previousYearTurnover.get(i);
			PastPeriodTurnover pastPeriodReport = new PastPeriodTurnover();
			pastPeriodReport.setStoreId(prevYear.getStoreId());
			pastPeriodReport.setStoreCode(prevYear.getStoreCode());
			pastPeriodReport.setStoreName(prevYear.getStoreName());
			pastPeriodReport.setPrevYearTurnover(prevYear.getAmount());
			BigDecimal prevMonthTurnoverAmount = previousMonthTurnover.get(i).getAmount();
			pastPeriodReport.setPrevMonthTurnover(prevMonthTurnoverAmount);
			BigDecimal selectedMonthTurnoverAmount = selectedMonthTurnover.get(i).getAmount();
			pastPeriodReport.setSelectedMonthTurnover(selectedMonthTurnoverAmount);
			
			if (BigDecimal.ZERO.compareTo(prevYear.getAmount()) < 0) {
				BigDecimal prevYearDelta = selectedMonthTurnoverAmount.multiply(ONE_HUNDRED)
						.divide(prevYear.getAmount(), RoundingMode.HALF_UP).subtract(ONE_HUNDRED);
				pastPeriodReport.setPrevYearDelta(prevYearDelta);
			}
			if (BigDecimal.ZERO.compareTo(prevMonthTurnoverAmount) < 0) {
				BigDecimal prevMonthDelta = selectedMonthTurnoverAmount.multiply(ONE_HUNDRED)
						.divide(prevMonthTurnoverAmount, RoundingMode.HALF_UP).subtract(ONE_HUNDRED);
				pastPeriodReport.setPrevMonthDelta(prevMonthDelta);
			}
			pastPeriodReportList.add(pastPeriodReport);
		}
		
		return pastPeriodReportList;
		
	}

	private List<SalesByStore> getMonthlyTurnover(int year, int month, int day) {
		List<SalesByStore> salesByStore = new ArrayList<>();
		if (day == 1) {
			return salesByStore;
		} else {
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

			return saleDao.searchSaleByStore(startDateMilliseconds, endDateMilliseconds);
		}

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
	public DailyReportData getSaleItemDailyReportData(Long startDateTime, Long endDateTime, Integer storeId) {
		return saleDao.selectSaleItemTotalAndCount(startDateTime, endDateTime, storeId);
	}

}
