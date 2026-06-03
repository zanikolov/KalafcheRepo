package com.kalafche.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.kalafche.dao.SaleDao;
import com.kalafche.dao.ItemDao;
import com.kalafche.model.StoreDto;
import com.kalafche.model.product.Item;
import com.kalafche.model.protectplus.ProtectPlusCertificate;
import com.kalafche.model.protectplus.ProtectPlusCertificateStatus;
import com.kalafche.model.sale.Sale;
import com.kalafche.model.sale.SaleItem;
import com.kalafche.model.sale.TotalSumRequest;
import com.kalafche.service.DateService;
import com.kalafche.service.EntityService;
import com.kalafche.service.ProtectPlusCertificateService;
import com.kalafche.service.StockService;

@RunWith(MockitoJUnitRunner.class)
public class SaleServiceImplTest {

	@Mock
	private ItemDao itemDao;

	@Mock
	private SaleDao saleDao;

	@Mock
	private StockService stockService;

	@Mock
	private DateService dateService;

	@Mock
	private EntityService entityService;

	@Mock
	private ProtectPlusCertificateService protectPlusCertificateService;

	@InjectMocks
	private SaleServiceImpl saleService;

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

}
