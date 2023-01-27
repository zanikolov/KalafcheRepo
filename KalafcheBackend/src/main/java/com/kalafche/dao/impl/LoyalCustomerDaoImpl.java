package com.kalafche.dao.impl;

import java.util.List;

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
			"lc.*,db.id as device_brand_id, db.name as device_brand_name, dm.name as device_model_name, e.name as created_by_name, dc.code as discount_code_code " +
			"from loyal_customer lc " +
			"join device_model dm on lc.device_model_id = dm.id " +
			"join device_brand db on dm.device_brand_id = db.id " +
			"join discount_code dc on lc.discount_code_id = dc.id " +
			"join employee e on lc.created_by = e.id ";
	private static final String CODE_CLAUSE = " where lc.code = ? ";
	private static final String INSERT_LOYAL_CUSTOMER = "insert into loyal_customer (name, device_model_id, phone_number, discount_code_id, created_by, created_timestamp)"
			+ " values (?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_LOYAL_CUSTOMER = "update loyal_customer set name = ?, device_model_id = ?, phone_number = ? where id = ?";
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
	public void insertLoyalCustomer(LoyalCustomer loyalCustomer) {
		getJdbcTemplate().update(INSERT_LOYAL_CUSTOMER, loyalCustomer.getName(), loyalCustomer.getDeviceModelId(),
				loyalCustomer.getPhoneNumber(), loyalCustomer.getDiscountCodeId(),
				loyalCustomer.getCreatedById(), loyalCustomer.getCreatedTimestamp());
	}

	@Override
	public void updateLoyalCustomer(LoyalCustomer loyalCustomer) {
		getJdbcTemplate().update(UPDATE_LOYAL_CUSTOMER, loyalCustomer.getName(), loyalCustomer.getDeviceModelId(), loyalCustomer.getPhoneNumber(), loyalCustomer.getId());
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
