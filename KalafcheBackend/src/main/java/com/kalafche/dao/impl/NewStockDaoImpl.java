package com.kalafche.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.NewStockDao;
import com.kalafche.model.NewStock;

@Service
public class NewStockDaoImpl extends JdbcDaoSupport implements NewStockDao {
	private static final String GET_ALL_NEW_STOCK_QUERY = "select * from new_stock_vw ";
	
	private static final String BY_STORE_ID_CLAUSE = "where store_id = ? ";
	
	private static final String ORDER_BY_ID_CLAUSE = "order by id asc ";
	
	private static final String UPSERT_NEW_STOCK = "insert into new_stock "
			+ "(item_id, quantity, printed) values "
			+ "((select id from item where product_id = ? and device_model_id = ?), ?, false) "
			+ "on duplicate key update quantity = quantity + ?";

	private static final String DELETE_NEW_STOCK = "delete from new_stock where id = ?";

	private static final String INSERT_NEW_STOCK = "insert into new_stock(item_id, quantity, store_id) "
			+ " values ((select id from item where product_id = ? and device_model_id = ?), ?, ?);";
	
	private static final String INSERT_NEW_STOCK_FROM_FILE = "insert into new_stock(item_id, quantity, import_id, store_id) "
			+ " values ((select id from item where barcode = ?), ?, ?, ?);";

	private static final String UPDATE_NEW_STOCK = "update new_stock set quantity = quantity + ? "
			+ " where item_id = (select id from item where product_id = ? and device_model_id = ?)";
	
	private static final String INSERT_NEW_STOCK_IMPORT = "insert into new_stock_import(import_timestamp, employee_id, file_name) "
			+ " values (?, ?, ?)";

	private BeanPropertyRowMapper<NewStock> rowMapper;

	@Autowired
	public NewStockDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<NewStock> getRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<NewStock>(NewStock.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}

		return rowMapper;
	}

	@Override
	public List<NewStock> getAllNewStocks() {
		List<NewStock> newStocks = getJdbcTemplate().query(GET_ALL_NEW_STOCK_QUERY + ORDER_BY_ID_CLAUSE,
				getRowMapper());

		return newStocks;
	}
	
	@Override
	public void deleteNewStock(Integer newStockId) {
		getJdbcTemplate().update(DELETE_NEW_STOCK, newStockId);
	}
	
	@Override
	public void insertOrUpdateQuantityOfNewStock(Integer productId, Integer deviceModelId, Integer quantity) {
		getJdbcTemplate().update(UPSERT_NEW_STOCK, 
				productId, deviceModelId, quantity, quantity);	
	}
	
	@Override
	public void insertNewStock(Integer productId, Integer deviceModelId, Integer quantity, Integer storeId) {
		getJdbcTemplate().update(INSERT_NEW_STOCK, 
				productId, deviceModelId, quantity, storeId);	
	}
	
	@Override
	public void updateQuantityOfNewStock(Integer productId, Integer deviceModelId, Integer quantity) {
		getJdbcTemplate().update(UPDATE_NEW_STOCK, 
				quantity, productId, deviceModelId);	
	}

	@Override
	public void insertNewStockFromFile(String barcode, Integer quantity, Integer importId, Integer storeId) {
		getJdbcTemplate().update(INSERT_NEW_STOCK_FROM_FILE, 
				barcode, quantity, importId, storeId);			
	}
		
	@Override
	public Integer insertNewStockImport(Long importTimestamp, Integer employeeId, String fileName) throws SQLException {
		try (Connection connection = getDataSource().getConnection();
				PreparedStatement statement = connection.prepareStatement(
						INSERT_NEW_STOCK_IMPORT, Statement.RETURN_GENERATED_KEYS);) {
			statement.setLong(1, importTimestamp);
			statement.setInt(2, employeeId);
			statement.setString(3, fileName);

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException(
						"Creating new stock import failed, no rows affected.");
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1);
				} else {
					throw new SQLException(
							"Creating product failed, no ID obtained.");
				}
			}
		}
	}

	@Override
	public List<NewStock> getNewStockByStoreId(Integer storeId) {
		List<NewStock> newStocks = getJdbcTemplate().query(GET_ALL_NEW_STOCK_QUERY + BY_STORE_ID_CLAUSE + ORDER_BY_ID_CLAUSE,
				getRowMapper(), storeId);

		return newStocks;
	}

}
