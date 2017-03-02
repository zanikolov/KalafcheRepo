package com.kalafche.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.DeviceModelDao;
import com.kalafche.model.device.DeviceModel;

@Service
public class DeviceModelDaoImpl extends JdbcDaoSupport implements
		DeviceModelDao {
	private static final String GET_ALL_MODELS_BY_BRAND_QUERY = "select * from device_model where device_brand_id = ?";
	private static final String GET_ALL_MODELS = "select * from device_model";
	private static final String INSERT_MODEL = "insert into device_model (name, device_brand_id, device_type_id) values (?, ?, ?)";

	private BeanPropertyRowMapper<DeviceModel> rowMapper;
	
	@Autowired
	public DeviceModelDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<DeviceModel> getRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<DeviceModel>(DeviceModel.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return rowMapper;
	}
	
	@Override
	public List<DeviceModel> getModelsByBrand(int brandId) {
		List<DeviceModel> models = getJdbcTemplate().query(GET_ALL_MODELS_BY_BRAND_QUERY, getRowMapper(), brandId);

		return models;
	}

	@Override
	public void insertModel(DeviceModel model) {
		getJdbcTemplate().update(INSERT_MODEL, model.getName(), model.getDeviceBrandId(), model.getDeviceTypeId());
	}

	@Override
	public List<DeviceModel> getAllDeviceModels() {
		List<DeviceModel> models = getJdbcTemplate().query(GET_ALL_MODELS, getRowMapper());
		
		return models;
	}
}
