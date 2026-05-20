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
import com.kalafche.model.protectplus.ProtectPlusCertificate;
import com.kalafche.model.protectplus.ProtectPlusCertificateSearchResult;
import com.kalafche.model.protectplus.ProtectPlusCertificateStatus;

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
			"ppc.confirmation_email_sent_timestamp, " +
			"ppc.gdpr_consent_file_id, " +
			"ppc.call_recording_file_id, " +
			"ppc.free_protector_used, " +
			"ppc.created_by as created_by_id, " +
			"ppc.created_timestamp, " +
			"ppc.updated_by as updated_by_id, " +
			"ppc.last_update_timestamp, " +
			"lc.name as customer_name, " +
			"lc.phone_number, " +
			"lc.email, " +
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

	private static final String BY_ID_CLAUSE = "where ppc.id = ? ";
	private static final String BY_NUMBER_CLAUSE = "where ppc.certificate_number = ? ";
	private static final String INSERT_CERTIFICATE = "insert into protect_plus_certificate " +
			"(certificate_number, loyal_customer_id, device_model_id, sold_store_id, sold_by_employee_id, sold_sale_id, " +
			"status, valid_from_timestamp, valid_until_timestamp, gdpr_consent_file_id, free_protector_used, created_by, created_timestamp) " +
			"values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String ACTIVATE_CERTIFICATE = "update protect_plus_certificate set status = ?, " +
			"loyal_customer_id = ?, device_model_id = ?, valid_from_timestamp = ?, valid_until_timestamp = ?, " +
			"activated_by = ?, activated_timestamp = ?, gdpr_consent_file_id = ?, last_update_timestamp = ?, updated_by = ? where id = ?";
	private static final String UPDATE_CERTIFICATE_USAGE = "update protect_plus_certificate set free_protector_used = ?, " +
			"valid_until_timestamp = ?, last_update_timestamp = ?, updated_by = ? where id = ?";
	private static final String UPDATE_CALL_RECORDING_FILE = "update protect_plus_certificate set call_recording_file_id = ?, last_update_timestamp = ?, updated_by = ? where id = ?";
	private static final String SELECT_MAX_CERTIFICATE_NUMBER = "select max(certificate_number) from protect_plus_certificate";
	private static final String CERTIFICATE_NUMBER_FILTER = " and ppc.certificate_number = ? ";
	private static final String PHONE_NUMBER_FILTER = " and lc.phone_number = ? ";
	private static final String STATUS_FILTER = " and ppc.status = ? ";
	private static final String SOLD_STORE_FILTER = " and ppc.sold_store_id in (%s) ";
	private static final String DEVICE_BRAND_FILTER = " and db.id = ? ";
	private static final String DEVICE_MODEL_FILTER = " and ppc.device_model_id = ? ";
	private static final String ORDER_BY_VALIDITY = " order by ppc.valid_until_timestamp desc ";
	private static final String ORDER_BY_CREATED = " order by ppc.created_timestamp desc ";

	private BeanPropertyRowMapper<ProtectPlusCertificate> certificateRowMapper;
	private BeanPropertyRowMapper<ProtectPlusCertificateSearchResult> searchResultRowMapper;

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
				statement.setInt(12, certificate.getCreatedById());
				statement.setLong(13, certificate.getCreatedTimestamp());
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
			Long validUntilTimestamp, Integer updatedById, Long lastUpdateTimestamp) {
		getJdbcTemplate().update(UPDATE_CERTIFICATE_USAGE, Boolean.TRUE.equals(freeProtectorUsed), validUntilTimestamp,
				lastUpdateTimestamp, updatedById, certificateId);
	}

	@Override
	public void updateCallRecordingFile(Integer certificateId, String callRecordingFileId, Integer updatedById,
			Long lastUpdateTimestamp) {
		getJdbcTemplate().update(UPDATE_CALL_RECORDING_FILE, callRecordingFileId, lastUpdateTimestamp, updatedById,
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

		String query = SELECT_CERTIFICATE + "where 1 = 1 ";
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
			query += STATUS_FILTER;
			args.add(status.name());
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
