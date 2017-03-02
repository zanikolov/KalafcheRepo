package com.kalafche.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.PartnerStoreDao;
import com.kalafche.model.partner.PartnerStore;

@Service
public class PartnerStoreDaoImpl extends JdbcDaoSupport implements PartnerStoreDao {
	private static final String GET_ALL_PARTNER_STORES_QUERY = "select * from partner_store order by city, name";
	private static final String INSERT_PARTNER_STORE = "insert into partner_store (name, city) values (?, ?)";

	private BeanPropertyRowMapper<PartnerStore> rowMapper;
	
	@Autowired
	public PartnerStoreDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<PartnerStore> getRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<PartnerStore>(PartnerStore.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return rowMapper;
	}
	
	@Override
	public List<PartnerStore> getAllPartnerStores() {
		List<PartnerStore> partnerStores = getJdbcTemplate().query(
				GET_ALL_PARTNER_STORES_QUERY, getRowMapper());

		return partnerStores;
	}

	@Override
	public void insertPartnerStore(PartnerStore partnerStore) {
		getJdbcTemplate().update(INSERT_PARTNER_STORE, partnerStore.getName(), partnerStore.getCity());
	}

}
