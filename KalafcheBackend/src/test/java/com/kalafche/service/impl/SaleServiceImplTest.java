package com.kalafche.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.kalafche.dao.SaleDao;
import com.kalafche.dao.DeviceModelDao;
import com.kalafche.dao.ItemDao;
import com.kalafche.model.StoreDto;
import com.kalafche.model.product.Item;
import com.kalafche.model.protectplus.ProtectPlusCertificate;
import com.kalafche.model.protectplus.ProtectPlusCertificateStatus;
import com.kalafche.model.protectplus.ProtectPlusDiscountPolicy;
import com.kalafche.model.protectplus.ProtectPlusDiscountPolicyProductRule;
import com.kalafche.model.sale.ProtectPlusKpiReport;
import com.kalafche.model.sale.ProtectPlusKpiRow;
import com.kalafche.model.sale.Sale;
import com.kalafche.model.sale.SaleItem;
import com.kalafche.model.sale.TotalSumRequest;
import com.kalafche.service.DateService;
import com.kalafche.service.EntityService;
import com.kalafche.service.ProtectPlusCertificateService;
import com.kalafche.service.ProtectPlusDiscountPolicyService;
import com.kalafche.service.StockService;

@RunWith(MockitoJUnitRunner.class)
public class SaleServiceImplTest {

	@Mock
	private ItemDao itemDao;

	@Mock
	private SaleDao saleDao;

	@Mock
	private DeviceModelDao deviceModelDao;

	@Mock
	private StockService stockService;

	@Mock
	private DateService dateService;

	@Mock
	private EntityService entityService;

	@Mock
	private ProtectPlusCertificateService protectPlusCertificateService;

	@Mock
	private ProtectPlusDiscountPolicyService protectPlusDiscountPolicyService;

	@InjectMocks
	private SaleServiceImpl saleService;

	@Before
	public void setUp() {
		lenient().when(protectPlusDiscountPolicyService.getActivePolicy(any())).thenReturn(createDefaultProtectPlusDiscountPolicy());
		final int[] saleItemId = {1000};
		lenient().when(saleDao.insertSaleItem(any(SaleItem.class))).thenAnswer(invocation -> {
			SaleItem saleItem = invocation.getArgument(0);
			Integer id = ++saleItemId[0];
			saleItem.setId(id);
			return id;
		});
	}

	@Test
	public void testSubmitSaleRegistersProtectPlusUsageEvenWhenNoSaleItemDiscountIsApplied() throws SQLException {
		ProtectPlusCertificate certificate = new ProtectPlusCertificate();
		certificate.setId(10);
		certificate.setDeviceModelId(100);
		certificate.setValidUntilTimestamp(null);

		SaleItem saleItem = new SaleItem();
		saleItem.setItemId(1);

		Sale sale = new Sale();
		sale.setEmployeeId(82);
		sale.setStoreId(19);
		sale.setProtectPlusCertificateId(10);
		sale.setSaleItems(Arrays.asList(saleItem));

		Item item = new Item();
		item.setId(1);
		item.setDeviceModelId(200);
		item.setProductCode("10001");
		item.setProductMasterTypeName("ACCESSORY");

		StoreDto store = new StoreDto();
		store.setFdSerialNo("FD123456");

		Sale insertedSale = new Sale();
		insertedSale.setId(22);

		when(dateService.getCurrentMillisBGTimezone()).thenReturn(1770000000000L);
		when(itemDao.getItem(1)).thenReturn(item);
		when(itemDao.getItemPriceByStoreId(1, 19)).thenReturn(new BigDecimal("20.00"));
		when(protectPlusCertificateService.validateActiveCertificate(10)).thenReturn(certificate);
		when(saleDao.insertTransaction(org.mockito.ArgumentMatchers.any())).thenReturn(11);
		when(saleDao.insertSale(sale)).thenReturn(22);
		when(entityService.getStoreById(19)).thenReturn(store);
		when(saleDao.selectSaleByUniqueSaleId("FD123456-0082-000022")).thenReturn(insertedSale);
		when(saleDao.getSaleItemsBySaleId(22)).thenReturn(Arrays.asList(saleItem));

		saleService.submitSale(sale);

		verify(protectPlusCertificateService).registerCertificateUsage(certificate, false, false, false, 22, 19, 82);
	}

	@Test
	public void testSubmitReplacementSaleReusesTransactionWithoutUpdatingIt() throws SQLException {
		SaleItem saleItem = new SaleItem();
		saleItem.setItemId(1);

		Sale sale = new Sale();
		sale.setEmployeeId(82);
		sale.setStoreId(19);
		sale.setReplacementSaleUSI("FD123456-0082-000001");
		sale.setSaleItems(Arrays.asList(saleItem));

		Item item = new Item();
		item.setId(1);
		item.setDeviceModelId(200);
		item.setProductCode("10001");
		item.setProductMasterTypeName("ACCESSORY");

		StoreDto store = new StoreDto();
		store.setFdSerialNo("FD123456");

		Sale insertedSale = new Sale();
		insertedSale.setId(22);

		when(dateService.getCurrentMillisBGTimezone()).thenReturn(1770000000000L);
		when(saleDao.getSaleTransactionId("FD123456-0082-000001")).thenReturn(44);
		when(itemDao.getItem(1)).thenReturn(item);
		when(itemDao.getItemPriceByStoreId(1, 19)).thenReturn(new BigDecimal("20.00"));
		when(saleDao.insertSale(sale)).thenReturn(22);
		when(entityService.getStoreById(19)).thenReturn(store);
		when(saleDao.selectSaleByUniqueSaleId("FD123456-0082-000022")).thenReturn(insertedSale);
		when(saleDao.getSaleItemsBySaleId(22)).thenReturn(Arrays.asList(saleItem));

		saleService.submitSale(sale);

		assertEquals(Integer.valueOf(44), sale.getTransactionId());
		assertEquals(false, sale.getIsInitial());
		verify(saleDao, never()).udpateTransaction(org.mockito.ArgumentMatchers.anyInt(),
				org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyInt());
		verify(saleDao, never()).insertTransaction(any());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSubmitSaleRequiresSoldForDeviceModelWhenProtectPlusDeviceModelIsAmbiguous() throws SQLException {
		SaleItem protectPlusSaleItem = createSaleItem(1, null);
		SaleItem firstProtectorSaleItem = createSaleItem(2, null);
		SaleItem secondProtectorSaleItem = createSaleItem(3, null);

		Sale sale = new Sale();
		sale.setEmployeeId(82);
		sale.setStoreId(19);
		sale.setSaleItems(Arrays.asList(protectPlusSaleItem, firstProtectorSaleItem, secondProtectorSaleItem));

		when(dateService.getCurrentMillisBGTimezone()).thenReturn(1770000000000L);
		when(saleDao.insertTransaction(any())).thenReturn(11);
		when(itemDao.getItem(1)).thenReturn(createProtectPlusItem(1));
		when(itemDao.getItem(2)).thenReturn(createProtectorItem(2, 100));
		when(itemDao.getItem(3)).thenReturn(createProtectorItem(3, 200));

		saleService.submitSale(sale);
	}

	@Test
	public void testSubmitSaleCreatesPendingCertificateForEachProtectPlusSaleItem() throws SQLException {
		SaleItem firstProtectPlusSaleItem = createSaleItem(1, 100);
		SaleItem secondProtectPlusSaleItem = createSaleItem(2, 200);
		SaleItem firstProtectorSaleItem = createSaleItem(3, null);
		SaleItem secondProtectorSaleItem = createSaleItem(4, null);

		Sale sale = new Sale();
		sale.setEmployeeId(82);
		sale.setStoreId(19);
		sale.setSaleItems(Arrays.asList(firstProtectPlusSaleItem, secondProtectPlusSaleItem, firstProtectorSaleItem,
				secondProtectorSaleItem));

		StoreDto store = new StoreDto();
		store.setFdSerialNo("FD123456");

		Sale insertedSale = new Sale();
		insertedSale.setId(22);

		when(dateService.getCurrentMillisBGTimezone()).thenReturn(1770000000000L);
		when(saleDao.insertTransaction(any())).thenReturn(11);
		when(saleDao.insertSale(sale)).thenReturn(22);
		when(entityService.getStoreById(19)).thenReturn(store);
		when(saleDao.selectSaleByUniqueSaleId("FD123456-0082-000022")).thenReturn(insertedSale);
		when(saleDao.getSaleItemsBySaleId(22)).thenReturn(sale.getSaleItems());
		when(itemDao.getItem(1)).thenReturn(createProtectPlusItem(1));
		when(itemDao.getItem(2)).thenReturn(createProtectPlusItem(2));
		when(itemDao.getItem(3)).thenReturn(createProtectorItem(3, 100));
		when(itemDao.getItem(4)).thenReturn(createProtectorItem(4, 200));
		when(itemDao.getItemPriceByStoreId(1, 19)).thenReturn(new BigDecimal("20.00"));
		when(itemDao.getItemPriceByStoreId(2, 19)).thenReturn(new BigDecimal("20.00"));
		when(itemDao.getItemPriceByStoreId(3, 19)).thenReturn(new BigDecimal("10.00"));
		when(itemDao.getItemPriceByStoreId(4, 19)).thenReturn(new BigDecimal("10.00"));

		saleService.submitSale(sale);

		verify(protectPlusCertificateService).createPendingCertificateForSale(22, 1001, 19, 82, 100);
		verify(protectPlusCertificateService).createPendingCertificateForSale(22, 1002, 19, 82, 200);
		verify(protectPlusCertificateService, times(2)).createPendingCertificateForSale(
				org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.anyInt(),
				org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.anyInt(),
				org.mockito.ArgumentMatchers.anyInt());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSubmitSaleRequiresProtectorForEachProtectPlusCertificate() throws SQLException {
		SaleItem firstProtectPlusSaleItem = createSaleItem(1, 100);
		SaleItem secondProtectPlusSaleItem = createSaleItem(2, 100);
		SaleItem protectorSaleItem = createSaleItem(3, null);

		Sale sale = new Sale();
		sale.setEmployeeId(82);
		sale.setStoreId(19);
		sale.setSaleItems(Arrays.asList(firstProtectPlusSaleItem, secondProtectPlusSaleItem, protectorSaleItem));

		when(dateService.getCurrentMillisBGTimezone()).thenReturn(1770000000000L);
		when(saleDao.insertTransaction(any())).thenReturn(11);
		when(itemDao.getItem(1)).thenReturn(createProtectPlusItem(1));
		when(itemDao.getItem(2)).thenReturn(createProtectPlusItem(2));
		when(itemDao.getItem(3)).thenReturn(createProtectorItem(3, 100));

		saleService.submitSale(sale);
	}

	@Test
	public void testCalculateTotalSum_appliesProtectPlusBenefitsForExpiredCertificateWithoutValidityTimestamp() {
		ProtectPlusCertificate certificate = new ProtectPlusCertificate();
		certificate.setId(10);
		certificate.setStatus(ProtectPlusCertificateStatus.EXPIRED);
		certificate.setDeviceModelId(100);
		certificate.setValidUntilTimestamp(null);
		certificate.setFreeDisplayReplacementServiceUsed(false);

		SaleItem saleItem = new SaleItem();
		saleItem.setItemId(1);
		saleItem.setItemPrice(new BigDecimal("30.00"));
		saleItem.setSoldForDeviceModelId(100);

		Item item = new Item();
		item.setId(1);
		item.setProductCode("90001");

		when(protectPlusCertificateService.validateActiveCertificate(10)).thenReturn(certificate);
		when(itemDao.getItem(1)).thenReturn(item);

		TotalSumRequest request = new TotalSumRequest();
		request.setProtectPlusCertificateId(10);
		request.setSelectedSaleItems(Arrays.asList(saleItem));

		TotalSumReport totalSumReport = saleService.calculateTotalSum(request);
		SaleItem calculatedSaleItem = totalSumReport.getSelectedSaleItems().get(0);

		assertEquals(BigDecimal.ZERO.setScale(2), calculatedSaleItem.getSalePrice());
		assertEquals(new BigDecimal("30.00"), calculatedSaleItem.getDiscountAmount());
		assertEquals(new BigDecimal("100"), calculatedSaleItem.getDiscountPercent());
		assertTrue(calculatedSaleItem.getProtectPlusApplied());
	}

	@Test
	public void testCalculateTotalSum_setsSaleItemDiscountAmountAndPercent() {
		SaleItem saleItem = new SaleItem();
		saleItem.setItemId(1);
		saleItem.setItemPrice(new BigDecimal("100.00"));
		saleItem.setDiscountType("PERCENTAGE");
		saleItem.setDiscountValue("20");

		TotalSumRequest request = new TotalSumRequest();
		request.setSelectedSaleItems(Arrays.asList(saleItem));

		TotalSumReport totalSumReport = saleService.calculateTotalSum(request);
		SaleItem calculatedSaleItem = totalSumReport.getSelectedSaleItems().get(0);

		assertEquals(new BigDecimal("80.00"), calculatedSaleItem.getSalePrice());
		assertEquals(new BigDecimal("20.00"), calculatedSaleItem.getDiscountAmount());
		assertEquals(new BigDecimal("20"), calculatedSaleItem.getDiscountPercent());
		assertEquals(new BigDecimal("20.00"), totalSumReport.getDiscount());
		assertEquals(new BigDecimal("80.00"), totalSumReport.getTotalSumAfterDiscount());
	}

	@Test
	public void testCalculateTotalSum_roundsSaleItemDiscountPercent() {
		SaleItem saleItem = new SaleItem();
		saleItem.setItemId(1);
		saleItem.setItemPrice(new BigDecimal("19.97"));
		saleItem.setDiscountType("AMOUNT");
		saleItem.setDiscountValue("3.00");

		TotalSumRequest request = new TotalSumRequest();
		request.setSelectedSaleItems(Arrays.asList(saleItem));

		TotalSumReport totalSumReport = saleService.calculateTotalSum(request);
		SaleItem calculatedSaleItem = totalSumReport.getSelectedSaleItems().get(0);

		assertEquals(new BigDecimal("15"), calculatedSaleItem.getDiscountPercent());
	}

	@Test
	public void testCalculateTotalSum_appliesFreeDisplayReplacementServiceWhenAvailable() {
		ProtectPlusCertificate certificate = new ProtectPlusCertificate();
		certificate.setId(10);
		certificate.setDeviceModelId(100);
		certificate.setFreeDisplayReplacementServiceUsed(false);

		SaleItem saleItem = new SaleItem();
		saleItem.setItemId(1);
		saleItem.setItemPrice(new BigDecimal("30.00"));
		saleItem.setSoldForDeviceModelId(100);

		Item item = new Item();
		item.setId(1);
		item.setProductCode("90001");

		when(protectPlusCertificateService.validateActiveCertificate(10)).thenReturn(certificate);
		when(itemDao.getItem(1)).thenReturn(item);

		TotalSumRequest request = new TotalSumRequest();
		request.setProtectPlusCertificateId(10);
		request.setSelectedSaleItems(Arrays.asList(saleItem));

		TotalSumReport totalSumReport = saleService.calculateTotalSum(request);
		SaleItem calculatedSaleItem = totalSumReport.getSelectedSaleItems().get(0);

		assertEquals(BigDecimal.ZERO.setScale(2), calculatedSaleItem.getSalePrice());
		assertEquals(new BigDecimal("30.00"), calculatedSaleItem.getDiscountAmount());
		assertEquals(new BigDecimal("100"), calculatedSaleItem.getDiscountPercent());
		assertTrue(calculatedSaleItem.getProtectPlusApplied());
	}

	@Test
	public void testCalculateTotalSum_appliesStandardProtectPlusDiscountWhenFreeBatteryServiceAlreadyUsed() {
		ProtectPlusCertificate certificate = new ProtectPlusCertificate();
		certificate.setId(10);
		certificate.setDeviceModelId(100);
		certificate.setFreeBatteryReplacementServiceUsed(true);

		SaleItem saleItem = new SaleItem();
		saleItem.setItemId(1);
		saleItem.setItemPrice(new BigDecimal("30.00"));
		saleItem.setSoldForDeviceModelId(100);

		Item item = new Item();
		item.setId(1);
		item.setProductCode("90002");

		when(protectPlusCertificateService.validateActiveCertificate(10)).thenReturn(certificate);
		when(itemDao.getItem(1)).thenReturn(item);

		TotalSumRequest request = new TotalSumRequest();
		request.setProtectPlusCertificateId(10);
		request.setSelectedSaleItems(Arrays.asList(saleItem));

		TotalSumReport totalSumReport = saleService.calculateTotalSum(request);
		SaleItem calculatedSaleItem = totalSumReport.getSelectedSaleItems().get(0);

		assertEquals(new BigDecimal("25.50"), calculatedSaleItem.getSalePrice());
		assertEquals(new BigDecimal("4.50"), calculatedSaleItem.getDiscountAmount());
		assertEquals(new BigDecimal("15"), calculatedSaleItem.getDiscountPercent());
		assertTrue(calculatedSaleItem.getProtectPlusApplied());
	}

	@Test
	public void testCalculateTotalSum_appliesStandardProtectPlusDiscountForDisplayServiceForDifferentDeviceModel() {
		ProtectPlusCertificate certificate = new ProtectPlusCertificate();
		certificate.setId(10);
		certificate.setDeviceModelId(100);
		certificate.setFreeDisplayReplacementServiceUsed(false);

		SaleItem saleItem = new SaleItem();
		saleItem.setItemId(1);
		saleItem.setItemPrice(new BigDecimal("30.00"));
		saleItem.setSoldForDeviceModelId(200);

		Item item = new Item();
		item.setId(1);
		item.setProductCode("90001");

		when(protectPlusCertificateService.validateActiveCertificate(10)).thenReturn(certificate);
		when(itemDao.getItem(1)).thenReturn(item);

		TotalSumRequest request = new TotalSumRequest();
		request.setProtectPlusCertificateId(10);
		request.setSelectedSaleItems(Arrays.asList(saleItem));

		TotalSumReport totalSumReport = saleService.calculateTotalSum(request);
		SaleItem calculatedSaleItem = totalSumReport.getSelectedSaleItems().get(0);

		assertEquals(new BigDecimal("25.50"), calculatedSaleItem.getSalePrice());
		assertEquals(new BigDecimal("4.50"), calculatedSaleItem.getDiscountAmount());
		assertEquals(new BigDecimal("15"), calculatedSaleItem.getDiscountPercent());
		assertTrue(calculatedSaleItem.getProtectPlusApplied());
	}

	@Test
	public void testCalculateTotalSum_appliesProtectPlusProductSpecificDiscount() {
		ProtectPlusCertificate certificate = new ProtectPlusCertificate();
		certificate.setId(10);
		certificate.setDeviceModelId(100);

		SaleItem saleItem = new SaleItem();
		saleItem.setItemId(1);
		saleItem.setItemPrice(new BigDecimal("100.00"));

		Item item = new Item();
		item.setId(1);
		item.setProductId(55);
		item.setProductMasterTypeName("ACCESSORY");

		ProtectPlusDiscountPolicy policy = createDefaultProtectPlusDiscountPolicy();
		ProtectPlusDiscountPolicyProductRule productRule = new ProtectPlusDiscountPolicyProductRule();
		productRule.setProductId(55);
		productRule.setDiscountPercent(new BigDecimal("30"));
		policy.setProductRules(Arrays.asList(productRule));

		when(protectPlusCertificateService.validateActiveCertificate(10)).thenReturn(certificate);
		when(protectPlusDiscountPolicyService.getActivePolicy(any())).thenReturn(policy);
		when(itemDao.getItem(1)).thenReturn(item);

		TotalSumRequest request = new TotalSumRequest();
		request.setProtectPlusCertificateId(10);
		request.setSelectedSaleItems(Arrays.asList(saleItem));

		TotalSumReport totalSumReport = saleService.calculateTotalSum(request);
		SaleItem calculatedSaleItem = totalSumReport.getSelectedSaleItems().get(0);

		assertEquals(new BigDecimal("70.00"), calculatedSaleItem.getSalePrice());
		assertEquals(new BigDecimal("30.00"), calculatedSaleItem.getDiscountAmount());
		assertEquals(new BigDecimal("30"), calculatedSaleItem.getDiscountPercent());
		assertTrue(calculatedSaleItem.getProtectPlusApplied());
	}

	@Test
	public void testSearchProtectPlusKpiPeriodReportCalculatesUtilityRateFromActiveBase() {
		ProtectPlusKpiRow visibleStoreRow = createProtectPlusKpiRow(19, true, "Store 19",
				new BigDecimal("10"), new BigDecimal("4"));
		visibleStoreRow.setUtilityCount1(new BigDecimal("6"));
		visibleStoreRow.setUtilityCount2(new BigDecimal("3"));
		ProtectPlusKpiRow inactiveStoreRow = createProtectPlusKpiRow(24, false, "Inactive Store",
				new BigDecimal("5"), new BigDecimal("1"));
		inactiveStoreRow.setUtilityCount1(new BigDecimal("2"));
		inactiveStoreRow.setUtilityCount2(new BigDecimal("1"));

		when(saleDao.searchProtectPlusAllStoreKpiRows(1000L, 2000L, 2)).thenReturn(
				Arrays.asList(visibleStoreRow, inactiveStoreRow));

		ProtectPlusKpiReport report = saleService.searchProtectPlusKpiPeriodReport(1000L, 2000L, 2);

		ProtectPlusKpiRow companyRow = report.getSelectedMonthRows().get(0);
		ProtectPlusKpiRow resultStoreRow = report.getSelectedMonthRows().get(1);

		assertEquals(Integer.valueOf(0), companyRow.getStoreId());
		assertEquals(new BigDecimal("15"), companyRow.getActiveBase());
		assertEquals(new BigDecimal("5"), companyRow.getUtilityCount());
		assertEquals(new BigDecimal("33.33"), companyRow.getUtilityRate());
		assertEquals(new BigDecimal("53.33"), companyRow.getUtilityRate1());
		assertEquals(new BigDecimal("26.67"), companyRow.getRetentionRate());
		assertEquals(Integer.valueOf(19), resultStoreRow.getStoreId());
		assertEquals(new BigDecimal("40.00"), resultStoreRow.getUtilityRate());
		assertEquals(new BigDecimal("60.00"), resultStoreRow.getUtilityRate1());
		assertEquals(new BigDecimal("30.00"), resultStoreRow.getRetentionRate());
	}

	@Test
	public void testSearchProtectPlusKpiTrendReportCalculatesForecastForCurrentMonth() {
		when(dateService.getCurrentMillisBGTimezone()).thenReturn(getMillisInSofia(2026, Calendar.JUNE, 15));
		when(dateService.getFullMonthInMillis(anyInt(), anyInt())).thenReturn(new com.kalafche.model.PeriodInMillis(1000L, 2000L));
		when(saleDao.searchProtectPlusAllStoreKpiRows(1000L, 2000L, 0)).thenAnswer(invocation -> {
			ProtectPlusKpiRow storeRow = createProtectPlusKpiRow(19, true, "Store 19",
					new BigDecimal("30"), BigDecimal.ZERO);
			storeRow.setProtectPlusTurnover(new BigDecimal("150.00"));
			return Arrays.asList(storeRow);
		});

		ProtectPlusKpiReport report = saleService.searchProtectPlusKpiTrendReport("5-2026", 0, true);

		ProtectPlusKpiRow companyRow = report.getTrendRows().get(0);
		ProtectPlusKpiRow resultStoreRow = report.getTrendRows().get(1);

		assertEquals(Integer.valueOf(Calendar.JUNE), resultStoreRow.getMonth());
		assertEquals(new BigDecimal("300.00"), resultStoreRow.getProtectPlusTurnoverForecast());
		assertEquals(new BigDecimal("1000.00"), resultStoreRow.getRevenuePer100ActiveBaseForecast());
		assertEquals(new BigDecimal("300.00"), companyRow.getProtectPlusTurnoverForecast());
		assertEquals(new BigDecimal("1000.00"), companyRow.getRevenuePer100ActiveBaseForecast());
	}

	private ProtectPlusDiscountPolicy createDefaultProtectPlusDiscountPolicy() {
		ProtectPlusDiscountPolicy policy = new ProtectPlusDiscountPolicy();
		policy.setSameModelProtectorDiscountPercent(new BigDecimal("50"));
		policy.setOtherProductsDiscountPercent(new BigDecimal("15"));
		return policy;
	}

	private SaleItem createSaleItem(Integer itemId, Integer soldForDeviceModelId) {
		SaleItem saleItem = new SaleItem();
		saleItem.setItemId(itemId);
		saleItem.setSoldForDeviceModelId(soldForDeviceModelId);
		return saleItem;
	}

	private Item createProtectPlusItem(Integer itemId) {
		Item item = new Item();
		item.setId(itemId);
		item.setProductCode("0500");
		item.setProductBonusPts(0);
		return item;
	}

	private Item createProtectorItem(Integer itemId, Integer deviceModelId) {
		Item item = new Item();
		item.setId(itemId);
		item.setDeviceModelId(deviceModelId);
		item.setProductMasterTypeName("PROTECTOR");
		item.setProductBonusPts(0);
		return item;
	}

	private ProtectPlusKpiRow createProtectPlusKpiRow(Integer storeId, boolean isStore, String storeName,
			BigDecimal activeBase, BigDecimal utilityCount) {
		ProtectPlusKpiRow row = new ProtectPlusKpiRow();
		row.setStoreId(storeId);
		row.setStoreName(storeName);
		row.setIsStore(isStore);
		row.setActiveBase(activeBase);
		row.setUtilityCount(utilityCount);
		row.setUtilityCount1(BigDecimal.ZERO);
		row.setUtilityCount2(BigDecimal.ZERO);
		row.setSoldProtectPlusCount(BigDecimal.ZERO);
		row.setSoldProtectorCount(BigDecimal.ZERO);
		row.setProtectPlusTurnover(BigDecimal.ZERO);
		row.setTotalTurnover(BigDecimal.ZERO);
		return row;
	}

	private Long getMillisInSofia(Integer year, Integer month, Integer day) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Sofia"));
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, 12);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

}
