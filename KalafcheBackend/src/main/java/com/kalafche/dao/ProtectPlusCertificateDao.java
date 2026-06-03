package com.kalafche.dao;

import java.util.List;

import com.kalafche.model.protectplus.ProtectPlusCallRecord;
import com.kalafche.model.protectplus.ProtectPlusCertificate;
import com.kalafche.model.protectplus.ProtectPlusCertificateSearchResult;
import com.kalafche.model.protectplus.ProtectPlusCertificateStatus;
import com.kalafche.model.protectplus.ProtectPlusDeviceModelChangeRecord;
import com.kalafche.model.protectplus.ProtectPlusRenewalRecord;
import com.kalafche.model.protectplus.ProtectPlusUsageRecord;

public interface ProtectPlusCertificateDao {

	Integer insertProtectPlusCertificate(ProtectPlusCertificate certificate);

	void activateProtectPlusCertificate(ProtectPlusCertificate certificate);

	void updateProtectPlusCertificateUsage(Integer certificateId, Boolean freeProtectorUsed,
				Boolean freeDisplayReplacementServiceUsed, Boolean freeBatteryReplacementServiceUsed,
				Long validUntilTimestamp, ProtectPlusCertificateStatus status, Integer updatedById, Long lastUpdateTimestamp);

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

	List<ProtectPlusCertificateSearchResult> searchProtectPlusCertificates(Integer certificateNumber, String phoneNumber,
			ProtectPlusCertificateStatus status, String storeIds, Integer deviceBrandId, Integer deviceModelId,
			Integer limit);

	Integer getMaxCertificateNumber();
}
