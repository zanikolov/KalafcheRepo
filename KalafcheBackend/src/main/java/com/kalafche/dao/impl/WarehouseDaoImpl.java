package com.kalafche.dao.impl;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.WarehouseDao;
import com.kalafche.model.Stock;

@Service
public class WarehouseDaoImpl extends JdbcDaoSupport implements WarehouseDao {

	private static final String UPSERT_APPROVED_IN_STOCK = "insert into stock "
			+ "(item_id, store_id, quantity, approved) values "
			+ "(?, (select id from store where code = 'RU_WH'), ?, true) "
			+ "on duplicate key update quantity = quantity + ?";
	
	private BeanPropertyRowMapper<Stock> rowMapper;

	@Autowired
	public WarehouseDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
//	private BeanPropertyRowMapper<Stock> getRowMapper() {
//		if (rowMapper == null) {
//			rowMapper = new BeanPropertyRowMapper<Stock>(Stock.class);
//			rowMapper.setPrimitivesDefaultedForNullValue(true);
//		}
//
//		return rowMapper;
//	}
	
	@Override
	public void upsertStock(Integer itemId, Integer quantity) {
		getJdbcTemplate().update(UPSERT_APPROVED_IN_STOCK, 
				itemId, quantity, quantity);	
	}
	
}
