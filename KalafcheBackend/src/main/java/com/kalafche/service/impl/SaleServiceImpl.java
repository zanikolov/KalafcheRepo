package com.kalafche.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import com.kalafche.model.protectplus.ProtectPlusCertificate;
import com.kalafche.model.protectplus.ProtectPlusDiscountPolicy;
import com.kalafche.model.sale.PastPeriodSaleReport;
import com.kalafche.model.sale.PastPeriodTurnover;
import com.kalafche.model.sale.ProtectPlusKpiReport;
import com.kalafche.model.sale.ProtectPlusKpiRow;
import com.kalafche.model.sale.Sale;
import com.kalafche.model.sale.SaleItem;
import com.kalafche.model.sale.SaleReport;
import com.kalafche.model.sale.SaleSplitReportRequest;
import com.kalafche.model.sale.SalesByStore;
import com.kalafche.model.sale.SalesByStoreByDayByProductType;
import com.kalafche.model.sale.TotalSumRequest;
import com.kalafche.model.sale.Transaction;
import com.kalafche.model.sale.TransactionsByStoreByDay;
import com.kalafche.service.DateService;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.EntityService;
import com.kalafche.service.SaleService;
import com.kalafche.service.StockService;
import com.kalafche.service.ProtectPlusCertificateService;
import com.kalafche.service.ProtectPlusDiscountPolicyService;
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

	@Autowired
	ProtectPlusCertificateService protectPlusCertificateService;

	@Autowired
	ProtectPlusDiscountPolicyService protectPlusDiscountPolicyService;

	private static final TimeZone timeZone = TimeZone.getTimeZone("Europe/Sofia");

	private static final BigDecimal ZERO = BigDecimal.ZERO;
	private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
	private static final String PROTECTOR_MASTER_TYPE = "PROTECTOR";
	private static final String PROTECT_PLUS_PRODUCT_CODE = "0500";
	private static final int DEFAULT_PROTECT_PLUS_UTILITY_USAGE_THRESHOLD = 0;
	private static final String FREE_DISPLAY_REPLACEMENT_SERVICE_PRODUCT_CODE = "90001";
	private static final String FREE_BATTERY_REPLACEMENT_SERVICE_PRODUCT_CODE = "90002";

	@Override
	@Transactional
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
			sale.setTransactionId(transactionId);
			sale.setIsInitial(false);
		}

		sale.setEmployeeId(saleEmployeeId);
		sale.setStoreId(sale.getStoreId());
		sale.setSaleTimestamp(currentMillis);

		Map<Integer, Item> itemsById = getItemsById(sale.getSaleItems());
		boolean containsProtectPlusProduct = containsProtectPlusProduct(itemsById);
		validateRequiredSoldForDeviceModels(sale, itemsById);
		validateProtectPlusSaleRequest(sale, itemsById, containsProtectPlusProduct);

		ProtectPlusCertificate protectPlusCertificate = null;
		if (sale.getProtectPlusCertificateId() != null) {
			protectPlusCertificate = protectPlusCertificateService.validateActiveCertificate(sale.getProtectPlusCertificateId());
		}

		Integer saleId = saleDao.insertSale(sale);
		StoreDto store = entityService.getStoreById(sale.getStoreId());
		String usi = generateUSI(store.getFdSerialNo(), saleEmployeeId, saleId);
		saleDao.updateSaleUSI(saleId, usi);

		ProtectPlusDiscountPolicy protectPlusDiscountPolicy = getProtectPlusDiscountPolicy(protectPlusCertificate,
				currentMillis);
		ProtectPlusDiscountUsage protectPlusDiscountUsage = saveSaleItems(sale, saleId, protectPlusCertificate,
				protectPlusDiscountPolicy, itemsById);
		if (protectPlusCertificate != null) {
			protectPlusCertificateService.registerCertificateUsage(protectPlusCertificate,
					protectPlusDiscountUsage.isFreeProtectorUsed(),
					protectPlusDiscountUsage.isFreeDisplayReplacementServiceUsed(),
					protectPlusDiscountUsage.isFreeBatteryReplacementServiceUsed(), saleId, sale.getStoreId(),
					saleEmployeeId);
		}

		if (containsProtectPlusProduct) {
			createPendingCertificatesForSale(sale.getSaleItems(), itemsById, saleId, sale.getStoreId(), saleEmployeeId);
		}

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

	private Map<Integer, Item> getItemsById(List<SaleItem> saleItems) {
		return saleItems.stream()
				.map(saleItem -> itemDao.getItem(saleItem.getItemId()))
				.collect(Collectors.toMap(Item::getId, Function.identity(), (first, second) -> first));
	}

	private Item getItem(Integer itemId, Map<Integer, Item> itemsById) {
		Item item = itemsById.get(itemId);
		if (item == null) {
			throw new DomainObjectNotFoundException("itemId", "Non-existing item.");
		}

		return item;
	}

	private boolean containsProtectPlusProduct(Map<Integer, Item> itemsById) {
		return itemsById.values().stream().anyMatch(this::isProtectPlusProduct);
	}

	private void validateProtectPlusSaleRequest(Sale sale, Map<Integer, Item> itemsById, boolean containsProtectPlusProduct) {
		if (containsProtectPlusProduct && !containsProtectorForProtectPlusPurchase(itemsById)) {
			throw new IllegalArgumentException("protectPlusPurchaseRequiresProtector");
		}
		if (containsProtectPlusProduct) {
			validateProtectPlusCertificateDeviceModels(sale, itemsById);
		}
	}

	private void validateRequiredSoldForDeviceModels(Sale sale, Map<Integer, Item> itemsById) {
		for (SaleItem saleItem : sale.getSaleItems()) {
			Item item = getItem(saleItem.getItemId(), itemsById);
			if (Boolean.TRUE.equals(item.getSoldForDeviceModelRequired())
					&& saleItem.getSoldForDeviceModelId() == null) {
				throw new IllegalArgumentException("soldForDeviceModelRequired");
			}
			if (Boolean.TRUE.equals(item.getSoldForDeviceModelRequired())
					&& deviceModelDao.isUnknownDeviceModel(saleItem.getSoldForDeviceModelId())
					&& isBlank(sale.getDescription())) {
				throw new IllegalArgumentException("soldForUnknownDeviceModelDescriptionRequired");
			}
		}
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	private boolean containsProtectorForProtectPlusPurchase(Map<Integer, Item> itemsById) {
		return itemsById.values().stream()
				.anyMatch(item -> isProtector(item) && !isProtectPlusProduct(item));
	}

	private void validateProtectPlusCertificateDeviceModels(Sale sale, Map<Integer, Item> itemsById) {
		List<SaleItem> protectPlusSaleItems = getProtectPlusSaleItems(sale.getSaleItems(), itemsById);
		Set<Integer> protectorDeviceModelIds = getProtectorDeviceModelIds(sale.getSaleItems(), itemsById);

		if (protectPlusSaleItems.size() > 1 || protectorDeviceModelIds.size() > 1) {
			for (SaleItem protectPlusSaleItem : protectPlusSaleItems) {
				Integer soldForDeviceModelId = protectPlusSaleItem.getSoldForDeviceModelId();
				if (soldForDeviceModelId == null) {
					throw new IllegalArgumentException("protectPlusSoldForDeviceModelRequired");
				}
				if (!protectorDeviceModelIds.contains(soldForDeviceModelId)) {
					throw new IllegalArgumentException("protectPlusProtectorForDeviceModelRequired");
				}
				if (deviceModelDao.isUnknownDeviceModel(soldForDeviceModelId) && isBlank(sale.getDescription())) {
					throw new IllegalArgumentException("soldForUnknownDeviceModelDescriptionRequired");
				}
			}
		}

		Map<Integer, Long> protectPlusDeviceModelCounts = getProtectPlusDeviceModelCounts(protectPlusSaleItems,
				protectorDeviceModelIds);
		Map<Integer, Long> protectorDeviceModelCounts = getProtectorDeviceModelCounts(sale.getSaleItems(), itemsById);
		for (Map.Entry<Integer, Long> protectPlusDeviceModelCount : protectPlusDeviceModelCounts.entrySet()) {
			Long protectorCount = protectorDeviceModelCounts.get(protectPlusDeviceModelCount.getKey());
			if (protectorCount == null || protectorCount < protectPlusDeviceModelCount.getValue()) {
				throw new IllegalArgumentException("protectPlusProtectorForDeviceModelRequired");
			}
		}
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

	private ProtectPlusDiscountUsage saveSaleItems(Sale sale, Integer saleId,
			ProtectPlusCertificate protectPlusCertificate, ProtectPlusDiscountPolicy protectPlusDiscountPolicy,
			Map<Integer, Item> itemsById) {
		List<SaleItem> saleItems = sale.getSaleItems();
		ProtectPlusDiscountUsage protectPlusDiscountUsage = saveSaleItemsWithoutDiscount(sale, saleId, saleItems,
				protectPlusCertificate, protectPlusDiscountPolicy, itemsById);

		List<SaleItem> percentageDiscounTypeItems = saleItems.stream()
				.filter(item -> "PERCENTAGE".equals(item.getDiscountType())).collect(Collectors.toList());
		saveSaleItemsWithPercentageDiscount(sale, saleId, percentageDiscounTypeItems);

		List<SaleItem> amountDiscounTypeItems = saleItems.stream()
				.filter(item -> "AMOUNT".equals(item.getDiscountType())).collect(Collectors.toList());
		saveSaleItemsWithAmountDiscount(sale, saleId, amountDiscounTypeItems);

		List<SaleItem> bundleDiscounTypeItems = saleItems.stream()
				.filter(item -> "BUNDLE".equals(item.getDiscountType())).collect(Collectors.toList());
		saveSaleItemsWithBundleDiscount(sale, saleId, bundleDiscounTypeItems);

		return protectPlusDiscountUsage;
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
					insertSaleItem(saleItem);
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

			insertSaleItem(saleItem);
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

			insertSaleItem(saleItem);
			stockService.updateTheQuantitiyOfSoldStock(saleItem.getItemId(), sale.getStoreId());
		}
	}

	private ProtectPlusDiscountUsage saveSaleItemsWithoutDiscount(Sale sale, Integer saleId,
			List<SaleItem> saleItems, ProtectPlusCertificate protectPlusCertificate,
			ProtectPlusDiscountPolicy protectPlusDiscountPolicy, Map<Integer, Item> itemsById) {
		ProtectPlusDiscountUsage protectPlusDiscountUsage = new ProtectPlusDiscountUsage(protectPlusCertificate);
		for (SaleItem saleItem : saleItems) {
			if (saleItem.getDiscountCode() == null) {
				saleItem.setSaleId(saleId);
				BigDecimal itemPrice = itemDao.getItemPriceByStoreId(saleItem.getItemId(), sale.getStoreId());
				Item item = getItem(saleItem.getItemId(), itemsById);
				saleItem.setBonusPts(item.getProductBonusPts());
				saleItem.setItemPrice(itemPrice);
				BigDecimal protectPlusDiscountPercent = getProtectPlusDiscountPercent(saleItem, item,
						protectPlusDiscountUsage, protectPlusDiscountPolicy);
				if (protectPlusDiscountPercent.compareTo(ZERO) > 0) {
					saleItem.setSalePrice(calculcatePercentageDiscountValuePrice(itemPrice, protectPlusDiscountPercent));
					saleItem.setProtectPlusApplied(true);
				} else {
					saleItem.setSalePrice(itemPrice);
					saleItem.setProtectPlusApplied(false);
				}

				insertSaleItem(saleItem);
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

		return protectPlusDiscountUsage;
	}

	private Integer insertSaleItem(SaleItem saleItem) {
		Integer saleItemId = saleDao.insertSaleItem(saleItem);
		saleItem.setId(saleItemId);
		return saleItemId;
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

	private BigDecimal getProtectPlusDiscountPercent(SaleItem saleItem, Item item,
			ProtectPlusDiscountUsage protectPlusDiscountUsage, ProtectPlusDiscountPolicy protectPlusDiscountPolicy) {
		ProtectPlusCertificate protectPlusCertificate = protectPlusDiscountUsage.getProtectPlusCertificate();
		if (protectPlusCertificate == null || item == null) {
			return ZERO;
		}
		if (isFreeDisplayReplacementServiceForCertificate(saleItem, item, protectPlusCertificate)
				&& protectPlusDiscountUsage.isFreeDisplayReplacementServiceAvailable()) {
			protectPlusDiscountUsage.markFreeDisplayReplacementServiceUsed();
			return ONE_HUNDRED;
		}
		if (isFreeBatteryReplacementServiceForCertificate(saleItem, item, protectPlusCertificate)
				&& protectPlusDiscountUsage.isFreeBatteryReplacementServiceAvailable()) {
			protectPlusDiscountUsage.markFreeBatteryReplacementServiceUsed();
			return ONE_HUNDRED;
		}
		if (isProtectorForCertificate(saleItem, item, protectPlusCertificate)) {
			if (protectPlusDiscountUsage.isFreeProtectorAvailable()) {
				protectPlusDiscountUsage.markFreeProtectorUsed();
				return ONE_HUNDRED;
			}
		}

		BigDecimal productSpecificDiscountPercent = protectPlusDiscountPolicy.getProductDiscountPercent(item.getProductId());
		if (productSpecificDiscountPercent != null) {
			protectPlusDiscountUsage.markProtectPlusApplied();
			return productSpecificDiscountPercent;
		}

		if (isProtectorForCertificate(saleItem, item, protectPlusCertificate)) {

			protectPlusDiscountUsage.markProtectPlusApplied();
			return protectPlusDiscountPolicy.getSameModelProtectorDiscountPercent();
		}

		protectPlusDiscountUsage.markProtectPlusApplied();
		return protectPlusDiscountPolicy.getOtherProductsDiscountPercent();
	}

	private ProtectPlusDiscountPolicy getProtectPlusDiscountPolicy(ProtectPlusCertificate protectPlusCertificate,
			Long timestamp) {
		if (protectPlusCertificate == null) {
			return null;
		}

		return protectPlusDiscountPolicyService.getActivePolicy(timestamp);
	}

	private boolean isProtectorForCertificate(SaleItem saleItem, Item item, ProtectPlusCertificate protectPlusCertificate) {
		Integer effectiveDeviceModelId = getEffectiveDeviceModelId(saleItem, item);
		return isProtector(item) && protectPlusCertificate.getDeviceModelId() != null
				&& protectPlusCertificate.getDeviceModelId().equals(effectiveDeviceModelId);
	}

	private boolean isProtector(Item item) {
		return item != null && PROTECTOR_MASTER_TYPE.equals(item.getProductMasterTypeName());
	}

	private boolean isFreeDisplayReplacementService(Item item) {
		return item != null && FREE_DISPLAY_REPLACEMENT_SERVICE_PRODUCT_CODE.equals(item.getProductCode());
	}

	private boolean isFreeBatteryReplacementService(Item item) {
		return item != null && FREE_BATTERY_REPLACEMENT_SERVICE_PRODUCT_CODE.equals(item.getProductCode());
	}

	private boolean isFreeDisplayReplacementServiceForCertificate(SaleItem saleItem, Item item,
			ProtectPlusCertificate protectPlusCertificate) {
		return isFreeDisplayReplacementService(item)
				&& isForCertificateDeviceModel(saleItem, item, protectPlusCertificate);
	}

	private boolean isFreeBatteryReplacementServiceForCertificate(SaleItem saleItem, Item item,
			ProtectPlusCertificate protectPlusCertificate) {
		return isFreeBatteryReplacementService(item)
				&& isForCertificateDeviceModel(saleItem, item, protectPlusCertificate);
	}

	private boolean isForCertificateDeviceModel(SaleItem saleItem, Item item, ProtectPlusCertificate protectPlusCertificate) {
		Integer effectiveDeviceModelId = getEffectiveDeviceModelId(saleItem, item);
		return protectPlusCertificate.getDeviceModelId() != null
				&& protectPlusCertificate.getDeviceModelId().equals(effectiveDeviceModelId);
	}

	private Integer getEffectiveDeviceModelId(SaleItem saleItem, Item item) {
		if (saleItem != null && saleItem.getSoldForDeviceModelId() != null) {
			return saleItem.getSoldForDeviceModelId();
		}

		return item == null ? null : item.getDeviceModelId();
	}

	private void createPendingCertificatesForSale(List<SaleItem> saleItems, Map<Integer, Item> itemsById,
			Integer saleId, Integer storeId, Integer employeeId) {
		List<SaleItem> protectPlusSaleItems = getProtectPlusSaleItems(saleItems, itemsById);
		Integer inferredDeviceModelId = resolvePendingCertificateDeviceModelId(saleItems, itemsById);

		for (SaleItem protectPlusSaleItem : protectPlusSaleItems) {
			Integer deviceModelId = protectPlusSaleItem.getSoldForDeviceModelId() != null
					? protectPlusSaleItem.getSoldForDeviceModelId()
					: inferredDeviceModelId;
			protectPlusCertificateService.createPendingCertificateForSale(saleId, protectPlusSaleItem.getId(), storeId,
					employeeId, deviceModelId);
		}
	}

	private Integer resolvePendingCertificateDeviceModelId(List<SaleItem> saleItems, Map<Integer, Item> itemsById) {
		Set<Integer> protectorDeviceModelIds = getProtectorDeviceModelIds(saleItems, itemsById);

		return resolveSingleDeviceModelId(protectorDeviceModelIds);
	}

	private List<SaleItem> getProtectPlusSaleItems(List<SaleItem> saleItems, Map<Integer, Item> itemsById) {
		return saleItems.stream()
				.filter(saleItem -> isProtectPlusProduct(getItem(saleItem.getItemId(), itemsById)))
				.collect(Collectors.toList());
	}

	private Set<Integer> getProtectorDeviceModelIds(List<SaleItem> saleItems, Map<Integer, Item> itemsById) {
		return saleItems.stream()
				.map(saleItem -> {
					Item item = getItem(saleItem.getItemId(), itemsById);
					return isProtector(item) && !isProtectPlusProduct(item) ? getEffectiveDeviceModelId(saleItem, item) : null;
				})
				.filter(deviceModelId -> deviceModelId != null)
				.collect(Collectors.toSet());
	}

	private Map<Integer, Long> getProtectorDeviceModelCounts(List<SaleItem> saleItems, Map<Integer, Item> itemsById) {
		return saleItems.stream()
				.map(saleItem -> {
					Item item = getItem(saleItem.getItemId(), itemsById);
					return isProtector(item) && !isProtectPlusProduct(item) ? getEffectiveDeviceModelId(saleItem, item) : null;
				})
				.filter(deviceModelId -> deviceModelId != null)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}

	private Map<Integer, Long> getProtectPlusDeviceModelCounts(List<SaleItem> protectPlusSaleItems,
			Set<Integer> protectorDeviceModelIds) {
		return protectPlusSaleItems.stream()
				.map(saleItem -> saleItem.getSoldForDeviceModelId() != null
						? saleItem.getSoldForDeviceModelId()
						: resolveSingleDeviceModelId(protectorDeviceModelIds))
				.filter(deviceModelId -> deviceModelId != null)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}

	private Integer resolveSingleDeviceModelId(Set<Integer> deviceModelIds) {
		return deviceModelIds.size() == 1 ? deviceModelIds.iterator().next() : null;
	}

	private boolean isProtectPlusProduct(Item item) {
		return item != null && PROTECT_PLUS_PRODUCT_CODE.equals(item.getProductCode());
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
	public void updateSaleItemSoldForDeviceModel(Integer saleItemId, Integer deviceModelId) {
		if (!Boolean.TRUE.equals(employeeService.isLoggedInEmployeeAdmin())) {
			throw new IllegalArgumentException("adminRoleRequired");
		}
		if (saleItemId == null) {
			throw new IllegalArgumentException("saleItemIdRequired");
		}
		if (deviceModelId == null || deviceModelDao.selectDeviceModel(deviceModelId) == null) {
			throw new DomainObjectNotFoundException("deviceModelId", "Non-existing device model.");
		}

		saleDao.updateSaleItemSoldForDeviceModel(saleItemId, deviceModelId);
	}

	@Override
	public SaleReport searchSaleItems(Long startDateMilliseconds, Long endDateMilliseconds, String storeIds,
			String productCode, Integer deviceBrandId, Integer deviceModelId, Integer masterProductTypeId,
			Integer productTypeId, Float priceFrom, Float priceTo, String discountCampaignCode) {
		return searchSaleItems(startDateMilliseconds, endDateMilliseconds, storeIds, productCode, deviceBrandId,
				deviceModelId, masterProductTypeId, productTypeId, priceFrom, priceTo, discountCampaignCode, false, false);
	}

	@Override
	public SaleReport searchSaleItems(Long startDateMilliseconds, Long endDateMilliseconds, String storeIds,
			String productCode, Integer deviceBrandId, Integer deviceModelId, Integer masterProductTypeId,
			Integer productTypeId, Float priceFrom, Float priceTo, String discountCampaignCode,
			Boolean onlyUnknownSoldForDeviceModel, Boolean onlyProtectPlusApplied) {
		SaleReport saleReport = generateReport(storeIds, startDateMilliseconds, endDateMilliseconds, productCode,
				deviceBrandId, deviceModelId);

		List<SaleItem> saleItems = saleDao.searchSaleItems(startDateMilliseconds, endDateMilliseconds,
				entityService.getConcatenatedStoreIdsForFiltering(storeIds), productCode, deviceBrandId, deviceModelId,
				masterProductTypeId, productTypeId, priceFrom, priceTo, discountCampaignCode,
				onlyUnknownSoldForDeviceModel, onlyProtectPlusApplied);

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
	    		totalAttachRate = calculateProtectPlusKpiPercent(totalProtectorPlusCount, totalProtectorCount);
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
	}

	@Override
	public TotalSumReport calculateTotalSum(TotalSumRequest request) {
		TotalSumReport totalSumReport = new TotalSumReport();
		List<SaleItem> selectedSaleItems = request.getSelectedSaleItems();
		validateRequiredSoldForDeviceModels(selectedSaleItems);
		initializeSaleItemDiscounts(selectedSaleItems);

		BigDecimal totalSum = selectedSaleItems.stream().map(SaleItem::getItemPrice)
				.reduce(ZERO, BigDecimal::add);
		totalSumReport.setTotalSum(totalSum);

		List<SaleItem> percentageDiscounTypeItems = selectedSaleItems.stream()
				.filter(item -> "PERCENTAGE".equals(item.getDiscountType())).collect(Collectors.toList());
		if (!percentageDiscounTypeItems.isEmpty()) {
			for (SaleItem item : percentageDiscounTypeItems) {
				BigDecimal salePrice = calculcatePercentageDiscountValuePrice(item.getItemPrice(),
						new BigDecimal(item.getDiscountValue()));
				applySaleItemDiscount(item, salePrice);
			}
		}

		List<SaleItem> amountDiscounTypeItems = selectedSaleItems.stream()
				.filter(item -> "AMOUNT".equals(item.getDiscountType())).collect(Collectors.toList());
		if (!amountDiscounTypeItems.isEmpty()) {
			for (SaleItem item : amountDiscounTypeItems) {
				BigDecimal salePrice = calculcateAmountDiscountValuePrice(item.getItemPrice(),
						new BigDecimal(item.getDiscountValue()));
				applySaleItemDiscount(item, salePrice);
			}
		}

		List<SaleItem> bundleDiscounTypeItems = selectedSaleItems.stream()
				.filter(item -> "BUNDLE".equals(item.getDiscountType())).collect(Collectors.toList());
		if (!bundleDiscounTypeItems.isEmpty()) {
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
						BigDecimal salePrice = calculcatePercentageDiscountValuePrice(
								bundleSortedByPrice.get(i).getItemPrice(), discountValue);
						applySaleItemDiscount(bundleSortedByPrice.get(i), salePrice);
					}
				}
			}
		}

		if (request.getProtectPlusCertificateId() != null) {
			ProtectPlusCertificate protectPlusCertificate = protectPlusCertificateService
					.validateActiveCertificate(request.getProtectPlusCertificateId());
			ProtectPlusDiscountPolicy protectPlusDiscountPolicy = getProtectPlusDiscountPolicy(protectPlusCertificate,
					dateService.getCurrentMillisBGTimezone());
			ProtectPlusDiscountUsage protectPlusDiscountUsage = new ProtectPlusDiscountUsage(protectPlusCertificate);
			for (SaleItem selectedSaleItem : selectedSaleItems) {
				if (selectedSaleItem.getDiscountCode() == null) {
					Item item = itemDao.getItem(selectedSaleItem.getItemId());
					BigDecimal protectPlusDiscountPercent = getProtectPlusDiscountPercent(selectedSaleItem, item,
							protectPlusDiscountUsage, protectPlusDiscountPolicy);
					if (protectPlusDiscountPercent.compareTo(ZERO) > 0) {
						BigDecimal salePrice = calculcatePercentageDiscountValuePrice(selectedSaleItem.getItemPrice(),
								protectPlusDiscountPercent);
						applySaleItemDiscount(selectedSaleItem, salePrice);
						selectedSaleItem.setProtectPlusApplied(true);
					}
				}
			}
		}

		BigDecimal discount = selectedSaleItems.stream().map(SaleItem::getDiscountAmount)
				.reduce(ZERO, BigDecimal::add);
		totalSumReport.setDiscount(discount);
		totalSumReport.setTotalSumAfterDiscount(totalSum.subtract(discount));
		totalSumReport.setSelectedSaleItems(selectedSaleItems);

		calculateChange(request.getPaid(), request.getCurrency(), totalSumReport);

		return totalSumReport;
	}

	private void initializeSaleItemDiscounts(List<SaleItem> saleItems) {
		for (SaleItem saleItem : saleItems) {
			saleItem.setSalePrice(saleItem.getItemPrice());
			saleItem.setDiscountAmount(ZERO);
			saleItem.setDiscountPercent(ZERO);
			saleItem.setProtectPlusApplied(false);
		}
	}

	private void applySaleItemDiscount(SaleItem saleItem, BigDecimal salePrice) {
		saleItem.setSalePrice(salePrice);

		BigDecimal discountAmount = saleItem.getItemPrice().subtract(salePrice);
		if (discountAmount.compareTo(ZERO) < 0) {
			discountAmount = ZERO;
		}
		saleItem.setDiscountAmount(discountAmount);

		if (saleItem.getItemPrice().compareTo(ZERO) > 0) {
			BigDecimal discountPercent = discountAmount.multiply(ONE_HUNDRED)
					.divide(saleItem.getItemPrice(), 0, RoundingMode.HALF_UP);
			saleItem.setDiscountPercent(discountPercent);
		}
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
			if (paid.compareTo(totalSumReport.getTotalSumAfterDiscount()) > 0) {
				change = paid.subtract(totalSumReport.getTotalSumAfterDiscount());
				totalSumReport.setChange(change);
			}
		}
	}

	private void validateRequiredSoldForDeviceModels(List<SaleItem> saleItems) {
		for (SaleItem saleItem : saleItems) {
			if (Boolean.TRUE.equals(saleItem.getSoldForDeviceModelRequired())
					&& saleItem.getSoldForDeviceModelId() == null) {
				throw new IllegalArgumentException("soldForDeviceModelRequired");
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
			previousYearPeriodInMillis = dateService.getMonthInMillis(currentYear - 1, currentMonth, day);
			previousMonthPeriodInMillis = dateService.getMonthInMillis(previousMonthYear, previousMonthMonth, day);
			selectedMonthPeriodInMillis = dateService.getMonthInMillis(currentYear, currentMonth, day);
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

			previousYearPeriodInMillis = dateService.getMonthInMillis(currentYear - 2, selectedMonth, selectedMonthDay);
			previousMonthPeriodInMillis = dateService.getMonthInMillis(currentYear - 1, selectedMonth - 1, previousMonthDay);
			selectedMonthPeriodInMillis = dateService.getMonthInMillis(currentYear - 1, selectedMonth, selectedMonthDay);
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

			previousYearPeriodInMillis = dateService.getMonthInMillis(currentYear - 1, selectedMonth, selectedMonthDay);
			previousMonthPeriodInMillis = dateService.getMonthInMillis(previousMonthYear, previousMonthMonth, previousMonthDay);
			selectedMonthPeriodInMillis = dateService.getMonthInMillis(currentYear, selectedMonth, selectedMonthDay);
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

	@Override
	public ProtectPlusKpiReport searchProtectPlusKpiMonthReport(String month) {
		ProtectPlusKpiReport report = new ProtectPlusKpiReport();
		int selectedMonth = Integer.valueOf(month.split("-")[0]);
		int selectedYear = Integer.valueOf(month.split("-")[1]);
		PeriodInMillis selectedMonthPeriod = dateService.getFullMonthInMillis(selectedYear, selectedMonth);

		report.setSelectedMonthMonth(selectedMonth);
		report.setSelectedMonthYear(selectedYear);
		List<ProtectPlusKpiRow> allStoreRows =
				getProtectPlusAllStoreKpiRows(selectedMonthPeriod, selectedMonth, selectedYear,
						DEFAULT_PROTECT_PLUS_UTILITY_USAGE_THRESHOLD);
		List<ProtectPlusKpiRow> selectedMonthRows = getVisibleProtectPlusStoreRows(allStoreRows);
		selectedMonthRows.add(0, createProtectPlusCompanyKpiRow(allStoreRows, selectedMonth, selectedYear));
		report.setSelectedMonthRows(selectedMonthRows);

		return report;
	}

	@Override
	public ProtectPlusKpiReport searchProtectPlusKpiPeriodReport(Long startDateMilliseconds, Long endDateMilliseconds,
			Integer utilityUsageThreshold) {
		ProtectPlusKpiReport report = new ProtectPlusKpiReport();
		PeriodInMillis selectedPeriod = new PeriodInMillis(startDateMilliseconds, endDateMilliseconds);
		Integer resolvedUtilityUsageThreshold = resolveProtectPlusUtilityUsageThreshold(utilityUsageThreshold);

		List<ProtectPlusKpiRow> allStoreRows = getProtectPlusAllStoreKpiRows(selectedPeriod, null, null,
				resolvedUtilityUsageThreshold);
		List<ProtectPlusKpiRow> selectedPeriodRows = getVisibleProtectPlusStoreRows(allStoreRows);
		selectedPeriodRows.add(0, createProtectPlusCompanyKpiRow(allStoreRows, null, null));
		report.setSelectedMonthRows(selectedPeriodRows);

		return report;
	}

	@Override
	public ProtectPlusKpiReport searchProtectPlusKpiTrendReport(String month, Integer storeId, Boolean includeStoreRows) {
		ProtectPlusKpiReport report = new ProtectPlusKpiReport();
		int selectedMonth = Integer.valueOf(month.split("-")[0]);
		int selectedYear = Integer.valueOf(month.split("-")[1]);

		report.setSelectedMonthMonth(selectedMonth);
		report.setSelectedMonthYear(selectedYear);
		report.setTrendRows(getProtectPlusKpiTrendRows(selectedYear, selectedMonth, storeId, Boolean.TRUE.equals(includeStoreRows)));

		return report;
	}

	private List<ProtectPlusKpiRow> getProtectPlusKpiTrendRows(int selectedYear, int selectedMonth, Integer storeId,
			boolean includeStoreRows) {
		List<ProtectPlusKpiRow> trendRows = Lists.newArrayList();
		Calendar monthCursor = Calendar.getInstance(timeZone);
		monthCursor.set(Calendar.YEAR, selectedYear);
		monthCursor.set(Calendar.MONTH, selectedMonth);
		monthCursor.set(Calendar.DAY_OF_MONTH, 1);

		for (int i = 0; i < 12; i++) {
			int reportYear = monthCursor.get(Calendar.YEAR);
			int reportMonth = monthCursor.get(Calendar.MONTH);
			PeriodInMillis period = dateService.getFullMonthInMillis(reportYear, reportMonth);
			trendRows.addAll(getProtectPlusKpiTrendRowsForMonth(period, reportMonth, reportYear, storeId, includeStoreRows));
			monthCursor.add(Calendar.MONTH, -1);
		}

		return trendRows;
	}

	private List<ProtectPlusKpiRow> getProtectPlusKpiTrendRowsForMonth(PeriodInMillis period, int month, int year,
			Integer storeId, boolean includeStoreRows) {
		boolean companyTrend = Integer.valueOf(0).equals(storeId);
		if (companyTrend && includeStoreRows) {
			List<ProtectPlusKpiRow> allStoreRows = getProtectPlusAllStoreKpiRows(period, month, year,
					DEFAULT_PROTECT_PLUS_UTILITY_USAGE_THRESHOLD);
			List<ProtectPlusKpiRow> visibleRows = getVisibleProtectPlusStoreRows(allStoreRows);
			visibleRows.add(0, createProtectPlusCompanyKpiRow(allStoreRows, month, year));
			return visibleRows;
		}
		if (companyTrend) {
			List<ProtectPlusKpiRow> companyRows = new ArrayList<ProtectPlusKpiRow>();
			companyRows.add(getProtectPlusCompanyKpiRow(period, month, year));
			return companyRows;
		}

		List<ProtectPlusKpiRow> rows = getProtectPlusKpiRows(period, month, year, storeId);
		return rows;
	}

	private List<ProtectPlusKpiRow> getProtectPlusKpiRows(PeriodInMillis period, Integer month, Integer year, Integer storeId) {
		List<ProtectPlusKpiRow> storeRows =
				saleDao.searchProtectPlusKpiRows(period.getStartDateTime(), period.getEndDateTime(), storeId,
						DEFAULT_PROTECT_PLUS_UTILITY_USAGE_THRESHOLD);
		storeRows.forEach(row -> enrichProtectPlusKpiRow(row, month, year));

		return storeRows;
	}

	private List<ProtectPlusKpiRow> getProtectPlusAllStoreKpiRows(PeriodInMillis period, Integer month, Integer year,
			Integer utilityUsageThreshold) {
		List<ProtectPlusKpiRow> storeRows =
				saleDao.searchProtectPlusAllStoreKpiRows(period.getStartDateTime(), period.getEndDateTime(),
						utilityUsageThreshold);
		storeRows.forEach(row -> enrichProtectPlusKpiRow(row, month, year));

		return storeRows;
	}

	private List<ProtectPlusKpiRow> getVisibleProtectPlusStoreRows(List<ProtectPlusKpiRow> storeRows) {
		return storeRows.stream()
				.filter(row -> Boolean.TRUE.equals(row.getIsStore()))
				.collect(Collectors.toList());
	}

	private ProtectPlusKpiRow createProtectPlusCompanyKpiRow(List<ProtectPlusKpiRow> storeRows, Integer month, Integer year) {
		ProtectPlusKpiRow companyRow = new ProtectPlusKpiRow();
		companyRow.setStoreId(0);
		companyRow.setStoreCode("COMPANY");
		companyRow.setStoreName("Всички магазини");
		companyRow.setIsStore(false);
		companyRow.setActiveBase(sumProtectPlusKpiValue(storeRows, ProtectPlusKpiRow::getActiveBase));
		companyRow.setSoldProtectPlusCount(sumProtectPlusKpiValue(storeRows, ProtectPlusKpiRow::getSoldProtectPlusCount));
		companyRow.setSoldProtectorCount(sumProtectPlusKpiValue(storeRows, ProtectPlusKpiRow::getSoldProtectorCount));
		companyRow.setProtectPlusTurnover(sumProtectPlusKpiValue(storeRows, ProtectPlusKpiRow::getProtectPlusTurnover));
		companyRow.setTotalTurnover(sumProtectPlusKpiValue(storeRows, ProtectPlusKpiRow::getTotalTurnover));
		companyRow.setUtilityCount(sumProtectPlusKpiValue(storeRows, ProtectPlusKpiRow::getUtilityCount));
		companyRow.setUtilityCount1(sumProtectPlusKpiValue(storeRows, ProtectPlusKpiRow::getUtilityCount1));
		companyRow.setUtilityCount2(sumProtectPlusKpiValue(storeRows, ProtectPlusKpiRow::getUtilityCount2));
		enrichProtectPlusKpiRow(companyRow, month, year);

		return companyRow;
	}

	private ProtectPlusKpiRow getProtectPlusCompanyKpiRow(PeriodInMillis period, Integer month, Integer year) {
		ProtectPlusKpiRow companyRow =
				saleDao.searchProtectPlusCompanyKpiRow(period.getStartDateTime(), period.getEndDateTime(),
						DEFAULT_PROTECT_PLUS_UTILITY_USAGE_THRESHOLD);
		enrichProtectPlusKpiRow(companyRow, month, year);

		return companyRow;
	}

	private BigDecimal sumProtectPlusKpiValue(List<ProtectPlusKpiRow> rows,
			Function<ProtectPlusKpiRow, BigDecimal> valueExtractor) {
		return rows.stream()
				.map(valueExtractor)
				.filter(value -> value != null)
				.reduce(ZERO, BigDecimal::add);
	}

	private void enrichProtectPlusKpiRow(ProtectPlusKpiRow row, Integer month, Integer year) {
		row.setMonth(month);
		row.setYear(year);
		setCalculatedProtectPlusKpiValues(row);
		setProtectPlusKpiForecastValues(row, month, year);
	}

	private void setCalculatedProtectPlusKpiValues(ProtectPlusKpiRow row) {
		row.setAttachRate(calculateProtectPlusKpiPercent(row.getSoldProtectPlusCount(), row.getSoldProtectorCount()));
		row.setProtectPlusShare(calculateProtectPlusKpiPercent(row.getProtectPlusTurnover(), row.getTotalTurnover()));
		row.setRevenuePer100ActiveBase(calculateProtectPlusKpiPercent(row.getProtectPlusTurnover(), row.getActiveBase()));
		row.setUtilityRate(calculateProtectPlusKpiPercent(row.getUtilityCount(), row.getActiveBase()));
		row.setUtilityRate1(calculateProtectPlusKpiPercent(row.getUtilityCount1(), row.getActiveBase()));
		row.setRetentionRate(calculateProtectPlusKpiPercent(row.getUtilityCount2(), row.getActiveBase()));
	}

	private void setProtectPlusKpiForecastValues(ProtectPlusKpiRow row, Integer month, Integer year) {
		row.setProtectPlusTurnoverForecast(null);
		row.setRevenuePer100ActiveBaseForecast(null);
		if (month == null || year == null) {
			return;
		}

		Calendar currentDate = Calendar.getInstance(timeZone);
		currentDate.setTimeInMillis(dateService.getCurrentMillisBGTimezone());
		if (currentDate.get(Calendar.YEAR) != year || currentDate.get(Calendar.MONTH) != month) {
			return;
		}

		Integer elapsedDays = currentDate.get(Calendar.DAY_OF_MONTH);
		Integer daysInMonth = currentDate.getActualMaximum(Calendar.DAY_OF_MONTH);
		BigDecimal protectPlusTurnoverForecast = calculateProtectPlusMonthlyForecast(row.getProtectPlusTurnover(),
				elapsedDays, daysInMonth);
		row.setProtectPlusTurnoverForecast(protectPlusTurnoverForecast);
		row.setRevenuePer100ActiveBaseForecast(calculateProtectPlusKpiPercent(protectPlusTurnoverForecast,
				row.getActiveBase()));
	}

	private BigDecimal calculateProtectPlusMonthlyForecast(BigDecimal currentValue, Integer elapsedDays,
			Integer daysInMonth) {
		if (currentValue == null || elapsedDays == null || elapsedDays <= 0 || daysInMonth == null || daysInMonth <= 0) {
			return ZERO;
		}

		return currentValue.multiply(new BigDecimal(daysInMonth))
				.divide(new BigDecimal(elapsedDays), 2, RoundingMode.HALF_UP);
	}

	private Integer resolveProtectPlusUtilityUsageThreshold(Integer utilityUsageThreshold) {
		if (utilityUsageThreshold == null) {
			return DEFAULT_PROTECT_PLUS_UTILITY_USAGE_THRESHOLD;
		}
		if (utilityUsageThreshold < 0) {
			throw new IllegalArgumentException("Protect+ utility usage threshold should be greater than or equal to 0.");
		}

		return utilityUsageThreshold;
	}

	private BigDecimal calculateProtectPlusKpiPercent(BigDecimal numerator, BigDecimal denominator) {
		if (numerator == null || denominator == null || ZERO.compareTo(denominator) >= 0) {
			return ZERO;
		}

		return numerator.multiply(ONE_HUNDRED).divide(denominator, 2, RoundingMode.HALF_UP);
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

	private static class ProtectPlusDiscountUsage {

		private final ProtectPlusCertificate protectPlusCertificate;
		private boolean freeProtectorUsedInSale;
		private boolean freeDisplayReplacementServiceUsedInSale;
		private boolean freeBatteryReplacementServiceUsedInSale;
		private boolean protectPlusApplied;

		ProtectPlusDiscountUsage(ProtectPlusCertificate protectPlusCertificate) {
			this.protectPlusCertificate = protectPlusCertificate;
		}

		ProtectPlusCertificate getProtectPlusCertificate() {
			return protectPlusCertificate;
		}

		boolean isFreeProtectorAvailable() {
			return protectPlusCertificate != null
					&& !Boolean.TRUE.equals(protectPlusCertificate.getFreeProtectorUsed())
					&& !freeProtectorUsedInSale;
		}

		void markFreeProtectorUsed() {
			freeProtectorUsedInSale = true;
			protectPlusApplied = true;
		}

		boolean isFreeDisplayReplacementServiceAvailable() {
			return protectPlusCertificate != null
					&& !Boolean.TRUE.equals(protectPlusCertificate.getFreeDisplayReplacementServiceUsed())
					&& !freeDisplayReplacementServiceUsedInSale;
		}

		void markFreeDisplayReplacementServiceUsed() {
			freeDisplayReplacementServiceUsedInSale = true;
			protectPlusApplied = true;
		}

		boolean isFreeBatteryReplacementServiceAvailable() {
			return protectPlusCertificate != null
					&& !Boolean.TRUE.equals(protectPlusCertificate.getFreeBatteryReplacementServiceUsed())
					&& !freeBatteryReplacementServiceUsedInSale;
		}

		void markFreeBatteryReplacementServiceUsed() {
			freeBatteryReplacementServiceUsedInSale = true;
			protectPlusApplied = true;
		}

		void markProtectPlusApplied() {
			protectPlusApplied = true;
		}

		boolean isFreeProtectorUsed() {
			return freeProtectorUsedInSale;
		}

		boolean isFreeDisplayReplacementServiceUsed() {
			return freeDisplayReplacementServiceUsedInSale;
		}

		boolean isFreeBatteryReplacementServiceUsed() {
			return freeBatteryReplacementServiceUsedInSale;
		}

		boolean isProtectPlusApplied() {
			return protectPlusApplied;
		}
	}
}
