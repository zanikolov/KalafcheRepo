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

import com.kalafche.dao.DailyShiftDao;
import com.kalafche.model.DailyShift;

@Service
public class DailyShiftDaoImpl extends JdbcDaoSupport implements DailyShiftDao {

	private static final String INSERT_DAILY_SHIFT = "insert into daily_shift " +
			"(create_timestamp,created_by,employee_id,calendar_id,monthly_schedule_id, working_shift_id) VALUES " +
			"(?               ,?         ,?          ,?          ,?                  ,?) ";
	
	private static final String GET_DAILY_SHIFTS = "select " +
			"ds.id, " +
			"ds.employee_id, " +
			"ds.calendar_id, " +
			"ds.WORKING_SHIFT_ID, " +
			"ds.MONTHLY_SCHEDULE_ID, " +
			"c.date as calendar_date, " +
			"ws.name as working_shift_name, " +
			"ws.start_time as working_shift_start_time, " +
			"ws.end_time as working_shift_end_time, " +
			"ws.duration_minutes as working_shift_duration_minutes " +
			"from " +
			"keysoo.daily_shift ds " +
			"join calendar c on ds.CALENDAR_ID = c.id " +
			"left join working_shift ws on ds.working_shift_id = ws.id ";
	
	private static final String MONTHLY_SCHEDULE_ID_CLAUSE = "where monthly_schedule_id = ? ";
	
	private static final String ORDER_BY_CLAUSE = "order by c.day , c.month , c.year , ds.EMPLOYEE_ID"; 
	
	private static final String UPDATE_DAILY_SHIFT = "update daily_shift set working_shift_id = ?, last_update_timestamp = ?, updated_by = ? where id = ?";

	private BeanPropertyRowMapper<DailyShift> dailyShiftRowMapper;
	
	@Autowired
	public DailyShiftDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<DailyShift> getDailyShiftRowMapper() {
		if (dailyShiftRowMapper == null) {
			dailyShiftRowMapper = new BeanPropertyRowMapper<DailyShift>(DailyShift.class);
			dailyShiftRowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return dailyShiftRowMapper;
	}
	
	@Override
	public Integer insertDailyShift(DailyShift dailyShift) throws SQLException {
		try (Connection connection = getDataSource().getConnection();
				PreparedStatement statement = connection.prepareStatement(
						INSERT_DAILY_SHIFT, Statement.RETURN_GENERATED_KEYS);) {
			statement.setLong(1, dailyShift.getCreateTimestamp());
			statement.setInt(2, dailyShift.getCreatedByEmployeeId());
			statement.setInt(3, dailyShift.getEmployeeId());
			statement.setInt(4, dailyShift.getCalendarId());
			statement.setInt(5, dailyShift.getMonthlyScheduleId());
			statement.setObject(6, dailyShift.getWorkingShiftId());

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
	public List<DailyShift> getDailyShiftByMonthlyScheduleId(Integer monthlyScheduleId) {
		return getJdbcTemplate().query(GET_DAILY_SHIFTS + MONTHLY_SCHEDULE_ID_CLAUSE + ORDER_BY_CLAUSE, getDailyShiftRowMapper(), monthlyScheduleId);
	}

	@Override
	public void updateDailyShift(DailyShift dailyShift) {
		getJdbcTemplate().update(UPDATE_DAILY_SHIFT, dailyShift.getWorkingShiftId(), dailyShift.getLastUpdateTimestamp(), dailyShift.getUpdatedByEmployeeId(), dailyShift.getId());		
	}

	@Override
	public void createDailyShiftBatch(List<DailyShift> dailyShifts) {
		try (Connection connection = getDataSource().getConnection();
				PreparedStatement statement = connection.prepareStatement(INSERT_DAILY_SHIFT)) {
			connection.setAutoCommit(false);
			
			for (DailyShift dailyShift : dailyShifts) {
				statement.setLong(1, dailyShift.getCreateTimestamp());
				statement.setInt(2, dailyShift.getCreatedByEmployeeId());
				statement.setInt(3, dailyShift.getEmployeeId());
				statement.setInt(4, dailyShift.getCalendarId());
				statement.setInt(5, dailyShift.getMonthlyScheduleId());
				statement.setObject(6, dailyShift.getWorkingShiftId());
				statement.addBatch();
			}
			statement.executeBatch();
			connection.commit();
		}
		catch(SQLException e) {
			e.printStackTrace();
		} 
	}

}
