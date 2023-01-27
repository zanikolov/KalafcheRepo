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
import org.springframework.transaction.annotation.Transactional;

import com.kalafche.dao.OrderedStockDao;
import com.kalafche.dao.StockOrderDao;
import com.kalafche.exceptions.CommonException;
import com.kalafche.model.OrderedStock;

@Service
public class OrderedStockDaoImpl extends JdbcDaoSupport implements OrderedStockDao {
	
	@Autowired
	StockOrderDao stockOrderDao;
	
	//private static final String INSERT_ORDERED_STOCK_2 = "insert into ordered_stock(stock_order_id, product_id, device_model_id, quantity, create_timestamp, created_by) values (?, ?, ?, ?, ?, ?)";
	private static final String INSERT_ORDERED_STOCK = "insert into ordered_stock(stock_order_id, product_id, device_model_id, quantity, create_timestamp, created_by) values (?, ?, ?, ?, ?, ?) " +
			"on duplicate key update quantity = ?";
	private static final String GET_ALL_ORDERED_STOCK_FOR_STOCK_ORDER = "select " +
			"os.ID, " +
			"db.ID as device_brand_id, " +
			"db.NAME as device_brand_name, " +
			"dm.ID as device_model_id, " +
			"dm.NAME as device_model_name, " +
			"p.ID as product_id, " +
			"p.CODE as product_code, " +
			"p.NAME as product_name, " +
			"p.PRICE as product_price, " +
			"os.QUANTITY " +
			"from ordered_stock os " +
			"join device_model dm on os.DEVICE_MODEL_ID=dm.ID " +
			"join device_brand db on dm.DEVICE_BRAND_ID=db.ID " +
			"join product p on os.PRODUCT_ID=p.ID " +
			"where os.stock_order_id = ? " +
			"order by device_brand_name, device_model_name, quantity desc ";
	private static final String DELETE_ORDERED_STOCK = "delete from ordered_stock where id = ?";
	
	private BeanPropertyRowMapper<OrderedStock> rowMapper;
	
	@Autowired
	public OrderedStockDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<OrderedStock> getRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<OrderedStock>(OrderedStock.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return rowMapper;
	}
	
	
	@Override
	public int insertOrderedStock(OrderedStock orderedStock) throws CommonException, SQLException {
		try (Connection connection = getDataSource().getConnection();
				PreparedStatement statement = connection.prepareStatement(
						INSERT_ORDERED_STOCK, Statement.RETURN_GENERATED_KEYS);) {
			statement.setInt(1, orderedStock.getStockOrderId());
			statement.setInt(2, orderedStock.getProductId());
			statement.setInt(3, orderedStock.getDeviceModelId());
			statement.setInt(4, orderedStock.getQuantity());
			statement.setLong(5, orderedStock.getCreateTimestamp());
			statement.setInt(6, orderedStock.getCreatedBy());
			statement.setInt(7, orderedStock.getQuantity());

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				throw new CommonException(
						"Creating ordered stock failed, no rows affected.");
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1);
				} else {
					System.out.println("The ordered stock was updated.");
					return 0;
				}
			}
		}
	}

	@Override
	public List<OrderedStock> getAllOrderedStockForStockOrder(int orderId) {
		List<OrderedStock> orderedStocks = getJdbcTemplate().query(GET_ALL_ORDERED_STOCK_FOR_STOCK_ORDER, getRowMapper(), orderId);
		
		return orderedStocks;
	}

	@Override
	@Transactional
	public void deleteOrderedStock(int orderedStockId, int stockOrderId) {
		long currentTime = System.currentTimeMillis();
		
		stockOrderDao.updateStockOrderUpdateTimestamp(stockOrderId, null, currentTime);
		getJdbcTemplate().update(DELETE_ORDERED_STOCK, orderedStockId);
	}

}
