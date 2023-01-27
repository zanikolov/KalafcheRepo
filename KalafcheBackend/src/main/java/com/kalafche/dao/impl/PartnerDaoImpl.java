package com.kalafche.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.PartnerDao;
import com.kalafche.model.Partner;

@Service
public class PartnerDaoImpl extends JdbcDaoSupport implements PartnerDao {
	private static final String GET_ALL_PARTNERS_QUERY = "select p.*, CONCAT(ps.name, ', ', ps.city) as partner_store_name, dc.code as discount_code_code "
			+ " from partner p"
			+ " join partner_store ps on p.partner_store_id = ps.id "
			+ " join discount_code dc on p.discount_code_id = dc.id ";
	private static final String CODE_CLAUSE = " where p.code = ? ";
	private static final String INSERT_PARTNER = "insert into partner (name, partner_store_id, phone_number, discount_code_id)"
			+ " values (?, ?, ?, ?)";
	private static final String UPDATE_PARTNER = "update partner set name = ?, partner_store_id = ?, phone_number = ? where id = ?";
	private static final String CHECK_IF_PARTNER_NAME_EXISTS = "select count(*) from partner where name = ? and partner_store_id = ? ";
	private static final String CHECK_IF_PARTNER_CODE_EXISTS = "select count(*) from partner where discount_code_id = ? ";
	private static final String ID_NOT_CLAUSE = " and id <> ?";

	private BeanPropertyRowMapper<Partner> rowMapper;
	
	@Autowired
	public PartnerDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<Partner> getRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<Partner>(Partner.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return rowMapper;
	}
	
	@Override
	public List<Partner> getAllPartners() {
		List<Partner> partners = getJdbcTemplate().query(
				GET_ALL_PARTNERS_QUERY, getRowMapper());

		return partners;
	}

	@Override
	public Partner getPartnerByCode(String partnerCode) {
		List<Partner> partners = getJdbcTemplate().query(
				GET_ALL_PARTNERS_QUERY + CODE_CLAUSE, getRowMapper(), partnerCode);

		return partners.isEmpty() ? null : partners.get(0);
	}
	
	@Override
	public void insertPartner(Partner partner) {
		getJdbcTemplate().update(INSERT_PARTNER, partner.getName(), partner.getPartnerStoreId(), partner.getPhoneNumber(), partner.getDiscountCodeId());
	}

	@Override
	public void updatePartner(Partner partner) {
		getJdbcTemplate().update(UPDATE_PARTNER, partner.getName(), partner.getPartnerStoreId(), partner.getPhoneNumber(), partner.getId());
	}

	@Override
	public boolean checkIfPartnerNameExists(Partner partner) {
		Integer exists = null;
		if (partner.getId() == null) {
			exists = getJdbcTemplate().queryForObject(CHECK_IF_PARTNER_NAME_EXISTS, Integer.class, partner.getName(), partner.getPartnerStoreId());
		} else {
			exists = getJdbcTemplate().queryForObject(CHECK_IF_PARTNER_NAME_EXISTS + ID_NOT_CLAUSE, Integer.class, partner.getName(), partner.getPartnerStoreId(), partner.getId());
		}
			
		return exists != null && exists > 0 ;
	}

	@Override
	public boolean checkIfPartnerDiscountCodeExists(Partner partner) {
		Integer exists = null;
		if (partner.getId() == null) {
			exists = getJdbcTemplate().queryForObject(CHECK_IF_PARTNER_CODE_EXISTS, Integer.class, partner.getDiscountCodeId());
		} else {
			exists = getJdbcTemplate().queryForObject(CHECK_IF_PARTNER_CODE_EXISTS + ID_NOT_CLAUSE, Integer.class, partner.getDiscountCodeId(), partner.getId());
		}
			
		return exists != null && exists > 0 ;
	}
}
