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
import com.kalafche.model.device.DeviceModel;
import com.kalafche.model.employee.Employee;
import com.kalafche.model.revision.MissingRevisionItem;
import com.kalafche.model.revision.Revision;
import com.kalafche.model.revision.RevisionItem;
import com.kalafche.model.revision.RevisionType;

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
			"where store_id = ? and is_finalized is false";
	
	private static final String SELECT_LAST_REVISION_LAST_DEVICE_MODEL_BY_STORE = "SELECT " +
			 "    rdm.device_model_id " +
			 "FROM " +
			 "    revision_device_model rdm " +
			 "WHERE " +
			 "    revision_id = (SELECT " +
			 "            r.id " +
			 "        FROM " +
			 "            keysoo.revision r " +
			 "                JOIN " +
			 "            revision_type rt ON r.type_id = rt.id " +
			 "        WHERE " +
			 "            store_id = ? AND rt.code = 'DAILY' " +
			 "        ORDER BY SUBMIT_TIMESTAMP DESC " +
			 "        LIMIT 1) " +
			 "ORDER BY device_model_id DESC " +
			 "LIMIT 1;";
	
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
	
	private static final String PRODUCT_CODE_CLAUSE = "and iv.product_code = ? ";
	
	private static final String INSERT_REVISION_ITEM = "insert into revision_item(item_id, revision_id, expected, actual, product_price) values (?, ?, ?, ?, ?);";
	
	private static final String SELECT_REVISION_ITEMS = "select " +
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
			"join item_vw iv on ri.item_id = iv.id ";
	
	private static final String MISMATCHES_CLAUSE = "and ri.actual <> ri.expected ";
	
	private static final String BARCODE_CLAUSE = "and iv.barcode = ? ";
	
	private static final String RI_REVISION_ID_CLAUSE = "where ri.revision_id = ? ";
	
	private static final String REVISION_ITEM_ID_CLAUSE = "where ri.id = ? ";
	
	private static final String INSERT_REVISION_EMPLOYEE = "insert into revision_employee(revision_id, employee_id) values (?, ?)";
	
	private static final String INSERT_REVISION = "insert into revision(create_timestamp, comment, store_id, type_id, created_by, is_finalized, actual_synced) values (?, ?, ?, ?, ?, ?, ?)";
	
	private static final String SELECT_REVISION_TYPE_CODE = "select code from revision_type where id = ?";
	
	private static final String SELECT_REVISER_EMPLOYEE_IDS_BY_REVISION_ID = "select employee_id from revision_employee where revision_id = ?;"; 
	
	private static final String SELECT_DEVICE_MODEL_IDS_BY_REVISION_ID = "select device_model_id from revision_device_model where revision_id = ?;"; 
	
	private static final String SELECT_REVISION_TYPES = "select rt.id, rt.name, rt.code, rt.strategy from revision_type rt ";
	
	private static final String REVISION_TYPE_ID_CLAUSE = " where rt.id = ? ";
	
	private static final String REVISION_TYPE_CODE_CLAUSE = " where rt.code = ? ";
	
	private static final String UPDATE_REVISION_ITEM_ACTUAL = "update revision_item set actual = actual + ? where id = ?";

	private static final String FINALIZE_REVISION = "update revision set last_update_timestamp = ?, submit_timestamp = ?, updated_by = ?, shortage_amount = ?, shortage_count = ?, surplus_amount = ?, surplus_count = ?, absolute_amount_balance = ?, absolute_count_balance = ?, total_amount = ?, comment = ?, is_finalized = ? where id = ?";
	
	private static final String SELECT_ALL_REVISIONS = "select " +
			"r.id, " +
			"r.submit_timestamp, " +
			"r.store_id, " +
			"CONCAT(ks.city, \", \", ks.name) as store_name, " +
			"r.shortage_amount, " +
			"r.shortage_count, " +
			"r.surplus_amount, " +
			"r.surplus_count, " +
			"r.absolute_amount_balance, " +
			"r.absolute_count_balance, " +
			"r.total_amount " +
			"from revision r " +
			"join store ks on r.store_id = ks.id ";
	
	private static final String SELECT_LAST_REVISION_IDS_BY_STORE_ID = "select r.id from revision r join revision_type rt on r.type_id = rt.id where r.store_id = ? and rt.code != 'FULL' order by create_timestamp desc limit %d";
	
	private static final String SELECT_DEVICE_MODEL_IDS_BY_REVISION_IDS = "select device_model_id from revision_device_model where revision_id in (%s)";
	
	private static final String PERIOD_CLAUSE = " where r.submit_timestamp between ? and ? ";
	
	private static final String STORE_CLAUSE = " and r.store_id = ? ";
	
	private static final String REVISION_TYPE_CLAUSE = " and r.type_id = ? ";
	
	private static final String ORDER_BY_CLAUSE = " order by r.submit_timestamp";

	private static final String SYNC_STOCK_MISMATCH = "update stock set quantity = quantity - ? where item_id = ? and store_id = ?";
	
	private static final String SELECT_REVISION_WITH_ITEM_MISSING = "select "
			+ "ri.id, (ri.expected - ri.actual) as missing_count, "
			+ "ri.revision_id as revision_id, "
			+ "r.SUBMIT_TIMESTAMP as revision_date "
			+ "from revision_item ri join revision r on ri.revision_id = r.id "
			+ "where ri.revision_id in (select r1.id from revision r1 where r1.store_id = ?) and ri.expected > ri.actual and ri.item_id = ? and r.SUBMIT_TIMESTAMP > ? order by r.SUBMIT_TIMESTAMP desc";
	
	private BeanPropertyRowMapper<Revision> revisionRowMapper;
	
	private BeanPropertyRowMapper<RevisionItem> revisionItemRowMapper;
	
	private BeanPropertyRowMapper<RevisionType> revisionTypeRowMapper;
	
	private BeanPropertyRowMapper<MissingRevisionItem> missingRevisionItemRowMapper;
	
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
	
	private BeanPropertyRowMapper<MissingRevisionItem> getMissingRevisionItemRowMapper() {
		if (missingRevisionItemRowMapper == null) {
			missingRevisionItemRowMapper = new BeanPropertyRowMapper<MissingRevisionItem>(MissingRevisionItem.class);
			missingRevisionItemRowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return missingRevisionItemRowMapper;
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
			statement.setBoolean(6, revision.getIsFinalized());
			statement.setBoolean(7, revision.getActualSynced());

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
	public List<RevisionItem> getRevisionItemsByRevisionId(Integer revisionId, Boolean onlyMismatches) {
		String searchQuery = SELECT_REVISION_ITEMS + RI_REVISION_ID_CLAUSE;
		if (onlyMismatches) {
			searchQuery += MISMATCHES_CLAUSE;
		}
		return getJdbcTemplate().query(searchQuery, getRevisionItemRowMapper(), revisionId);
	}
	

	@Override
	public RevisionItem getRevisionItemById(Integer revisionItemId) {
		List<RevisionItem> result = getJdbcTemplate().query(SELECT_REVISION_ITEMS + REVISION_ITEM_ID_CLAUSE, getRevisionItemRowMapper(), revisionItemId);
		
		return result.size() == 1 ? result.get(0) : null;
	}

	@Override
	public String selectRevisionTypeCode(Integer revisionTypeId) {
		List<String> result = getJdbcTemplate().queryForList(SELECT_REVISION_TYPE_CODE, String.class, revisionTypeId);
		
		return result.size() == 1 ? result.get(0) : null;
	}
	
	@Override
	public RevisionType selectRevisionType(Integer revisionTypeId) {
		List<RevisionType> result = getJdbcTemplate().query(SELECT_REVISION_TYPES + REVISION_TYPE_ID_CLAUSE, getRevisionTypeRowMapper(), revisionTypeId);
		
		return result.size() == 1 ? result.get(0) : null;
	}
	
	@Override
	public RevisionType selectRevisionTypeByCode(String revisionTypeCode) {
		List<RevisionType> result = getJdbcTemplate().query(SELECT_REVISION_TYPES + REVISION_TYPE_CODE_CLAUSE, getRevisionTypeRowMapper(), revisionTypeCode);
		
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
		List<RevisionItem> result = getJdbcTemplate().query(SELECT_REVISION_ITEMS + RI_REVISION_ID_CLAUSE + BARCODE_CLAUSE, getRevisionItemRowMapper(), revisionId, barcode);
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
	public void finalizeRevision(Revision revision) {
		getJdbcTemplate().update(FINALIZE_REVISION, revision.getLastUpdateTimestamp(), revision.getSubmitTimestamp(),
				revision.getUpdatedByEmployeeId(), revision.getShortageAmount(), revision.getShortageCount(),
				revision.getSurplusAmount(), revision.getSurplusCount(), revision.getAbsoluteAmountBalance(),
				revision.getAbsoluteCountBalance(), revision.getTotalAmount(), revision.getComment(), revision.getIsFinalized(), revision.getId());
	}

	@Override
	public List<Revision> selectRevisions(Long startDateMilliseconds, Long endDateMilliseconds, Integer storeId, Integer typeId) {
		String searchQuery = SELECT_ALL_REVISIONS + PERIOD_CLAUSE;
		List<Object> argsList = new ArrayList<Object>();
		argsList.add(startDateMilliseconds);
		argsList.add(endDateMilliseconds);
		
		if (storeId != null && storeId != 0) {
			searchQuery += STORE_CLAUSE;
			argsList.add(storeId);
		}
		
		if (typeId != null && typeId != 0) {
			searchQuery += REVISION_TYPE_CLAUSE;
			argsList.add(typeId);
		}
		
		searchQuery += ORDER_BY_CLAUSE;
		
		Object[] argsArr = new Object[argsList.size()];
		argsArr = argsList.toArray(argsArr);

		return getJdbcTemplate().query(
				searchQuery, argsArr, getRevisionRowMapper());
	}

	@Override
	public List<RevisionItem> getItemsForRevision(Integer storeId, List<DeviceModel> deviceModels, String productCode) {
		String commaSeparatedDeviceModelIds = deviceModels.stream().map(deviceModel -> deviceModel.getId().toString())
				.collect(Collectors.joining(","));
		String searchQuery = String.format(SELECT_ITEMS_FOR_REVISION + DEVICE_MODEL_CLAUSE,
				commaSeparatedDeviceModelIds);

		List<Object> argsList = new ArrayList<Object>();
		argsList.add(storeId);
		argsList.add(storeId);

		if (productCode != null) {
			searchQuery += PRODUCT_CODE_CLAUSE;
			argsList.add(productCode);
		}

		Object[] argsArr = new Object[argsList.size()];
		argsArr = argsList.toArray(argsArr);

		return getJdbcTemplate().query(searchQuery, argsArr, getRevisionItemRowMapper());
	}

	@Override
	public void syncRevisionItemsActualWithStockQuantities(Integer revisionId, Integer storeId,
			List<RevisionItem> mismatchedRevisionItems) {
		mismatchedRevisionItems.forEach(revisionItem -> {
			Integer rowUpdated = getJdbcTemplate().update(SYNC_STOCK_MISMATCH, revisionItem.getExpected() - revisionItem.getActual(), revisionItem.getItemId(), storeId);
			
			if (rowUpdated == 0) {
				stockDao.insertOrUpdateQuantityOfInStock(revisionItem.getItemId(), storeId, revisionItem.getActual());
			}
		});
	}
	
	@Override
	public List<Integer> getDeviceModelIdsFromLastRevisionByStore(Integer storeId, int lastRevisionsCount) {
		String query = String.format(SELECT_LAST_REVISION_IDS_BY_STORE_ID, lastRevisionsCount);
		List<Integer> lastRevisionIds = getJdbcTemplate().queryForList(query, Integer.class, storeId);
		
		if (lastRevisionIds != null && !lastRevisionIds.isEmpty()) {
			String commaSeparatedLastRevisionIds = lastRevisionIds.stream().map(String::valueOf).collect(Collectors.joining(","));			
			return getJdbcTemplate().queryForList(String.format(SELECT_DEVICE_MODEL_IDS_BY_REVISION_IDS, commaSeparatedLastRevisionIds), Integer.class);		
		}
		return new ArrayList<Integer>();
	}

	@Override
	public List<MissingRevisionItem> findLastRevisionTheItemIsMissing(Integer itemId, Integer storeId, Long submitDate) {
		return getJdbcTemplate().query(SELECT_REVISION_WITH_ITEM_MISSING, getMissingRevisionItemRowMapper(), storeId, itemId, submitDate);
	}

}
