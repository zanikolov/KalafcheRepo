package com.kalafche.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.kalafche.dao.ProtectPlusCertificateDao;
import com.kalafche.model.protectplus.ProtectPlusCallRecord;
import com.kalafche.model.protectplus.ProtectPlusCertificate;
import com.kalafche.model.protectplus.ProtectPlusCertificateSearchResult;
import com.kalafche.model.protectplus.ProtectPlusCertificateStatus;
import com.kalafche.model.protectplus.ProtectPlusDeviceModelChangeRecord;
import com.kalafche.model.protectplus.ProtectPlusRenewalRecord;
import com.kalafche.model.protectplus.ProtectPlusUsageRecord;

@Service
public class ProtectPlusCertificateDaoImpl extends JdbcDaoSupport implements ProtectPlusCertificateDao {

	private static final String SELECT_CERTIFICATE = "select " +
			"ppc.id, " +
			"ppc.certificate_number, " +
			"ppc.loyal_customer_id, " +
			"ppc.device_model_id, " +
			"ppc.sold_store_id, " +
			"ppc.sold_by_employee_id, " +
			"ppc.sold_sale_id, " +
			"ppc.status, " +
			"ppc.valid_from_timestamp, " +
			"ppc.valid_until_timestamp, " +
				"ppc.activated_by as activated_by_id, " +
				"ppc.activated_timestamp, " +
				"ppc.gdpr_consent_file_id, " +
			"ppc.call_recording_file_id, " +
			"ppc.free_protector_used, " +
			"ppc.free_display_replacement_service_used, " +
			"ppc.free_battery_replacement_service_used, " +
			"ppc.device_model_change_used, " +
			"ppc.usage_count, " +
			"ppc.created_by as created_by_id, " +
			"ppc.created_timestamp, " +
			"ppc.updated_by as updated_by_id, " +
			"ppc.last_update_timestamp, " +
			"lc.name as loyal_customer_name, " +
			"lc.phone_number as loyal_customer_phone_number, " +
			"lc.email as loyal_customer_email, " +
			"concat(db.name, ' ', dm.name) as device_model_name, " +
			"concat(s.city, ', ', s.name) as sold_store_name, " +
			"se.name as sold_by_employee_name, " +
			"ae.name as activated_by_name, " +
			"ce.name as created_by_name, " +
			"ue.name as updated_by_name " +
			"from protect_plus_certificate ppc " +
			"left join loyal_customer lc on ppc.loyal_customer_id = lc.id " +
			"left join device_model dm on ppc.device_model_id = dm.id " +
			"left join device_brand db on dm.device_brand_id = db.id " +
			"join store s on ppc.sold_store_id = s.id " +
			"join employee se on ppc.sold_by_employee_id = se.id " +
			"left join employee ae on ppc.activated_by = ae.id " +
			"join employee ce on ppc.created_by = ce.id " +
			"left join employee ue on ppc.updated_by = ue.id ";

	private static final String SELECT_SEARCH_RESULT = "select " +
			"ppc.id, " +
			"ppc.certificate_number, " +
			"ppc.status, " +
			"ppc.valid_until_timestamp, " +
			"ppc.free_protector_used, " +
				"ppc.device_model_change_used, " +
				"ppc.usage_count, " +
				"ppc.gdpr_consent_file_id, " +
				"lc.email as loyal_customer_email, " +
				"lc.name as customer_name, " +
			"lc.phone_number as phone_number, " +
			"db.id as device_brand_id, " +
			"ppc.free_display_replacement_service_used, " +
			"ppc.free_battery_replacement_service_used, " +
			"ppc.device_model_id, " +
			"concat(db.name, ' ', dm.name) as device_model_name, " +
			"concat(s.city, ', ', s.name) as sold_store_name, " +
			"se.name as sold_by_employee_name, " +
			"ppc.created_timestamp " +
			"from protect_plus_certificate ppc " +
			"left join loyal_customer lc on ppc.loyal_customer_id = lc.id " +
			"left join device_model dm on ppc.device_model_id = dm.id " +
			"left join device_brand db on dm.device_brand_id = db.id " +
			"join store s on ppc.sold_store_id = s.id " +
			"join employee se on ppc.sold_by_employee_id = se.id ";

	private static final String BY_ID_CLAUSE = "where ppc.id = ? ";
	private static final String BY_NUMBER_CLAUSE = "where ppc.certificate_number = ? ";
	private static final String INSERT_CERTIFICATE = "insert into protect_plus_certificate " +
			"(certificate_number, loyal_customer_id, device_model_id, sold_store_id, sold_by_employee_id, sold_sale_id, " +
			"status, valid_from_timestamp, valid_until_timestamp, gdpr_consent_file_id, free_protector_used, " +
			"free_display_replacement_service_used, free_battery_replacement_service_used, device_model_change_used, " +
			"usage_count, created_by, created_timestamp) " +
			"values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String ACTIVATE_CERTIFICATE = "update protect_plus_certificate set status = ?, " +
			"loyal_customer_id = ?, device_model_id = ?, valid_from_timestamp = ?, valid_until_timestamp = ?, " +
			"activated_by = ?, activated_timestamp = ?, gdpr_consent_file_id = ?, last_update_timestamp = ?, updated_by = ? where id = ?";
	private static final String UPDATE_CERTIFICATE_USAGE = "update protect_plus_certificate set free_protector_used = ?, " +
			"free_display_replacement_service_used = ?, free_battery_replacement_service_used = ?, " +
			"usage_count = coalesce(usage_count, 0) + 1, valid_until_timestamp = ?, status = ?, last_update_timestamp = ?, updated_by = ? where id = ?";
	private static final String EXPIRE_ACTIVE_CERTIFICATES = "update protect_plus_certificate set status = ?, " +
			"last_update_timestamp = ?, updated_by = null where status = ? and valid_until_timestamp is not null " +
			"and valid_until_timestamp < ?";
	private static final String INSERT_CERTIFICATE_RENEWAL = "insert into protect_plus_certificate_renewal " +
			"(protect_plus_certificate_id, sale_id, store_id, employee_id, old_valid_until_timestamp, " +
			"new_valid_until_timestamp, source, created_timestamp) values (?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_DEVICE_MODEL = "update protect_plus_certificate set device_model_id = ?, " +
			"device_model_change_used = ?, last_update_timestamp = ?, updated_by = ? where id = ?";
	private static final String INSERT_DEVICE_MODEL_CHANGE = "insert into protect_plus_certificate_device_model_change " +
			"(protect_plus_certificate_id, store_id, employee_id, old_device_model_id, new_device_model_id, " +
			"admin_override, created_timestamp) values (?, ?, ?, ?, ?, ?, ?)";
	private static final String INSERT_CALL_RECORD = "insert into protect_plus_certificate_call_record " +
			"(protect_plus_certificate_id, store_id, employee_id, call_recording_file_id, call_recording_file_name, " +
			"note, created_timestamp) values (?, ?, ?, ?, ?, ?, ?)";
	private static final String SELECT_CALL_RECORDS = "select " +
			"cr.id, " +
			"cr.protect_plus_certificate_id, " +
			"cr.store_id, " +
			"concat(s.city, ', ', s.name) as store_name, " +
			"cr.employee_id, " +
			"e.name as employee_name, " +
			"cr.call_recording_file_id, " +
			"cr.call_recording_file_name, " +
			"cr.note, " +
			"cr.created_timestamp " +
			"from protect_plus_certificate_call_record cr " +
			"left join store s on cr.store_id = s.id " +
			"join employee e on cr.employee_id = e.id " +
			"where 1 = 1 ";
	private static final String CALL_RECORD_ID_FILTER = " and cr.id = ? ";
	private static final String CALL_RECORD_CERTIFICATE_FILTER = " and cr.protect_plus_certificate_id = ? ";
	private static final String ORDER_CALL_RECORDS_BY_CREATED = " order by cr.created_timestamp desc";
	private static final String SELECT_USAGE_RECORDS = "select " +
			"s.id as sale_id, " +
			"s.sale_timestamp, " +
			"concat(st.city, ', ', st.name) as store_name, " +
			"e.name as employee_name, " +
			"sum(si.sale_price) as total_amount, " +
			"sum(case when si.protect_plus_applied = true then 1 else 0 end) as protect_plus_applied_items_count " +
			"from sale s " +
			"join sale_item si on si.sale_id = s.id " +
			"join store st on s.store_id = st.id " +
			"join employee e on s.employee_id = e.id " +
			"where s.protect_plus_certificate_id = ? " +
			"group by s.id, s.sale_timestamp, st.city, st.name, e.name " +
			"order by s.sale_timestamp desc";
	private static final String SELECT_RENEWAL_RECORDS = "select " +
			"r.id, " +
			"r.sale_id, " +
			"concat(st.city, ', ', st.name) as store_name, " +
			"e.name as employee_name, " +
			"r.old_valid_until_timestamp, " +
			"r.new_valid_until_timestamp, " +
			"r.source, " +
			"r.created_timestamp " +
			"from protect_plus_certificate_renewal r " +
			"join store st on r.store_id = st.id " +
			"join employee e on r.employee_id = e.id " +
			"where r.protect_plus_certificate_id = ? " +
			"order by r.created_timestamp desc";
	private static final String SELECT_DEVICE_MODEL_CHANGE_RECORDS = "select " +
			"dmc.id, " +
			"concat(st.city, ', ', st.name) as store_name, " +
			"e.name as employee_name, " +
			"concat(old_db.name, ' ', old_dm.name) as old_device_model_name, " +
			"concat(new_db.name, ' ', new_dm.name) as new_device_model_name, " +
			"dmc.admin_override, " +
			"dmc.created_timestamp " +
			"from protect_plus_certificate_device_model_change dmc " +
			"left join store st on dmc.store_id = st.id " +
			"join employee e on dmc.employee_id = e.id " +
			"left join device_model old_dm on dmc.old_device_model_id = old_dm.id " +
			"left join device_brand old_db on old_dm.device_brand_id = old_db.id " +
			"join device_model new_dm on dmc.new_device_model_id = new_dm.id " +
			"join device_brand new_db on new_dm.device_brand_id = new_db.id " +
			"where dmc.protect_plus_certificate_id = ? " +
			"order by dmc.created_timestamp desc";
	private static final String SELECT_MAX_CERTIFICATE_NUMBER = "select max(certificate_number) from protect_plus_certificate";
	private static final String CERTIFICATE_NUMBER_FILTER = " and ppc.certificate_number = ? ";
	private static final String PHONE_NUMBER_FILTER = " and lc.phone_number = ? ";
	private static final String STATUS_FILTER = " and ppc.status = ? ";
	private static final String USABLE_STATUS_FILTER = " and ppc.status in (?, ?) ";
	private static final String SOLD_STORE_FILTER = " and ppc.sold_store_id in (%s) ";
	private static final String DEVICE_BRAND_FILTER = " and db.id = ? ";
	private static final String DEVICE_MODEL_FILTER = " and ppc.device_model_id = ? ";
	private static final String ORDER_BY_VALIDITY = " order by ppc.valid_until_timestamp desc ";
	private static final String ORDER_BY_CREATED = " order by ppc.created_timestamp desc ";

	private BeanPropertyRowMapper<ProtectPlusCertificate> certificateRowMapper;
	private BeanPropertyRowMapper<ProtectPlusCertificateSearchResult> searchResultRowMapper;
	private BeanPropertyRowMapper<ProtectPlusCallRecord> callRecordRowMapper;
	private BeanPropertyRowMapper<ProtectPlusUsageRecord> usageRecordRowMapper;
	private BeanPropertyRowMapper<ProtectPlusRenewalRecord> renewalRecordRowMapper;
	private BeanPropertyRowMapper<ProtectPlusDeviceModelChangeRecord> deviceModelChangeRecordRowMapper;

	@Autowired
	public ProtectPlusCertificateDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}

	private BeanPropertyRowMapper<ProtectPlusCertificate> getCertificateRowMapper() {
		if (certificateRowMapper == null) {
			certificateRowMapper = new BeanPropertyRowMapper<ProtectPlusCertificate>(ProtectPlusCertificate.class);
			certificateRowMapper.setPrimitivesDefaultedForNullValue(true);
		}

		return certificateRowMapper;
	}

	private BeanPropertyRowMapper<ProtectPlusCertificateSearchResult> getSearchResultRowMapper() {
		if (searchResultRowMapper == null) {
			searchResultRowMapper = new BeanPropertyRowMapper<ProtectPlusCertificateSearchResult>(ProtectPlusCertificateSearchResult.class);
			searchResultRowMapper.setPrimitivesDefaultedForNullValue(true);
		}

		return searchResultRowMapper;
	}

	private BeanPropertyRowMapper<ProtectPlusCallRecord> getCallRecordRowMapper() {
		if (callRecordRowMapper == null) {
			callRecordRowMapper = new BeanPropertyRowMapper<ProtectPlusCallRecord>(ProtectPlusCallRecord.class);
			callRecordRowMapper.setPrimitivesDefaultedForNullValue(true);
		}

		return callRecordRowMapper;
	}

	private BeanPropertyRowMapper<ProtectPlusUsageRecord> getUsageRecordRowMapper() {
		if (usageRecordRowMapper == null) {
			usageRecordRowMapper = new BeanPropertyRowMapper<ProtectPlusUsageRecord>(ProtectPlusUsageRecord.class);
			usageRecordRowMapper.setPrimitivesDefaultedForNullValue(true);
		}

		return usageRecordRowMapper;
	}

	private BeanPropertyRowMapper<ProtectPlusRenewalRecord> getRenewalRecordRowMapper() {
		if (renewalRecordRowMapper == null) {
			renewalRecordRowMapper = new BeanPropertyRowMapper<ProtectPlusRenewalRecord>(ProtectPlusRenewalRecord.class);
			renewalRecordRowMapper.setPrimitivesDefaultedForNullValue(true);
		}

		return renewalRecordRowMapper;
	}

	private BeanPropertyRowMapper<ProtectPlusDeviceModelChangeRecord> getDeviceModelChangeRecordRowMapper() {
		if (deviceModelChangeRecordRowMapper == null) {
			deviceModelChangeRecordRowMapper =
					new BeanPropertyRowMapper<ProtectPlusDeviceModelChangeRecord>(ProtectPlusDeviceModelChangeRecord.class);
			deviceModelChangeRecordRowMapper.setPrimitivesDefaultedForNullValue(true);
		}

		return deviceModelChangeRecordRowMapper;
	}

	@Override
	public Integer insertProtectPlusCertificate(ProtectPlusCertificate certificate) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		int affectedRows = getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement statement = connection.prepareStatement(INSERT_CERTIFICATE, Statement.RETURN_GENERATED_KEYS);
				statement.setInt(1, certificate.getCertificateNumber());
				setNullableInteger(statement, 2, certificate.getLoyalCustomerId());
				setNullableInteger(statement, 3, certificate.getDeviceModelId());
				statement.setInt(4, certificate.getSoldStoreId());
				statement.setInt(5, certificate.getSoldByEmployeeId());
				setNullableInteger(statement, 6, certificate.getSoldSaleId());
				statement.setString(7, certificate.getStatus().name());
				setNullableLong(statement, 8, certificate.getValidFromTimestamp());
				setNullableLong(statement, 9, certificate.getValidUntilTimestamp());
				statement.setString(10, certificate.getGdprConsentFileId());
				statement.setBoolean(11, Boolean.TRUE.equals(certificate.getFreeProtectorUsed()));
				statement.setBoolean(12, Boolean.TRUE.equals(certificate.getFreeDisplayReplacementServiceUsed()));
				statement.setBoolean(13, Boolean.TRUE.equals(certificate.getFreeBatteryReplacementServiceUsed()));
				statement.setBoolean(14, Boolean.TRUE.equals(certificate.getDeviceModelChangeUsed()));
				statement.setInt(15, certificate.getUsageCount() == null ? 0 : certificate.getUsageCount());
				statement.setInt(16, certificate.getCreatedById());
				statement.setLong(17, certificate.getCreatedTimestamp());
				return statement;
			}
		}, keyHolder);

		if (affectedRows == 0 || keyHolder.getKey() == null) {
			throw new IllegalStateException("Creating Protect+ certificate failed, no ID obtained.");
		}

		return keyHolder.getKey().intValue();
	}

	@Override
	public void activateProtectPlusCertificate(ProtectPlusCertificate certificate) {
		getJdbcTemplate().update(ACTIVATE_CERTIFICATE, certificate.getStatus().name(), certificate.getLoyalCustomerId(),
				certificate.getDeviceModelId(), certificate.getValidFromTimestamp(), certificate.getValidUntilTimestamp(),
				certificate.getActivatedById(), certificate.getActivatedTimestamp(), certificate.getGdprConsentFileId(),
				certificate.getLastUpdateTimestamp(), certificate.getUpdatedById(), certificate.getId());
	}

	@Override
	public void updateProtectPlusCertificateUsage(Integer certificateId, Boolean freeProtectorUsed,
				Boolean freeDisplayReplacementServiceUsed, Boolean freeBatteryReplacementServiceUsed,
				Long validUntilTimestamp, ProtectPlusCertificateStatus status, Integer updatedById, Long lastUpdateTimestamp) {
		getJdbcTemplate().update(UPDATE_CERTIFICATE_USAGE, Boolean.TRUE.equals(freeProtectorUsed),
				Boolean.TRUE.equals(freeDisplayReplacementServiceUsed),
				Boolean.TRUE.equals(freeBatteryReplacementServiceUsed), validUntilTimestamp, status.name(), lastUpdateTimestamp,
				updatedById, certificateId);
	}

	@Override
	public int expireActiveProtectPlusCertificates(Long currentTimestamp) {
		return getJdbcTemplate().update(EXPIRE_ACTIVE_CERTIFICATES, ProtectPlusCertificateStatus.EXPIRED.name(),
				currentTimestamp, ProtectPlusCertificateStatus.ACTIVE.name(), currentTimestamp);
	}

	@Override
	public void insertProtectPlusCertificateRenewal(Integer certificateId, Integer saleId, Integer storeId,
			Integer employeeId, Long oldValidUntilTimestamp, Long newValidUntilTimestamp, String source,
			Long createdTimestamp) {
		getJdbcTemplate().update(INSERT_CERTIFICATE_RENEWAL, certificateId, saleId, storeId, employeeId,
				oldValidUntilTimestamp, newValidUntilTimestamp, source, createdTimestamp);
	}

	@Override
	public void updateDeviceModel(Integer certificateId, Integer deviceModelId, Boolean deviceModelChangeUsed,
			Integer updatedById, Long lastUpdateTimestamp) {
		getJdbcTemplate().update(UPDATE_DEVICE_MODEL, deviceModelId, Boolean.TRUE.equals(deviceModelChangeUsed),
				lastUpdateTimestamp, updatedById, certificateId);
	}

	@Override
	public void insertProtectPlusCertificateDeviceModelChange(Integer certificateId, Integer storeId,
			Integer employeeId, Integer oldDeviceModelId, Integer newDeviceModelId, Boolean adminOverride,
			Long createdTimestamp) {
		getJdbcTemplate().update(INSERT_DEVICE_MODEL_CHANGE, certificateId, storeId, employeeId, oldDeviceModelId,
				newDeviceModelId, Boolean.TRUE.equals(adminOverride), createdTimestamp);
	}

	@Override
	public Integer insertProtectPlusCallRecord(ProtectPlusCallRecord callRecord) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		int affectedRows = getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement statement = connection.prepareStatement(INSERT_CALL_RECORD,
						Statement.RETURN_GENERATED_KEYS);
				statement.setInt(1, callRecord.getProtectPlusCertificateId());
				setNullableInteger(statement, 2, callRecord.getStoreId());
				statement.setInt(3, callRecord.getEmployeeId());
				statement.setString(4, callRecord.getCallRecordingFileId());
				statement.setString(5, callRecord.getCallRecordingFileName());
				statement.setString(6, callRecord.getNote());
				statement.setLong(7, callRecord.getCreatedTimestamp());
				return statement;
			}
		}, keyHolder);

		if (affectedRows == 0 || keyHolder.getKey() == null) {
			throw new IllegalStateException("Creating Protect+ call record failed, no ID obtained.");
		}

		return keyHolder.getKey().intValue();
	}

	@Override
	public List<ProtectPlusCallRecord> getProtectPlusCallRecords(Integer certificateId) {
		return getJdbcTemplate().query(SELECT_CALL_RECORDS + CALL_RECORD_CERTIFICATE_FILTER +
				ORDER_CALL_RECORDS_BY_CREATED, getCallRecordRowMapper(), certificateId);
	}

	@Override
	public ProtectPlusCallRecord getProtectPlusCallRecord(Integer callRecordId) {
		List<ProtectPlusCallRecord> callRecords = getJdbcTemplate().query(SELECT_CALL_RECORDS + CALL_RECORD_ID_FILTER,
				getCallRecordRowMapper(), callRecordId);

		return callRecords.isEmpty() ? null : callRecords.get(0);
	}

	@Override
	public List<ProtectPlusUsageRecord> getProtectPlusUsageRecords(Integer certificateId) {
		return getJdbcTemplate().query(SELECT_USAGE_RECORDS, getUsageRecordRowMapper(), certificateId);
	}

	@Override
	public List<ProtectPlusRenewalRecord> getProtectPlusRenewalRecords(Integer certificateId) {
		return getJdbcTemplate().query(SELECT_RENEWAL_RECORDS, getRenewalRecordRowMapper(), certificateId);
	}

	@Override
	public List<ProtectPlusDeviceModelChangeRecord> getProtectPlusDeviceModelChangeRecords(Integer certificateId) {
		return getJdbcTemplate().query(SELECT_DEVICE_MODEL_CHANGE_RECORDS, getDeviceModelChangeRecordRowMapper(),
				certificateId);
	}

	@Override
	public ProtectPlusCertificate getProtectPlusCertificate(Integer id) {
		List<ProtectPlusCertificate> certificates = getJdbcTemplate().query(SELECT_CERTIFICATE + BY_ID_CLAUSE,
				getCertificateRowMapper(), id);

		return certificates.isEmpty() ? null : certificates.get(0);
	}

	@Override
	public ProtectPlusCertificate getProtectPlusCertificateByNumber(Integer certificateNumber) {
		List<ProtectPlusCertificate> certificates = getJdbcTemplate().query(SELECT_CERTIFICATE + BY_NUMBER_CLAUSE,
				getCertificateRowMapper(), certificateNumber);

		return certificates.isEmpty() ? null : certificates.get(0);
	}

	@Override
	public List<ProtectPlusCertificateSearchResult> searchProtectPlusCertificates(Integer certificateNumber,
			String phoneNumber, ProtectPlusCertificateStatus status, String storeIds, Integer deviceBrandId, Integer deviceModelId,
			Integer limit) {
		if (certificateNumber == null && StringUtils.isEmpty(phoneNumber) && status == null
				&& StringUtils.isEmpty(storeIds) && deviceBrandId == null && deviceModelId == null) {
			return new ArrayList<ProtectPlusCertificateSearchResult>();
		}

		String query = SELECT_SEARCH_RESULT + "where 1 = 1 ";
		List<Object> args = new ArrayList<Object>();

		if (certificateNumber != null) {
			query += CERTIFICATE_NUMBER_FILTER;
			args.add(certificateNumber);
		}

		if (!StringUtils.isEmpty(phoneNumber)) {
			query += PHONE_NUMBER_FILTER;
			args.add(phoneNumber);
		}

		if (status != null) {
			if (ProtectPlusCertificateStatus.ACTIVE.equals(status)) {
				query += USABLE_STATUS_FILTER;
				args.add(ProtectPlusCertificateStatus.ACTIVE.name());
				args.add(ProtectPlusCertificateStatus.EXPIRED.name());
			} else {
				query += STATUS_FILTER;
				args.add(status.name());
			}
		}

		if (!StringUtils.isEmpty(storeIds)) {
			query += String.format(SOLD_STORE_FILTER, storeIds);
		}

		if (deviceBrandId != null) {
			query += DEVICE_BRAND_FILTER;
			args.add(deviceBrandId);
		}

		if (deviceModelId != null) {
			query += DEVICE_MODEL_FILTER;
			args.add(deviceModelId);
		}

		if (ProtectPlusCertificateStatus.INACTIVE.equals(status)) {
			query += ORDER_BY_CREATED;
		} else {
			query += ORDER_BY_VALIDITY;
		}

		if (limit != null) {
			query += " limit ? ";
			args.add(limit);
		}

		Object[] argsArr = new Object[args.size()];
		argsArr = args.toArray(argsArr);

		return getJdbcTemplate().query(query, argsArr, getSearchResultRowMapper());
	}

	@Override
	public Integer getMaxCertificateNumber() {
		return getJdbcTemplate().queryForObject(SELECT_MAX_CERTIFICATE_NUMBER, Integer.class);
	}

	private void setNullableInteger(PreparedStatement statement, int index, Integer value) throws SQLException {
		if (value == null) {
			statement.setNull(index, Types.INTEGER);
		} else {
			statement.setInt(index, value);
		}
	}

	private void setNullableLong(PreparedStatement statement, int index, Long value) throws SQLException {
		if (value == null) {
			statement.setNull(index, Types.BIGINT);
		} else {
			statement.setLong(index, value);
		}
	}
}
