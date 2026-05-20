package com.kalafche.dao.impl;

import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.LoyalCustomerDao;
import com.kalafche.model.LoyalCustomer;

@Service
public class LoyalCustomerDaoImpl extends JdbcDaoSupport implements LoyalCustomerDao {
	private static final String GET_ALL_LOYAL_CUSTOMERS_QUERY = "select " +
			"lc.id, lc.name, lc.phone_number, lc.email, lc.discount_code_id, lc.created_by as created_by_id, " +
			"lc.created_timestamp, lc.updated_by as updated_by_id, lc.last_update_timestamp, " +
			"e.name as created_by_name, ue.name as updated_by_name, dc.code as discount_code_code " +
			"from loyal_customer lc " +
			"left join discount_code dc on lc.discount_code_id = dc.id " +
			"left join employee e on lc.created_by = e.id " +
			"left join employee ue on lc.updated_by = ue.id ";
	private static final String CODE_CLAUSE = " where dc.code = ? ";
	private static final String ID_CLAUSE = " where lc.id = ? ";
	private static final String PHONE_OR_EMAIL_CLAUSE = " where lc.phone_number = ? or lc.email = ? order by lc.id desc ";
	private static final String INSERT_LOYAL_CUSTOMER = "insert into loyal_customer (name, phone_number, email, discount_code_id, created_by, created_timestamp)"
			+ " values (?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_LOYAL_CUSTOMER = "update loyal_customer set name = ?, phone_number = ?, email = ?, updated_by = ?, last_update_timestamp = ? where id = ?";
	private static final String CHECK_IF_LOYAL_CUSTOMER_CODE_EXISTS = "select count(*) from loyal_customer where discount_code_id = ? ";
	private static final String ID_NOT_CLAUSE = " and id <> ?";

	private BeanPropertyRowMapper<LoyalCustomer> rowMapper;

	@Autowired
	public LoyalCustomerDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}

	private BeanPropertyRowMapper<LoyalCustomer> getRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<LoyalCustomer>(LoyalCustomer.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}

		return rowMapper;
	}

	@Override
	public List<LoyalCustomer> getAllLoyalCustomers() {
		List<LoyalCustomer> loyalCustomers = getJdbcTemplate().query(
				GET_ALL_LOYAL_CUSTOMERS_QUERY, getRowMapper());

		return loyalCustomers;
	}

	@Override
	public LoyalCustomer getLoyalCustomerByCode(String loyalCustomerCode) {
		List<LoyalCustomer> loyalCustomers = getJdbcTemplate().query(
				GET_ALL_LOYAL_CUSTOMERS_QUERY + CODE_CLAUSE, getRowMapper(), loyalCustomerCode);

		return loyalCustomers.isEmpty() ? null : loyalCustomers.get(0);
	}

	@Override
	public LoyalCustomer getLoyalCustomerById(Integer id) {
		List<LoyalCustomer> loyalCustomers = getJdbcTemplate().query(
				GET_ALL_LOYAL_CUSTOMERS_QUERY + ID_CLAUSE, getRowMapper(), id);

		return loyalCustomers.isEmpty() ? null : loyalCustomers.get(0);
	}

	@Override
	public List<LoyalCustomer> getLoyalCustomersByPhoneNumberOrEmail(String phoneNumber, String email) {
		return getJdbcTemplate().query(
				GET_ALL_LOYAL_CUSTOMERS_QUERY + PHONE_OR_EMAIL_CLAUSE, getRowMapper(), phoneNumber, email);
	}

	@Override
	public Integer insertLoyalCustomer(LoyalCustomer loyalCustomer) {
		try (Connection connection = getDataSource().getConnection();
				PreparedStatement statement = connection.prepareStatement(
						INSERT_LOYAL_CUSTOMER, Statement.RETURN_GENERATED_KEYS);) {
			statement.setString(1, loyalCustomer.getName());
			statement.setString(2, loyalCustomer.getPhoneNumber());
			statement.setString(3, loyalCustomer.getEmail());
			if (loyalCustomer.getDiscountCodeId() == null) {
				statement.setNull(4, java.sql.Types.INTEGER);
			} else {
				statement.setInt(4, loyalCustomer.getDiscountCodeId());
			}
			statement.setInt(5, loyalCustomer.getCreatedById());
			statement.setLong(6, loyalCustomer.getCreatedTimestamp());

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Creating loyal customer failed, no rows affected.");
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1);
				} else {
					throw new SQLException("Creating loyal customer failed, no ID obtained.");
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Unable to create loyal customer.", exception);
		}
	}

	@Override
	public void updateLoyalCustomer(LoyalCustomer loyalCustomer) {
		getJdbcTemplate().update(UPDATE_LOYAL_CUSTOMER, loyalCustomer.getName(), loyalCustomer.getPhoneNumber(),
				loyalCustomer.getEmail(), loyalCustomer.getUpdatedById(), loyalCustomer.getLastUpdateTimestamp(),
				loyalCustomer.getId());
	}

	@Override
	public boolean checkIfLoyalCustomerDiscountCodeExists(LoyalCustomer loyalCustomer) {
		Integer exists = null;
		if (loyalCustomer.getId() == null) {
			exists = getJdbcTemplate().queryForObject(CHECK_IF_LOYAL_CUSTOMER_CODE_EXISTS, Integer.class, loyalCustomer.getDiscountCodeId());
		} else {
			exists = getJdbcTemplate().queryForObject(CHECK_IF_LOYAL_CUSTOMER_CODE_EXISTS + ID_NOT_CLAUSE, Integer.class, loyalCustomer.getDiscountCodeId(), loyalCustomer.getId());
		}

		return exists != null && exists > 0 ;
	}
}
