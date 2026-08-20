package com.kalafche.dao;

import java.util.List;

import com.kalafche.model.protectplus.ProtectPlusCallRecord;
import com.kalafche.model.protectplus.ProtectPlusCertificate;
import com.kalafche.model.protectplus.ProtectPlusCertificateSearchFilter;
import com.kalafche.model.protectplus.ProtectPlusCertificateSearchResult;
import com.kalafche.model.protectplus.ProtectPlusCertificateStatus;
import com.kalafche.model.protectplus.ProtectPlusDeviceModelChangeRecord;
import com.kalafche.model.protectplus.ProtectPlusRenewalRecord;
import com.kalafche.model.protectplus.ProtectPlusUsageRecord;

public interface ProtectPlusCertificateDao {

	Integer insertProtectPlusCertificate(ProtectPlusCertificate certificate);

	void activateProtectPlusCertificate(ProtectPlusCertificate certificate);

	void updateInactiveProtectPlusCertificateDraft(Integer certificateId, Integer loyalCustomerId, Integer deviceModelId,
			Integer updatedById, Long lastUpdateTimestamp);

	void updateGdprConsentFile(Integer certificateId, String gdprConsentFileId, Integer updatedById,
			Long lastUpdateTimestamp);

	void updateProtectPlusCertificateUsage(Integer certificateId, Boolean freeProtectorUsed,
				Boolean freeDisplayReplacementServiceUsed, Boolean freeBatteryReplacementServiceUsed,
				Long validUntilTimestamp, ProtectPlusCertificateStatus status, Integer updatedById, Long lastUpdateTimestamp);

	void cancelProtectPlusCertificate(Integer certificateId, Integer updatedById, Long lastUpdateTimestamp);

	Integer countProtectPlusUsageSales(Integer certificateId);

	int expireActiveProtectPlusCertificates(Long currentTimestamp);

	void insertProtectPlusCertificateRenewal(Integer certificateId, Integer saleId, Integer storeId, Integer employeeId,
			Long oldValidUntilTimestamp, Long newValidUntilTimestamp, String source, Long createdTimestamp);

	void updateDeviceModel(Integer certificateId, Integer deviceModelId, Boolean deviceModelChangeUsed,
			Integer updatedById, Long lastUpdateTimestamp);

	void insertProtectPlusCertificateDeviceModelChange(Integer certificateId, Integer storeId, Integer employeeId,
			Integer oldDeviceModelId, Integer newDeviceModelId, Boolean adminOverride, Long createdTimestamp);

	Integer insertProtectPlusCallRecord(ProtectPlusCallRecord callRecord);

	ProtectPlusCallRecord getProtectPlusCallRecord(Integer callRecordId);

	List<ProtectPlusCallRecord> getProtectPlusCallRecords(Integer certificateId);

	List<ProtectPlusUsageRecord> getProtectPlusUsageRecords(Integer certificateId);

	List<ProtectPlusRenewalRecord> getProtectPlusRenewalRecords(Integer certificateId);

	List<ProtectPlusDeviceModelChangeRecord> getProtectPlusDeviceModelChangeRecords(Integer certificateId);

	ProtectPlusCertificate getProtectPlusCertificate(Integer id);

	ProtectPlusCertificate getProtectPlusCertificateByNumber(Integer certificateNumber);

	ProtectPlusCertificate getProtectPlusCertificateBySoldSaleItemId(Integer soldSaleItemId);

	List<ProtectPlusCertificate> getProtectPlusCertificatesByCertificateNumberRange(Integer certificateNumberFrom,
			Integer certificateNumberTo);

	List<ProtectPlusCertificateSearchResult> searchProtectPlusCertificates(Integer certificateNumber, String phoneNumber,
				ProtectPlusCertificateStatus status, String storeIds, Integer deviceBrandId, Integer deviceModelId,
				Integer limit);

	List<ProtectPlusCertificateSearchResult> searchProtectPlusCertificates(ProtectPlusCertificateSearchFilter filter,
			ProtectPlusCertificateStatus status, String storeIds, Integer limit);

	Integer getMaxCertificateNumber();
}
