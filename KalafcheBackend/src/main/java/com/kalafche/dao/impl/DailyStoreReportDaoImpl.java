package com.kalafche.dao.impl;

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
		return getJdbcTemplate().queryForObject(SELECT_DAILY_STORE_REPORT_BY_STORE_AND_DAY, getRowMapper(), storeId, day.getStartDateTime(), day.getEndDateTime());
	}

}
