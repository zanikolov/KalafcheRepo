package com.kalafche.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.DeviceTypeDao;
import com.kalafche.model.DeviceType;

@Service
public class DeviceTypeDaoImpl extends JdbcDaoSupport implements DeviceTypeDao {
	private static final String GET_ALL_DEVICE_TYPES_QUERY = "select * from device_type";
	private static final String INSERT_DEVICE_TYPE = "insert into device_type (name) values (?)";;

	private BeanPropertyRowMapper<DeviceType> rowMapper;
	
	@Autowired
	public DeviceTypeDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<DeviceType> getRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<DeviceType>(DeviceType.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return rowMapper;
	}
	
	@Override
	public List<DeviceType> getAllDeviceTypes() {
		List<DeviceType> deviceTypes = getJdbcTemplate().query(GET_ALL_DEVICE_TYPES_QUERY, getRowMapper());

		return deviceTypes;
	}
	
	@Override
	public void insertDeviceType(DeviceType type) {
		getJdbcTemplate().update(INSERT_DEVICE_TYPE, type.getName());
	}
}
