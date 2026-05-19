package com.kalafche.dao;

import java.util.List;

import com.kalafche.model.protectplus.ProtectPlusCertificate;
import com.kalafche.model.protectplus.ProtectPlusCertificateSearchResult;

public interface ProtectPlusCertificateDao {

	Integer insertProtectPlusCertificate(ProtectPlusCertificate certificate);

	void activateProtectPlusCertificate(ProtectPlusCertificate certificate);

	void updateProtectPlusCertificateUsage(Integer certificateId, Boolean freeProtectorUsed, Long validUntilTimestamp,
			Integer updatedById, Long lastUpdateTimestamp);

	void updateCallRecordingFile(Integer certificateId, String callRecordingFileId, Integer updatedById,
			Long lastUpdateTimestamp);

	ProtectPlusCertificate getProtectPlusCertificate(Integer id);

	ProtectPlusCertificate getProtectPlusCertificateByNumber(Integer certificateNumber);

	List<ProtectPlusCertificateSearchResult> searchProtectPlusCertificates(Integer certificateNumber, String phoneNumber,
			String status, Integer storeId, Integer deviceBrandId, Integer deviceModelId, Integer limit);

	Integer getMaxCertificateNumber();
}
