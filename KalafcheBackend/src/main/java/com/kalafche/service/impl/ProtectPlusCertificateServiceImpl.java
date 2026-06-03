package com.kalafche.service.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.kalafche.dao.LoyalCustomerDao;
import com.kalafche.dao.ProtectPlusCertificateDao;
import com.kalafche.dao.DeviceModelDao;
import com.kalafche.exceptions.DomainObjectNotFoundException;
import com.kalafche.exceptions.IllegalStateTransferException;
import com.kalafche.model.LoyalCustomer;
import com.kalafche.model.device.DeviceModel;
import com.kalafche.model.employee.Employee;
import com.kalafche.model.protectplus.ProtectPlusCallRecord;
import com.kalafche.model.protectplus.ProtectPlusCallRecordDownload;
import com.kalafche.model.protectplus.ProtectPlusCertificate;
import com.kalafche.model.protectplus.ProtectPlusCustomerEmailUpdateRequest;
import com.kalafche.model.protectplus.ProtectPlusDeviceModelChangeRecord;
import com.kalafche.model.protectplus.ProtectPlusDeviceModelChangeRequest;
import com.kalafche.model.protectplus.ProtectPlusCertificateRequest;
import com.kalafche.model.protectplus.ProtectPlusCertificateSearchResult;
import com.kalafche.model.protectplus.ProtectPlusCertificateStatus;
import com.kalafche.model.protectplus.ProtectPlusRenewalRecord;
import com.kalafche.model.protectplus.ProtectPlusUsageRecord;
import com.kalafche.service.DateService;
import com.kalafche.service.EmailService;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.EntityService;
import com.kalafche.service.ProtectPlusCertificateService;
import com.kalafche.service.fileutil.ImageUploadService;

@Service
public class ProtectPlusCertificateServiceImpl implements ProtectPlusCertificateService {

	private static final int INITIAL_VALIDITY_MONTHS = 12;
	private static final int USAGE_EXTENSION_MONTHS = 6;
	private static final String RENEWAL_SOURCE_SALE_USAGE = "SALE_USAGE";

	@Autowired
	ProtectPlusCertificateDao protectPlusCertificateDao;

	@Autowired
	DeviceModelDao deviceModelDao;

	@Autowired
	LoyalCustomerDao loyalCustomerDao;

	@Autowired
	DateService dateService;

	@Autowired
	EmployeeService employeeService;

	@Autowired
	EntityService entityService;

	@Autowired
	ImageUploadService imageUploadService;

	@Autowired
	EmailService emailService;

	@Override
	public void createPendingCertificateForSale(Integer saleId, Integer storeId, Integer employeeId, Integer deviceModelId) {
		long currentTimestamp = dateService.getCurrentMillisBGTimezone();

		ProtectPlusCertificate certificate = new ProtectPlusCertificate();
		certificate.setCertificateNumber(generateCertificateNumber());
		certificate.setDeviceModelId(deviceModelId);
		certificate.setSoldStoreId(storeId);
		certificate.setSoldByEmployeeId(employeeId);
		certificate.setSoldSaleId(saleId);
		certificate.setStatus(ProtectPlusCertificateStatus.INACTIVE);
		certificate.setFreeProtectorUsed(false);
		certificate.setDeviceModelChangeUsed(false);
		certificate.setCreatedById(employeeId);
		certificate.setCreatedTimestamp(currentTimestamp);

		protectPlusCertificateDao.insertProtectPlusCertificate(certificate);
	}

	@Scheduled(cron = "0 5 0 * * *", zone = "Europe/Sofia")
	public void expireExpiredProtectPlusCertificates() {
		long currentTimestamp = dateService.getCurrentMillisBGTimezone();
		protectPlusCertificateDao.expireActiveProtectPlusCertificates(currentTimestamp);
	}

	@Override
	public ProtectPlusCertificate activateProtectPlusCertificate(Integer certificateId, ProtectPlusCertificateRequest request,
			MultipartFile gdprConsentImage) {
		validateActivationRequest(request, gdprConsentImage);

		ProtectPlusCertificate certificate = getProtectPlusCertificate(certificateId);
		if (ProtectPlusCertificateStatus.CANCELLED.equals(certificate.getStatus())) {
			throw new IllegalStateTransferException("status", "Cancelled Protect+ certificate can not be activated.");
		}
		if (!request.getDeviceModelId().equals(certificate.getDeviceModelId())) {
			throw new IllegalStateTransferException("deviceModelId",
					"Protect+ certificate can be activated only for the device model selected during sale.");
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
		ProtectPlusCertificate activatedCertificate = getProtectPlusCertificate(certificateId);
		emailService.sendProtectPlusActivationEmail(activatedCertificate);

		return activatedCertificate;
	}

	@Override
	public ProtectPlusCertificate updateCustomerEmail(Integer certificateId, ProtectPlusCustomerEmailUpdateRequest request) {
		validateCustomerEmailUpdateRequest(request);
		if (!Boolean.TRUE.equals(employeeService.isLoggedInEmployeeAdmin())) {
			throw new IllegalStateTransferException("employee", "Only admin can update Protect+ customer email.");
		}

		ProtectPlusCertificate certificate = validateActiveCertificate(certificateId);
		Employee loggedInEmployee = employeeService.getLoggedInEmployee();
		long currentTimestamp = dateService.getCurrentMillisBGTimezone();
		updateLoyalCustomerEmail(certificate, request.getEmail(), loggedInEmployee.getId(), currentTimestamp);

		return getProtectPlusCertificate(certificateId);
	}

	private void validateCustomerEmailUpdateRequest(ProtectPlusCustomerEmailUpdateRequest request) {
		if (request == null || StringUtils.isEmpty(request.getEmail())) {
			throw new IllegalArgumentException("Customer email is required.");
		}
	}

	private void updateLoyalCustomerEmail(ProtectPlusCertificate certificate, String email, Integer updatedById,
			Long currentTimestamp) {
		if (certificate.getLoyalCustomerId() == null) {
			throw new IllegalStateTransferException("loyalCustomerId", "Protect+ certificate has no loyal customer.");
		}

		loyalCustomerDao.updateLoyalCustomerEmail(certificate.getLoyalCustomerId(), email, updatedById, currentTimestamp);
		certificate.setLoyalCustomerEmail(email);
	}

	@Override
	public void registerCertificateUsage(ProtectPlusCertificate certificate, boolean freeProtectorUsedInSale,
			boolean freeDisplayReplacementServiceUsedInSale, boolean freeBatteryReplacementServiceUsedInSale,
			Integer saleId, Integer storeId, Integer employeeId) {
		long currentTimestamp = dateService.getCurrentMillisBGTimezone();
		long extendedUntilTimestamp = dateService.endOfDayInMillisBGTimezone(
				dateService.addMonthsInMillisBGTimezone(currentTimestamp, USAGE_EXTENSION_MONTHS));
		Long oldValidUntilTimestamp = certificate.getValidUntilTimestamp();
		Long newValidUntilTimestamp = oldValidUntilTimestamp;
		boolean certificateRenewed = oldValidUntilTimestamp == null || oldValidUntilTimestamp < extendedUntilTimestamp;
		if (certificateRenewed) {
			newValidUntilTimestamp = extendedUntilTimestamp;
		}

		Boolean freeProtectorUsed = Boolean.TRUE.equals(certificate.getFreeProtectorUsed()) || freeProtectorUsedInSale;
		Boolean freeDisplayReplacementServiceUsed =
				Boolean.TRUE.equals(certificate.getFreeDisplayReplacementServiceUsed())
						|| freeDisplayReplacementServiceUsedInSale;
		Boolean freeBatteryReplacementServiceUsed =
				Boolean.TRUE.equals(certificate.getFreeBatteryReplacementServiceUsed())
						|| freeBatteryReplacementServiceUsedInSale;
		protectPlusCertificateDao.updateProtectPlusCertificateUsage(certificate.getId(), freeProtectorUsed,
				freeDisplayReplacementServiceUsed, freeBatteryReplacementServiceUsed, newValidUntilTimestamp,
				ProtectPlusCertificateStatus.ACTIVE, employeeId, currentTimestamp);
		if (certificateRenewed) {
			protectPlusCertificateDao.insertProtectPlusCertificateRenewal(certificate.getId(), saleId, storeId,
					employeeId, oldValidUntilTimestamp, newValidUntilTimestamp, RENEWAL_SOURCE_SALE_USAGE,
					currentTimestamp);
		}
	}

	@Override
	public ProtectPlusCallRecord uploadCallRecording(Integer certificateId, MultipartFile callRecording, String note) {
		validateCallRecording(callRecording);
		validateActiveCertificate(certificateId);
		Employee loggedInEmployee = employeeService.getLoggedInEmployee();
		long currentTimestamp = dateService.getCurrentMillisBGTimezone();
		String fileId = imageUploadService.uploadProtectPlusCallRecording(callRecording);

		ProtectPlusCallRecord callRecord = new ProtectPlusCallRecord();
		callRecord.setProtectPlusCertificateId(certificateId);
		callRecord.setStoreId(loggedInEmployee.getStoreId());
		callRecord.setEmployeeId(loggedInEmployee.getId());
		callRecord.setCallRecordingFileId(fileId);
		callRecord.setCallRecordingFileName(callRecording.getOriginalFilename());
		callRecord.setNote(note);
		callRecord.setCreatedTimestamp(currentTimestamp);

		Integer callRecordId = protectPlusCertificateDao.insertProtectPlusCallRecord(callRecord);
		callRecord.setId(callRecordId);
		callRecord.setEmployeeName(loggedInEmployee.getName());
		return callRecord;
	}

	@Override
	public List<ProtectPlusCallRecord> getCallRecords(Integer certificateId) {
		getProtectPlusCertificate(certificateId);
		return protectPlusCertificateDao.getProtectPlusCallRecords(certificateId);
	}

	@Override
	public ProtectPlusCallRecordDownload downloadCallRecording(Integer certificateId, Integer callRecordId) {
		getProtectPlusCertificate(certificateId);
		ProtectPlusCallRecord callRecord = protectPlusCertificateDao.getProtectPlusCallRecord(callRecordId);
		if (callRecord == null || !certificateId.equals(callRecord.getProtectPlusCertificateId())) {
			throw new DomainObjectNotFoundException("callRecordId", "Non-existing Protect+ call record.");
		}

		ProtectPlusCallRecordDownload download = new ProtectPlusCallRecordDownload();
		download.setFileName(callRecord.getCallRecordingFileName());
		download.setContents(imageUploadService.downloadFile(callRecord.getCallRecordingFileId()));
		return download;
	}

	@Override
	public ProtectPlusCallRecordDownload downloadGdprConsent(Integer certificateId) {
		ProtectPlusCertificate certificate = getProtectPlusCertificate(certificateId);
		if (StringUtils.isEmpty(certificate.getGdprConsentFileId())) {
			throw new DomainObjectNotFoundException("gdprConsentFileId", "Protect+ GDPR consent file does not exist.");
		}

		ProtectPlusCallRecordDownload download = new ProtectPlusCallRecordDownload();
		download.setFileName("protect-plus-gdpr-" + certificate.getCertificateNumber());
		download.setContents(imageUploadService.downloadFile(certificate.getGdprConsentFileId()));
		return download;
	}

	@Override
	public List<ProtectPlusUsageRecord> getUsageRecords(Integer certificateId) {
		getProtectPlusCertificate(certificateId);
		return protectPlusCertificateDao.getProtectPlusUsageRecords(certificateId);
	}

	@Override
	public List<ProtectPlusRenewalRecord> getRenewalRecords(Integer certificateId) {
		getProtectPlusCertificate(certificateId);
		return protectPlusCertificateDao.getProtectPlusRenewalRecords(certificateId);
	}

	@Override
	public List<ProtectPlusDeviceModelChangeRecord> getDeviceModelChangeRecords(Integer certificateId) {
		getProtectPlusCertificate(certificateId);
		return protectPlusCertificateDao.getProtectPlusDeviceModelChangeRecords(certificateId);
	}

	@Override
	public ProtectPlusCertificate changeDeviceModel(Integer certificateId, ProtectPlusDeviceModelChangeRequest request) {
		validateDeviceModelChangeRequest(request);
		ProtectPlusCertificate certificate = validateActiveCertificate(certificateId);
		if (certificate.getDeviceModelId() != null && certificate.getDeviceModelId().equals(request.getDeviceModelId())) {
			throw new IllegalArgumentException("New device model should be different from the current one.");
		}

		DeviceModel deviceModel = deviceModelDao.selectDeviceModel(request.getDeviceModelId());
		if (deviceModel == null) {
			throw new DomainObjectNotFoundException("deviceModelId", "Non-existing device model.");
		}

		Employee loggedInEmployee = employeeService.getLoggedInEmployee();
		boolean adminOverride = Boolean.TRUE.equals(employeeService.isLoggedInEmployeeAdmin());
		if (!adminOverride && Boolean.TRUE.equals(certificate.getDeviceModelChangeUsed())) {
			throw new IllegalStateTransferException("deviceModelId",
					"Protect+ device model can be changed only once.");
		}

		long currentTimestamp = dateService.getCurrentMillisBGTimezone();
		boolean deviceModelChangeUsed = Boolean.TRUE.equals(certificate.getDeviceModelChangeUsed()) || !adminOverride;
		protectPlusCertificateDao.updateDeviceModel(certificateId, request.getDeviceModelId(), deviceModelChangeUsed,
				loggedInEmployee.getId(), currentTimestamp);
		protectPlusCertificateDao.insertProtectPlusCertificateDeviceModelChange(certificateId,
				loggedInEmployee.getStoreId(), loggedInEmployee.getId(), certificate.getDeviceModelId(),
				request.getDeviceModelId(), adminOverride, currentTimestamp);

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

		String storeIds = resolveVisibleStoreIds(storeId);
		return protectPlusCertificateDao.searchProtectPlusCertificates(null, null,
				ProtectPlusCertificateStatus.INACTIVE, storeIds, null, null, null);
	}

	@Override
	public List<ProtectPlusCertificateSearchResult> searchActiveProtectPlusCertificates(Integer certificateNumber,
				String phoneNumber, Integer storeId, Integer deviceBrandId, Integer deviceModelId) {
		boolean admin = Boolean.TRUE.equals(employeeService.isLoggedInEmployeeAdmin());
		if (!admin && certificateNumber == null && StringUtils.isEmpty(phoneNumber)) {
			throw new IllegalArgumentException("Certificate number or phone number is required.");
		}

		Integer limit = admin ? null : 1;
		String storeIds = isDirectCertificateLookup(certificateNumber, phoneNumber) ? null : resolveVisibleStoreIds(storeId);
		Integer resolvedDeviceBrandId = admin ? deviceBrandId : null;
		Integer resolvedDeviceModelId = admin ? deviceModelId : null;

		List<ProtectPlusCertificateSearchResult> certificates = protectPlusCertificateDao.searchProtectPlusCertificates(
				certificateNumber, phoneNumber, ProtectPlusCertificateStatus.ACTIVE, storeIds, resolvedDeviceBrandId,
				resolvedDeviceModelId, limit);
		return hidePersonalDataForNonAdmin(certificates);
	}

	@Override
	public List<ProtectPlusCertificateSearchResult> searchActiveProtectPlusCertificatesByQuery(String query) {
		if (StringUtils.isEmpty(query) || query.length() < 5) {
			throw new IllegalArgumentException("Protect+ search query should be at least 5 symbols.");
		}

		if (query.matches("\\d+")) {
			try {
				List<ProtectPlusCertificateSearchResult> certificatesByNumber = protectPlusCertificateDao.searchProtectPlusCertificates(
							Integer.valueOf(query), null, ProtectPlusCertificateStatus.ACTIVE, null, null, null, null);
				if (!certificatesByNumber.isEmpty()) {
					return hidePersonalDataForNonAdmin(certificatesByNumber);
				}
			} catch (NumberFormatException e) {
				// Continue with phone search when the numeric query does not fit an integer certificate number.
			}
		}

		List<ProtectPlusCertificateSearchResult> certificates = protectPlusCertificateDao.searchProtectPlusCertificates(
				null, query, ProtectPlusCertificateStatus.ACTIVE, null, null, null, null);
		return hidePersonalDataForNonAdmin(certificates);
	}

	private List<ProtectPlusCertificateSearchResult> hidePersonalDataForNonAdmin(
			List<ProtectPlusCertificateSearchResult> certificates) {
		if (Boolean.TRUE.equals(employeeService.isLoggedInEmployeeAdmin())) {
			return certificates;
		}

		for (ProtectPlusCertificateSearchResult certificate : certificates) {
			certificate.setCustomerName(null);
			certificate.setPhoneNumber(null);
			certificate.setLoyalCustomerEmail(null);
		}

		return certificates;
	}

	@Override
	public ProtectPlusCertificate validateActiveCertificate(Integer certificateId) {
		ProtectPlusCertificate certificate = getProtectPlusCertificate(certificateId);
		long currentTimestamp = dateService.getCurrentMillisBGTimezone();

		if (!isUsableForSale(certificate)) {
			throw new IllegalStateTransferException("protectPlusCertificateId",
					"Protect+ certificate is not usable for sale.");
		}
		if (certificate.getValidFromTimestamp() != null && currentTimestamp < certificate.getValidFromTimestamp()) {
			throw new IllegalStateTransferException("protectPlusCertificateId",
					"Protect+ certificate is not valid for the current date.");
		}

		return certificate;
	}

	private boolean isUsableForSale(ProtectPlusCertificate certificate) {
		return ProtectPlusCertificateStatus.ACTIVE.equals(certificate.getStatus())
				|| ProtectPlusCertificateStatus.EXPIRED.equals(certificate.getStatus());
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
		LoyalCustomer existingLoyalCustomer = findMatchingLoyalCustomer(loyalCustomer);
		if (existingLoyalCustomer != null) {
			return existingLoyalCustomer.getId();
		}

		loyalCustomer.setCreatedById(loggedInEmployee.getId());
		loyalCustomer.setCreatedTimestamp(currentTimestamp);
		return loyalCustomerDao.insertLoyalCustomer(loyalCustomer);
	}

	private LoyalCustomer findMatchingLoyalCustomer(LoyalCustomer newLoyalCustomer) {
		List<LoyalCustomer> candidates = loyalCustomerDao.getLoyalCustomersByPhoneNumberOrEmail(
				newLoyalCustomer.getPhoneNumber(), newLoyalCustomer.getEmail());

		for (LoyalCustomer candidate : candidates) {
			if (namesMatch(candidate.getName(), newLoyalCustomer.getName())) {
				return candidate;
			}
		}

		return null;
	}

	private boolean namesMatch(String existingName, String newName) {
		if (StringUtils.isEmpty(existingName) || StringUtils.isEmpty(newName)) {
			return false;
		}

		return normalizeName(existingName).equals(normalizeName(newName));
	}

	private String normalizeName(String name) {
		return name.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
	}

	private String resolveVisibleStoreIds(Integer requestedStoreId) {
		if (Boolean.TRUE.equals(employeeService.isLoggedInEmployeeAdmin())) {
			return requestedStoreId == null ? entityService.getConcatenatedStoreIdsForFiltering("0") : requestedStoreId.toString();
		}

		return entityService.getConcatenatedStoreIdsForFiltering("0");
	}

	private boolean isDirectCertificateLookup(Integer certificateNumber, String phoneNumber) {
		return certificateNumber != null || !StringUtils.isEmpty(phoneNumber);
	}

	private void validateActivationRequest(ProtectPlusCertificateRequest request, MultipartFile gdprConsentImage) {
		if (request == null) {
			throw new IllegalArgumentException("Protect+ certificate request is required.");
		}
		if (gdprConsentImage == null || gdprConsentImage.isEmpty()) {
			throw new IllegalArgumentException("GDPR consent image is required.");
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

	private void validateDeviceModelChangeRequest(ProtectPlusDeviceModelChangeRequest request) {
		if (request == null || request.getDeviceModelId() == null) {
			throw new IllegalArgumentException("Device model is required.");
		}
	}

	private void validateCallRecording(MultipartFile callRecording) {
		if (callRecording == null || callRecording.isEmpty()) {
			throw new IllegalArgumentException("Call recording is required.");
		}
	}

	private Integer generateCertificateNumber() {
		Integer maxCertificateNumber = protectPlusCertificateDao.getMaxCertificateNumber();
		return maxCertificateNumber == null ? 10000 : maxCertificateNumber + 1;
	}
}
