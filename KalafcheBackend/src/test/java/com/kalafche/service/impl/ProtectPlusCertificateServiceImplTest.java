package com.kalafche.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.kalafche.dao.ProtectPlusCertificateDao;
import com.kalafche.exceptions.IllegalStateTransferException;
import com.kalafche.model.protectplus.ProtectPlusCertificate;
import com.kalafche.model.protectplus.ProtectPlusCertificateStatus;
import com.kalafche.service.DateService;
import com.kalafche.service.EmployeeService;

@RunWith(MockitoJUnitRunner.class)
public class ProtectPlusCertificateServiceImplTest {

	@Mock
	private ProtectPlusCertificateDao protectPlusCertificateDao;

	@Mock
	private DateService dateService;

	@Mock
	private EmployeeService employeeService;

	@InjectMocks
	private ProtectPlusCertificateServiceImpl protectPlusCertificateService;

	@Test
	public void testCreatePendingCertificateForSaleCreatesInactiveCertificate() {
		long currentTimestamp = 1763654400000L;

		ProtectPlusCertificate insertedCertificate = new ProtectPlusCertificate();
		insertedCertificate.setId(20);
		insertedCertificate.setCertificateNumber(10006);

		when(dateService.getCurrentMillisBGTimezone()).thenReturn(currentTimestamp);
		when(protectPlusCertificateDao.getMaxCertificateNumber()).thenReturn(10005);
		when(protectPlusCertificateDao.insertProtectPlusCertificate(any(ProtectPlusCertificate.class))).thenReturn(20);
		when(protectPlusCertificateDao.getProtectPlusCertificate(20)).thenReturn(insertedCertificate);

		ProtectPlusCertificate result = protectPlusCertificateService.createPendingCertificateForSale(34, 3, 7);

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
		assertEquals(insertedCertificate, result);
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
}
