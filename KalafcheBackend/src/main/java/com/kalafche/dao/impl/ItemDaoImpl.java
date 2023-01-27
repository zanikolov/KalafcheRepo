package com.kalafche.dao.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.ItemDao;
import com.kalafche.model.Item;

@Service
public class ItemDaoImpl extends JdbcDaoSupport implements ItemDao {
	
	private static final String GET_ALL_ITEMS = "select * from item_vw";
	private static final String GET_ITEM = "select * from item where product_id = ? and device_model_id = ?";
	private static final String GET_ITEM_BY_BARCODE = "select * from item_vw where barcode = ?";
	private static final String GET_ITEM_BY_ID = "select * from item where id = ?";
	private static final String INSERT_ITEM = "insert into item(product_id, device_model_id, barcode) values (?, ?, ?)";
	private static final String UPDATE_ITEM = "update item set barcode = ?  where id = ?";
	private static final String UPDATE_ITEM_BY_PRODUCT_ID_AND_DEVICE_MODEL_ID = "update item set barcode = ?  where product_id = ? and device_model_id = ?";
	private static final String GET_ITEM_PRICE_BY_STORE = "select " +
			"coalesce " +
			"( " +
			"   ( " +
			"   	 select " +
			"      specific_price " +
			"      from product_specific_price " +
			"      where store_id = ? and product_id = (select product_id from item where id = ?)), " +
			"   ( " +
			"      select " +
			"      price " +
			"      from product " +
			"      where id = (select product_id from item where id = ?) " +
			"   ) " +
			") ";

	private BeanPropertyRowMapper<Item> rowMapper;
	
	@Autowired
	public ItemDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<Item> getRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<Item>(Item.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return rowMapper;
	}
	
	@Override
	public Item getItem(Integer productId, Integer deviceModelId) {
		List<Item> items = getJdbcTemplate().query(GET_ITEM, getRowMapper(), productId, deviceModelId);
		
		return items.isEmpty() ? null : items.get(0);
	}

	@Override
	public List<Item> getAllItems() {
		return getJdbcTemplate().query(GET_ALL_ITEMS, getRowMapper());
	}

	@Override
	public void insertItem(Integer productId, Integer deviceModelId, String barcode) {
		getJdbcTemplate().update(INSERT_ITEM, productId, deviceModelId, barcode);
	}

	@Override
	public Item getItem(String barcode) {
		List<Item> items = getJdbcTemplate().query(GET_ITEM_BY_BARCODE, getRowMapper(), barcode);
		
		return items.isEmpty() ? null : items.get(0);
	}

	@Override
	public void updateItemBarcode(Integer id, String barcode) {
		getJdbcTemplate().update(UPDATE_ITEM, barcode, id);
	}

	@Override
	public Item getItem(Integer id) {
		List<Item> items =  getJdbcTemplate().query(GET_ITEM_BY_ID, getRowMapper(), id);
		
		return items.isEmpty() ? null : items.get(0);
	}

	@Override
	public BigDecimal getItemPriceByStoreId(Integer itemId, Integer storeId) {
		return getJdbcTemplate().queryForObject(GET_ITEM_PRICE_BY_STORE, BigDecimal.class, storeId, itemId, itemId);
	}

	@Override
	public void updateItemBarcode(Integer productId, Integer deviceModelId, String barcode) {
		getJdbcTemplate().update(UPDATE_ITEM_BY_PRODUCT_ID_AND_DEVICE_MODEL_ID, barcode, productId, deviceModelId);	
	}

}
