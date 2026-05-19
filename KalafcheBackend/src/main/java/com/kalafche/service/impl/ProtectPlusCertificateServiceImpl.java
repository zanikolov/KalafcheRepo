package com.kalafche.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.kalafche.dao.LoyalCustomerDao;
import com.kalafche.dao.ProtectPlusCertificateDao;
import com.kalafche.exceptions.DomainObjectNotFoundException;
import com.kalafche.exceptions.IllegalStateTransferException;
import com.kalafche.model.LoyalCustomer;
import com.kalafche.model.employee.Employee;
import com.kalafche.model.protectplus.ProtectPlusCertificate;
import com.kalafche.model.protectplus.ProtectPlusCertificateRequest;
import com.kalafche.model.protectplus.ProtectPlusCertificateSearchResult;
import com.kalafche.model.protectplus.ProtectPlusCertificateStatus;
import com.kalafche.service.DateService;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.ProtectPlusCertificateService;
import com.kalafche.service.fileutil.ImageUploadService;

@Service
public class ProtectPlusCertificateServiceImpl implements ProtectPlusCertificateService {

	private static final int INITIAL_VALIDITY_MONTHS = 12;
	private static final int USAGE_EXTENSION_MONTHS = 6;

	@Autowired
	ProtectPlusCertificateDao protectPlusCertificateDao;

	@Autowired
	LoyalCustomerDao loyalCustomerDao;

	@Autowired
	DateService dateService;

	@Autowired
	EmployeeService employeeService;

	@Autowired
	ImageUploadService imageUploadService;

	@Override
	public ProtectPlusCertificate createPendingCertificateForSale(Integer saleId, Integer storeId, Integer employeeId) {
		long currentTimestamp = dateService.getCurrentMillisBGTimezone();

		ProtectPlusCertificate certificate = new ProtectPlusCertificate();
		certificate.setCertificateNumber(generateCertificateNumber());
		certificate.setSoldStoreId(storeId);
		certificate.setSoldByEmployeeId(employeeId);
		certificate.setSoldSaleId(saleId);
		certificate.setStatus(ProtectPlusCertificateStatus.INACTIVE);
		certificate.setFreeProtectorUsed(false);
		certificate.setCreatedById(employeeId);
		certificate.setCreatedTimestamp(currentTimestamp);

		Integer certificateId = protectPlusCertificateDao.insertProtectPlusCertificate(certificate);
		return getProtectPlusCertificate(certificateId);
	}

	@Override
	public ProtectPlusCertificate activateProtectPlusCertificate(Integer certificateId, ProtectPlusCertificateRequest request,
			MultipartFile gdprConsentImage) {
		validateActivationRequest(request);

		ProtectPlusCertificate certificate = getProtectPlusCertificate(certificateId);
		if (ProtectPlusCertificateStatus.CANCELLED.equals(certificate.getStatus())) {
			throw new IllegalStateTransferException("status", "Cancelled Protect+ certificate can not be activated.");
		}

		Employee loggedInEmployee = employeeService.getLoggedInEmployee();
		long currentTimestamp = dateService.getCurrentMillisBGTimezone();
		Integer loyalCustomerId = resolveLoyalCustomerId(request, loggedInEmployee, currentTimestamp);
		String gdprConsentFileId = imageUploadService.uploadProtectPlusGdprConsentImage(gdprConsentImage);

		certificate.setStatus(ProtectPlusCertificateStatus.ACTIVE);
		certificate.setLoyalCustomerId(loyalCustomerId);
		certificate.setDeviceModelId(request.getDeviceModelId());
		certificate.setValidFromTimestamp(currentTimestamp);
		certificate.setValidUntilTimestamp(dateService.addMonthsInMillisBGTimezone(currentTimestamp, INITIAL_VALIDITY_MONTHS));
		certificate.setActivatedById(loggedInEmployee.getId());
		certificate.setActivatedTimestamp(currentTimestamp);
		certificate.setGdprConsentFileId(gdprConsentFileId);
		certificate.setUpdatedById(loggedInEmployee.getId());
		certificate.setLastUpdateTimestamp(currentTimestamp);

		protectPlusCertificateDao.activateProtectPlusCertificate(certificate);
		return getProtectPlusCertificate(certificateId);
	}

	@Override
	public void registerCertificateUsage(ProtectPlusCertificate certificate, boolean freeProtectorUsedInSale) {
		Employee loggedInEmployee = employeeService.getLoggedInEmployee();
		long currentTimestamp = dateService.getCurrentMillisBGTimezone();
		long extendedUntilTimestamp = dateService.addMonthsInMillisBGTimezone(currentTimestamp, USAGE_EXTENSION_MONTHS);
		Long validUntilTimestamp = certificate.getValidUntilTimestamp();
		if (validUntilTimestamp == null || validUntilTimestamp < extendedUntilTimestamp) {
			validUntilTimestamp = extendedUntilTimestamp;
		}

		Boolean freeProtectorUsed = Boolean.TRUE.equals(certificate.getFreeProtectorUsed()) || freeProtectorUsedInSale;
		protectPlusCertificateDao.updateProtectPlusCertificateUsage(certificate.getId(), freeProtectorUsed,
				validUntilTimestamp, loggedInEmployee.getId(), currentTimestamp);
	}

	@Override
	public ProtectPlusCertificate uploadCallRecording(Integer certificateId, MultipartFile callRecording) {
		getProtectPlusCertificate(certificateId);
		Employee loggedInEmployee = employeeService.getLoggedInEmployee();
		long currentTimestamp = dateService.getCurrentMillisBGTimezone();
		String fileId = imageUploadService.uploadProtectPlusCallRecording(callRecording);

		protectPlusCertificateDao.updateCallRecordingFile(certificateId, fileId, loggedInEmployee.getId(),
				currentTimestamp);
		return getProtectPlusCertificate(certificateId);
	}

	@Override
	public ProtectPlusCertificate getProtectPlusCertificate(Integer certificateId) {
		ProtectPlusCertificate certificate = protectPlusCertificateDao.getProtectPlusCertificate(certificateId);
		if (certificate == null) {
			throw new DomainObjectNotFoundException("protectPlusCertificateId", "Non-existing Protect+ certificate.");
		}

		return certificate;
	}

	@Override
	public ProtectPlusCertificate getProtectPlusCertificateByNumber(Integer certificateNumber) {
		ProtectPlusCertificate certificate = protectPlusCertificateDao.getProtectPlusCertificateByNumber(certificateNumber);
		if (certificate == null) {
			throw new DomainObjectNotFoundException("certificateNumber", "Non-existing Protect+ certificate.");
		}

		return certificate;
	}

	@Override
	public List<ProtectPlusCertificateSearchResult> getInactiveProtectPlusCertificatesForCurrentStore() {
		Employee loggedInEmployee = employeeService.getLoggedInEmployee();
		boolean admin = Boolean.TRUE.equals(employeeService.isLoggedInEmployeeAdmin());
		if (!admin && (loggedInEmployee.getStoreId() == null || loggedInEmployee.getStoreId() == 0)) {
			throw new IllegalArgumentException("Employee store is required.");
		}

		Integer storeId = loggedInEmployee.getStoreId();
		if (admin && (storeId == null || storeId == 0)) {
			storeId = null;
		}

		return protectPlusCertificateDao.searchProtectPlusCertificates(null, null,
				ProtectPlusCertificateStatus.INACTIVE, storeId, null, null, null);
	}

	@Override
	public List<ProtectPlusCertificateSearchResult> searchProtectPlusCertificates(Integer certificateNumber,
			String phoneNumber, Integer storeId, Integer deviceBrandId, Integer deviceModelId) {
		boolean admin = Boolean.TRUE.equals(employeeService.isLoggedInEmployeeAdmin());
		if (!admin && certificateNumber == null && StringUtils.isEmpty(phoneNumber)) {
			throw new IllegalArgumentException("Certificate number or phone number is required.");
		}

		Integer limit = admin ? null : 1;
		Integer resolvedStoreId = admin ? storeId : null;
		Integer resolvedDeviceBrandId = admin ? deviceBrandId : null;
		Integer resolvedDeviceModelId = admin ? deviceModelId : null;

		return protectPlusCertificateDao.searchProtectPlusCertificates(certificateNumber, phoneNumber,
				ProtectPlusCertificateStatus.ACTIVE, resolvedStoreId, resolvedDeviceBrandId, resolvedDeviceModelId, limit);
	}

	@Override
	public List<ProtectPlusCertificateSearchResult> searchActiveProtectPlusCertificates(String query) {
		if (StringUtils.isEmpty(query) || query.length() < 5) {
			throw new IllegalArgumentException("Protect+ search query should be at least 5 symbols.");
		}

		if (query.matches("\\d+")) {
			try {
				List<ProtectPlusCertificateSearchResult> certificatesByNumber = protectPlusCertificateDao.searchProtectPlusCertificates(
						Integer.valueOf(query), null, ProtectPlusCertificateStatus.ACTIVE, null, null, null, null);
				if (!certificatesByNumber.isEmpty()) {
					return certificatesByNumber;
				}
			} catch (NumberFormatException e) {
				// Continue with phone search when the numeric query does not fit an integer certificate number.
			}
		}

		return protectPlusCertificateDao.searchProtectPlusCertificates(null, query,
				ProtectPlusCertificateStatus.ACTIVE, null, null, null, null);
	}

	@Override
	public ProtectPlusCertificate validateActiveCertificate(Integer certificateId) {
		ProtectPlusCertificate certificate = getProtectPlusCertificate(certificateId);
		long currentTimestamp = dateService.getCurrentMillisBGTimezone();

		if (!ProtectPlusCertificateStatus.ACTIVE.equals(certificate.getStatus())) {
			throw new IllegalStateTransferException("protectPlusCertificateId", "Protect+ certificate is not active.");
		}
		if (certificate.getValidFromTimestamp() == null || certificate.getValidUntilTimestamp() == null) {
			throw new IllegalStateTransferException("protectPlusCertificateId", "Protect+ certificate has no validity period.");
		}
		if (currentTimestamp < certificate.getValidFromTimestamp() || currentTimestamp > certificate.getValidUntilTimestamp()) {
			throw new IllegalStateTransferException("protectPlusCertificateId", "Protect+ certificate is not valid for the current date.");
		}

		return certificate;
	}

	private Integer resolveLoyalCustomerId(ProtectPlusCertificateRequest request, Employee loggedInEmployee,
			long currentTimestamp) {
		if (request.getLoyalCustomerId() != null) {
			LoyalCustomer loyalCustomer = loyalCustomerDao.getLoyalCustomerById(request.getLoyalCustomerId());
			if (loyalCustomer == null) {
				throw new DomainObjectNotFoundException("loyalCustomerId", "Non-existing loyal customer.");
			}
			if (StringUtils.isEmpty(loyalCustomer.getEmail())) {
				throw new IllegalArgumentException("Customer email is required.");
			}

			return request.getLoyalCustomerId();
		}

		LoyalCustomer loyalCustomer = request.getLoyalCustomer();
		loyalCustomer.setCreatedById(loggedInEmployee.getId());
		loyalCustomer.setCreatedTimestamp(currentTimestamp);
		return loyalCustomerDao.insertLoyalCustomer(loyalCustomer);
	}

	private void validateActivationRequest(ProtectPlusCertificateRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Protect+ certificate request is required.");
		}
		if (request.getDeviceModelId() == null) {
			throw new IllegalArgumentException("Device model is required.");
		}
		if (request.getLoyalCustomerId() == null && request.getLoyalCustomer() == null) {
			throw new IllegalArgumentException("Loyal customer is required.");
		}
		if (request.getLoyalCustomer() != null && StringUtils.isEmpty(request.getLoyalCustomer().getName())) {
			throw new IllegalArgumentException("Customer name is required.");
		}
		if (request.getLoyalCustomer() != null && StringUtils.isEmpty(request.getLoyalCustomer().getPhoneNumber())) {
			throw new IllegalArgumentException("Customer phone number is required.");
		}
		if (request.getLoyalCustomer() != null && StringUtils.isEmpty(request.getLoyalCustomer().getEmail())) {
			throw new IllegalArgumentException("Customer email is required.");
		}
	}

	private Integer generateCertificateNumber() {
		Integer maxCertificateNumber = protectPlusCertificateDao.getMaxCertificateNumber();
		return maxCertificateNumber == null ? 10000 : maxCertificateNumber + 1;
	}
}
