package com.kalafche.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.StockOrderDao;
import com.kalafche.exceptions.CommonException;
import com.kalafche.model.Employee;
import com.kalafche.model.StockOrder;
import com.kalafche.service.EmployeeService;

@Service
public class StockOrderDaoImpl extends JdbcDaoSupport implements StockOrderDao {

	private static final String SELECT_UNCOMPLETED_STOCK_ORDER = "select * from stock_order where completed = false";
	private static final String INSERT_STOCK_ORDER = "insert into stock_order(arrived, completed, updated_by, update_timestamp, create_timestamp, created_by) values (?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_STOCK_ORDER_UPDATE_TIMESTAMP = "update stock_order set updated_by = ?, update_timestamp = ? where id = ?";
	
	private BeanPropertyRowMapper<StockOrder> rowMapper;
	
	@Autowired
	EmployeeService employeeService;
	
	@Autowired
	public StockOrderDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<StockOrder> getRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<StockOrder>(StockOrder.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return rowMapper;
	}
	
	@Override
	public StockOrder getCurrentStockOrder() throws CommonException {
		List<StockOrder> list = getJdbcTemplate().query(SELECT_UNCOMPLETED_STOCK_ORDER, getRowMapper());
		
		if (list == null || list.isEmpty()) {
			throw new CommonException("No current stock order. Please create new one");
		}
		
		if (list.size() > 1) {
			throw new CommonException("There are two uncompleted stock orders.");
		}
		
		return list.get(0);
	}

	@Override
	public void insertStockOrder() {
		Employee employee = employeeService.getLoggedInEmployee();
		long currentTime = System.currentTimeMillis(); 
		
		StockOrder stockOrder = new StockOrder(employee.getId(), currentTime, employee.getId(), currentTime, false, false);
		
		getJdbcTemplate().update(INSERT_STOCK_ORDER, stockOrder.isArrived(), stockOrder.isCompleted(), stockOrder.getUpdatedBy(), stockOrder.getUpdateTimestamp(), stockOrder.getCreateTimestamp(), stockOrder.getCreatedBy());
		
	}

	@Override
	public void updateStockOrderUpdateTimestamp(Integer orderId, Integer updater, Long updateTimestamp) {
		getJdbcTemplate().update(UPDATE_STOCK_ORDER_UPDATE_TIMESTAMP, updater, updateTimestamp, orderId);
		
	}

}
