package com.kalafche.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.WasteDao;
import com.kalafche.model.Waste;

@Service
public class WasteDaoImpl extends JdbcDaoSupport implements WasteDao {

	private static final String INSERT_WASTE = "insert into waste (employee_id, store_id, item_id, price, description, create_timestamp, file_id)"
			+ " values (?, ?, ?, ?, ?, ?, ?)";
	
	private static final String GET_WASTES_QUERY = "select " +
			"w.id, " +
			"w.item_id, " +
			"w.price, " +
			"w.description, " +
			"w.create_timestamp as timestamp, " +
			"w.file_id, " +
			"iv.product_id, " +
			"iv.product_code, " +
			"iv.product_name, " +
			"iv.device_model_id, " +
			"iv.device_model_name, " +
			"iv.device_brand_id, " +
			"e.id as employee_id, " +
			"e.name as employee_name, " +
			"ks.id as store_id, " +
			"CONCAT(ks.city, ', ', ks.name) as store_name " +
			"from waste w " +
			"join item_vw iv on iv.id = w.item_id " +
			"join store ks on ks.id = w.store_id " +
			"join employee e on e.id = w.employee_id ";
	
	private static final String PERIOD_CRITERIA_QUERY = " where create_timestamp between ? and ?";
	private static final String STORE_CRITERIA_QUERY = " and ks.id in (%s)";
	private static final String PRODUCT_CODE_QUERY = " and iv.product_code in (%s)";
	private static final String DEVICE_BRAND_QUERY = " and iv.device_brand_id = ?";
	private static final String DEVICE_MODEL_QUERY = " and iv.device_model_id = ?";	
	private static final String ORDER_BY = " order by w.create_timestamp";

	
	private BeanPropertyRowMapper<Waste> rowMapper;

	@Autowired
	public WasteDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<Waste> getRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<Waste>(Waste.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}

		return rowMapper;
	}

	@Override
	public void insertWaste(Waste waste) {
		getJdbcTemplate().update(INSERT_WASTE, waste.getEmployeeId(), waste.getStoreId(), waste.getItemId(), 
				waste.getPrice(), waste.getDescription(), waste.getTimestamp(), waste.getFileId());
	}
	
	@Override
	public List<Waste> searchWastes(Long startDateMilliseconds,
			Long endDateMilliseconds, String storeIds, String productCode, Integer deviceBrandId, Integer deviceModelId) {
		String searchQuery = GET_WASTES_QUERY + PERIOD_CRITERIA_QUERY + String.format(STORE_CRITERIA_QUERY, storeIds);
		List<Object> argsList = new ArrayList<Object>();
		argsList.add(startDateMilliseconds);
		argsList.add(endDateMilliseconds);
		
		searchQuery += addDetailedSearch(productCode, deviceBrandId, deviceModelId, argsList);
		
		searchQuery += ORDER_BY;
		
		Object[] argsArr = new Object[argsList.size()];
		argsArr = argsList.toArray(argsArr);
		
		return getJdbcTemplate().query(
				searchQuery, argsArr, getRowMapper());
	}
	
	private String addDetailedSearch(String productCode, Integer deviceBrandId, Integer deviceModelId, List<Object> args) {
		String detailedQuery = "";
		if (productCode != null && productCode != "") {
			detailedQuery += String.format(PRODUCT_CODE_QUERY, productCode);
		}
		
		if (deviceBrandId != null) {
			detailedQuery += DEVICE_BRAND_QUERY;
			args.add(deviceBrandId);
		}
		
		if (deviceModelId != null) {
			detailedQuery += DEVICE_MODEL_QUERY;
			args.add(deviceModelId);
		}
		
		return detailedQuery;
	}

}
