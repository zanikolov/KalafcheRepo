package com.kalafche.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;

import com.kalafche.dao.LoyalCustomerDao;
import com.kalafche.dao.ProtectPlusCertificateDao;
import com.kalafche.exceptions.IllegalStateTransferException;
import com.kalafche.model.LoyalCustomer;
import com.kalafche.model.employee.Employee;
import com.kalafche.model.protectplus.ProtectPlusCertificate;
import com.kalafche.model.protectplus.ProtectPlusCertificateRequest;
import com.kalafche.model.protectplus.ProtectPlusCertificateStatus;
import com.kalafche.service.DateService;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.fileutil.ImageUploadService;

@RunWith(MockitoJUnitRunner.class)
public class ProtectPlusCertificateServiceImplTest {

	@Mock
	private ProtectPlusCertificateDao protectPlusCertificateDao;

	@Mock
	private LoyalCustomerDao loyalCustomerDao;

	@Mock
	private DateService dateService;

	@Mock
	private EmployeeService employeeService;

	@Mock
	private ImageUploadService imageUploadService;

	@Mock
	private MultipartFile gdprConsentImage;

	@InjectMocks
	private ProtectPlusCertificateServiceImpl protectPlusCertificateService;

	@Test
	public void testCreatePendingCertificateForSaleCreatesInactiveCertificate() {
		long currentTimestamp = 1763654400000L;

		when(dateService.getCurrentMillisBGTimezone()).thenReturn(currentTimestamp);
		when(protectPlusCertificateDao.getMaxCertificateNumber()).thenReturn(10005);
		when(protectPlusCertificateDao.insertProtectPlusCertificate(any(ProtectPlusCertificate.class))).thenReturn(20);

		protectPlusCertificateService.createPendingCertificateForSale(34, 3, 7);

		ArgumentCaptor<ProtectPlusCertificate> certificateCaptor = ArgumentCaptor.forClass(ProtectPlusCertificate.class);
		verify(protectPlusCertificateDao).insertProtectPlusCertificate(certificateCaptor.capture());
		ProtectPlusCertificate certificate = certificateCaptor.getValue();

		assertEquals(Integer.valueOf(10006), certificate.getCertificateNumber());
		assertEquals(Integer.valueOf(3), certificate.getSoldStoreId());
		assertEquals(Integer.valueOf(7), certificate.getSoldByEmployeeId());
		assertEquals(Integer.valueOf(34), certificate.getSoldSaleId());
		assertEquals(ProtectPlusCertificateStatus.INACTIVE, certificate.getStatus());
		assertEquals(Boolean.FALSE, certificate.getFreeProtectorUsed());
		assertEquals(Long.valueOf(currentTimestamp), certificate.getCreatedTimestamp());
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
	public void testActivateCertificateUsesMatchingLoyalCustomerByNameWhenContactDataMatchesMultipleCustomers() {
		long currentTimestamp = 1763654400000L;
		long validUntilTimestamp = 1795190400000L;

		Employee loggedInEmployee = new Employee();
		loggedInEmployee.setId(7);

		ProtectPlusCertificate certificate = new ProtectPlusCertificate();
		certificate.setId(10);
		certificate.setStatus(ProtectPlusCertificateStatus.INACTIVE);

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
}
