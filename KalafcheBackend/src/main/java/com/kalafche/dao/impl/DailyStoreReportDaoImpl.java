package com.kalafche.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.DailyStoreReportDao;
import com.kalafche.model.DailyStoreReport;
import com.kalafche.model.DayInMillis;

@Service
public class DailyStoreReportDaoImpl extends JdbcDaoSupport implements DailyStoreReportDao {

	private static final String INSERT_DAILY_STORE_REPORT = "insert into daily_store_report " +
			"(create_timestamp,employee_id,last_update_timestamp,updated_by,store_id,income,collected,expense,card_payment,initial_balance,final_balance,sold_items_count,refunded_items_count) VALUES " +
			"(?               ,?          ,?                    ,?         ,?       ,?     ,?        ,?      ,?           ,?              ,?            ,?               ,?                   ) ";
	private static final String SELECT_DAILY_STORE_REPORT_BY_STORE_AND_DAY = "select * from daily_store_report where store_id = ? and create_timestamp between ? and ?";
	
	private static final String SEARCH_DAILY_STORE_REPORT_QUERY = "select " +
			"dsr.id, " +
			"dsr.create_timestamp as create_timestamp, " +
			"em.id as employee_id, " +
			"em.name as employee_name, " +
			"ks.id as store_id, " +
			"CONCAT(ks.city, ', ', ks.name) as store_name, " +
			"dsr.income as income, " +
			"dsr.collected as collected, " +
			"dsr.expense as expense, " +
			"dsr.card_payment as card_payment, " +
			"dsr.initial_balance as initial_balance, " +
			"dsr.final_balance as final_balance, " +
			"dsr.sold_items_count as sold_items_count, " +
			"dsr.refunded_items_count as refunded_items_count " +
			"from daily_store_report dsr " +
			"join store ks on ks.id = dsr.store_id " +
			"left join employee em on em.id = dsr.employee_id ";
	
	private static final String PERIOD_CRITERIA_QUERY = " where create_timestamp between ? and ?";
	private static final String STORE_CRITERIA_QUERY = " and ks.id in (%s)";
	private static final String ORDER_BY = " order by create_timestamp, ks.id";
	
	private BeanPropertyRowMapper<DailyStoreReport> rowMapper;
	
	@Autowired
	public DailyStoreReportDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<DailyStoreReport> getRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<DailyStoreReport>(DailyStoreReport.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return rowMapper;
	}
	
	@Override
	public void insertDailyStoreReport(DailyStoreReport dailyStoreReport) {
		getJdbcTemplate().update(INSERT_DAILY_STORE_REPORT, dailyStoreReport.getCreateTimestamp(),
				dailyStoreReport.getEmployeeId(), dailyStoreReport.getLastUpdateTimestamp(),
				dailyStoreReport.getUpdatedByEmployeeId(), dailyStoreReport.getStoreId(), dailyStoreReport.getIncome(),
				dailyStoreReport.getCollected(), dailyStoreReport.getExpense(), dailyStoreReport.getCardPayment(),
				dailyStoreReport.getInitialBalance(), dailyStoreReport.getFinalBalance(),
				dailyStoreReport.getSoldItemsCount(), dailyStoreReport.getRefundedItemsCount());
	}
	
	@Override
	public DailyStoreReport getDailyStoreReport(Integer storeId, DayInMillis day) {
		List<DailyStoreReport> reports = getJdbcTemplate().query(SELECT_DAILY_STORE_REPORT_BY_STORE_AND_DAY, new Object[] {storeId, day.getStartDateTime(), day.getEndDateTime()}, getRowMapper());
		return reports != null && !reports.isEmpty() ? reports.get(0) : null;
	}

	@Override
	public List<DailyStoreReport> searchDailyStoreReports(Long startDateMilliseconds, Long endDateMilliseconds,
			String storeIds) {
		String searchQuery = SEARCH_DAILY_STORE_REPORT_QUERY + PERIOD_CRITERIA_QUERY
				+ String.format(STORE_CRITERIA_QUERY, storeIds) + ORDER_BY;
		List<Object> argsList = new ArrayList<Object>();
		argsList.add(startDateMilliseconds);
		argsList.add(endDateMilliseconds);

		return getJdbcTemplate().query(searchQuery, getRowMapper(), startDateMilliseconds, endDateMilliseconds);
	}
}
