package com.kalafche.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.StockRelocationDao;
import com.kalafche.model.stock.StockRelocation;

@Service
public class StockRelocationDaoImpl extends JdbcDaoSupport implements
		StockRelocationDao {

	private static final String INSERT_STOCK_RELOCATION = "insert into stock_relocation (relocation_request_timestamp, employee_id, stock_id, quantity, from_kalafche_store_id, to_kalafche_store_id, approved, arrived, relocation_complete_timestamp, price, archived)"
			+ " values (?, ?, ?, ?, ?, ?, ? , ?, ?, ? * (select i.purchase_price from stock st join item i on st.item_id = i.id where st.id = ?), ?)";
	
	private static final String GET_STOCK_RELOCATIONS_BY_KALAFCHE_STORE = "select " +
			"sr.id, " +
			"sr.employee_id, " +
			"sr.stock_id, " +
			"sr.quantity, " +			
			"sr.approved, " +
			"sr.arrived, " +
			"sr.rejected, " +
			"sr.relocation_request_timestamp, " +
			"sr.relocation_complete_timestamp, " +
			"sr.to_kalafche_store_id, " +
			"sr.from_kalafche_store_id, " +
			"sr.archived, " +
			"i.price as itemPrice, " +
			"e.name as employee_name, " +
			"i.name as item_name, " +
			"i.product_code as item_product_code, " +
			"CONCAT(ks.city, \", \", ks.name) as from_kalafche_store_name, " +
			"ks.id as from_kalafche_store_id, " +
			"CONCAT(ks2.city, \", \", ks2.name) as to_kalafche_store_name, " +
			"ks2.id as to_kalafche_store_id, " +
			"dm.id as device_model_id, " +
			"dm.name as device_model_name, " +
			"db.id as device_brand_id, " +
			"db.name as device_brand_name " +
			"from stock_relocation sr " +
			"join employee e on sr.employee_id = e.id " +
			"join stock st on sr.stock_id = st.id " +
			"join item i on st.item_id = i.id " +
			"join kalafche_store ks on st.KALAFCHE_STORE_ID = ks.ID " +
			"join kalafche_store ks2 on sr.TO_KALAFCHE_STORE_ID = ks2.ID " +
			"join device_model dm on st.device_model_id = dm.id " +
			"join device_brand db on dm.device_brand_id = db.id " +
			"where archived = false";
	
	private static final String BY_TO_KALAFCHE_STORE_ID = " and sr.to_kalafche_store_id = ? ";
	
	private static final String BY_FROM_KALAFCHE_STORE_ID = " and sr.from_kalafche_store_id = ? ";
	
	private static final String APPROVE_STOCK_RELOCATION = "update stock_relocation set approved = true, rejected = false where id = ?";
	
	private static final String REJECT_STOCK_RELOCATION = "update stock_relocation set approved = false, rejected = true, relocation_complete_timestamp = ? where id = ?";

	private static final String UPDATE_STOCK_RELOCATION_ARRIVED = "update stock_relocation set arrived = true, relocation_complete_timestamp = ? where id = ?";

	private static final String ARCHIVE_STOCK_RELOCATION = "update stock_relocation set archived = true where id = ?";
	
	private static final String GET_TEST = "select " +
			"sr.id, " +
			"sr.employee_id, " +
			"sr.stock_id, " +
			"sr.quantity, " +			
			"sr.approved, " +
			"sr.arrived, " +
			"sr.rejected, " +
			"sr.relocation_request_timestamp, " +
			"sr.relocation_complete_timestamp, " +
			"sr.to_kalafche_store_id, " +
			"sr.archived, " +
			"i.price as itemPrice, " +
			"e.name as employee_name, " +
			"i.name as item_name, " +
			"i.product_code as item_product_code, " +
			"CONCAT(ks.city, \", \", ks.name) as from_kalafche_store_name, " +
			"ks.id as from_kalafche_store_id, " +
			"CONCAT(ks2.city, \", \", ks2.name) as to_kalafche_store_name, " +
			"ks2.id as to_kalafche_store_id, " +
			"dm.id as device_model_id, " +
			"dm.name as device_model_name, " +
			"db.id as device_brand_id, " +
			"db.name as device_brand_name " +
			"from stock_relocation sr " +
			"join employee e on sr.employee_id = e.id " +
			"join stock st on sr.stock_id = st.id " +
			"join item i on st.item_id = i.id " +
			"join kalafche_store ks on st.KALAFCHE_STORE_ID = ks.ID " +
			"join kalafche_store ks2 on sr.TO_KALAFCHE_STORE_ID = ks2.ID " +
			"join device_model dm on st.device_model_id = dm.id " +
			"join device_brand db on dm.device_brand_id = db.id " +
			"where RELOCATION_REQUEST_TIMESTAMP = RELOCATION_COMPLETE_TIMESTAMP and from_kalafche_store_id=3 and to_kalafche_store_id=2";
	
	private BeanPropertyRowMapper<StockRelocation> rowMapper;

	@Autowired
	public StockRelocationDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<StockRelocation> getRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<StockRelocation>(StockRelocation.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}

		return rowMapper;
	}

	@Override
	public List<StockRelocation> getAllStockRelocations() {
			return getJdbcTemplate().query(GET_STOCK_RELOCATIONS_BY_KALAFCHE_STORE, getRowMapper());
	}
	
	@Override
	public List<StockRelocation> getAllStockRelocationsTest() {
			return getJdbcTemplate().query(GET_TEST, getRowMapper());
	}
	
	@Override
	public List<StockRelocation> getIncomingStockRelocations(int kalafcheStoreId) {
			return getJdbcTemplate().query(GET_STOCK_RELOCATIONS_BY_KALAFCHE_STORE + BY_TO_KALAFCHE_STORE_ID,
					getRowMapper(), kalafcheStoreId);
	}
	
	@Override
	public List<StockRelocation> getOutgoingStockRelocations(int kalafcheStoreId) {
			return getJdbcTemplate().query(GET_STOCK_RELOCATIONS_BY_KALAFCHE_STORE + BY_FROM_KALAFCHE_STORE_ID,
					getRowMapper(), kalafcheStoreId);
	}

	@Override
	public void insertStockRelocation(StockRelocation stockRelocation) {
		getJdbcTemplate().update(INSERT_STOCK_RELOCATION,
				stockRelocation.getRelocationRequestTimestamp(),
				stockRelocation.getEmployeeId(), 
				stockRelocation.getStockId(),
				stockRelocation.getQuantity(),
				stockRelocation.getFromKalafcheStoreId(),
				stockRelocation.getToKalafcheStoreId(),
				stockRelocation.isApproved(), 
				stockRelocation.isArrived(),
				stockRelocation.getRelocationCompleteTimestamp(), 
				stockRelocation.getQuantity(), 
				stockRelocation.getStockId(), 
				stockRelocation.isArchived());				
	}

	@Override
	public void updateStockRelocationArrived(StockRelocation stockRelocation) {
		System.out.println(">>> " + stockRelocation.getId());
		getJdbcTemplate().update(UPDATE_STOCK_RELOCATION_ARRIVED, stockRelocation.getRelocationCompleteTimestamp(), stockRelocation.getId());		
	}

	@Override
	public void approveStockRelocation(Integer stockRelocationId) {
		getJdbcTemplate().update(APPROVE_STOCK_RELOCATION, stockRelocationId);		
	}
	
	@Override
	public void rejectStockRelocation(StockRelocation stockRelocation) {
		getJdbcTemplate().update(REJECT_STOCK_RELOCATION, stockRelocation.getRelocationCompleteTimestamp(), stockRelocation.getId());		
	}

	@Override
	public void archiveStockRelocation(Integer stockRelocationId) {
		getJdbcTemplate().update(ARCHIVE_STOCK_RELOCATION, stockRelocationId);	
	}

	
}
