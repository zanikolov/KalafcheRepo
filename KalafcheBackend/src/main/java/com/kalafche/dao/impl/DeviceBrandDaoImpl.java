package com.kalafche.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.DeviceBrandDao;
import com.kalafche.model.DeviceBrand;

@Service
public class DeviceBrandDaoImpl extends JdbcDaoSupport implements
		DeviceBrandDao {
	private static final String GET_ALL_BRANDS_QUERY = "select * from device_brand";
	private static final String INSERT_BRAND = "insert into device_brand (name) values (?)";
	private static final String CHECK_IF_BRAND_EXISTS = "select count(*) from device_brand where name = ?";
	private static final String SELECT_DEVICE_BRAND = "select * from device_brand where id = ?";

	private BeanPropertyRowMapper<DeviceBrand> rowMapper;
	
	@Autowired
	public DeviceBrandDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<DeviceBrand> getRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<DeviceBrand>(DeviceBrand.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return rowMapper;
	}
	
	public List<DeviceBrand> getAllBrands() {		
		List<DeviceBrand> brands = getJdbcTemplate().query(GET_ALL_BRANDS_QUERY, getRowMapper());

		return brands;
	}

	@Override
	public void insertBrand(DeviceBrand brand) {
		getJdbcTemplate().update(INSERT_BRAND, brand.getName());
	}

	@Override
	public boolean checkIfDeviceBrandExists(DeviceBrand brand) {
		Integer exists = getJdbcTemplate().queryForObject(CHECK_IF_BRAND_EXISTS, Integer.class, brand.getName());
		
		return exists != null && exists > 0 ;
	}

	@Override
	public DeviceBrand selectDeviceBrand(Integer deviceBrandId) {
		List<DeviceBrand> deviceBrand = getJdbcTemplate().query(SELECT_DEVICE_BRAND, getRowMapper(), deviceBrandId);
		
		return deviceBrand.isEmpty() ? null : deviceBrand.get(0);
	}
}
