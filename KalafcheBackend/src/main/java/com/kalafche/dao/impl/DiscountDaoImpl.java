package com.kalafche.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.DiscountDao;
import com.kalafche.model.DiscountCampaign;
import com.kalafche.model.DiscountCode;
import com.kalafche.model.DiscountType;

@Service
public class DiscountDaoImpl extends JdbcDaoSupport implements DiscountDao {
	
	private static final String INSERT_DISCOUNT_CAMPAIGN = "INSERT INTO discount_campaign (CREATE_TIMESTAMP, CREATED_BY, NAME, DESCRIPTION, DISCOUNT_TYPE_ID, DISCOUNT_VALUE, CODE) " +
			"VALUES (?, ?, ?, ?, ?, ?, ?); ";
	
	private static final String SELECT_DISCOUNT_CAMPAIGN = "select dc.*, " +
			"dt.code as discount_type_code, " +
			"dt.name as discount_type_name, " +
			"dt.sign as discount_type_sign " +
			"from discount_campaign dc " +
			"join discount_type dt on dc.discount_type_id = dt.id ";
	
	private static final String BY_ID_CLAUSE = "where dc.id = ? ";
	
	private static final String SELECT_DISCOUNT_TYPES = "select * from discount_type ";
	
	private static final String INSERT_DISCOUNT_CODE = "INSERT INTO discount_code (CODE, DISCOUNT_CAMPAIGN_ID, ACTIVE) VALUES (?, ?, ?); ";

	private static final String SELECT_DISCOUNT_CODE = "select dco.*, " +
					"dca.name as discount_campaign_name, " +
					"dca.code as discount_campaign_code, " +
					"dca.discount_value, " +
					"dt.id as discount_type_id, " +
					"dt.code as discount_type_code, " +
					"dt.sign as discount_type_sign, " +
					"p.id as partner_id, " +
					"p.name as partner_name, " +
					"concat(ps.city, ', ', ps.name) as partner_store_name, " +
					"lc.id as loyal_customer_id, " +
					"lc.name as loyal_customer_name " +
					"from discount_code dco " +
					"join discount_campaign dca on dco.discount_campaign_id = dca.id " +
					"join discount_type dt on dca.discount_type_id = dt.id " +
					"left join partner p on p.discount_code_id = dco.id " +
					"left join partner_store ps on p.partner_store_id = ps.id " +
					"left join loyal_customer lc on lc.discount_code_id = dco.id ";
	
	private static final String BY_DCO_ID_CLAUSE = "where dco.code = ? ";
	
	private static final String UPDATE_DISCOUNT_CAMPAIGN = "update discount_campaign set description = ?, discount_value = ?, discount_type_id = ? where id = ?";
	
	private static final String CHECK_IF_DISCOUNT_CODE_EXISTS = "select count(*) from discount_code where code = ?";
	
	private static final String BY_NO_ID_CLAUSE = " and id <> ?";
	
	private static final String UPDATE_DISCOUNT_CODE = "update discount_code set active = ? where id = ?";
	
	private static final String SELECT_AVAILABLE_PARTNER_CODES = "select * from discount_code where id not in (select coalesce(0, discount_code_id) from partner) and discount_campaign_id = (select id from discount_campaign where code = 'PARTNER') ";		
	
	private static final String SELECT_AVAILABLE_LOYAL_CODES = "select * from discount_code where id not in (select coalesce(0, discount_code_id) from loyal_customer) and discount_campaign_id = (select id from discount_campaign where code = 'LOYAL') ";	
	
	private BeanPropertyRowMapper<DiscountCampaign> discountCampaignRowMapper;
	
	private BeanPropertyRowMapper<DiscountCode> discountCodeRowMapper;
	
	private BeanPropertyRowMapper<DiscountType> discountTypeRowMapper;
	
	@Autowired
	public DiscountDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<DiscountCampaign> getDiscountCampaignRowMapper() {
		if (discountCampaignRowMapper == null) {
			discountCampaignRowMapper = new BeanPropertyRowMapper<DiscountCampaign>(DiscountCampaign.class);
			discountCampaignRowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return discountCampaignRowMapper;
	}
	
	private BeanPropertyRowMapper<DiscountCode> getDiscountCodeRowMapper() {
		if (discountCodeRowMapper == null) {
			discountCodeRowMapper = new BeanPropertyRowMapper<DiscountCode>(DiscountCode.class);
			discountCodeRowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return discountCodeRowMapper;
	}
	
	private BeanPropertyRowMapper<DiscountType> getDiscountTypeRowMapper() {
		if (discountTypeRowMapper == null) {
			discountTypeRowMapper = new BeanPropertyRowMapper<DiscountType>(DiscountType.class);
			discountTypeRowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return discountTypeRowMapper;
	}
	
	@Override
	public Integer insertDiscountCampaign(DiscountCampaign discountCampaign) throws SQLException {
		try (Connection connection = getDataSource().getConnection();
				PreparedStatement statement = connection.prepareStatement(
						INSERT_DISCOUNT_CAMPAIGN, Statement.RETURN_GENERATED_KEYS);) {
			statement.setLong(1, discountCampaign.getCreateTimestamp());
			statement.setInt(2, discountCampaign.getCreatedByEmployeeId());
			statement.setString(3, discountCampaign.getName());
			statement.setString(4, discountCampaign.getDescription());
			statement.setInt(5, discountCampaign.getDiscountTypeId());
			statement.setString(6, discountCampaign.getDiscountValue());
			statement.setString(7, discountCampaign.getCode());

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException(
						"Creating the discount campaign " + discountCampaign.getCreateTimestamp() + " failed, no rows affected.");
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1);
				} else {
					throw new SQLException(
							"Creating the discount campaign " + discountCampaign.getCreateTimestamp() + " failed, no rows affected.");
				}
			}
		}
	}

	@Override
	public DiscountCampaign selectDiscountCampaignById(Integer discountCampaignId) {
		List<DiscountCampaign> result = getJdbcTemplate().query(SELECT_DISCOUNT_CAMPAIGN + BY_ID_CLAUSE, getDiscountCampaignRowMapper(), discountCampaignId);
		
		return result.isEmpty() ? null : result.get(0);
	}
	
	@Override
	public List<DiscountCampaign> selectAllDiscountCampaigns() {
		return getJdbcTemplate().query(SELECT_DISCOUNT_CAMPAIGN, getDiscountCampaignRowMapper());
	}

	@Override
	public List<DiscountType> selectDiscountTypes() {
		return getJdbcTemplate().query(SELECT_DISCOUNT_TYPES, getDiscountTypeRowMapper());
	}

	@Override
	public Integer insertDiscountCode(DiscountCode discountCode) throws SQLException {
		try (Connection connection = getDataSource().getConnection();
				PreparedStatement statement = connection.prepareStatement(
						INSERT_DISCOUNT_CODE, Statement.RETURN_GENERATED_KEYS);) {
			statement.setInt(1, discountCode.getCode());
			statement.setInt(2, discountCode.getDiscountCampaignId());
			statement.setBoolean(3, discountCode.getActive());

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException(
						"Creating the discount code " + discountCode.getCode() + " failed, no rows affected.");
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1);
				} else {
					throw new SQLException(
							"Creating the discount code " + discountCode.getCode() + " failed, no rows affected.");
				}
			}
		}
	}
	
	@Override
	public DiscountCode selectDiscountCode(Integer code) {
		List<DiscountCode> result = getJdbcTemplate().query(SELECT_DISCOUNT_CODE + BY_DCO_ID_CLAUSE, getDiscountCodeRowMapper(), code);
		return result.isEmpty() ? null : result.get(0);
	}

	@Override
	public void updateDiscountCampaign(DiscountCampaign discountCampaign) {
		getJdbcTemplate().update(UPDATE_DISCOUNT_CAMPAIGN, discountCampaign.getDescription(), discountCampaign.getDiscountValue(), discountCampaign.getDiscountTypeId(), discountCampaign.getId());
	}
	
	@Override
	public Boolean checkIfDiscountCodeExists(DiscountCode discountCode) {
		Integer exists = null;
		if (discountCode.getId() == null) {
			exists = getJdbcTemplate().queryForObject(CHECK_IF_DISCOUNT_CODE_EXISTS, Integer.class, discountCode.getCode());
		} else {
			exists = getJdbcTemplate().queryForObject(CHECK_IF_DISCOUNT_CODE_EXISTS + BY_NO_ID_CLAUSE, Integer.class, discountCode.getCode(), discountCode.getId());
		}
			
		return exists != null && exists > 0 ;
	}

	@Override
	public void updateDiscountCode(DiscountCode discountCode) {
		getJdbcTemplate().update(UPDATE_DISCOUNT_CODE, discountCode.getActive(), discountCode.getId());		
	}

	@Override
	public List<DiscountCode> selectAllDiscountCodes() {
		return getJdbcTemplate().query(SELECT_DISCOUNT_CODE, getDiscountCodeRowMapper());
	}

	@Override
	public List<DiscountCode> selectAvailableDiscountCodesForPartnerCampaign() {
		return getJdbcTemplate().query(SELECT_AVAILABLE_PARTNER_CODES, getDiscountCodeRowMapper());
	}

	@Override
	public List<DiscountCode> selectAvailableDiscountCodesForLoyalCampaign() {
		return getJdbcTemplate().query(SELECT_AVAILABLE_LOYAL_CODES, getDiscountCodeRowMapper());
	}

}
