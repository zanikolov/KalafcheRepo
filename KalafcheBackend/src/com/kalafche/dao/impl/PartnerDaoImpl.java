package com.kalafche.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.PartnerDao;
import com.kalafche.model.partner.Partner;

@Service
public class PartnerDaoImpl extends JdbcDaoSupport implements PartnerDao {
	private static final String GET_ALL_PARTNERS_QUERY = "select p.*, CONCAT(ps.name, ', ', ps.city) as partner_store_name from partner p join partner_store ps on p.partner_store_id = ps.id";
	private static final String GET_PARTNER_BY_CODE_QUERY = "select p.*, CONCAT(ps.name, ' - ', ps.city) as partner_store_name from partner p join partner_store ps on p.partner_store_id = ps.id where p.code = ? ";
	private static final String INSERT_PARTNER = "insert into partner (code, name, partner_store_id, phone_number)"
			+ " values (?, ?, ?, ?)";

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
		Partner partner = null;
		try {
			partner = getJdbcTemplate().queryForObject(
					GET_PARTNER_BY_CODE_QUERY, getRowMapper(), partnerCode);
		} catch(EmptyResultDataAccessException e) {
			e.printStackTrace();
		}

		return partner;
	}
	
	@Override
	public void insertPartner(Partner partner) {
		getJdbcTemplate().update(INSERT_PARTNER, partner.getCode(), partner.getName(), partner.getPartnerStoreId(), partner.getPhoneNumber());
	}
}
