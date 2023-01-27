package com.kalafche.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.RefundDao;
import com.kalafche.model.Refund;

@Service
public class RefundDaoImpl extends JdbcDaoSupport implements RefundDao {

	private static final String INSERT_REFUND = "insert into refund (employee_id, sale_item_id, description, create_timestamp)"
			+ " values (?, ?, ?, ?)";
	
	private static final String GET_REFUNDS_QUERY = "select " +
			"r.id, " +
			"r.sale_item_id, " +
			"si.sale_price as price, " +
			"r.description, " +
			"r.create_timestamp as timestamp, " +
			"s.sale_timestamp as saleTimestamp, " +
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
			"from refund r " +
			"join sale_item si on si.id = r.sale_item_id " +
			"join sale s on s.id = si.sale_id " +
			"join item_vw iv on iv.id = si.item_id " +
			"join store ks on ks.id = s.store_id " +
			"join employee e on e.id = r.employee_id ";
	
	private static final String PERIOD_CRITERIA_QUERY = " where r.create_timestamp between ? and ?";
	private static final String STORE_CRITERIA_QUERY = " and ks.id in (%s)";
	private static final String PRODUCT_CODE_QUERY = " and iv.product_code in (%s)";
	private static final String DEVICE_BRAND_QUERY = " and iv.device_brand_id = ?";
	private static final String DEVICE_MODEL_QUERY = " and iv.device_model_id = ?";	
	private static final String ORDER_BY = " order by r.create_timestamp";
	
	private BeanPropertyRowMapper<Refund> rowMapper;

	@Autowired
	public RefundDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<Refund> getRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<Refund>(Refund.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}

		return rowMapper;
	}
	
	@Override
	public void insertRefund(Refund refund) {
		getJdbcTemplate().update(INSERT_REFUND, refund.getEmployeeId(), refund.getSaleItemId(), refund.getDescription(),
				refund.getTimestamp());
	}
	
	@Override
	public List<Refund> searchRefunds(Long startDateMilliseconds,
			Long endDateMilliseconds, String storeIds, String productCode, Integer deviceBrandId, Integer deviceModelId) {
		String searchQuery = GET_REFUNDS_QUERY + PERIOD_CRITERIA_QUERY + String.format(STORE_CRITERIA_QUERY, storeIds);
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
