package com.kalafche.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.kalafche.dao.StockDao;
import com.kalafche.model.stock.Stock;

@Service
public class StockDaoImpl extends JdbcDaoSupport implements StockDao {
	private static final String GET_ALL_STOCKS_QUERY = "select * from stock";
	
	private static final String GET_UNAPPROVED_STOCKS_BY_KALAFCHE_STORE_ID = "select " +
			"s.ID, " +
			"db.ID as device_brand_id, " +
			"db.NAME as device_brand_name, " +
			"dm.ID as device_model_id, " +
			"dm.NAME as device_model_name, " +
			"i.ID as item_id, " +
			"i.PRODUCT_CODE as item_product_code, " +
			"i.NAME as item_name, " +
			"i.description as item_description, " +
			"i.PRICE as item_price, " +
			"ks.ID as kalafche_store_id, " +
			"CONCAT(ks.CITY, \", \", ks.NAME) as kalafche_store_name, " +
			"s.QUANTITY, " +
			"s.approved, " +
			"s.approver, " +
			"coalesce(s2.quantity, 0) as quantity_in_stock " +
			"from stock s " +
			"join device_model dm on s.DEVICE_MODEL_ID=dm.ID " +
			"join device_brand db on dm.DEVICE_BRAND_ID=db.ID " +
			"join kalafche_store ks on s.KALAFCHE_STORE_ID=ks.ID " +
			"join item i on s.ITEM_ID=i.ID " +
			"left join (select id, device_model_id, item_id, KALAFCHE_STORE_ID, quantity from stock where approved = true) s2 on s.device_model_id = s2.device_model_id and s.item_id = s2.item_id and s.KALAFCHE_STORE_ID = s2.KALAFCHE_STORE_ID " +
			"where s.approved is false " +
			"and s.kalafche_store_id = ? ";
	
	private static final String GET_ALL_UNAPPROVED_STOCKS = "select " +
			"s.ID, " +
			"db.ID as device_brand_id, " +
			"db.NAME as device_brand_name, " +
			"dm.ID as device_model_id, " +
			"dm.NAME as device_model_name, " +
			"i.ID as item_id, " +
			"i.PRODUCT_CODE as item_product_code, " +
			"i.NAME as item_name, " +
			"i.description as item_description, " +
			"i.PRICE as item_price, " +
			"ks.ID as kalafche_store_id, " +
			"CONCAT(ks.CITY, \", \", ks.NAME) as kalafche_store_name, " +
			"s.QUANTITY, " +
			"s.approved, " +
			"s.approver, " +
			"coalesce(s2.quantity, 0) as quantity_in_stock " +
			"from stock s " +
			"join device_model dm on s.DEVICE_MODEL_ID=dm.ID " +
			"join device_brand db on dm.DEVICE_BRAND_ID=db.ID " +
			"join kalafche_store ks on s.KALAFCHE_STORE_ID=ks.ID " +
			"join item i on s.ITEM_ID=i.ID " +
			"left join (select id, device_model_id, item_id, KALAFCHE_STORE_ID, quantity from stock where approved = true) s2 on s.device_model_id = s2.device_model_id and s.item_id = s2.item_id and s.KALAFCHE_STORE_ID = s2.KALAFCHE_STORE_ID " +
			"where s.approved is false ";
	
	private static final String INSERT_APPROVED_STOCK = "insert into stock "
			+ "(device_model_id, item_id, kalafche_store_id, quantity, approved, approver) values "
			+ "(?, ?, ?, ?, ?, ?) "
			+ "on duplicate key update quantity = quantity + ?";
	
	private static final String INSERT_STOCK_FOR_APPROVAL = "insert into stock "
			+ "(device_model_id, item_id, kalafche_store_id, quantity, approved, approver) values "
			+ "(?, ?, ?, ?, ?, ?);";
	
	private static final String DELETE_STOCK_FOR_APPROVAL = "delete from stock where id = ?";

	private static final String GET_ALL_APPROVED_STOCKS = "select " +
			"s.ID, " +
			"db.ID as device_brand_id, " +
			"db.NAME as device_brand_name, " +
			"dm.ID as device_model_id, " +
			"dm.NAME as device_model_name, " +
			"i.ID as item_id, " +
			"i.PRODUCT_CODE as item_product_code, " +
			"i.NAME as item_name, " +
			"i.description as item_description, " +
			"i.PRICE as item_price, " +
			"ks.ID as kalafche_store_id, " +
			"CONCAT(ks.CITY, \", \", ks.NAME) as kalafche_store_name, " +
			"s.QUANTITY, " +
			"os.QUANTITY as ordered_quantity, " +
			"s.approved, " +
			"s.approver " +
			"from stock s " +
			"join device_model dm on s.DEVICE_MODEL_ID=dm.ID " +
			"join device_brand db on dm.DEVICE_BRAND_ID=db.ID " +
			"join kalafche_store ks on s.KALAFCHE_STORE_ID=ks.ID " +
			"join item i on s.ITEM_ID=i.ID " +
			"left join ordered_stock os on os.item_id = i.id and os.DEVICE_MODEL_ID = dm.id and os.STOCK_ORDER_ID = (select id from stock_order order by id desc limit 1) " +
			"where s.approved is true " + 
			"order by device_brand_name, device_model_name, item_id, kalafche_store_id"; 
	
	private static final String UPDATE_QUANTITY_OF_SOLD_STOCK = "update stock set quantity = quantity - 1 where id = ?";
	
	private static final String GET_QUANTITY_OF_STOCK = "select coalesce((select quantity from stock where item_id = ? and device_model_id = ? and kalafche_store_id = ? and approved = true), 0)";

	private static final String UPDATE_QUANTITY_OF_RELOCATED_STOCK = "update stock set quantity = quantity + ? where id = ?";
	
	private static final String GET_STOCK_BY_ID = "select * from stock where id = ?";
	
	private static final String GET_STOCK_BY_INFO = "select * from stock where kalafche_store_id = ? and device_model_id = ? and item_id = (select id from item where product_code = ?)";

	private static final String GET_ALL_STOCKS_FOR_REPORT = "select " +
			"s.ID, " +
			"db.ID as device_brand_id, " +
			"db.NAME as device_brand_name, " +
			"dm.ID as device_model_id, " +
			"dm.NAME as device_model_name, " +
			"i.ID as item_id, " +
			"i.PRODUCT_CODE as item_product_code, " +
			"i.NAME as item_name, " +
			"i.PRICE as item_price, " +
			"os.QUANTITY as ordered_quantity, " +
			"sum(s.QUANTITY) as quantity " +
			"from stock s " +
			"join device_model dm on s.DEVICE_MODEL_ID=dm.ID " +
			"join device_brand db on dm.DEVICE_BRAND_ID=db.ID " +
			"join kalafche_store ks on s.KALAFCHE_STORE_ID=ks.ID " +
			"join item i on s.ITEM_ID=i.ID " +
			"left join ordered_stock os on os.item_id = i.id and os.DEVICE_MODEL_ID = dm.id and os.STOCK_ORDER_ID = ? " +
			"where s.approved is true " +
			"group by i.PRODUCT_CODE, i.NAME, db.name, dm.name " +
			"order by device_brand_name, device_model_name, quantity desc ";

	private BeanPropertyRowMapper<Stock> rowMapper;

	@Autowired
	public StockDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<Stock> getRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<Stock>(Stock.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}

		return rowMapper;
	}

	@Override
	public List<Stock> getAllStocks() {
		List<Stock> stocks = getJdbcTemplate().query(GET_ALL_STOCKS_QUERY,
				getRowMapper());

		return stocks;
	}
	
	@Override
	public List<Stock> getUnapprovedStocksByKalafcheStoreId(int kalafcheStoreId) {
		User loggedUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Collection<GrantedAuthority> authorities = loggedUser.getAuthorities();
		
		boolean isAdmin = false;
		for (GrantedAuthority authority : authorities) {
			isAdmin = authority.getAuthority().equals("ROLE_ADMIN");
			if (isAdmin) {
				break;
			}
		}		
		
		List<Stock> stocks = new ArrayList<Stock>();
		if (isAdmin) {
			stocks = getJdbcTemplate().query(GET_ALL_UNAPPROVED_STOCKS,
					getRowMapper());
		} else {
			stocks = getJdbcTemplate().query(GET_UNAPPROVED_STOCKS_BY_KALAFCHE_STORE_ID,
					getRowMapper(), kalafcheStoreId);	
		}

		return stocks;
	}

	@Override
	public void insertStockForApproval(Stock stock) throws DuplicateKeyException {
		getJdbcTemplate().update(INSERT_STOCK_FOR_APPROVAL, 
				stock.getDeviceModelId(), stock.getItemId(),
				stock.getKalafcheStoreId(), stock.getQuantity(), 
				stock.isApproved(), stock.getApprover());
	}
	
	@Override
	public void deleteStocksForApproval(List<Stock> stocks) {
		for (Stock stock : stocks) {
			getJdbcTemplate().update(DELETE_STOCK_FOR_APPROVAL, stock.getId());
		}
	}
	
	@Override
	public void approveStockForApproval(Stock stock) {
			stock.setApproved(true);
			getJdbcTemplate().update(INSERT_APPROVED_STOCK, 
					stock.getDeviceModelId(), stock.getItemId(),
					stock.getKalafcheStoreId(), stock.getQuantity(), 
					stock.isApproved(), stock.getApprover(), stock.getQuantity());	
	}

	public List<Stock> getAllApprovedStocks() {
		List<Stock> stocks = getJdbcTemplate().query(GET_ALL_APPROVED_STOCKS,
				getRowMapper());;
		return stocks;
	}
	
	@Override
	public List<Stock> searchStocks(int deviceBrandId, int deviceModelId,
			int itemTypeId) {
		List<Stock> items;
		List<Integer> args = new ArrayList<Integer>();

		StringBuilder query = new StringBuilder(GET_ALL_STOCKS_QUERY);

		if (deviceBrandId != 0 || deviceModelId != 0 || itemTypeId != 0) {
			boolean shouldAppendAnd = false;
			query.append(" WHERE");

			if (deviceBrandId != 0) {
				query.append(" DEVICE_BRAND_ID = ?");
				args.add(deviceBrandId);
				shouldAppendAnd = true;
			}

			if (deviceModelId != 0) {
				if (shouldAppendAnd) {
					query.append(" and");
				}
				query.append(" DEVICE_MODEL_ID = ?");
				args.add(deviceModelId);
			}

			if (itemTypeId != 0) {
				if (shouldAppendAnd) {
					query.append(" and");
				}
				query.append(" ITEM_TYPE_ID = ?");
				args.add(itemTypeId);
			}

			items = getJdbcTemplate().query(query.toString(),
					args.toArray(new Integer[args.size()]), getRowMapper());
		} else {
			items = getJdbcTemplate().query(GET_ALL_STOCKS_QUERY,
					getRowMapper());
		}

		return items;
	}

	@Override
	public void updateTheQuantitiyOfSoldStock(int stockId) {
		getJdbcTemplate().update(UPDATE_QUANTITY_OF_SOLD_STOCK, stockId);
	}
	
	public Integer getQuantitiyOfStock(int itemId, int deviceModelId, int kalafcheStoreId) {
		Integer quantity = getJdbcTemplate().queryForObject(GET_QUANTITY_OF_STOCK, Integer.class, new Object[] {itemId, deviceModelId, kalafcheStoreId});
		
		return quantity;
	}

	@Override
	public void updateTheQuantitiyOfRelocatedStock(int quantity, int stockId) {
		getJdbcTemplate().update(UPDATE_QUANTITY_OF_RELOCATED_STOCK, quantity, stockId);
	}

	@Override
	public Stock getStockById(int stockId) {
		List<Stock> stocks = getJdbcTemplate().query(GET_STOCK_BY_ID, getRowMapper(), stockId);
		
		return stocks.get(0);
	}
	
	@Override
	public Stock getStockByInfo(int kalafcheStoreId, int deviceModelId, String productCode) {
		List<Stock> stocks = getJdbcTemplate().query(GET_STOCK_BY_INFO, getRowMapper(), kalafcheStoreId, deviceModelId, productCode);
		
		return stocks.get(0);
	}

	@Override
	public List<Stock> getAllStocksForReport(int stockOrderId) {
		List<Stock> stocks = getJdbcTemplate().query(GET_ALL_STOCKS_FOR_REPORT,
				getRowMapper(), stockOrderId);;
		return stocks;
	}

}
