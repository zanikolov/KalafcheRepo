package com.kalafche.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.RelocationDao;
import com.kalafche.enums.RelocationStatus;
import com.kalafche.model.Relocation;

@Service
public class RelocationDaoImpl extends JdbcDaoSupport implements
		RelocationDao {

	private static final String INSERT_RELOCATION = "insert into relocation "
			+ "(relocation_request_timestamp, employee_id, item_id, quantity, source_store_id, dest_store_id, status, archived, amount) "
			+ "values (?, ?, ?, ?, ?, ?, ?, false, ? * (select iv.product_purchase_price from item_vw iv where iv.id = ?))";

	private static final String GET_RELOCATIONS = "select " +
			"r.id, " +
			"r.employee_id, " +
			"r.item_id, " +
			"r.quantity, " +			
			"r.status, " +
			"r.relocation_request_timestamp, " +
			"r.relocation_complete_timestamp, " +
			"r.dest_store_id, " +
			"r.source_store_id, " +
			"r.archived, " +
			"iv.product_price, " +
			"e.name as employee_name, " +
			"iv.product_name, " +
			"iv.product_code, " +
			"CONCAT(ks.city, \", \", ks.name) as source_store_name, " +
			"ks.id as source_store_id, " +
			"ks.code as source_store_code, " +
			"CONCAT(ks2.city, \", \", ks2.name) as dest_store_name, " +
			"ks2.id as dest_store_id, " +
			"ks2.code as dest_store_code, " +
			"iv.device_model_id, " +
			"iv.device_model_name, " +
			"iv.device_brand_id " +
			"from relocation r " +
			"join employee e on r.employee_id = e.id " +
			"join item_vw iv on r.item_id = iv.id " +
			"join store ks on r.SOURCE_STORE_ID = ks.ID " +
			"join store ks2 on r.DEST_STORE_ID = ks2.ID " +
			"where archived = false ";
	
	private static final String ORDER_BY_CLAUSE = "order by r.relocation_request_timestamp desc ";
	
	private static final String BY_DEST_STORE_ID = " and r.dest_store_id = ? ";
	
	private static final String BY_SOURCE_STORE_ID = " and r.source_store_id = ? ";
	
	private static final String COMPLETED_CLAUSE = " and r.status in ('DELIVERED', 'REJECTED') ";

	private static final String UNCOMPLETED_CLAUSE = " and r.status in ('INITIATED', 'APPROVED', 'SENT') ";

	private static final String ARCHIVE_RELOCATION = "update relocation set archived = true where id = ?";

	private static final String UPDATE_RELOCATION_STATUS = "update relocation set status = ? where id = ?";
	
	private static final String UPDATE_RELOCATION_COMPLETE_TIMESTAMP = "update relocation set relocation_complete_timestamp = ? where id = ?";

	private static final String GET_RELOCATION_STATUS = "select status from relocation where id = ?";
	
	private BeanPropertyRowMapper<Relocation> rowMapper;

	@Autowired
	public RelocationDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<Relocation> getRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<Relocation>(Relocation.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}

		return rowMapper;
	}
	
	@Override
	public void insertRelocation(Relocation relocation) {
		getJdbcTemplate().update(INSERT_RELOCATION,
				relocation.getRelocationRequestTimestamp(),
				relocation.getEmployeeId(), 
				relocation.getItemId(),
				relocation.getQuantity(),
				relocation.getSourceStoreId(),
				relocation.getDestStoreId(),
				relocation.getStatus().toString(),	
				relocation.getQuantity(), 
				relocation.getItemId());			
	}
	
	@Override
	public void archiveStockRelocation(Integer relocationId) {
		getJdbcTemplate().update(ARCHIVE_RELOCATION, relocationId);	
	}

	@Override
	public void updateRelocationStatus(Integer relocationId, RelocationStatus status) {
		getJdbcTemplate().update(UPDATE_RELOCATION_STATUS, status.toString(), relocationId);		
	}
	
	@Override
	public void updateRelocationCompleteTimestamp(Integer relocationId, long completeTimestamp) {
		getJdbcTemplate().update(UPDATE_RELOCATION_COMPLETE_TIMESTAMP, completeTimestamp, relocationId);		
	}

	@Override
	public RelocationStatus getRelocationStatus(Integer relocationId) {
		return getJdbcTemplate().queryForObject(GET_RELOCATION_STATUS, new Object[] {relocationId}, RelocationStatus.class);
	}

	@Override
	public List<Relocation> getRelocations(Integer sourceStoreId, Integer destStoreId, boolean isCompleted) {
		String query = GET_RELOCATIONS;
		List<Object> argList = new ArrayList<>();
		
		if (sourceStoreId != null && sourceStoreId != 0) {
			query += BY_SOURCE_STORE_ID;
			argList.add(sourceStoreId);
		}
		
		if (destStoreId != null && destStoreId != 0) {
			query += BY_DEST_STORE_ID;
			argList.add(destStoreId);
		}
		
		if (isCompleted) {
			query += COMPLETED_CLAUSE;
		} else {
			query += UNCOMPLETED_CLAUSE;
		}
		
		query += ORDER_BY_CLAUSE;
		
		
		return getJdbcTemplate().query(query,
				getRowMapper(), argList.toArray(new Object[argList.size()]));
	}
	
}
