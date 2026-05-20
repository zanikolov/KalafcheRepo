package com.kalafche.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.kalafche.model.protectplus.ProtectPlusCertificate;
import com.kalafche.model.protectplus.ProtectPlusCertificateRequest;
import com.kalafche.model.protectplus.ProtectPlusCertificateSearchResult;

public interface ProtectPlusCertificateService {

	void createPendingCertificateForSale(Integer saleId, Integer storeId, Integer employeeId);

	ProtectPlusCertificate activateProtectPlusCertificate(Integer certificateId, ProtectPlusCertificateRequest request,
			MultipartFile gdprConsentImage);

	void registerCertificateUsage(ProtectPlusCertificate certificate, boolean freeProtectorUsedInSale);

	ProtectPlusCertificate uploadCallRecording(Integer certificateId, MultipartFile callRecording);

	ProtectPlusCertificate getProtectPlusCertificate(Integer certificateId);

	ProtectPlusCertificate getProtectPlusCertificateByNumber(Integer certificateNumber);

	List<ProtectPlusCertificateSearchResult> getInactiveProtectPlusCertificatesForCurrentStore();

	List<ProtectPlusCertificateSearchResult> searchActiveProtectPlusCertificates(Integer certificateNumber, String phoneNumber,
				Integer storeId, Integer deviceBrandId, Integer deviceModelId);

	List<ProtectPlusCertificateSearchResult> searchActiveProtectPlusCertificatesByQuery(String query);

	ProtectPlusCertificate validateActiveCertificate(Integer certificateId);
}
