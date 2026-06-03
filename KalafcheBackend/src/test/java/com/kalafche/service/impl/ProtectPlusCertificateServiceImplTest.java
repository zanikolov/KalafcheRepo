package com.kalafche.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;

import com.kalafche.dao.LoyalCustomerDao;
import com.kalafche.dao.ProtectPlusCertificateDao;
import com.kalafche.dao.DeviceModelDao;
import com.kalafche.exceptions.IllegalStateTransferException;
import com.kalafche.model.LoyalCustomer;
import com.kalafche.model.device.DeviceModel;
import com.kalafche.model.employee.Employee;
import com.kalafche.model.protectplus.ProtectPlusCallRecord;
import com.kalafche.model.protectplus.ProtectPlusCertificate;
import com.kalafche.model.protectplus.ProtectPlusCustomerEmailUpdateRequest;
import com.kalafche.model.protectplus.ProtectPlusDeviceModelChangeRequest;
import com.kalafche.model.protectplus.ProtectPlusCertificateRequest;
import com.kalafche.model.protectplus.ProtectPlusCertificateStatus;
import com.kalafche.service.DateService;
import com.kalafche.service.EmailService;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.EntityService;
import com.kalafche.service.fileutil.ImageUploadService;
import com.kalafche.model.protectplus.ProtectPlusCertificateSearchResult;

@RunWith(MockitoJUnitRunner.class)
public class ProtectPlusCertificateServiceImplTest {

	@Mock
	private ProtectPlusCertificateDao protectPlusCertificateDao;

	@Mock
	private DeviceModelDao deviceModelDao;

	@Mock
	private LoyalCustomerDao loyalCustomerDao;

	@Mock
	private DateService dateService;

	@Mock
	private EmployeeService employeeService;

	@Mock
	private EntityService entityService;

	@Mock
	private ImageUploadService imageUploadService;

	@Mock
	private EmailService emailService;

	@Mock
	private MultipartFile gdprConsentImage;

	@Mock
	private MultipartFile callRecording;

	@InjectMocks
	private ProtectPlusCertificateServiceImpl protectPlusCertificateService;

	@Test
	public void testExpireExpiredProtectPlusCertificates() {
		long currentTimestamp = 1779926700000L;
		when(dateService.getCurrentMillisBGTimezone()).thenReturn(currentTimestamp);

		protectPlusCertificateService.expireExpiredProtectPlusCertificates();

		verify(protectPlusCertificateDao).expireActiveProtectPlusCertificates(currentTimestamp);
	}

	@Test
	public void testSearchActiveCertificatesByNumberBypassesStoreVisibility() {
		ProtectPlusCertificateSearchResult searchResult = new ProtectPlusCertificateSearchResult();
		searchResult.setCertificateNumber(10000);

		when(employeeService.isLoggedInEmployeeAdmin()).thenReturn(false);
		when(protectPlusCertificateDao.searchProtectPlusCertificates(eq(10000), isNull(),
				eq(ProtectPlusCertificateStatus.ACTIVE), isNull(), isNull(), isNull(), eq(1)))
				.thenReturn(Arrays.asList(searchResult));

		List<ProtectPlusCertificateSearchResult> results = protectPlusCertificateService.searchActiveProtectPlusCertificates(
				10000, null, null, null, null);

		assertEquals(1, results.size());
		verify(protectPlusCertificateDao).searchProtectPlusCertificates(eq(10000), isNull(),
				eq(ProtectPlusCertificateStatus.ACTIVE), isNull(), isNull(), isNull(), eq(1));
	}

	@Test
	public void testSearchActiveCertificatesByPhoneNumberBypassesStoreVisibility() {
		ProtectPlusCertificateSearchResult searchResult = new ProtectPlusCertificateSearchResult();
		searchResult.setPhoneNumber("+359884629324");

		when(employeeService.isLoggedInEmployeeAdmin()).thenReturn(false);
		when(protectPlusCertificateDao.searchProtectPlusCertificates(isNull(), eq("+359884629324"),
				eq(ProtectPlusCertificateStatus.ACTIVE), isNull(), isNull(), isNull(), eq(1)))
				.thenReturn(Arrays.asList(searchResult));

		List<ProtectPlusCertificateSearchResult> results = protectPlusCertificateService.searchActiveProtectPlusCertificates(
				null, "+359884629324", null, null, null);

		assertEquals(1, results.size());
		verify(protectPlusCertificateDao).searchProtectPlusCertificates(isNull(), eq("+359884629324"),
				eq(ProtectPlusCertificateStatus.ACTIVE), isNull(), isNull(), isNull(), eq(1));
	}

	@Test
	public void testSearchActiveCertificatesByNumericQueryBypassesStoreVisibility() {
		ProtectPlusCertificateSearchResult searchResult = new ProtectPlusCertificateSearchResult();
		searchResult.setCertificateNumber(10000);

		when(protectPlusCertificateDao.searchProtectPlusCertificates(eq(10000), isNull(),
				eq(ProtectPlusCertificateStatus.ACTIVE), isNull(), isNull(), isNull(), isNull()))
				.thenReturn(Arrays.asList(searchResult));

		List<ProtectPlusCertificateSearchResult> results = protectPlusCertificateService.searchActiveProtectPlusCertificatesByQuery(
				"10000");

		assertEquals(1, results.size());
		verify(protectPlusCertificateDao).searchProtectPlusCertificates(eq(10000), isNull(),
				eq(ProtectPlusCertificateStatus.ACTIVE), isNull(), isNull(), isNull(), isNull());
	}

	@Test
	public void testSearchActiveCertificatesByNumericPhoneQueryBypassesStoreVisibility() {
		ProtectPlusCertificateSearchResult searchResult = new ProtectPlusCertificateSearchResult();
		searchResult.setPhoneNumber("0884629324");

		when(protectPlusCertificateDao.searchProtectPlusCertificates(eq(884629324), isNull(),
				eq(ProtectPlusCertificateStatus.ACTIVE), isNull(), isNull(), isNull(), isNull()))
				.thenReturn(Arrays.asList());
		when(protectPlusCertificateDao.searchProtectPlusCertificates(isNull(), eq("0884629324"),
				eq(ProtectPlusCertificateStatus.ACTIVE), isNull(), isNull(), isNull(), isNull()))
				.thenReturn(Arrays.asList(searchResult));

		List<ProtectPlusCertificateSearchResult> results = protectPlusCertificateService.searchActiveProtectPlusCertificatesByQuery(
				"0884629324");

		assertEquals(1, results.size());
		verify(protectPlusCertificateDao).searchProtectPlusCertificates(eq(884629324), isNull(),
				eq(ProtectPlusCertificateStatus.ACTIVE), isNull(), isNull(), isNull(), isNull());
		verify(protectPlusCertificateDao).searchProtectPlusCertificates(isNull(), eq("0884629324"),
				eq(ProtectPlusCertificateStatus.ACTIVE), isNull(), isNull(), isNull(), isNull());
	}

	@Test
	public void testCreatePendingCertificateForSaleCreatesInactiveCertificate() {
		long currentTimestamp = 1763654400000L;

		when(dateService.getCurrentMillisBGTimezone()).thenReturn(currentTimestamp);
		when(protectPlusCertificateDao.getMaxCertificateNumber()).thenReturn(10005);
		when(protectPlusCertificateDao.insertProtectPlusCertificate(any(ProtectPlusCertificate.class))).thenReturn(20);

		protectPlusCertificateService.createPendingCertificateForSale(34, 3, 7, 1087);

		ArgumentCaptor<ProtectPlusCertificate> certificateCaptor = ArgumentCaptor.forClass(ProtectPlusCertificate.class);
		verify(protectPlusCertificateDao).insertProtectPlusCertificate(certificateCaptor.capture());
		ProtectPlusCertificate certificate = certificateCaptor.getValue();

		assertEquals(Integer.valueOf(10006), certificate.getCertificateNumber());
		assertEquals(Integer.valueOf(3), certificate.getSoldStoreId());
		assertEquals(Integer.valueOf(7), certificate.getSoldByEmployeeId());
		assertEquals(Integer.valueOf(34), certificate.getSoldSaleId());
		assertEquals(Integer.valueOf(1087), certificate.getDeviceModelId());
		assertEquals(ProtectPlusCertificateStatus.INACTIVE, certificate.getStatus());
		assertEquals(Boolean.FALSE, certificate.getFreeProtectorUsed());
		assertEquals(Long.valueOf(currentTimestamp), certificate.getCreatedTimestamp());
	}

	@Test
	public void testRegisterCertificateUsageExtendsValidityAndCreatesRenewalHistory() {
		long currentTimestamp = 1763654400000L;
		long oldValidUntilTimestamp = 1766246400000L;
		long extendedUntilTimestamp = 1779235200000L;
		long extendedUntilEndOfDayTimestamp = 1779310799999L;

		ProtectPlusCertificate certificate = new ProtectPlusCertificate();
		certificate.setId(10);
		certificate.setValidUntilTimestamp(oldValidUntilTimestamp);
		certificate.setFreeProtectorUsed(false);

		when(dateService.getCurrentMillisBGTimezone()).thenReturn(currentTimestamp);
		when(dateService.addMonthsInMillisBGTimezone(currentTimestamp, 6)).thenReturn(extendedUntilTimestamp);
		when(dateService.endOfDayInMillisBGTimezone(extendedUntilTimestamp)).thenReturn(extendedUntilEndOfDayTimestamp);

		protectPlusCertificateService.registerCertificateUsage(certificate, true, false, false, 44, 3, 7);

		verify(protectPlusCertificateDao).updateProtectPlusCertificateUsage(10, true, false, false,
				extendedUntilEndOfDayTimestamp, ProtectPlusCertificateStatus.ACTIVE, 7, currentTimestamp);
		verify(protectPlusCertificateDao).insertProtectPlusCertificateRenewal(10, 44, 3, 7, oldValidUntilTimestamp,
				extendedUntilEndOfDayTimestamp, "SALE_USAGE", currentTimestamp);
	}

	@Test
	public void testRegisterCertificateUsageDoesNotCreateRenewalHistoryWhenValidityIsAlreadyLongEnough() {
		long currentTimestamp = 1763654400000L;
		long validUntilTimestamp = 1795190400000L;
		long extendedUntilTimestamp = 1779235200000L;
		long extendedUntilEndOfDayTimestamp = 1779310799999L;

		ProtectPlusCertificate certificate = new ProtectPlusCertificate();
		certificate.setId(10);
		certificate.setValidUntilTimestamp(validUntilTimestamp);
		certificate.setFreeProtectorUsed(true);

		when(dateService.getCurrentMillisBGTimezone()).thenReturn(currentTimestamp);
		when(dateService.addMonthsInMillisBGTimezone(currentTimestamp, 6)).thenReturn(extendedUntilTimestamp);
		when(dateService.endOfDayInMillisBGTimezone(extendedUntilTimestamp)).thenReturn(extendedUntilEndOfDayTimestamp);

		protectPlusCertificateService.registerCertificateUsage(certificate, false, false, false, 44, 3, 7);

		verify(protectPlusCertificateDao).updateProtectPlusCertificateUsage(10, true, false, false,
				validUntilTimestamp, ProtectPlusCertificateStatus.ACTIVE, 7, currentTimestamp);
		verify(protectPlusCertificateDao, never()).insertProtectPlusCertificateRenewal(any(Integer.class),
				any(Integer.class), any(Integer.class), any(Integer.class), any(Long.class), any(Long.class),
				any(String.class), any(Long.class));
	}

	@Test
	public void testChangeDeviceModelAllowsFirstNonAdminChange() {
		long currentTimestamp = 1763654400000L;

		ProtectPlusCertificate certificate = createValidActiveCertificate(10, 1087);
		ProtectPlusDeviceModelChangeRequest request = new ProtectPlusDeviceModelChangeRequest();
		request.setDeviceModelId(1090);

		Employee loggedInEmployee = new Employee();
		loggedInEmployee.setId(7);
		loggedInEmployee.setStoreId(3);

		when(protectPlusCertificateDao.getProtectPlusCertificate(10)).thenReturn(certificate);
		when(dateService.getCurrentMillisBGTimezone()).thenReturn(currentTimestamp);
		when(deviceModelDao.selectDeviceModel(1090)).thenReturn(new DeviceModel());
		when(employeeService.getLoggedInEmployee()).thenReturn(loggedInEmployee);
		when(employeeService.isLoggedInEmployeeAdmin()).thenReturn(false);

		protectPlusCertificateService.changeDeviceModel(10, request);

		verify(protectPlusCertificateDao).updateDeviceModel(10, 1090, true, 7, currentTimestamp);
		verify(protectPlusCertificateDao).insertProtectPlusCertificateDeviceModelChange(10, 3, 7, 1087, 1090,
				false, currentTimestamp);
	}

	@Test
	public void testUploadCallRecordingCreatesCallRecord() {
		long currentTimestamp = 1763654400000L;

		ProtectPlusCertificate certificate = createValidActiveCertificate(10, 1087);
		Employee loggedInEmployee = new Employee();
		loggedInEmployee.setId(7);
		loggedInEmployee.setName("Иван Иванов");
		loggedInEmployee.setStoreId(3);

		when(callRecording.isEmpty()).thenReturn(false);
		when(callRecording.getOriginalFilename()).thenReturn("call.mp3");
		when(protectPlusCertificateDao.getProtectPlusCertificate(10)).thenReturn(certificate);
		when(employeeService.getLoggedInEmployee()).thenReturn(loggedInEmployee);
		when(dateService.getCurrentMillisBGTimezone()).thenReturn(currentTimestamp);
		when(imageUploadService.uploadProtectPlusCallRecording(callRecording)).thenReturn("audio-file-id");
		when(protectPlusCertificateDao.insertProtectPlusCallRecord(any(ProtectPlusCallRecord.class))).thenReturn(55);

		ProtectPlusCallRecord result = protectPlusCertificateService.uploadCallRecording(10, callRecording, "Follow-up");

		ArgumentCaptor<ProtectPlusCallRecord> callRecordCaptor = ArgumentCaptor.forClass(ProtectPlusCallRecord.class);
		verify(protectPlusCertificateDao).insertProtectPlusCallRecord(callRecordCaptor.capture());

		ProtectPlusCallRecord callRecord = callRecordCaptor.getValue();
		assertEquals(Integer.valueOf(10), callRecord.getProtectPlusCertificateId());
		assertEquals(Integer.valueOf(3), callRecord.getStoreId());
		assertEquals(Integer.valueOf(7), callRecord.getEmployeeId());
		assertEquals("audio-file-id", callRecord.getCallRecordingFileId());
		assertEquals("call.mp3", callRecord.getCallRecordingFileName());
		assertEquals("Follow-up", callRecord.getNote());
		assertEquals(Long.valueOf(currentTimestamp), callRecord.getCreatedTimestamp());
		assertEquals(Integer.valueOf(55), result.getId());
		assertEquals("Иван Иванов", result.getEmployeeName());
	}

	@Test(expected = IllegalStateTransferException.class)
	public void testChangeDeviceModelRejectsSecondNonAdminChange() {
		long currentTimestamp = 1763654400000L;

		ProtectPlusCertificate certificate = createValidActiveCertificate(10, 1087);
		certificate.setDeviceModelChangeUsed(true);
		ProtectPlusDeviceModelChangeRequest request = new ProtectPlusDeviceModelChangeRequest();
		request.setDeviceModelId(1090);

		Employee loggedInEmployee = new Employee();
		loggedInEmployee.setId(7);
		loggedInEmployee.setStoreId(3);

		when(protectPlusCertificateDao.getProtectPlusCertificate(10)).thenReturn(certificate);
		when(dateService.getCurrentMillisBGTimezone()).thenReturn(currentTimestamp);
		when(deviceModelDao.selectDeviceModel(1090)).thenReturn(new DeviceModel());
		when(employeeService.getLoggedInEmployee()).thenReturn(loggedInEmployee);
		when(employeeService.isLoggedInEmployeeAdmin()).thenReturn(false);

		protectPlusCertificateService.changeDeviceModel(10, request);
	}

	@Test
	public void testChangeDeviceModelAllowsAdminOverrideWhenNonAdminChangeAlreadyExists() {
		long currentTimestamp = 1763654400000L;

		ProtectPlusCertificate certificate = createValidActiveCertificate(10, 1087);
		certificate.setDeviceModelChangeUsed(true);
		ProtectPlusDeviceModelChangeRequest request = new ProtectPlusDeviceModelChangeRequest();
		request.setDeviceModelId(1090);

		Employee loggedInEmployee = new Employee();
		loggedInEmployee.setId(7);
		loggedInEmployee.setStoreId(3);

		when(protectPlusCertificateDao.getProtectPlusCertificate(10)).thenReturn(certificate);
		when(dateService.getCurrentMillisBGTimezone()).thenReturn(currentTimestamp);
		when(deviceModelDao.selectDeviceModel(1090)).thenReturn(new DeviceModel());
		when(employeeService.getLoggedInEmployee()).thenReturn(loggedInEmployee);
		when(employeeService.isLoggedInEmployeeAdmin()).thenReturn(true);

		protectPlusCertificateService.changeDeviceModel(10, request);

		verify(protectPlusCertificateDao).updateDeviceModel(10, 1090, true, 7, currentTimestamp);
		verify(protectPlusCertificateDao).insertProtectPlusCertificateDeviceModelChange(10, 3, 7, 1087, 1090,
				true, currentTimestamp);
	}

	@Test
	public void testChangeDeviceModelAdminOverrideDoesNotConsumeNonAdminChangeRight() {
		long currentTimestamp = 1763654400000L;

		ProtectPlusCertificate certificate = createValidActiveCertificate(10, 1087);
		certificate.setDeviceModelChangeUsed(false);
		ProtectPlusDeviceModelChangeRequest request = new ProtectPlusDeviceModelChangeRequest();
		request.setDeviceModelId(1090);

		Employee loggedInEmployee = new Employee();
		loggedInEmployee.setId(7);
		loggedInEmployee.setStoreId(3);

		when(protectPlusCertificateDao.getProtectPlusCertificate(10)).thenReturn(certificate);
		when(dateService.getCurrentMillisBGTimezone()).thenReturn(currentTimestamp);
		when(deviceModelDao.selectDeviceModel(1090)).thenReturn(new DeviceModel());
		when(employeeService.getLoggedInEmployee()).thenReturn(loggedInEmployee);
		when(employeeService.isLoggedInEmployeeAdmin()).thenReturn(true);

		protectPlusCertificateService.changeDeviceModel(10, request);

		verify(protectPlusCertificateDao).updateDeviceModel(10, 1090, false, 7, currentTimestamp);
		verify(protectPlusCertificateDao).insertProtectPlusCertificateDeviceModelChange(10, 3, 7, 1087, 1090,
				true, currentTimestamp);
	}

	@Test(expected = IllegalStateTransferException.class)
	public void testValidateActiveCertificateRejectsInactiveCertificate() {
		ProtectPlusCertificate certificate = new ProtectPlusCertificate();
		certificate.setId(1);
		certificate.setStatus(ProtectPlusCertificateStatus.INACTIVE);
		certificate.setValidFromTimestamp(1000L);
		certificate.setValidUntilTimestamp(2000L);

		when(protectPlusCertificateDao.getProtectPlusCertificate(1)).thenReturn(certificate);
		when(dateService.getCurrentMillisBGTimezone()).thenReturn(1500L);

		protectPlusCertificateService.validateActiveCertificate(1);
	}

	@Test
	public void testValidateActiveCertificateAllowsExpiredCertificate() {
		ProtectPlusCertificate certificate = new ProtectPlusCertificate();
		certificate.setId(1);
		certificate.setStatus(ProtectPlusCertificateStatus.EXPIRED);
		certificate.setValidFromTimestamp(1000L);
		certificate.setValidUntilTimestamp(2000L);

		when(protectPlusCertificateDao.getProtectPlusCertificate(1)).thenReturn(certificate);
		when(dateService.getCurrentMillisBGTimezone()).thenReturn(3000L);

		ProtectPlusCertificate result = protectPlusCertificateService.validateActiveCertificate(1);

		assertEquals(certificate, result);
	}

	@Test
	public void testActivateCertificateUsesMatchingLoyalCustomerByNameWhenContactDataMatchesMultipleCustomers() {
		long currentTimestamp = 1763654400000L;
		long validUntilTimestamp = 1795190400000L;

		Employee loggedInEmployee = new Employee();
		loggedInEmployee.setId(7);

		ProtectPlusCertificate certificate = new ProtectPlusCertificate();
		certificate.setId(10);
		certificate.setStatus(ProtectPlusCertificateStatus.INACTIVE);
		certificate.setDeviceModelId(1087);

		LoyalCustomer differentCustomer = new LoyalCustomer();
		differentCustomer.setId(21);
		differentCustomer.setName("Петър Петров");

		LoyalCustomer matchingCustomer = new LoyalCustomer();
		matchingCustomer.setId(22);
		matchingCustomer.setName("Иван   Иванов");

		LoyalCustomer requestCustomer = new LoyalCustomer();
		requestCustomer.setName("иван иванов");
		requestCustomer.setPhoneNumber("0894316055");
		requestCustomer.setEmail("ivan@example.com");

		ProtectPlusCertificateRequest request = new ProtectPlusCertificateRequest();
		request.setLoyalCustomer(requestCustomer);
		request.setDeviceModelId(1087);

		when(gdprConsentImage.isEmpty()).thenReturn(false);
		when(protectPlusCertificateDao.getProtectPlusCertificate(10)).thenReturn(certificate);
		when(employeeService.getLoggedInEmployee()).thenReturn(loggedInEmployee);
		when(dateService.getCurrentMillisBGTimezone()).thenReturn(currentTimestamp);
		when(dateService.addMonthsInMillisBGTimezone(currentTimestamp, 12)).thenReturn(validUntilTimestamp);
		when(imageUploadService.uploadProtectPlusGdprConsentImage(gdprConsentImage)).thenReturn("gdpr-file-id");
		when(loyalCustomerDao.getLoyalCustomersByPhoneNumberOrEmail("0894316055", "ivan@example.com"))
				.thenReturn(Arrays.asList(differentCustomer, matchingCustomer));

		protectPlusCertificateService.activateProtectPlusCertificate(10, request, gdprConsentImage);

		ArgumentCaptor<ProtectPlusCertificate> certificateCaptor = ArgumentCaptor.forClass(ProtectPlusCertificate.class);
		verify(protectPlusCertificateDao).activateProtectPlusCertificate(certificateCaptor.capture());
		verify(loyalCustomerDao, never()).insertLoyalCustomer(any(LoyalCustomer.class));

		ProtectPlusCertificate activatedCertificate = certificateCaptor.getValue();
		assertEquals(Integer.valueOf(22), activatedCertificate.getLoyalCustomerId());
		assertEquals(ProtectPlusCertificateStatus.ACTIVE, activatedCertificate.getStatus());
		assertEquals(Integer.valueOf(1087), activatedCertificate.getDeviceModelId());
		assertEquals("gdpr-file-id", activatedCertificate.getGdprConsentFileId());
	}

	@Test(expected = IllegalStateTransferException.class)
	public void testActivateCertificateRejectsChangedDeviceModel() {
		ProtectPlusCertificate certificate = new ProtectPlusCertificate();
		certificate.setId(10);
		certificate.setStatus(ProtectPlusCertificateStatus.INACTIVE);
		certificate.setDeviceModelId(1087);

		LoyalCustomer requestCustomer = new LoyalCustomer();
		requestCustomer.setName("Иван Иванов");
		requestCustomer.setPhoneNumber("0894316055");
		requestCustomer.setEmail("ivan@example.com");

		ProtectPlusCertificateRequest request = new ProtectPlusCertificateRequest();
		request.setLoyalCustomer(requestCustomer);
		request.setDeviceModelId(1090);

		when(gdprConsentImage.isEmpty()).thenReturn(false);
		when(protectPlusCertificateDao.getProtectPlusCertificate(10)).thenReturn(certificate);

		protectPlusCertificateService.activateProtectPlusCertificate(10, request, gdprConsentImage);
	}

	@Test
	public void testUpdateCustomerEmailUpdatesLoyalCustomerWhenEmployeeIsAdmin() {
		long currentTimestamp = 1763654400000L;
		Employee loggedInEmployee = new Employee();
		loggedInEmployee.setId(7);

		ProtectPlusCertificate certificate = createValidActiveCertificate(10, 1087);
		certificate.setLoyalCustomerId(22);

		ProtectPlusCustomerEmailUpdateRequest request = new ProtectPlusCustomerEmailUpdateRequest();
		request.setEmail("corrected@example.com");

		ProtectPlusCertificate updatedCertificate = createValidActiveCertificate(10, 1087);
		updatedCertificate.setLoyalCustomerId(22);
		updatedCertificate.setLoyalCustomerEmail("corrected@example.com");

		when(employeeService.isLoggedInEmployeeAdmin()).thenReturn(true);
		when(protectPlusCertificateDao.getProtectPlusCertificate(10)).thenReturn(certificate, updatedCertificate);
		when(employeeService.getLoggedInEmployee()).thenReturn(loggedInEmployee);
		when(dateService.getCurrentMillisBGTimezone()).thenReturn(currentTimestamp);

		ProtectPlusCertificate result = protectPlusCertificateService.updateCustomerEmail(10, request);

		verify(loyalCustomerDao).updateLoyalCustomerEmail(22, "corrected@example.com", 7, currentTimestamp);
		assertEquals("corrected@example.com", certificate.getLoyalCustomerEmail());
		assertEquals("corrected@example.com", result.getLoyalCustomerEmail());
	}

	@Test(expected = IllegalStateTransferException.class)
	public void testUpdateCustomerEmailRejectsNonAdminEmployee() {
		ProtectPlusCustomerEmailUpdateRequest request = new ProtectPlusCustomerEmailUpdateRequest();
		request.setEmail("corrected@example.com");

		when(employeeService.isLoggedInEmployeeAdmin()).thenReturn(false);

		protectPlusCertificateService.updateCustomerEmail(10, request);
	}

	private ProtectPlusCertificate createValidActiveCertificate(Integer certificateId, Integer deviceModelId) {
		ProtectPlusCertificate certificate = new ProtectPlusCertificate();
		certificate.setId(certificateId);
		certificate.setStatus(ProtectPlusCertificateStatus.ACTIVE);
		certificate.setDeviceModelId(deviceModelId);
		certificate.setValidFromTimestamp(1000L);
		certificate.setValidUntilTimestamp(9999999999999L);

		return certificate;
	}
}
