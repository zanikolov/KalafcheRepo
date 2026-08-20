package com.kalafche.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.kalafche.dao.LoyalCustomerDao;
import com.kalafche.dao.ProtectPlusCertificateDao;
import com.kalafche.dao.SaleDao;
import com.kalafche.dao.DeviceModelDao;
import com.kalafche.exceptions.DomainObjectNotFoundException;
import com.kalafche.exceptions.IllegalStateTransferException;
import com.kalafche.model.LoyalCustomer;
import com.kalafche.model.email.EmailSendResult;
import com.kalafche.model.Refund;
import com.kalafche.model.device.DeviceModel;
import com.kalafche.model.employee.Employee;
import com.kalafche.model.protectplus.ProtectPlusActivationEmailResendReport;
import com.kalafche.model.protectplus.ProtectPlusActivationEmailResendRequest;
import com.kalafche.model.protectplus.ProtectPlusActivationEmailResendResult;
import com.kalafche.model.protectplus.ProtectPlusCallRecord;
import com.kalafche.model.protectplus.ProtectPlusCallRecordDownload;
import com.kalafche.model.protectplus.ProtectPlusCertificate;
import com.kalafche.model.protectplus.ProtectPlusCustomerEmailUpdateRequest;
import com.kalafche.model.protectplus.ProtectPlusCustomerNameUpdateRequest;
import com.kalafche.model.protectplus.ProtectPlusCustomerPhoneUpdateRequest;
import com.kalafche.model.protectplus.ProtectPlusDeviceModelChangeRecord;
import com.kalafche.model.protectplus.ProtectPlusDeviceModelChangeRequest;
import com.kalafche.model.protectplus.ProtectPlusCertificateRequest;
import com.kalafche.model.protectplus.ProtectPlusCertificateSearchFilter;
import com.kalafche.model.protectplus.ProtectPlusCertificateSearchResult;
import com.kalafche.model.protectplus.ProtectPlusCertificateStatus;
import com.kalafche.model.protectplus.ProtectPlusRenewalRecord;
import com.kalafche.model.protectplus.ProtectPlusUsageRecord;
import com.kalafche.service.DateService;
import com.kalafche.service.EmailService;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.EntityService;
import com.kalafche.service.ProtectPlusCertificateService;
import com.kalafche.service.RefundService;
import com.kalafche.service.fileutil.ImageUploadService;

@Service
public class ProtectPlusCertificateServiceImpl implements ProtectPlusCertificateService {

	private static final int INITIAL_VALIDITY_MONTHS = 12;
	private static final int USAGE_EXTENSION_MONTHS = 6;
	private static final String RENEWAL_SOURCE_SALE_USAGE = "SALE_USAGE";
	private static final String PROTECT_PLUS_PRODUCT_CODE = "0500";
	private static final int DEFAULT_ACTIVATION_EMAIL_RESEND_DELAY_MILLIS = 1500;
	private static final int MAX_ACTIVATION_EMAIL_RESEND_DELAY_MILLIS = 60000;

	@Autowired
	ProtectPlusCertificateDao protectPlusCertificateDao;

	@Autowired
	DeviceModelDao deviceModelDao;

	@Autowired
	LoyalCustomerDao loyalCustomerDao;

	@Autowired
	SaleDao saleDao;

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

	@Autowired
	RefundService refundService;

	@Override
	public void createPendingCertificateForSale(Integer saleId, Integer saleItemId, Integer storeId, Integer employeeId,
			Integer deviceModelId) {
		long currentTimestamp = dateService.getCurrentMillisBGTimezone();

		ProtectPlusCertificate certificate = new ProtectPlusCertificate();
		certificate.setCertificateNumber(generateCertificateNumber());
		certificate.setDeviceModelId(deviceModelId);
		certificate.setSoldStoreId(storeId);
		certificate.setSoldByEmployeeId(employeeId);
		certificate.setSoldSaleId(saleId);
		certificate.setSoldSaleItemId(saleItemId);
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
		ProtectPlusCertificate certificate = getProtectPlusCertificate(certificateId);
		validateActivationRequest(request, certificate, gdprConsentImage);
		if (ProtectPlusCertificateStatus.CANCELLED.equals(certificate.getStatus())) {
			throw new IllegalStateTransferException("status", "Cancelled Protect+ certificate can not be activated.");
		}
		if (!request.getDeviceModelId().equals(certificate.getDeviceModelId())) {
			throw new IllegalStateTransferException("deviceModelId",
					"Protect+ certificate can be activated only for the device model selected during sale.");
		}
		if (Boolean.TRUE.equals(deviceModelDao.isUnknownDeviceModel(request.getDeviceModelId()))) {
			throw new IllegalStateTransferException("deviceModelId",
					"Protect+ certificate can not be activated with Unknown device model.");
		}

		Employee loggedInEmployee = employeeService.getLoggedInEmployee();
		long currentTimestamp = dateService.getCurrentMillisBGTimezone();
		Integer loyalCustomerId = resolveLoyalCustomerId(request, loggedInEmployee, currentTimestamp);
		String gdprConsentFileId = resolveGdprConsentFileId(certificate, gdprConsentImage);

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

	@Override
	public ProtectPlusCertificate updateCustomerName(Integer certificateId, ProtectPlusCustomerNameUpdateRequest request) {
		validateCustomerNameUpdateRequest(request);
		if (!Boolean.TRUE.equals(employeeService.isLoggedInEmployeeAdmin())) {
			throw new IllegalStateTransferException("employee", "Only admin can update Protect+ customer name.");
		}

		ProtectPlusCertificate certificate = validateActiveCertificate(certificateId);
		Employee loggedInEmployee = employeeService.getLoggedInEmployee();
		long currentTimestamp = dateService.getCurrentMillisBGTimezone();
		updateLoyalCustomerName(certificate, request.getName(), loggedInEmployee.getId(), currentTimestamp);

		return getProtectPlusCertificate(certificateId);
	}

	@Override
	public ProtectPlusCertificate updateCustomerPhone(Integer certificateId, ProtectPlusCustomerPhoneUpdateRequest request) {
		validateCustomerPhoneUpdateRequest(request);
		if (!Boolean.TRUE.equals(employeeService.isLoggedInEmployeeAdmin())) {
			throw new IllegalStateTransferException("employee", "Only admin can update Protect+ customer phone.");
		}

		ProtectPlusCertificate certificate = validateActiveCertificate(certificateId);
		Employee loggedInEmployee = employeeService.getLoggedInEmployee();
		long currentTimestamp = dateService.getCurrentMillisBGTimezone();
		updateLoyalCustomerPhoneNumber(certificate, request.getPhoneNumber(), loggedInEmployee.getId(),
				currentTimestamp);

		return getProtectPlusCertificate(certificateId);
	}

	private void validateCustomerEmailUpdateRequest(ProtectPlusCustomerEmailUpdateRequest request) {
		if (request == null || StringUtils.isEmpty(request.getEmail())) {
			throw new IllegalArgumentException("Customer email is required.");
		}
	}

	private void validateCustomerNameUpdateRequest(ProtectPlusCustomerNameUpdateRequest request) {
		if (request == null || StringUtils.isEmpty(request.getName())) {
			throw new IllegalArgumentException("Customer name is required.");
		}
	}

	private void validateCustomerPhoneUpdateRequest(ProtectPlusCustomerPhoneUpdateRequest request) {
		if (request == null || StringUtils.isEmpty(request.getPhoneNumber())) {
			throw new IllegalArgumentException("Customer phone is required.");
		}
	}

	private void updateLoyalCustomerName(ProtectPlusCertificate certificate, String name, Integer updatedById,
			Long currentTimestamp) {
		if (certificate.getLoyalCustomerId() == null) {
			throw new IllegalStateTransferException("loyalCustomerId", "Protect+ certificate has no loyal customer.");
		}

		loyalCustomerDao.updateLoyalCustomerName(certificate.getLoyalCustomerId(), name, updatedById, currentTimestamp);
		certificate.setLoyalCustomerName(name);
	}

	private void updateLoyalCustomerEmail(ProtectPlusCertificate certificate, String email, Integer updatedById,
			Long currentTimestamp) {
		if (certificate.getLoyalCustomerId() == null) {
			throw new IllegalStateTransferException("loyalCustomerId", "Protect+ certificate has no loyal customer.");
		}

		loyalCustomerDao.updateLoyalCustomerEmail(certificate.getLoyalCustomerId(), email, updatedById, currentTimestamp);
		certificate.setLoyalCustomerEmail(email);
	}

	private void updateLoyalCustomerPhoneNumber(ProtectPlusCertificate certificate, String phoneNumber, Integer updatedById,
			Long currentTimestamp) {
		if (certificate.getLoyalCustomerId() == null) {
			throw new IllegalStateTransferException("loyalCustomerId", "Protect+ certificate has no loyal customer.");
		}

		loyalCustomerDao.updateLoyalCustomerPhoneNumber(certificate.getLoyalCustomerId(), phoneNumber, updatedById,
				currentTimestamp);
		certificate.setLoyalCustomerPhoneNumber(phoneNumber);
	}

	@Transactional
	@Override
	public ProtectPlusCertificate cancelProtectPlusCertificate(Integer certificateId) {
		if (!Boolean.TRUE.equals(employeeService.isLoggedInEmployeeAdmin())) {
			throw new IllegalStateTransferException("employee", "Only admin can cancel Protect+ certificate.");
		}

		ProtectPlusCertificate certificate = getProtectPlusCertificate(certificateId);
		if (ProtectPlusCertificateStatus.CANCELLED.equals(certificate.getStatus())) {
			throw new IllegalStateTransferException("status", "Protect+ certificate is already cancelled.");
		}

		Employee loggedInEmployee = employeeService.getLoggedInEmployee();
		long currentTimestamp = dateService.getCurrentMillisBGTimezone();
		if (!isCertificateUsed(certificate)) {
			refundCertificatePurchase(certificate);
		}

		cancelCertificate(certificate, loggedInEmployee.getId(), currentTimestamp);

		return getProtectPlusCertificate(certificateId);
	}

	@Override
	public ProtectPlusActivationEmailResendReport resendActivationEmails(
			ProtectPlusActivationEmailResendRequest request) {
		if (!Boolean.TRUE.equals(employeeService.isLoggedInEmployeeAdmin())) {
			throw new IllegalStateTransferException("employee", "Only admin can resend Protect+ activation emails.");
		}
		validateActivationEmailResendRequest(request);

		boolean dryRun = Boolean.TRUE.equals(request.getDryRun());
		Integer delayBetweenEmailsMillis = resolveActivationEmailResendDelay(request.getDelayBetweenEmailsMillis());
		Integer certificateNumberFrom = Math.min(request.getCertificateNumberFrom(), request.getCertificateNumberTo());
		Integer certificateNumberTo = Math.max(request.getCertificateNumberFrom(), request.getCertificateNumberTo());
		List<ProtectPlusCertificate> certificates = protectPlusCertificateDao.getProtectPlusCertificatesByCertificateNumberRange(
				certificateNumberFrom, certificateNumberTo);
		List<ProtectPlusActivationEmailResendResult> results = new ArrayList<ProtectPlusActivationEmailResendResult>();

		for (int i = 0; i < certificates.size(); i++) {
			ProtectPlusCertificate certificate = certificates.get(i);
			ProtectPlusActivationEmailResendResult result = createActivationEmailResendResult(certificate);
			if (dryRun) {
				result.setSkipped(true);
				result.setMessage("Dry run.");
			} else {
				EmailSendResult sendResult = emailService.sendProtectPlusActivationEmail(certificate);
				result.setSent(sendResult.isSent());
				result.setSkipped(false);
				result.setMessage(sendResult.isSent() ? "Sent." : sendResult.getErrorMessage());
				if (isActivationEmailThrottleError(sendResult.getErrorMessage())) {
					results.add(result);
					addSkippedActivationEmailResultsAfterThrottle(certificates, results, i + 1);
					break;
				}
				sleepBetweenActivationEmails(delayBetweenEmailsMillis);
			}
			results.add(result);
		}

		ProtectPlusActivationEmailResendReport report = new ProtectPlusActivationEmailResendReport();
		report.setDryRun(dryRun);
		report.setCandidateCount(certificates.size());
		report.setSentCount((int) results.stream().filter(ProtectPlusActivationEmailResendResult::isSent).count());
		report.setSkippedCount((int) results.stream().filter(ProtectPlusActivationEmailResendResult::isSkipped).count());
		report.setFailedCount((int) results.stream()
				.filter(result -> !result.isSent() && !result.isSkipped())
				.count());
		report.setResults(results);
		return report;
	}

	private void validateActivationEmailResendRequest(ProtectPlusActivationEmailResendRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Protect+ activation email resend request is required.");
		}
		if (request.getCertificateNumberFrom() == null) {
			throw new IllegalArgumentException("Certificate number from is required.");
		}
		if (request.getCertificateNumberTo() == null) {
			throw new IllegalArgumentException("Certificate number to is required.");
		}
	}

	private Integer resolveActivationEmailResendDelay(Integer delayBetweenEmailsMillis) {
		if (delayBetweenEmailsMillis == null) {
			return DEFAULT_ACTIVATION_EMAIL_RESEND_DELAY_MILLIS;
		}
		if (delayBetweenEmailsMillis < 0 || delayBetweenEmailsMillis > MAX_ACTIVATION_EMAIL_RESEND_DELAY_MILLIS) {
			throw new IllegalArgumentException("Delay between emails should be between 0 and 60000 milliseconds.");
		}

		return delayBetweenEmailsMillis;
	}

	private boolean isActivationEmailThrottleError(String errorMessage) {
		if (errorMessage == null) {
			return false;
		}

		return errorMessage.contains("454") && errorMessage.contains("Too many login attempts");
	}

	private void addSkippedActivationEmailResultsAfterThrottle(List<ProtectPlusCertificate> certificates,
			List<ProtectPlusActivationEmailResendResult> results, int startIndex) {
		for (int i = startIndex; i < certificates.size(); i++) {
			ProtectPlusActivationEmailResendResult skippedResult = createActivationEmailResendResult(certificates.get(i));
			skippedResult.setSkipped(true);
			skippedResult.setMessage("Skipped because Gmail throttled SMTP login attempts.");
			results.add(skippedResult);
		}
	}

	private void sleepBetweenActivationEmails(Integer delayBetweenEmailsMillis) {
		if (delayBetweenEmailsMillis == null || delayBetweenEmailsMillis <= 0) {
			return;
		}
		try {
			Thread.sleep(delayBetweenEmailsMillis);
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("Protect+ activation email resend was interrupted.", exception);
		}
	}

	private ProtectPlusActivationEmailResendResult createActivationEmailResendResult(ProtectPlusCertificate certificate) {
		ProtectPlusActivationEmailResendResult result = new ProtectPlusActivationEmailResendResult();
		result.setCertificateId(certificate.getId());
		result.setCertificateNumber(certificate.getCertificateNumber());
		result.setCustomerEmail(certificate.getLoyalCustomerEmail());
		return result;
	}

	private boolean isCertificateUsed(ProtectPlusCertificate certificate) {
		return Boolean.TRUE.equals(certificate.getFreeProtectorUsed())
				|| Boolean.TRUE.equals(certificate.getFreeDisplayReplacementServiceUsed())
				|| Boolean.TRUE.equals(certificate.getFreeBatteryReplacementServiceUsed())
				|| (certificate.getUsageCount() != null && certificate.getUsageCount() > 0)
				|| protectPlusCertificateDao.countProtectPlusUsageSales(certificate.getId()) > 0;
	}

	private void refundCertificatePurchase(ProtectPlusCertificate certificate) {
		Integer saleItemId = resolveSoldSaleItemId(certificate);
		if (saleItemId == null) {
			throw new IllegalStateTransferException("soldSaleItemId",
					"Protect+ certificate sale item can not be resolved for refund.");
		}

		Boolean refunded = saleDao.isSaleItemRefunded(saleItemId);
		if (refunded == null) {
			throw new DomainObjectNotFoundException("saleItemId", "Non-existing Protect+ certificate sale item.");
		}
		if (Boolean.TRUE.equals(refunded)) {
			return;
		}

		refundService.submitProtectPlusCancellationRefund(new Refund(saleItemId,
				"Protect+ certificate cancellation: " + certificate.getCertificateNumber()));
	}

	private void cancelCertificate(ProtectPlusCertificate certificate, Integer employeeId, Long currentTimestamp) {
		anonymizeLoyalCustomer(certificate, employeeId, currentTimestamp);
		protectPlusCertificateDao.cancelProtectPlusCertificate(certificate.getId(), employeeId, currentTimestamp);
	}

	private Integer resolveSoldSaleItemId(ProtectPlusCertificate certificate) {
		if (certificate.getSoldSaleItemId() != null) {
			return certificate.getSoldSaleItemId();
		}
		if (certificate.getSoldSaleId() == null) {
			return null;
		}

		return saleDao.getSingleSaleItemIdBySaleIdAndProductCode(certificate.getSoldSaleId(), PROTECT_PLUS_PRODUCT_CODE);
	}

	private void anonymizeLoyalCustomer(ProtectPlusCertificate certificate, Integer updatedById, Long currentTimestamp) {
		if (certificate.getLoyalCustomerId() == null) {
			return;
		}

		LoyalCustomer loyalCustomer = new LoyalCustomer();
		loyalCustomer.setId(certificate.getLoyalCustomerId());
		loyalCustomer.setUpdatedById(updatedById);
		loyalCustomer.setLastUpdateTimestamp(currentTimestamp);
		loyalCustomerDao.updateLoyalCustomer(loyalCustomer);
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
	public ProtectPlusCertificate saveInactiveCertificateDraft(Integer certificateId, ProtectPlusCertificateRequest request) {
		return saveInactiveCertificateDraft(certificateId, request, null);
	}

	@Override
	public ProtectPlusCertificate saveInactiveCertificateDraft(Integer certificateId, ProtectPlusCertificateRequest request,
			MultipartFile gdprConsentImage) {
		validateInactiveCertificateDraftRequest(request);
		ProtectPlusCertificate certificate = getProtectPlusCertificate(certificateId);
		if (!ProtectPlusCertificateStatus.INACTIVE.equals(certificate.getStatus())) {
			throw new IllegalStateTransferException("status",
					"Only inactive Protect+ certificate draft can be saved before activation.");
		}

		validateInactiveCertificateVisibility(certificate, request);
		validateExistingDeviceModel(request.getDeviceModelId());

		Employee loggedInEmployee = employeeService.getLoggedInEmployee();
		long currentTimestamp = dateService.getCurrentMillisBGTimezone();
		Integer loyalCustomerId = resolveDraftLoyalCustomerId(certificate, request, loggedInEmployee, currentTimestamp);
		protectPlusCertificateDao.updateInactiveProtectPlusCertificateDraft(certificateId, loyalCustomerId,
				request.getDeviceModelId(), loggedInEmployee.getId(), currentTimestamp);
		if (hasGdprConsentImage(gdprConsentImage)) {
			String gdprConsentFileId = imageUploadService.uploadProtectPlusGdprConsentImage(gdprConsentImage);
			protectPlusCertificateDao.updateGdprConsentFile(certificateId, gdprConsentFileId, loggedInEmployee.getId(),
					currentTimestamp);
		}

		return getProtectPlusCertificate(certificateId);
	}

	@Override
	public ProtectPlusCertificate changeDeviceModel(Integer certificateId, ProtectPlusDeviceModelChangeRequest request) {
		validateDeviceModelChangeRequest(request);
		ProtectPlusCertificate certificate = validateActiveCertificate(certificateId);
		if (certificate.getDeviceModelId() != null && certificate.getDeviceModelId().equals(request.getDeviceModelId())) {
			throw new IllegalArgumentException("New device model should be different from the current one.");
		}

		validateExistingDeviceModel(request.getDeviceModelId());

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

		ProtectPlusCertificate updatedCertificate = getProtectPlusCertificate(certificateId);
		emailService.sendProtectPlusDeviceModelChangeEmail(updatedCertificate, certificate.getDeviceModelName());

		return updatedCertificate;
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

		String storeIds = admin ? null : resolveVisibleStoreIds(loggedInEmployee.getStoreId());
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
	public List<ProtectPlusCertificateSearchResult> searchActiveProtectPlusCertificates(
			ProtectPlusCertificateSearchFilter filter) {
		boolean admin = Boolean.TRUE.equals(employeeService.isLoggedInEmployeeAdmin());
		if (!admin && filter.getCertificateNumber() == null && StringUtils.isEmpty(filter.getPhoneNumber())) {
			throw new IllegalArgumentException("Certificate number or phone number is required.");
		}

		Integer limit = admin ? null : 1;
		String storeIds = isDirectCertificateLookup(filter.getCertificateNumber(), filter.getPhoneNumber())
				? null : resolveVisibleStoreIds(filter.getStoreId());
		if (!admin) {
			filter.setDeviceBrandId(null);
			filter.setDeviceModelId(null);
		}

		List<ProtectPlusCertificateSearchResult> certificates = protectPlusCertificateDao.searchProtectPlusCertificates(
				filter, ProtectPlusCertificateStatus.ACTIVE, storeIds, limit);
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

	private void validateActivationRequest(ProtectPlusCertificateRequest request, ProtectPlusCertificate certificate,
			MultipartFile gdprConsentImage) {
		if (request == null) {
			throw new IllegalArgumentException("Protect+ certificate request is required.");
		}
		if (!hasGdprConsentImage(gdprConsentImage) && StringUtils.isEmpty(certificate.getGdprConsentFileId())) {
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

	private String resolveGdprConsentFileId(ProtectPlusCertificate certificate, MultipartFile gdprConsentImage) {
		if (hasGdprConsentImage(gdprConsentImage)) {
			return imageUploadService.uploadProtectPlusGdprConsentImage(gdprConsentImage);
		}

		return certificate.getGdprConsentFileId();
	}

	private boolean hasGdprConsentImage(MultipartFile gdprConsentImage) {
		return gdprConsentImage != null && !gdprConsentImage.isEmpty();
	}

	private void validateInactiveCertificateDraftRequest(ProtectPlusCertificateRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("Protect+ certificate request is required.");
		}
		if (request.getDeviceModelId() == null) {
			throw new IllegalArgumentException("Device model is required.");
		}
	}

	private void validateInactiveCertificateVisibility(ProtectPlusCertificate certificate,
			ProtectPlusCertificateRequest request) {
		if (Boolean.TRUE.equals(employeeService.isLoggedInEmployeeAdmin())) {
			return;
		}

		Employee loggedInEmployee = employeeService.getLoggedInEmployee();
		if (loggedInEmployee.getStoreId() == null || !loggedInEmployee.getStoreId().equals(certificate.getSoldStoreId())) {
			throw new IllegalStateTransferException("employee",
					"Employee can update only inactive Protect+ certificates sold in their store.");
		}
		if (certificate.getDeviceModelId() == null || !certificate.getDeviceModelId().equals(request.getDeviceModelId())) {
			throw new IllegalStateTransferException("deviceModelId",
					"Non-admin employee can not update inactive Protect+ certificate device model.");
		}
	}

	private Integer resolveDraftLoyalCustomerId(ProtectPlusCertificate certificate, ProtectPlusCertificateRequest request,
			Employee loggedInEmployee, long currentTimestamp) {
		if (!hasValidDraftLoyalCustomer(request)) {
			return certificate.getLoyalCustomerId();
		}

		LoyalCustomer requestCustomer = request.getLoyalCustomer();
		if (certificate.getLoyalCustomerId() != null) {
			requestCustomer.setId(certificate.getLoyalCustomerId());
			requestCustomer.setUpdatedById(loggedInEmployee.getId());
			requestCustomer.setLastUpdateTimestamp(currentTimestamp);
			loyalCustomerDao.updateLoyalCustomer(requestCustomer);
			return certificate.getLoyalCustomerId();
		}

		LoyalCustomer existingLoyalCustomer = findMatchingLoyalCustomer(requestCustomer);
		if (existingLoyalCustomer != null) {
			return existingLoyalCustomer.getId();
		}

		requestCustomer.setCreatedById(loggedInEmployee.getId());
		requestCustomer.setCreatedTimestamp(currentTimestamp);
		return loyalCustomerDao.insertLoyalCustomer(requestCustomer);
	}

	private boolean hasDraftCustomerData(ProtectPlusCertificateRequest request) {
		return request.getLoyalCustomer() != null
				&& (!StringUtils.isEmpty(request.getLoyalCustomer().getName())
						|| !StringUtils.isEmpty(request.getLoyalCustomer().getPhoneNumber())
						|| !StringUtils.isEmpty(request.getLoyalCustomer().getEmail()));
	}

	private boolean hasValidDraftLoyalCustomer(ProtectPlusCertificateRequest request) {
		return hasDraftCustomerData(request)
				&& !StringUtils.isEmpty(request.getLoyalCustomer().getName())
				&& !StringUtils.isEmpty(request.getLoyalCustomer().getPhoneNumber());
	}

	private void validateDeviceModelChangeRequest(ProtectPlusDeviceModelChangeRequest request) {
		if (request == null || request.getDeviceModelId() == null) {
			throw new IllegalArgumentException("Device model is required.");
		}
	}

	private DeviceModel validateExistingDeviceModel(Integer deviceModelId) {
		DeviceModel deviceModel = deviceModelDao.selectDeviceModel(deviceModelId);
		if (deviceModel == null) {
			throw new DomainObjectNotFoundException("deviceModelId", "Non-existing device model.");
		}

		return deviceModel;
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
