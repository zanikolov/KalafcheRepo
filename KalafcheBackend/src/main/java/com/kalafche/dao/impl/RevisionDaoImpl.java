package com.kalafche.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.RevisionDao;
import com.kalafche.model.DeviceModel;
import com.kalafche.model.Employee;
import com.kalafche.model.Revision;
import com.kalafche.model.RevisionItem;
import com.kalafche.model.RevisionType;

@Service
public class RevisionDaoImpl extends JdbcDaoSupport implements RevisionDao {

	@Autowired
	StockDaoImpl stockDao;
	
	private static final String SELECT_REVISION_BY_ID = "select r.*, " +
			"st.name as store_name, " + 
			"rt.name as type_name, " + 
			"rt.code as type_code " + 
			"from revision r " +
			"join store st on r.store_id = st.id " +
			"join revision_type rt on r.type_id = rt.id " +
			"where r.id = ?";
	
	private static final String SELECT_CURRENT_REVISION_BY_STORE_ID = "select r.*, " +
			"st.id as store_id, " + 
			"CONCAT(st.city, ' ', st.name) as store_name, " +
			"rt.id as type_id, " + 			
			"rt.name as type_name, " + 
			"rt.code as type_code " + 
			"from revision r " +
			"join store st on r.store_id = st.id " +
			"join revision_type rt on r.type_id = rt.id " +
			"where store_id = ? and submit_timestamp is null";
	
	private static final String SELECT_LAST_REVISION_LAST_DEVICE_MODEL_BY_STORE = "select " +
	"coalesce(max(t.device_model_id),0) " +
	"from " +
	"( " +
	"   select " +
	"   ri1.device_model_id " +
	"   from revision_device_model ri1 " +
	"   join " +
	"   ( " +
	"      select " +
	"      max(submit_timestamp), " +
	"      r.id " +
	"      from revision_device_model ri2 " +
	"      join revision r on ri2.revision_id = r.id " +
	"      join store st on st.id = r.store_id " +
	"      join revision_type rt on rt.id = r.type_id " +
	"      where r.store_id = ? and rt.code = 'DAILY' " +
	"      group by r.store_id " +
	"   ) " +
	"   last_revision on last_revision.id = ri1.revision_id " +
	") t";
	
	private static final String INSERT_REVISION_DEVICE_MODEL = "insert into revision_device_model(revision_id, device_model_id) values (?, ?)";

	private static final String SELECT_ITEMS_FOR_REVISION = "select " +
			"item_id, " +
			"quantity as expected, " +
			"coalesce(psp.specific_price, iv.product_price) as product_price " +
			"from stock st " +
			"join item_vw iv on st.item_id = iv.id " +
			"left join product_specific_price psp on psp.product_id = iv.product_id and psp.store_id = ? " +
			"where st.store_id = ? ";
	
	private static final String DEVICE_MODEL_CLAUSE = "and iv.device_model_id in (%s) ";
	
	private static final String INSERT_REVISION_ITEM = "insert into revision_item(item_id, revision_id, expected, actual, product_price) values (?, ?, ?, ?, ?);";
	
	private static final String SELECT_REVISION_ITEMS_BY_REVISION_ID = "select " +
			"ri.id, " +
			"ri.revision_id, " +
			"ri.product_price, " +
			"iv.id as item_id, " +
			"iv.product_id, " +
			"iv.product_code, " +
			"iv.product_name, " +
			"iv.barcode, " + 
			"iv.device_model_id, " +
			"iv.device_model_name, " +
			"iv.device_brand_id, " +
			"ri.expected, " +
			"ri.actual " +
			"from revision_item ri " +
			"join item_vw iv on ri.item_id = iv.id " +
			"where ri.revision_id = ? ";
	
	private static final String SELECT_REVISION_ITEM_BY_ID = "select * from revision_item where id = ?";
	
	private static final String MISMATCHES_CLAUSE = "and ri.actual <> ri.expected ";
	
	private static final String BARCODE_CLAUSE = "and iv.barcode = ? ";
	
	private static final String INSERT_REVISION_EMPLOYEE = "insert into revision_employee(revision_id, employee_id) values (?, ?)";
	
	private static final String INSERT_REVISION = "insert into revision(create_timestamp, comment, store_id, type_id, created_by) values (?, ?, ?, ?, ?)";
	
	private static final String SELECT_REVISION_TYPE_CODE = "select code from revision_type where id = ?";
	
	private static final String SELECT_REVISER_EMPLOYEE_IDS_BY_REVISION_ID = "select employee_id from revision_employee where revision_id = ?;"; 
	
	private static final String SELECT_DEVICE_MODEL_IDS_BY_REVISION_ID = "select device_model_id from revision_device_model where revision_id = ?;"; 
	
	private static final String SELECT_REVISION_TYPES = "select * from revision_type";
	
	private static final String UPDATE_REVISION_ITEM_ACTUAL = "update revision_item set actual = actual + ? where id = ?";

	private static final String SUBMIT_REVISION = "update revision set submit_timestamp = ?, updated_by = ?, balance = ?, total_actual = ?, total_expected = ?, comment = ?, actual_synced = ? where id = ?;";
	
	private static final String SELECT_ALL_REVISIONS = "select " +
			"r.id, " +
			"r.submit_timestamp, " +
			"r.store_id, " +
			"CONCAT(ks.city, \", \", ks.name) as store_name, " +
			"r.balance, " +
			"r.total_actual, " +
			"r.total_expected " +
			"from revision r " +
			"join store ks on r.store_id = ks.id ";
	
	private static final String PERIOD_CLAUSE = " where r.submit_timestamp between ? and ? ";
	
	private static final String STORE_CLAUSE = " and r.store_id = ? ";
	
	private static final String ORDER_BY_CLAUSE = " order by r.submit_timestamp";

	private static final String SYNC_STOCK_MISMATCH = "update stock set quantity = quantity - ? where item_id = ? and store_id = ?";
	
	private BeanPropertyRowMapper<Revision> revisionRowMapper;
	
	private BeanPropertyRowMapper<RevisionItem> revisionItemRowMapper;
	
	private BeanPropertyRowMapper<RevisionType> revisionTypeRowMapper;
	
	@Autowired
	public RevisionDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<Revision> getRevisionRowMapper() {
		if (revisionRowMapper == null) {
			revisionRowMapper = new BeanPropertyRowMapper<Revision>(Revision.class);
			revisionRowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return revisionRowMapper;
	}
	
	private BeanPropertyRowMapper<RevisionItem> getRevisionItemRowMapper() {
		if (revisionItemRowMapper == null) {
			revisionItemRowMapper = new BeanPropertyRowMapper<RevisionItem>(RevisionItem.class);
			revisionItemRowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return revisionItemRowMapper;
	}
	
	private BeanPropertyRowMapper<RevisionType> getRevisionTypeRowMapper() {
		if (revisionTypeRowMapper == null) {
			revisionTypeRowMapper = new BeanPropertyRowMapper<RevisionType>(RevisionType.class);
			revisionTypeRowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return revisionTypeRowMapper;
	}
	
	@Override
	public Integer insertRevision(Revision revision) throws SQLException {	
		try (Connection connection = getDataSource().getConnection();
				PreparedStatement statement = connection.prepareStatement(
						INSERT_REVISION, Statement.RETURN_GENERATED_KEYS);) {
			statement.setLong(1, revision.getCreateTimestamp());
			statement.setString(2, revision.getComment());
			statement.setInt(3, revision.getStoreId());
			statement.setInt(4, revision.getTypeId());
			statement.setInt(5, revision.getCreatedByEmployeeId());

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException(
						"Creating the revision " + revision.getCreateTimestamp() + " failed, no rows affected.");
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1);
				} else {
					throw new SQLException(
							"Creating the revision " + revision.getCreateTimestamp() + " failed, no rows affected.");
				}
			}
		}	
	}

	@Override
	public void insertRevisers(Integer revisionId, List<Employee> revisers) {
		revisers.forEach(employee ->
		getJdbcTemplate().update(INSERT_REVISION_EMPLOYEE, revisionId, employee.getId()));
		
	}

	@Override
	public Revision getRevision(Integer revisionId) {
		return getJdbcTemplate().queryForObject(SELECT_REVISION_BY_ID, getRevisionRowMapper(), revisionId);
	}

	@Override
	public Integer getLastDeviceIdFromLastRevisionByStoreId(Integer storeId) {
		return getJdbcTemplate().queryForObject(SELECT_LAST_REVISION_LAST_DEVICE_MODEL_BY_STORE, Integer.class, storeId);
	}

	@Override
	public void insertRevisionDeviceModels(Integer revisionId, List<Integer> deviceModelIds) {
		deviceModelIds.forEach(
				deviceModelId -> getJdbcTemplate().update(INSERT_REVISION_DEVICE_MODEL, revisionId, deviceModelId));
	}

	@Override
	public void insertRevisionItems(Integer revisionId, List<RevisionItem> revisionItems) {
		revisionItems.forEach(revisionItem -> getJdbcTemplate().update(INSERT_REVISION_ITEM, revisionItem.getItemId(),
				revisionId, revisionItem.getExpected(), 0, revisionItem.getProductPrice()));
	}

	@Override
	public List<RevisionItem> getRevisionItemByRevisionId(Integer revisionId, Boolean onlyMismatches) {
		String searchQuery = SELECT_REVISION_ITEMS_BY_REVISION_ID;
		if (onlyMismatches) {
			searchQuery += MISMATCHES_CLAUSE;
		}
		return getJdbcTemplate().query(searchQuery, getRevisionItemRowMapper(), revisionId);
	}
	

	@Override
	public RevisionItem getRevisionItemById(Integer revisionItemId) {
		List<RevisionItem> result = getJdbcTemplate().query(SELECT_REVISION_ITEM_BY_ID, getRevisionItemRowMapper(), revisionItemId);
		
		return result.size() == 1 ? result.get(0) : null;
	}

	@Override
	public String selectRevisionTypeCode(Integer revisionTypeId) {
		List<String> result = getJdbcTemplate().queryForList(SELECT_REVISION_TYPE_CODE, String.class, revisionTypeId);
		
		return result.size() == 1 ? result.get(0) : null;
	}

	@Override
	public Revision getCurrentRevision(Integer storeId) {	
		List<Revision> result =  getJdbcTemplate().query(SELECT_CURRENT_REVISION_BY_STORE_ID, getRevisionRowMapper(), storeId);
		
		return result.size() == 1 ? result.get(0) : null;
	}

	@Override
	public List<Integer> getReviserEmployeeIds(Integer revisionId) {
		return getJdbcTemplate().queryForList(SELECT_REVISER_EMPLOYEE_IDS_BY_REVISION_ID, Integer.class, revisionId);
	}

	@Override
	public List<Integer> getDeviceModelIdByRevisionId(Integer revisionId) {
		return getJdbcTemplate().queryForList(SELECT_DEVICE_MODEL_IDS_BY_REVISION_ID, Integer.class, revisionId);

	}

	@Override
	public List<RevisionType> getAllRevisionTypes() {
		return getJdbcTemplate().query(SELECT_REVISION_TYPES, getRevisionTypeRowMapper());
	}

	@Override
	public RevisionItem selectRevisionItemByBarcode(Integer revisionId, String barcode) {
		List<RevisionItem> result = getJdbcTemplate().query(SELECT_REVISION_ITEMS_BY_REVISION_ID + BARCODE_CLAUSE, getRevisionItemRowMapper(), revisionId, barcode);
		return result.isEmpty() ? null : result.get(0);
	}

	@Override
	public void updateRevisionItemActual(Integer revisionItemId, Integer actualIncrement) {
		getJdbcTemplate().update(UPDATE_REVISION_ITEM_ACTUAL, actualIncrement, revisionItemId);
	}

	@Override
	public Integer insertNonExpectedRevisionItem(RevisionItem revisionItem) throws SQLException {
		try (Connection connection = getDataSource().getConnection();
				PreparedStatement statement = connection.prepareStatement(
						INSERT_REVISION_ITEM, Statement.RETURN_GENERATED_KEYS);) {
			statement.setInt(1, revisionItem.getItemId());
			statement.setInt(2, revisionItem.getRevisionId());
			statement.setInt(3, revisionItem.getExpected());
			statement.setInt(4, revisionItem.getActual());
			statement.setBigDecimal(5, revisionItem.getProductPrice());

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException(
						"Creating the revision item " + revisionItem.getRevisionId() + " " + revisionItem.getItemId() + " failed, no rows affected.");
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1);
				} else {
					throw new SQLException(
							"Creating the revision item " + revisionItem.getRevisionId() + " " + revisionItem.getItemId() + " failed, no rows affected.");
				}
			}
		}	
	}

	@Override
	public void submitRevision(Revision revision) {
		getJdbcTemplate().update(SUBMIT_REVISION, revision.getSubmitTimestamp(), revision.getUpdatedByEmployeeId(),
				revision.getBalance(), revision.getTotalActual(), revision.getTotalExpected(), revision.getComment(), revision.getActualSynced(),
				revision.getId());
	}

	@Override
	public List<Revision> selectRevisions(Long startDateMilliseconds, Long endDateMilliseconds, Integer storeId) {
		String searchQuery = SELECT_ALL_REVISIONS + PERIOD_CLAUSE;
		List<Object> argsList = new ArrayList<Object>();
		argsList.add(startDateMilliseconds);
		argsList.add(endDateMilliseconds);
		
		if (storeId != null && storeId != 0) {
			searchQuery += STORE_CLAUSE;
			argsList.add(storeId);
		}
		
		searchQuery += ORDER_BY_CLAUSE;
		
		Object[] argsArr = new Object[argsList.size()];
		argsArr = argsList.toArray(argsArr);

		return getJdbcTemplate().query(
				searchQuery, argsArr, getRevisionRowMapper());
	}

	@Override
	public List<RevisionItem> getItemsForRevision(Integer storeId, List<DeviceModel> deviceModels) {
		String commaSeparatedDeviceModelIds = deviceModels.stream().map(deviceModel -> deviceModel.getId().toString())
				.collect(Collectors.joining(","));

		return getJdbcTemplate().query(String.format(SELECT_ITEMS_FOR_REVISION + DEVICE_MODEL_CLAUSE, commaSeparatedDeviceModelIds),
				getRevisionItemRowMapper(), storeId, storeId);
	}

	@Override
	public void syncRevisionItemsActualWithStockQuantities(Integer storeId,
			List<RevisionItem> mismatchedRevisionItems) {
		mismatchedRevisionItems.forEach(revisionItem -> {
			Integer rowUpdated = getJdbcTemplate().update(SYNC_STOCK_MISMATCH, revisionItem.getExpected() - revisionItem.getActual(), revisionItem.getItemId(), storeId);
			
			if (rowUpdated == 0) {
				stockDao.insertOrUpdateQuantityOfInStock(revisionItem.getItemId(), storeId, revisionItem.getActual());
			}
		});
	}

}
