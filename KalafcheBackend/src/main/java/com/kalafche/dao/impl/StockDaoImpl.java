package com.kalafche.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.kalafche.model.Stock;

@Service
public class StockDaoImpl extends JdbcDaoSupport {

	private static final String UPSERT_APPROVED_IN_STOCK = "insert into stock "
			+ "(item_id, store_id, quantity, approved) values " + "(?, ?, ?, true) "
			+ "on duplicate key update quantity = quantity + ?";

	private static final String GET_ALL_APPROVED_STOCKS_FOR_STICKER_PRINTING_BY_STORE_ID = "select " +
			"s.ID, " +
			"iv.id as item_id, " +
			"iv.device_brand_id, " +
			"iv.device_model_id, " +
			"iv.device_model_name, " +
			"iv.product_id, " +
			"iv.product_code, " +
			"iv.product_name, " +
			"iv.product_type_name, " +
			"iv.barcode, " +
			"coalesce(psp.specific_price, iv.product_price) as product_price, " +
			"s.QUANTITY, " +
			"iv.product_fabric " +
			"from stock s " +
			"join item_vw iv on s.ITEM_ID=iv.ID " +
			"left join product_specific_price psp on psp.product_id = iv.product_id and psp.store_id = s.store_id " +
			"where s.approved is true " +
			"and s.store_id = ? " +
			"order by iv.device_brand_id, iv.device_model_id";
	
	private static final String GET_ALL_APPROVED_STOCKS_FOR_STORES = "select " +
			"s.ID, " +
			"iv.id as item_id, " +
			"iv.device_brand_id, " +
			"iv.device_model_id, " +
			"iv.device_model_name, " +
			"iv.product_id, " +
			"iv.product_code, " +
			"iv.product_name, " +
			"iv.barcode, " +
			"coalesce(psp.specific_price, iv.product_price) as product_price, " +
			"ks.ID as store_id, " +
			"CONCAT(ks.CITY,',',ks.NAME) as store_name, " +
			"s.QUANTITY, " +
			"ws.quantity as extraQuantity, " +
			"sum(sr2.quantity) as orderedQuantity " +
			"from stock s " +
			"join store ks on s.store_ID=ks.ID " +
			"join item_vw iv on s.ITEM_ID=iv.ID " +
			"left join stock ws on ws.item_id=iv.ID " +
			"and ws.store_id=4 " +
			"and ws.approved=true " +
			"left join " +
			"( " +
			"   select " +
			"   sr1.quantity, " +
			"   sr1.dest_store_id, " +
			"   st3.product_id, " +
			"   st3.device_model_id " +
			"   from relocation sr1 " +
			"   join stock st3 on sr1.stock_id=st3.id " +
			"   where sr1.source_store_id=4 " +
			"   and sr1.arrived=false " +
			"   and sr1.archived=false " +
			"   and (sr1.rejected=false or sr1.rejected is null) " +
			") " +
			"sr2 on sr2.product_id=iv.product_id " +
			"and sr2.device_model_id=iv.device_model_id " +
			"and ks.id=sr2.dest_store_id " +
			"left join product_specific_price psp on psp.product_id = iv.product_id and psp.store_id = ks.id " +
			"where s.approved is true " +
			"and ks.CODE <> 'RU_WH' ";
	private static final String BY_STORE_CLAUSE = "and ks.ID = ? ";

	private static final String GROUP_BY_CLAUSE = "group by s.id ";

	private static final String GET_ALL_APPROVED_STOCKS_FROM_WH = "select " + 
			"s.ID, " + 
			"iv.id as item_id, " + 
			"iv.device_brand_id, " + 
			"iv.device_model_id, " + 
			"iv.device_model_name, " + 
			"iv.product_id, " + 
			"iv.product_code, " + 
			"iv.product_name, " +
			"iv.barcode, " +
			"coalesce(psp.specific_price, iv.product_price) as product_price, " +
			"ks.ID as store_id, " + 
			"CONCAT(ks.CITY,\",\",ks.NAME) as store_name, " + 
			"s.QUANTITY, " + 
			"s.approved, " + 
			"s.approver, " + 
			"es.quantity as extraQuantity, " + 
			"sum(sr2.quantity) as orderedQuantity " + 
			"from stock s " + 
			"join store ks on s.store_ID=ks.ID " + 
			"join item_vw iv on s.ITEM_ID=iv.ID " + 
			"left join stock es on es.item_id=iv.ID and es.store_id=? and es.approved=true " + 
			"left join " + 
			"( " + 
			"   select " + 
			"   sr1.quantity, " + 
			"   sr1.dest_store_id, " + 
			"   st3.product_id, " + 
			"   st3.device_model_id " + 
			"   from relocation sr1 " + 
			"   join stock st3 on sr1.stock_id=st3.id " + 
			"   where sr1.source_store_id=4 " + 
			"   and sr1.arrived=false " + 
			"   and sr1.archived=false " + 
			"   and (sr1.rejected=false or sr1.rejected is null) " + 
			") " + 
			"sr2 on sr2.product_id=iv.product_id " + 
			"and sr2.device_model_id=iv.device_model_id " + 
			"and sr2.dest_store_id=? " + 
			"left join product_specific_price psp on psp.product_id = iv.product_id and psp.store_id = ks.id " +
			"where s.approved is true " + 
			"and ks.CODE = 'RU_WH' ";

	private static final String ORDER_BY_CLAUSE = "order by iv.device_model_name, iv.product_id, store_id ";

	private static final String UPDATE_QUANTITY_OF_SOLD_STOCK = "update stock set quantity = quantity - 1 where item_id = ? and store_id = ?";
	
	private static final String UPDATE_QUANTITY_OF_REVISED_STOCK = "update stock set quantity = quantity - ?, synced = true where item_id = ? and store_id = (select store_id from revision where id = ?)";

	private static final String UPDATE_QUANTITY_OF_REFUND_STOCK = "update stock set quantity = quantity + 1 where item_id = (select item_id from sale_item where id = ?) and store_id = ?";
	
	private static final String GET_QUANTITY_OF_STOCK_IN_WH = "select " +
			"coalesce(( " +
			"	select quantity " +
			"	from stock st " +
			"	join item_vw iv on st.item_id = iv.id " +
			"	join store ks on st.store_id = ks.id " +
			"	where iv.product_code = ? " +
			"	and iv.device_model_id = ? " +
			"	and ks.code = 'RU_WH' " +
			"	and approved = true) " +
			", 0); ";;

	private static final String GET_COMPANY_QUANTITY_OF_STOCK = "select coalesce((select " +
			"sum(st.quantity) " +
			"from stock st " +
			"join item_vw iv on st.item_id = iv.id " +
			"join store ks on ks.id = st.store_id " +
			"where iv.product_code = ? " +
			"and iv.device_model_id = ? " +
			"and ks.code != 'RU_KAUFL_1' " +
			"and ks.code != 'RU_KAUFL_2' " +
			"and ks.code != 'RU_DEF' " +
			"and ks.code != 'RU_OS'), 0); ";

	private static final String UPDATE_QUANTITY_OF_APPROVED_STOCK = "update stock set quantity = quantity + ? where item_id = ? and store_id = ?";

	private static final String GET_ALL_STOCKS_FOR_REPORT = "select " +
			"s.ID, " +
			"iv.device_brand_id, " +
			"iv.device_model_id, " +
			"iv.device_model_name, " +
			"iv.product_id, " +
			"iv.PRODUCT_CODE as product_code, " +
			"iv.PRODUCT_NAME as product_name, " +
			"iv.PRODUCT_PRICE as product_price, " +
			"os.QUANTITY as ordered_quantity, " +
			"sum(s.QUANTITY) as quantity " +
			"from stock s " +
			"join store ks on s.store_ID=ks.ID " +
			"join item_vw iv on s.ITEM_ID=iv.ID " +
			"left join ordered_stock os on os.product_id = iv.product_id " +
			"and os.DEVICE_MODEL_ID = iv.device_model_id " +
			"and os.STOCK_ORDER_ID = ? " +
			"where s.approved is true " +
			"group by iv.PRODUCT_CODE,iv.PRODUCT_NAME,iv.device_model_name " +
			"order by iv.device_model_name,quantity desc ";

	private static final String DEVICE_BRAND_CLAUSE = "and iv.device_brand_id = ? ";

	private static final String DEVICE_MODEL_CLAUSE = "and iv.device_model_id = ? ";

	private static final String PRODUCT_CODES_CLAUSE = "and iv.product_code in (%s) ";

	private static final String BARCODE_CLAUSE = "and iv.barcode = ? ";

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

	public void insertOrUpdateQuantityOfInStock(Integer itemId, Integer storeId, Integer quantity) {
		getJdbcTemplate().update(UPSERT_APPROVED_IN_STOCK, itemId, storeId, quantity, quantity);
	}

	public List<Stock> getAllApprovedStocks(int userStoreId, int selectedStoreId, Integer deviceBrandId, Integer deviceModelId, String productCodes, String barcode) {
		
		List<Object> storeArgsList;
		List<Object> searchArgsList = Lists.newArrayList();
		String searchCriteria;
		if (selectedStoreId == 0) {
			storeArgsList = Lists.newArrayList(userStoreId, userStoreId);
			searchCriteria = generateSearchCriteria(deviceBrandId, deviceModelId, productCodes, barcode, searchArgsList);

			storeArgsList.addAll(searchArgsList);		
			
			Object[] storeArgsArr = listToArr(storeArgsList);
			Object[] searchArgsArr = listToArr(searchArgsList);
			
			List<Stock> stocks = getJdbcTemplate()
					.query(GET_ALL_APPROVED_STOCKS_FOR_STORES + searchCriteria + GROUP_BY_CLAUSE + ORDER_BY_CLAUSE, searchArgsArr, getRowMapper());
			stocks.addAll(getJdbcTemplate().query(GET_ALL_APPROVED_STOCKS_FROM_WH + searchCriteria + GROUP_BY_CLAUSE + ORDER_BY_CLAUSE,
					storeArgsArr, getRowMapper()));

			return stocks;

		} else if (selectedStoreId == 4) {
			storeArgsList = Lists.newArrayList(userStoreId, userStoreId);
			searchCriteria = generateSearchCriteria(deviceBrandId, deviceModelId, productCodes, barcode, searchArgsList);

			storeArgsList.addAll(searchArgsList);		
			
			Object[] storeArgsArr = listToArr(storeArgsList);
			
			return getJdbcTemplate().query(GET_ALL_APPROVED_STOCKS_FROM_WH + searchCriteria + GROUP_BY_CLAUSE + ORDER_BY_CLAUSE,
					storeArgsArr, getRowMapper());
		} else {
			storeArgsList = Lists.newArrayList(selectedStoreId);
			searchCriteria = generateSearchCriteria(deviceBrandId, deviceModelId, productCodes, barcode, searchArgsList);

			storeArgsList.addAll(searchArgsList);		
			
			Object[] storeArgsArr = listToArr(storeArgsList);
			
			return getJdbcTemplate().query(
					GET_ALL_APPROVED_STOCKS_FOR_STORES + BY_STORE_CLAUSE + searchCriteria + GROUP_BY_CLAUSE + ORDER_BY_CLAUSE,
					storeArgsArr, getRowMapper());
		}
	}

	private Object[] listToArr(List<Object> list) {
		Object[] arr = new Object[list.size()];
		arr = list.toArray(arr);
		
		return arr;
	}

	private String generateSearchCriteria(Integer deviceBrandId, Integer deviceModelId, String productCodes,
			String barcode, List<Object> argsList) {

		String searchCriteria = "";
		
		if (deviceBrandId != null) {
			searchCriteria += DEVICE_BRAND_CLAUSE;
			argsList.add(deviceBrandId);
		}
		
		if (deviceModelId != null) {
			searchCriteria += DEVICE_MODEL_CLAUSE;
			argsList.add(deviceModelId);
		}
		
		if (Strings.isNotBlank(productCodes)) {			
			searchCriteria += String.format(PRODUCT_CODES_CLAUSE, productCodes);
		}
		
		if (Strings.isNotBlank(barcode)) {
			searchCriteria += BARCODE_CLAUSE;
			argsList.add(barcode);
		}
		
		return searchCriteria;
	}

	public void updateTheQuantitiyOfSoldStock(int itemId, int kalafceStoreId) {
		getJdbcTemplate().update(UPDATE_QUANTITY_OF_SOLD_STOCK, itemId, kalafceStoreId);
	}

	public Integer getQuantitiyOfStockInWH(String productCode, int deviceModelId) {
		Integer quantity = getJdbcTemplate().queryForObject(GET_QUANTITY_OF_STOCK_IN_WH, Integer.class,
				new Object[] { productCode, deviceModelId });

		return quantity;
	}

	public Integer getCompanyQuantityOfStock(String productCode, int deviceModelId) {
		Integer quantity = getJdbcTemplate().queryForObject(GET_COMPANY_QUANTITY_OF_STOCK, Integer.class,
				new Object[] { productCode, deviceModelId });

		return quantity;
	}

	public void updateQuantityOfApprovedStock(Integer itemId, Integer storeId, Integer quantity) {
		getJdbcTemplate().update(UPDATE_QUANTITY_OF_APPROVED_STOCK, quantity, itemId, storeId);
	}

	public List<Stock> getAllStocksForReport(int stockOrderId) {
		List<Stock> stocks = getJdbcTemplate().query(GET_ALL_STOCKS_FOR_REPORT, getRowMapper(), stockOrderId);
		;
		return stocks;
	}

	public void updateTheQuantitiyOfRefundStock(Integer saleItemId, int storeId) {
		getJdbcTemplate().update(UPDATE_QUANTITY_OF_REFUND_STOCK, saleItemId, storeId);
	}

	public List<Stock> getAllApprovedStocksForStickerPrinting(Integer storeId) {
		return getJdbcTemplate().query(GET_ALL_APPROVED_STOCKS_FOR_STICKER_PRINTING_BY_STORE_ID, getRowMapper(), storeId);
	}

	public void updateTheQuantitiyOfRevisedStock(Integer itemId, Integer revisionId, Integer difference) {
		getJdbcTemplate().update(UPDATE_QUANTITY_OF_REVISED_STOCK, difference, itemId, revisionId);	
	}

}
