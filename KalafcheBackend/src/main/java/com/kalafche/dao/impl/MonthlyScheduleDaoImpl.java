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

import com.kalafche.dao.MonthlyScheduleDao;
import com.kalafche.model.MonthlySchedule;

@Service
public class MonthlyScheduleDaoImpl extends JdbcDaoSupport implements MonthlyScheduleDao {

	private static final String INSERT_MONTHLY_SCHEDULE = "insert into monthly_schedule " +
			"(create_timestamp,created_by,last_update_timestamp,updated_by,store_id,is_finalized,month,year) VALUES " +
			"(?               ,?         ,?                    ,?         ,?       ,?           ,?     ,?) ";
	private static final String SELECT_MONTHLY_SCHEDULE = "select * from monthly_schedule where store_id = ? and month = ? and year = ?";
//	
//	private static final String PERIOD_CRITERIA_QUERY = " where create_timestamp between ? and ?";
//	private static final String STORE_CRITERIA_QUERY = " and ks.id in (%s)";
//	private static final String ORDER_BY = " order by create_timestamp, ks.id";
//	
//	private static final String UPDATE_DAILY_STORE_REPORT = "update daily_store_report " +
//			"set INCOME = ?, " +
//			"COLLECTED = ?, " +
//			"EXPENSE = ?, " +
//			"CARD_PAYMENT = ?, " +
//			"INITIAL_BALANCE = ?, " +
//			"FINAL_BALANCE = ?, " +
//			"SOLD_ITEMS_COUNT = ?, " +
//			"REFUNDED_ITEMS_COUNT = ?, " +
//			"DESCRIPTION = ?, " +
//			"LAST_UPDATE_TIMESTAMP = ?, " +
//			"UPDATED_BY = ? " +
//			"where id = ? ";
	
	private BeanPropertyRowMapper<MonthlySchedule> rowMapper;
	
	@Autowired
	public MonthlyScheduleDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<MonthlySchedule> getRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<MonthlySchedule>(MonthlySchedule.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return rowMapper;
	}

	@Override
	public Integer insertMonthlySchedule(MonthlySchedule monthlySchedule) throws SQLException {
		try (Connection connection = getDataSource().getConnection();
				PreparedStatement statement = connection.prepareStatement(
						INSERT_MONTHLY_SCHEDULE, Statement.RETURN_GENERATED_KEYS);) {
			statement.setLong(1, monthlySchedule.getCreateTimestamp());
			statement.setInt(2, monthlySchedule.getCreatedByEmployeeId());
			statement.setLong(3, monthlySchedule.getLastUpdateTimestamp());
			statement.setInt(4, monthlySchedule.getUpdatedByEmployeeId());
			statement.setInt(5, monthlySchedule.getStoreId());
			statement.setBoolean(6, monthlySchedule.getIsFinalized());
			statement.setInt(7, monthlySchedule.getMonth());
			statement.setInt(8, monthlySchedule.getYear());

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException(
						"Creating the employee failed, no rows affected.");
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1);
				} else {
					throw new SQLException(
							"Creating the employee failed, no rows affected.");
				}
			}
		}
		
	}

	@Override
	public MonthlySchedule getMonthlySchedule(Integer storeId, Integer month, Integer year) {
		List<MonthlySchedule> monthlySchedule = getJdbcTemplate().query(SELECT_MONTHLY_SCHEDULE, new Object[] {storeId, month, year}, getRowMapper());
		return monthlySchedule != null && !monthlySchedule.isEmpty() ? monthlySchedule.get(0) : null;
	}

	@Override
	public void updateDailyStoreReport(MonthlySchedule monthlySchedule) {
		// TODO Auto-generated method stub
		
	}
	

//
//	@Override
//	public void updateDailyStoreReport(DailyStoreReport dailyStoreReport) {
//		getJdbcTemplate().update(UPDATE_DAILY_STORE_REPORT, dailyStoreReport.getIncome(),
//				dailyStoreReport.getCollected(), dailyStoreReport.getExpense(), dailyStoreReport.getCardPayment(),
//				dailyStoreReport.getInitialBalance(), dailyStoreReport.getFinalBalance(),
//				dailyStoreReport.getSoldItemsCount(), dailyStoreReport.getRefundedItemsCount(),
//				dailyStoreReport.getDescription(), dailyStoreReport.getLastUpdateTimestamp(),
//				dailyStoreReport.getUpdatedByEmployeeId(), dailyStoreReport.getId());
//	}
}
