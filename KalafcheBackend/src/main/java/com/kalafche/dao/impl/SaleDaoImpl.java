package com.kalafche.dao.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.kalafche.dao.SaleDao;
import com.kalafche.model.DataReport;
import com.kalafche.model.PeriodInMillis;
import com.kalafche.model.sale.Sale;
import com.kalafche.model.sale.SaleItem;
import com.kalafche.model.sale.SalesByStore;
import com.kalafche.model.sale.SalesByStoreByDayByProductType;
import com.kalafche.model.sale.Transaction;
import com.kalafche.model.sale.TransactionsByStoreByDay;
import com.kalafche.service.CurrencyService;
import com.kalafche.service.DateService;
import com.kalafche.service.EmployeeService;

@Service
public class SaleDaoImpl extends JdbcDaoSupport implements SaleDao {

	@Autowired
	EmployeeService employeeService;
	
	@Autowired
	CurrencyService currencyService;
	
	private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
	
	private final class SalesByStoreRowMapper implements RowMapper<SalesByStore> {
		@Override
		public SalesByStore mapRow(ResultSet rs, int rowNum) throws SQLException {
			SalesByStore salesByStore = new SalesByStore();
			salesByStore.setStoreId(rs.getInt(1));
			salesByStore.setStoreCode(rs.getString(2));
			salesByStore.setStoreName(rs.getString(3));
			salesByStore.setAmount(rs.getBigDecimal(4));
			salesByStore.setItemCount(rs.getBigDecimal(5));
			salesByStore.setSaleCount(rs.getBigDecimal(6));
			salesByStore.setTransactionCount(rs.getBigDecimal(7));
			salesByStore.setBonusPts(rs.getBigDecimal(8));
			salesByStore.setProtectorCount(rs.getBigDecimal(9));
			salesByStore.setProtectorPlusCount(rs.getBigDecimal(10));
			if (BigDecimal.ZERO.compareTo(rs.getBigDecimal(5)) < 0 && BigDecimal.ZERO.compareTo(rs.getBigDecimal(7)) < 0) {
				salesByStore.setSpt(rs.getBigDecimal(5).divide(rs.getBigDecimal(7), 2, RoundingMode.HALF_UP));
			} else {
				salesByStore.setSpt(BigDecimal.ZERO);
			}
			if (BigDecimal.ZERO.compareTo(rs.getBigDecimal(8)) < 0 && BigDecimal.ZERO.compareTo(rs.getBigDecimal(5)) < 0) {
				salesByStore.setSqs(rs.getBigDecimal(8).divide(rs.getBigDecimal(5), 2, RoundingMode.HALF_UP));
			} else {
				salesByStore.setSqs(BigDecimal.ZERO);
			}
			if (BigDecimal.ZERO.compareTo(rs.getBigDecimal(9)) < 0 && BigDecimal.ZERO.compareTo(rs.getBigDecimal(10)) < 0) {
				salesByStore.setAttachRate(rs.getBigDecimal(10).divide(rs.getBigDecimal(9), 2, RoundingMode.HALF_UP).multiply(ONE_HUNDRED));
			} else {
				salesByStore.setAttachRate(BigDecimal.ZERO);
			}
		    return salesByStore;
		}
	}
	
	private final class SaleItemRowMapper implements RowMapper<SaleItem> {
		@Override
		public SaleItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			SaleItem saleItem = new SaleItem();
			saleItem.setId(rs.getInt("ID"));
			saleItem.setSaleId(rs.getInt("SALE_ID"));
			saleItem.setItemId(rs.getInt("ITEM_ID"));
			saleItem.setIsRefunded(rs.getBoolean("IS_REFUNDED"));
			saleItem.setSaleTimestamp(rs.getLong("SALE_TIMESTAMP"));
			saleItem.setBonusPts(rs.getInt("BONUS_PTS"));
			saleItem.setProductId(rs.getInt("PRODUCT_ID"));
			saleItem.setProductCode(rs.getString("PRODUCT_CODE"));
			saleItem.setProductName(rs.getString("PRODUCT_NAME"));
			saleItem.setProductTypeId(rs.getInt("PRODUCT_TYPE_ID"));
			saleItem.setProductTypeName(rs.getString("PRODUCT_TYPE_NAME"));
			saleItem.setProductMasterTypeId(rs.getInt("PRODUCT_MASTER_TYPE_ID"));
			saleItem.setProductMasterTypeName(rs.getString("PRODUCT_MASTER_TYPE_NAME"));
			saleItem.setDeviceModelId(rs.getInt("DEVICE_MODEL_ID"));
			saleItem.setDeviceModelName(rs.getString("DEVICE_MODEL_NAME"));
			saleItem.setDeviceBrandId(rs.getInt("DEVICE_BRAND_ID"));
			saleItem.setEmployeeId(rs.getInt("EMPLOYEE_ID"));
			saleItem.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
			saleItem.setStoreId(rs.getInt("STORE_ID"));
			saleItem.setStoreName(rs.getString("STORE_NAME"));
			saleItem.setDiscountCampaignCode(rs.getString("discountCampaignCode"));
			saleItem.setDiscountCode(rs.getInt("discountCode"));
			saleItem.setSalePrice(rs.getBigDecimal("SALE_PRICE"));
			saleItem.setItemPrice(rs.getBigDecimal("ITEM_PRICE"));
			saleItem.setSalePriceEuro(currencyService.convertToEuro(rs.getBigDecimal("SALE_PRICE")));		
			saleItem.setItemPriceEuro(currencyService.convertToEuro(rs.getBigDecimal("ITEM_PRICE")));	
			
			return saleItem;
		}
	}
		
	private final class SaleItemBySaleRowMapper implements RowMapper<SaleItem> {
		@Override
		public SaleItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			SaleItem saleItem = new SaleItem();
			saleItem.setId(rs.getInt("ID"));
			saleItem.setSaleId(rs.getInt("SALE_ID"));
			saleItem.setItemId(rs.getInt("ITEM_ID"));
			saleItem.setIsRefunded(rs.getBoolean("IS_REFUNDED"));
			saleItem.setBonusPts(rs.getInt("BONUS_PTS"));
			saleItem.setProductId(rs.getInt("PRODUCT_ID"));
			saleItem.setProductCode(rs.getString("PRODUCT_CODE"));
			saleItem.setProductName(rs.getString("PRODUCT_NAME"));
			saleItem.setDeviceModelId(rs.getInt("DEVICE_MODEL_ID"));
			saleItem.setDeviceModelName(rs.getString("DEVICE_MODEL_NAME"));
			saleItem.setDeviceBrandId(rs.getInt("DEVICE_BRAND_ID"));
			saleItem.setDeviceBrandName(rs.getString("DEVICE_BRAND_NAME"));
			saleItem.setSalePrice(rs.getBigDecimal("SALE_PRICE"));
			saleItem.setItemPrice(rs.getBigDecimal("ITEM_PRICE"));
			saleItem.setSalePriceEuro(currencyService.convertToEuro(rs.getBigDecimal("SALE_PRICE")));		
			saleItem.setItemPriceEuro(currencyService.convertToEuro(rs.getBigDecimal("ITEM_PRICE")));	
			
			return saleItem;
		}
	}
	
	private final class SaleRowMapper implements RowMapper<Sale> {
		@Override
		public Sale mapRow(ResultSet rs, int rowNum) throws SQLException {
			Sale sale = new Sale();
			sale.setId(rs.getInt("ID"));
			sale.setUniqueSaleId(rs.getString("UNIQUE_SALE_ID"));
			sale.setTransactionId(rs.getInt("TRANSACTION_ID"));
			sale.setSaleTimestamp(rs.getLong("SALE_TIMESTAMP"));
			sale.setEmployeeId(rs.getInt("EMPLOYEE_ID"));
			sale.setEmployeeName(rs.getString("EMPLOYEE_NAME"));
			sale.setStoreId(rs.getInt("STORE_ID"));
			sale.setStoreName(rs.getString("STORE_NAME"));
			sale.setIsCashPayment(rs.getBoolean("IS_CASH_PAYMENT"));
			sale.setBonusPts(rs.getInt("bonusPts"));
			sale.setAmount(rs.getBigDecimal("AMOUNT"));		
			sale.setAmountEuro(currencyService.convertToEuro(rs.getBigDecimal("AMOUNT")));	
		
			return sale;
		}
	}
	
	private static final String GET_ALL_SALES_QUERY = "select " +
			"s.id, " +
			"s.unique_sale_id, " +
			"s.transaction_id, " +
			"s.sale_timestamp, " +
			"s.employee_id, " +
			"s.store_id, " +
			"s.is_cash_payment, " +
			"s.bonus_pts, " +			
			"sum(si.sale_price) as amount, " +
			"sum(si.bonus_pts) as bonusPts, " +
			"e.name as employee_name, " +
			"CONCAT(ks.city, \", \", ks.name) as store_name " +
			"from sale s " +
			"join sale_item si on si.sale_id = s.id " +
			//"join sale_item si2 on si2.sale_id = s.id" +
			"join employee e on s.employee_id = e.id " +
			"join store ks on s.store_id = ks.id ";
	
	private static final String GET_ALL_SALE_ITEMS_QUERY = "select " +
			"si.id, " +
			"si.sale_id, " +
			"si.item_id, " +
			"si.is_refunded, " +
			"s.sale_timestamp, " +
			"iv.product_id, " +
			"iv.product_code, " +
			"iv.product_name, " +
			"iv.product_type_id, " +
			"iv.product_type_name, " +
			"iv.product_master_type_id, " +
			"iv.product_master_type_name, " +
			"iv.device_model_id, " +
			"iv.device_model_name, " +
			"iv.device_brand_id, " +
			"si.sale_price, " +
			"si.item_price, " +
			"si.bonus_pts, " +
			"e.id as employee_id, " +
			"e.name as employee_name, " +
			"ks.id as store_id, " +
			"CONCAT(ks.city, \", \", ks.name) as store_name, " +
			"dca.code as discountCampaignCode, " +
			"dc.code as discountCode " +
			"from sale_item si " +
			"join sale s on si.sale_id = s.id " +
			"left join discount_code dc on si.discount_code_id = dc.id " +
			"left join discount_campaign dca on dc.discount_campaign_id = dca.id " +
			"join item_vw iv on iv.id = si.item_id " +
			"join store ks on ks.id = s.store_id " +
			"join employee e on e.id = s.employee_id ";
	
	private static final String GET_SALE_ITEM_TOTAL_AND_COUNT_QUERY = "select " +
			"count(si.id) as count, " +
			"sum(si.sale_price) as totalAmount, " +
			"sum(si.bonus_pts) as totalBonusPts " +
			"from sale_item si " +
			"join sale s on si.sale_id = s.id " +
			"where s.sale_timestamp between ? and ? ";
	
	private static final String STORE_ID_CRITERIA = " and s.store_id = ? ";
	
	private static final String CARD_PAYMENT_CRITERIA = " and s.is_cash_payment <> true";
	
	private static final String GET_SALES_BY_STORE_QUERY = "select " +
			"ks.id as storeId, " +
			"ks.code as storeCode, " +
			"CONCAT(ks.city,\",\",ks.name) as store_name, " +
			"coalesce(sum(si.sale_price), 0.00) as amount, " +
			"coalesce(count(si.id), 0) as item_count, " +
			"coalesce(count(distinct(s.id)), 0) as sale_count, " +
			"coalesce(count(distinct(t.id)), 0) as transaction_count, " +
			"coalesce(sum(si.bonus_pts), 0) as bonus_pts, " +
			"coalesce(sum(case when (pmt.name = 'PROTECTOR' and (dc.code != 500 or si.discount_code_id is null)) then 1 else 0 end), 0) as protector_count, " +
			"coalesce(count(case when p.code = '0500' then 1 end), 0) as protector_plus_count " +
			"from store ks " +
			"left join sale s on s.store_id = ks.id and s.sale_timestamp between ? and ? " +
			"left join transaction t on s.transaction_id = t.id " +
			"left join sale_item si on si.sale_id = s.id and si.is_refunded <> true and si.sale_price > 0 " +
			"left join item i on i.id = si.item_id " +
			"left join product p on p.id = i.product_id " +
			"left join product_type pt on pt.id = p.type_id " +
			"left join product_master_type pmt on pmt.id = pt.master_type_id " +
			"left join discount_code dc on si.discount_code_id = dc.id " +
			"where ks.is_store is true ";
	
	private static final String GET_SALES_DATA_FOR_THE_COMPANY = "select " +
			"0 as storeId, " +
			"'COMPANY' as storeCode, " +
			"'Всички магазини' as store_name, " +
			"coalesce(sum(si.sale_price), 0.00) as amount, " +
			"coalesce(count(si.id), 0) as item_count, " +
			"coalesce(count(distinct(s.id)), 0) as sale_count, " +
			"coalesce(count(distinct(t.id)), 0) as transaction_count, " +
			"coalesce(sum(si.bonus_pts), 0) as bonus_pts, " +
			"coalesce(sum(case when (pmt.name = 'PROTECTOR' and (dc.code != 500 or si.discount_code_id is null)) then 1 else 0 end), 0) as protector_count, " +
			"coalesce(count(case when p.code = '0500' then 1 end), 0) as protector_plus_count " +
			"from store ks " +
			"left join sale s on s.store_id = ks.id and s.sale_timestamp between ? and ? " +
			"join transaction t on s.transaction_id = t.id " +
			"join sale_item si on si.sale_id = s.id and si.is_refunded <> true and si.sale_price > 0 " +
			"left join item i on i.id = si.item_id " +
			"left join product p on p.id = i.product_id " +
			"left join product_type pt on pt.id = p.type_id " +
			"left join product_master_type pmt on pmt.id = pt.master_type_id " +
			"left join discount_code dc on si.discount_code_id = dc.id " +
			"where ks.is_store is true ";
	
	private static final String USI_CRITERIA_QUERY = " where unique_sale_id = ?";
	private static final String PERIOD_CRITERIA_QUERY = " where sale_timestamp between ? and ?";
	private static final String STORE_CRITERIA_QUERY = " and ks.id in (%s)";
	private static final String NOT_REFUND_QUERY = " and si.is_refunded <> true";
	private static final String REFUND_QUERY = " and si.is_refunded is true";
	private static final String NON_REFUND_QUERY = " and si.is_refunded is false";
	private static final String PRODUCT_CODE_QUERY = " and iv.product_code in (%s)";
	private static final String DEVICE_BRAND_QUERY = " and iv.device_brand_id = ?";
	private static final String DEVICE_MODEL_QUERY = " and iv.device_model_id = ?";
	private static final String DISCOUNT_CAMPAIGN_QUERY = " and dca.code = ?";
	private static final String MASTER_PRODUCT_TYPE_QUERY = " and iv.product_master_type_id = ?";
	private static final String PRODUCT_TYPE_QUERY = " and iv.product_type_id = ?";
	private static final String PRICE_QUERY = " and si.sale_price between ? and ?";
	private static final String INSERT_SALE = "insert into sale (employee_id, store_id, sale_timestamp, is_cash_payment, transaction_id, is_initial)"
			+ " values (?, ?, ?, ?, ?, ?)";
	private static final String INSERT_TRANSACTION = "insert into transaction (create_timestamp, created_by, store_id)"
			+ " values (?, ?, ?)";
	private static final String ORDER_BY_SALE = " order by s.sale_timestamp";
	private static final String ORDER_BY_STOREID = " order by storeId";
	private static final String GROUP_BY_SALE = " group by s.id";
	private static final String GROUP_BY_STORE = " group by ks.id ";
	private static final String GET_SALE_ITEMS_PER_SALE = "select " +
			"si.id, " +
			"si.sale_id, " +
			"si.item_id, " +
			"si.is_refunded, " +
			"si.bonus_pts, " + 
			"p.id as product_id, " +
			"p.code as product_code, " +
			"p.name as product_name, " +
			"dm.id as device_model_id, " +
			"dm.name as device_model_name, " +
			"db.id as device_brand_id, " +
			"db.name as device_brand_name, " +
			"si.sale_price, " +
			"si.item_price, " +
			"si.bonus_pts " +
			"from sale_item si " +
			"join item i on si.item_id = i.id " +
			"join product p on i.product_id = p.id " +
			"join device_model dm on i.device_model_id = dm.id " +
			"join device_brand db on dm.device_brand_id = db.id " +
			"where si.sale_id = ?";
	
	private static final String INSERT_SALE_ITEM = "insert into sale_item(sale_id, item_id, item_price, sale_price, discount_code_id, bonus_pts) values (?, ?, ?, ?, ?, ?)";
	
	private static final String UPDATE_REFUNDED_SALE_ITEM = "update sale_item set is_refunded = true where id = ?";
	
	private static final String UPDATE_TRANSACTION = "update transaction set updated_by = ?, last_update_timestamp = ? where id = ?";
	
	private static final String UPDATE_SALE = "update sale set unique_sale_id = ? where id = ?";

	private static final String GET_SALE_ITEM_PRICE = "select sale_price from sale_item where id = ?";
	
	private static final String GET_SALE_TRANSACTION_ID = "select transaction_id from sale where unique_sale_id = ?";
	
	private static final String GET_PRODUCT_TYPE_SPLIT_REPORT = "select " +
			"t1.store_id, " +
			"t1.store_name, " +
			"t1.day as day, " +
			"t1.product_type_id, " +
			"t1.product_type_name, " +
			"t1.master_type_id, " +
			"t2.sold_items as sold_items_count " +
			"from " +
			"( " +
			"   select " +
			"   st.id as store_id, " +
			"   CONCAT(st.city,', ',st.name) as store_name, " +
			"   dateGen.day as day, " +
			"   pt.id as product_type_id, " +
			"   pt.name as product_type_name, " +
			"   pt.master_type_id " +
			"   from " +
			"   ( " +
			"	    select dg.d as day from ( " +
			"		    select curdate() - INTERVAL (a.a + (10 * b.a) + (100 * c.a) + (1000 * d.a) ) DAY as d " +
			"		    from (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as a " +
			"		    cross join (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as b " +
			"		    cross join (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as c " +
			"		    cross join (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as d) dg " +
			"	    where dg.d between ? and ? " +
			"   ) " +
			"   dateGen " +
			"   cross join product_type pt " +
			"   cross join store st " +
			"   where st.IS_STORE is true " +
			") " +
			"t1 " +
			"left join " +
			"( " +
			"   select " +
			"   s.store_id as store_id, " +
			"   CONCAT(st.city,', ',st.name) as store_name, " +
			"   DATE_FORMAT(from_unixtime(floor(s.SALE_TIMESTAMP/1000)),'%Y-%m-%d') as day, " +
			"   pt.id as product_type_id, " +
			"   pt.name as product_type_name, " +
			"   pt.master_type_id, " +
			"   count(si.id) as sold_items " +
			"   from product_type pt " +
			"   join product p on pt.id = p.type_id " +
			"   join item i on i.product_id = p.id " +
			"   join sale_item si on si.item_id = i.id " +
			"   join sale s on s.id = si.sale_id " +
			"   join store st on s.store_id = st.id " +
			"   where si.is_refunded is false " +
			"   and s.SALE_TIMESTAMP between ? and ? " +
			"   group by day, product_type_id, store_id " +
			") " +
			"t2 on t1.store_id = t2.store_id " +
			"and t1.store_name = t2.store_name " +
			"and t1.day = t2.day " +
			"and t1.product_type_id = t2.product_type_id " +
			"and t1.product_type_name = t2.product_type_name ";
	private static final String GET_TRANSACTIONS_SPLIT_REPORT = "select " +
			"curr.store_id, " +
			"curr.store_name, " +
			"curr.day, " +
			"curr.sold_items_count, " +
			"curr.transactions_count, " +
			"curr.turnover, " +
			"prev.sold_items_count as sold_items_count_prev_year, " +
			"prev.transactions_count as transactions_count_prev_year, " +
			"prev.turnover as turnover_prev_year " +
			"from " +
			"(select " +
			"t1.store_id, " +
			"t1.store_name, " +
			"t1.day as day, " +
			"t2.sold_items as sold_items_count, " +
			"t2.transactions as transactions_count, " +
			"t2.turnover " +
			"from " +
			"( " +
			"   select " +
			"   st.id as store_id, " +
			"   CONCAT(st.city,', ',st.name) as store_name, " +
			"   dateGen.day as day " +
			"   from " +
			"   ( " +
			"	    select dg.d as day from ( " +
			"		    select curdate() - INTERVAL (a.a + (10 * b.a) + (100 * c.a) + (1000 * d.a) ) DAY as d " +
			"		    from (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as a " +
			"		    cross join (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as b " +
			"		    cross join (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as c " +
			"		    cross join (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as d) dg " +
			"	    where dg.d between ? and ? " +
			"   ) " +
			"   dateGen " +
			"   cross join store st " +
			"   where st.IS_STORE is true " +
			") " +
			"t1 " +
			"left join " +
			"( " +
			"   select " +
			"   s.store_id as store_id, " +
			"   CONCAT(st.city,', ',st.name) as store_name, " +
			"   DATE_FORMAT(from_unixtime(floor(s.SALE_TIMESTAMP/1000)),'%Y-%m-%d') as day, " +
			"   count(si.id) as sold_items, " +
			"   sum(si.sale_price) as turnover, " +
			"   count(distinct(s.id)) as transactions " +
			"   from sale_item si " +
			"   join sale s on s.id = si.sale_id " +
			"   join store st on s.store_id = st.id " +
			"   where si.is_refunded is false and si.sale_price > 0.5 " +
			"   and s.SALE_TIMESTAMP between ? and ? " +
			"   group by day, store_id " +
			") " +
			"t2 on t1.store_id = t2.store_id " +
			"and t1.store_name = t2.store_name " +
			"and t1.day = t2.day) curr " +
			"left join " +
			"(select " +
			"t1.store_id, " +
			"t1.store_name, " +
			"t1.day as day, " +
			"t2.sold_items as sold_items_count, " +
			"t2.transactions as transactions_count, " +
			"t2.turnover " +
			"from " +
			"( " +
			"   select " +
			"   st.id as store_id, " +
			"   CONCAT(st.city,', ',st.name) as store_name, " +
			"   dateGen.day as day " +
			"   from " +
			"   ( " +
			"	    select dg.d as day from ( " +
			"		    select curdate() - INTERVAL (a.a + (10 * b.a) + (100 * c.a) + (1000 * d.a) ) DAY as d " +
			"		    from (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as a " +
			"		    cross join (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as b " +
			"		    cross join (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as c " +
			"		    cross join (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as d) dg " +
			"	    where dg.d between ? and ? " +
			"   ) " +
			"   dateGen " +
			"   cross join store st " +
			"   where st.IS_STORE is true " +
			") " +
			"t1 " +
			"left join " +
			"( " +
			"   select " +
			"   s.store_id as store_id, " +
			"   CONCAT(st.city,', ',st.name) as store_name, " +
			"   DATE_FORMAT(from_unixtime(floor(s.SALE_TIMESTAMP/1000)),'%Y-%m-%d') as day, " +
			"   count(si.id) as sold_items, " +
			"   sum(si.sale_price) as turnover, " +
			"   count(distinct(s.id)) as transactions " +
			"   from sale_item si " +
			"   join sale s on s.id = si.sale_id " +
			"   join store st on s.store_id = st.id " +
			"   where si.is_refunded is false and si.sale_price > 0.5 " +
			"   and s.SALE_TIMESTAMP between ? and ? " +
			"   group by day, store_id " +
			") " +
			"t2 on t1.store_id = t2.store_id " +
			"and t1.store_name = t2.store_name " +
			"and t1.day = t2.day) prev " +
			"on curr.store_id = prev.store_id " +
			"and SUBSTRING(curr.day, 6, 5) =  SUBSTRING(prev.day, 6, 5) ";
	private static final String FILTER_SPLIT_REPORT_BY_STORE = "where t1.store_id = %s "; 
	private static final String ORDER_PRODUCT_TYPE_SPLIT_REPORT = "order by store_id, day, master_type_id, product_type_id; ";
	private static final String ORDER_TRANSACTION_SPLIT_REPORT = "order by store_id, day; ";

	private static final String SELECT_STORE_IDS_BY_SALE_TIMESTAMP_PERIODS = "select GROUP_CONCAT(s.store_id) from ( " +
			"select distinct(store_id) from sale where sale_timestamp between ? and ? " +
			"intersect " +
			"select distinct(store_id) from sale where sale_timestamp between ? and ? " +
			"intersect " +
			"select distinct(store_id) from sale where sale_timestamp between ? and ?) s ";
	
	@Autowired
	private DateService dateService;
	
	private BeanPropertyRowMapper<SalesByStoreByDayByProductType> saleByStoreByDayByProductTypeRowMapper;
	private BeanPropertyRowMapper<TransactionsByStoreByDay> transactionsByStoreByDayRowMapper;
	private BeanPropertyRowMapper<DataReport> dailyReportDataRowMapper;

	@Autowired
	public SaleDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<SalesByStoreByDayByProductType> getSalesByStoreByDayByProductTypeRowMapper() {
		if (saleByStoreByDayByProductTypeRowMapper == null) {
			saleByStoreByDayByProductTypeRowMapper = new BeanPropertyRowMapper<SalesByStoreByDayByProductType>(SalesByStoreByDayByProductType.class);
			saleByStoreByDayByProductTypeRowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return saleByStoreByDayByProductTypeRowMapper;
	}
	
	private BeanPropertyRowMapper<TransactionsByStoreByDay> getTransactionsByStoreByDayRowMapper() {
		if (transactionsByStoreByDayRowMapper == null) {
			transactionsByStoreByDayRowMapper = new BeanPropertyRowMapper<TransactionsByStoreByDay>(TransactionsByStoreByDay.class);
			transactionsByStoreByDayRowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return transactionsByStoreByDayRowMapper;
	}
	
	private BeanPropertyRowMapper<DataReport> getDailyReportDataRowMapper() {
		if (dailyReportDataRowMapper == null) {
			dailyReportDataRowMapper = new BeanPropertyRowMapper<DataReport>(DataReport.class);
			dailyReportDataRowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return dailyReportDataRowMapper;
	}

	@Override
	public Integer insertSale(Sale sale) throws SQLException {		
		try (Connection connection = getDataSource().getConnection();
				PreparedStatement statement = connection.prepareStatement(
						INSERT_SALE, Statement.RETURN_GENERATED_KEYS);) {
			statement.setInt(1, sale.getEmployeeId());
			statement.setInt(2, sale.getStoreId());
			statement.setLong(3, sale.getSaleTimestamp());
			statement.setBoolean(4, sale.getIsCashPayment());
			statement.setInt(5, sale.getTransactionId());
			statement.setBoolean(6, sale.getIsInitial());

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Creating new sale failed, no rows affected.");
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1);
				} else {
					throw new SQLException("Creating sale failed, no ID obtained.");
				}
			}
		}
	}
	
	@Override
	public Integer insertTransaction(Transaction transaction) throws SQLException {		
		try (Connection connection = getDataSource().getConnection();
				PreparedStatement statement = connection.prepareStatement(
						INSERT_TRANSACTION, Statement.RETURN_GENERATED_KEYS);) {
			statement.setLong(1, transaction.getCreateTimestamp());
			statement.setInt(2, transaction.getCreatedByEmployeeId());
			statement.setInt(3, transaction.getStoreId());
			
			int affectedRows = statement.executeUpdate();
			
			if (affectedRows == 0) {
				throw new SQLException("Creating new transaction failed, no rows affected.");
			}
			
			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1);
				} else {
					throw new SQLException("Creating transaction failed, no ID obtained.");
				}
			}
		}
	}

	@Override
	public List<Sale> searchSales(Long startDateMilliseconds,
			Long endDateMilliseconds, String storeIds) {
		String searchQuery = GET_ALL_SALES_QUERY + PERIOD_CRITERIA_QUERY + String.format(STORE_CRITERIA_QUERY, storeIds);
		List<Object> argsList = new ArrayList<Object>();
		argsList.add(startDateMilliseconds);
		argsList.add(endDateMilliseconds);
		
		searchQuery += GROUP_BY_SALE;
		searchQuery += ORDER_BY_SALE;
		
		Object[] argsArr = new Object[argsList.size()];
		argsArr = argsList.toArray(argsArr);

		return getJdbcTemplate().query(
				searchQuery, argsArr, new SaleRowMapper());
	}
	
	@Override
	public List<SaleItem> searchSaleItems(Long startDateMilliseconds, Long endDateMilliseconds, String storeIds,
			String productCode, Integer deviceBrandId, Integer deviceModelId, Integer masterProductTypeId, Integer productTypeId, Float priceFrom,
			Float priceTo, String discountCampaignCode) {
		String searchQuery = GET_ALL_SALE_ITEMS_QUERY + PERIOD_CRITERIA_QUERY + String.format(STORE_CRITERIA_QUERY, storeIds) + NOT_REFUND_QUERY;
		List<Object> argsList = new ArrayList<Object>();
		argsList.add(startDateMilliseconds);
		argsList.add(endDateMilliseconds);
		
		searchQuery += addDetailedSearch(productCode, deviceBrandId, deviceModelId, masterProductTypeId, productTypeId, priceFrom, priceTo, discountCampaignCode, argsList);
		
		searchQuery += ORDER_BY_SALE;
		
		Object[] argsArr = new Object[argsList.size()];
		argsArr = argsList.toArray(argsArr);
				
		return getJdbcTemplate().query(
				searchQuery, argsArr, new SaleItemRowMapper());
	}

	private String addDetailedSearch(String productCode, Integer deviceBrandId, Integer deviceModelId,
			Integer productTypeId, Integer masterProductTypeId, Float priceFrom, Float priceTo,
			String discountCampaignCode, List<Object> args) {
		String detailedQuery = "";

		detailedQuery += PRICE_QUERY;
		if (priceFrom == null) {
			priceFrom = 0F;
		}
		if (priceTo == null) {
			priceTo = 5000f;
		}
		args.add(priceFrom);
		args.add(priceTo);

		if (productCode != null && productCode != "") {
			detailedQuery += String.format(PRODUCT_CODE_QUERY, productCode);
		}

		if (discountCampaignCode != null && discountCampaignCode != "") {
			detailedQuery += DISCOUNT_CAMPAIGN_QUERY;
			args.add(discountCampaignCode);
		}

		if (deviceBrandId != null) {
			detailedQuery += DEVICE_BRAND_QUERY;
			args.add(deviceBrandId);
		}

		if (deviceModelId != null) {
			detailedQuery += DEVICE_MODEL_QUERY;
			args.add(deviceModelId);
		}

		if (masterProductTypeId != null) {
			detailedQuery += PRODUCT_TYPE_QUERY;
			args.add(masterProductTypeId);
		}
		
		if (productTypeId != null) {
			detailedQuery += MASTER_PRODUCT_TYPE_QUERY;
			args.add(productTypeId);
		}

		return detailedQuery;
	}

	@Override
	public List<SaleItem> getSaleItemsBySaleId(Integer saleId) {
		return getJdbcTemplate().query(GET_SALE_ITEMS_PER_SALE, new SaleItemBySaleRowMapper(), saleId);
	}

	@Override
	public void insertSaleItem(SaleItem saleItem) {
		getJdbcTemplate().update(INSERT_SALE_ITEM, saleItem.getSaleId(), saleItem.getItemId(), saleItem.getItemPrice(),
				saleItem.getSalePrice(), saleItem.getDiscountCodeId(), saleItem.getBonusPts());
	}
	
	@Override
	public void updateRefundedSaleItem(Integer saleItemId) {
		getJdbcTemplate().update(UPDATE_REFUNDED_SALE_ITEM, saleItemId);
	}
	
	@Override
	public void udpateTransaction(Integer transactionId, long updateTimestamp, Integer updateEmployeeId) {
		getJdbcTemplate().update(UPDATE_TRANSACTION, updateEmployeeId, updateTimestamp, transactionId);
	}
	
	@Override
	public void updateSaleUSI(Integer saleId, String usi) {
		getJdbcTemplate().update(UPDATE_SALE, usi, saleId);
	}
	
	@Override
	public BigDecimal getSaleItemPrice(Integer saleItemId) {
		return getJdbcTemplate().queryForObject(GET_SALE_ITEM_PRICE, BigDecimal.class, saleItemId);
	}
	
	@Override
	public Integer getSaleTransactionId(String uniqueSaleId) {
		return getJdbcTemplate().queryForObject(GET_SALE_TRANSACTION_ID, Integer.class, uniqueSaleId);
	}

	@Override
	public List<SalesByStore> searchSaleByStore(Long startDateMilliseconds, Long endDateMilliseconds, String storeIds, boolean monthlyReportGeneration) {
		String searchQuery = GET_SALES_BY_STORE_QUERY;
		List<Object> argsList = new ArrayList<Object>();
		argsList.add(startDateMilliseconds);
		argsList.add(endDateMilliseconds);
		
		if (storeIds != null) {
			searchQuery += String.format(STORE_CRITERIA_QUERY, storeIds);
		}
		searchQuery += GROUP_BY_STORE;
		
//		if (monthlyReportGeneration) {
//			searchQuery = String.format(searchQuery, "left");
//		} else {
//			searchQuery = String.format(searchQuery, "");
//		}

		searchQuery += ORDER_BY_STOREID;
		
		Object[] argsArr = new Object[argsList.size()];
		argsArr = argsList.toArray(argsArr);

		return getJdbcTemplate().query(
				searchQuery, argsArr,  new SalesByStoreRowMapper());
	}

	@Override
	public List<SalesByStoreByDayByProductType> generateProductTypeSplitReport(Long startDateMilliseconds, Long endDateMilliseconds, String storeId) {
		String searchQuery = GET_PRODUCT_TYPE_SPLIT_REPORT;
		if (!StringUtils.isEmpty(storeId)) {
			searchQuery += String.format(FILTER_SPLIT_REPORT_BY_STORE, storeId);
		}
		searchQuery += ORDER_PRODUCT_TYPE_SPLIT_REPORT;
		
		List<Object> argsList = new ArrayList<Object>();
		argsList.add(dateService.convertMillisToDateTimeString(startDateMilliseconds, "yyyy-MM-dd", false));
		argsList.add(dateService.convertMillisToDateTimeString(endDateMilliseconds, "yyyy-MM-dd", false));
		argsList.add(startDateMilliseconds);
		argsList.add(endDateMilliseconds);
		
		Object[] argsArr = new Object[argsList.size()];
		argsArr = argsList.toArray(argsArr);
				
		return getJdbcTemplate().query(
				searchQuery, argsArr, getSalesByStoreByDayByProductTypeRowMapper());
	}

	@Override
	public List<TransactionsByStoreByDay> generateTransactionSplitReport(Long startDateMilliseconds, Long endDateMilliseconds, String storeId) {
		String searchQuery = GET_TRANSACTIONS_SPLIT_REPORT;
		if (!StringUtils.isEmpty(storeId)) {
			searchQuery += String.format(FILTER_SPLIT_REPORT_BY_STORE, storeId);
		}
		searchQuery += ORDER_TRANSACTION_SPLIT_REPORT;
		
		Long startDateMillisecondsPrevYear = dateService.getSameDayPrevYearInMillisBGTimezone(startDateMilliseconds);
		Long endDateMillisecondsPrevYear = dateService.getSameDayPrevYearInMillisBGTimezone(endDateMilliseconds);
		
		List<Object> argsList = new ArrayList<Object>();
		argsList.add(dateService.convertMillisToDateTimeString(startDateMilliseconds, "yyyy-MM-dd", false));
		argsList.add(dateService.convertMillisToDateTimeString(endDateMilliseconds, "yyyy-MM-dd", false));
		argsList.add(startDateMilliseconds);
		argsList.add(endDateMilliseconds);
		argsList.add(dateService.convertMillisToDateTimeString(startDateMillisecondsPrevYear, "yyyy-MM-dd", false));
		argsList.add(dateService.convertMillisToDateTimeString(endDateMillisecondsPrevYear, "yyyy-MM-dd", false));
		argsList.add(startDateMillisecondsPrevYear);
		argsList.add(endDateMillisecondsPrevYear);
		
		Object[] argsArr = new Object[argsList.size()];
		argsArr = argsList.toArray(argsArr);
		
		return getJdbcTemplate().query(
				searchQuery, argsArr, getTransactionsByStoreByDayRowMapper());
	}

	@Override
	public DataReport selectSaleItemTotalAndCountWithoutRefundByStoreId(Long startDateTime, Long endDateTime, Integer storeId) {
		return getJdbcTemplate().queryForObject(GET_SALE_ITEM_TOTAL_AND_COUNT_QUERY + NON_REFUND_QUERY + STORE_ID_CRITERIA, getDailyReportDataRowMapper(), startDateTime, endDateTime, storeId);
	}
	
	@Override
	public DataReport selectSaleItemTotalAndCountByStoreId(Long startDateTime, Long endDateTime, Integer storeId) {
		return getJdbcTemplate().queryForObject(GET_SALE_ITEM_TOTAL_AND_COUNT_QUERY + STORE_ID_CRITERIA, getDailyReportDataRowMapper(), startDateTime, endDateTime, storeId);
	}
	
	@Override
	public DataReport selectRefundedSaleItemTotalAndCount(Long startDateTime, Long endDateTime, Integer storeId) {
		return getJdbcTemplate().queryForObject(GET_SALE_ITEM_TOTAL_AND_COUNT_QUERY + STORE_ID_CRITERIA + REFUND_QUERY, getDailyReportDataRowMapper(), startDateTime, endDateTime, storeId);
	}
	
	@Override
	public DataReport selectSaleItemWithCardPaymentTotalAndCount(Long startDateTime, Long endDateTime, Integer storeId) {
		return getJdbcTemplate().queryForObject(GET_SALE_ITEM_TOTAL_AND_COUNT_QUERY + STORE_ID_CRITERIA + CARD_PAYMENT_CRITERIA, getDailyReportDataRowMapper(), startDateTime, endDateTime, storeId);
	}

	@Override
	public String selectStoreIdsForAllSalesInThePeriods(PeriodInMillis previousYearPeriodInMillis,
			PeriodInMillis previousMonthPeriodInMillis, PeriodInMillis selectedMonthPeriodInMillis) {
		return getJdbcTemplate().queryForObject(SELECT_STORE_IDS_BY_SALE_TIMESTAMP_PERIODS, String.class,
				previousYearPeriodInMillis.getStartDateTime(), previousYearPeriodInMillis.getEndDateTime(),
				previousMonthPeriodInMillis.getStartDateTime(), previousMonthPeriodInMillis.getEndDateTime(),
				selectedMonthPeriodInMillis.getStartDateTime(), selectedMonthPeriodInMillis.getEndDateTime());
	}
	

	@Override
	public List<SalesByStore> searchSaleTurnoverForCompany(Long startDateMilliseconds, Long endDateMilliseconds,
			String storeIds) {
		String searchQuery = GET_SALES_DATA_FOR_THE_COMPANY;
		List<Object> argsList = new ArrayList<Object>();
		argsList.add(startDateMilliseconds);
		argsList.add(endDateMilliseconds);

		if (storeIds != null) {
			searchQuery += String.format(STORE_CRITERIA_QUERY, storeIds);
		}

		Object[] argsArr = new Object[argsList.size()];
		argsArr = argsList.toArray(argsArr);

		return getJdbcTemplate().query(searchQuery, argsArr, new SalesByStoreRowMapper());
	}

	@Override
	public Sale selectSaleByUniqueSaleId(String uniqueSaleId) {
		List<Sale> result = getJdbcTemplate().query(GET_ALL_SALES_QUERY + USI_CRITERIA_QUERY + GROUP_BY_SALE, new SaleRowMapper(), uniqueSaleId);
		
		return result.isEmpty() ? null : result.get(0);
	}
	
}
