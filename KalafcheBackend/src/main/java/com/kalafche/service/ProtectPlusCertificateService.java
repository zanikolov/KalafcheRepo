package com.kalafche.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.kalafche.model.protectplus.ProtectPlusCallRecord;
import com.kalafche.model.protectplus.ProtectPlusCallRecordDownload;
import com.kalafche.model.protectplus.ProtectPlusCertificate;
import com.kalafche.model.protectplus.ProtectPlusActivationEmailResendReport;
import com.kalafche.model.protectplus.ProtectPlusActivationEmailResendRequest;
import com.kalafche.model.protectplus.ProtectPlusCertificateRequest;
import com.kalafche.model.protectplus.ProtectPlusCertificateSearchFilter;
import com.kalafche.model.protectplus.ProtectPlusCertificateSearchResult;
import com.kalafche.model.protectplus.ProtectPlusCustomerEmailUpdateRequest;
import com.kalafche.model.protectplus.ProtectPlusCustomerNameUpdateRequest;
import com.kalafche.model.protectplus.ProtectPlusCustomerPhoneUpdateRequest;
import com.kalafche.model.protectplus.ProtectPlusDeviceModelChangeRecord;
import com.kalafche.model.protectplus.ProtectPlusDeviceModelChangeRequest;
import com.kalafche.model.protectplus.ProtectPlusRenewalRecord;
import com.kalafche.model.protectplus.ProtectPlusUsageRecord;

public interface ProtectPlusCertificateService {

	void createPendingCertificateForSale(Integer saleId, Integer saleItemId, Integer storeId, Integer employeeId,
			Integer deviceModelId);

	void expireExpiredProtectPlusCertificates();

	ProtectPlusCertificate activateProtectPlusCertificate(Integer certificateId, ProtectPlusCertificateRequest request,
			MultipartFile gdprConsentImage);

	ProtectPlusCertificate updateCustomerEmail(Integer certificateId, ProtectPlusCustomerEmailUpdateRequest request);

	ProtectPlusCertificate updateCustomerName(Integer certificateId, ProtectPlusCustomerNameUpdateRequest request);

	ProtectPlusCertificate updateCustomerPhone(Integer certificateId, ProtectPlusCustomerPhoneUpdateRequest request);

	ProtectPlusCertificate cancelProtectPlusCertificate(Integer certificateId);

	ProtectPlusActivationEmailResendReport resendActivationEmails(ProtectPlusActivationEmailResendRequest request);

	void registerCertificateUsage(ProtectPlusCertificate certificate, boolean freeProtectorUsedInSale,
			boolean freeDisplayReplacementServiceUsedInSale, boolean freeBatteryReplacementServiceUsedInSale,
			Integer saleId, Integer storeId, Integer employeeId);

	ProtectPlusCallRecord uploadCallRecording(Integer certificateId, MultipartFile callRecording, String note);

	List<ProtectPlusCallRecord> getCallRecords(Integer certificateId);

	ProtectPlusCallRecordDownload downloadCallRecording(Integer certificateId, Integer callRecordId);

	ProtectPlusCallRecordDownload downloadGdprConsent(Integer certificateId);

	List<ProtectPlusUsageRecord> getUsageRecords(Integer certificateId);

	List<ProtectPlusRenewalRecord> getRenewalRecords(Integer certificateId);

	List<ProtectPlusDeviceModelChangeRecord> getDeviceModelChangeRecords(Integer certificateId);

	ProtectPlusCertificate saveInactiveCertificateDraft(Integer certificateId, ProtectPlusCertificateRequest request);

	ProtectPlusCertificate saveInactiveCertificateDraft(Integer certificateId, ProtectPlusCertificateRequest request,
			MultipartFile gdprConsentImage);

	ProtectPlusCertificate changeDeviceModel(Integer certificateId, ProtectPlusDeviceModelChangeRequest request);

	ProtectPlusCertificate getProtectPlusCertificate(Integer certificateId);

	ProtectPlusCertificate getProtectPlusCertificateByNumber(Integer certificateNumber);

	List<ProtectPlusCertificateSearchResult> getInactiveProtectPlusCertificatesForCurrentStore();

	List<ProtectPlusCertificateSearchResult> searchActiveProtectPlusCertificates(Integer certificateNumber, String phoneNumber,
					Integer storeId, Integer deviceBrandId, Integer deviceModelId);

	List<ProtectPlusCertificateSearchResult> searchActiveProtectPlusCertificates(ProtectPlusCertificateSearchFilter filter);

	List<ProtectPlusCertificateSearchResult> searchActiveProtectPlusCertificatesByQuery(String query);

	ProtectPlusCertificate validateActiveCertificate(Integer certificateId);
}
