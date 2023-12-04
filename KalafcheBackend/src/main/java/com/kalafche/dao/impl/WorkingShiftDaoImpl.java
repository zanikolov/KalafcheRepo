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

import com.kalafche.dao.WorkingShiftDao;
import com.kalafche.model.WorkingShift;

@Service
public class WorkingShiftDaoImpl extends JdbcDaoSupport implements WorkingShiftDao {
	
	private static final String INSERT_WORKING_SHIFT = "INSERT INTO working_shift (CREATE_TIMESTAMP, CREATED_BY, NAME, CODE, START_TIME_MINUTES, START_TIME, END_TIME_MINUTES, END_TIME, DURATION_MINUTES, DURATION) " +
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?); ";
	
	private static final String UPDATE_WORKING_SHIFT = "update working_shift set name = ?, updated_by = ?, last_update_timestamp = ?, start_time_minutes = ?, start_time = ?, end_time_minutes = ?, end_time = ?, duration_minutes = ?, duration = ? where id = ?";
	
	private static final String SELECT_WORKING_SHIFTS = "select * from working_shift;";

	private BeanPropertyRowMapper<WorkingShift> rowMapper;
	
	@Autowired
	public WorkingShiftDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<WorkingShift> getWorkingShiftRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<WorkingShift>(WorkingShift.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return rowMapper;
	}
	
	@Override
	public Integer insertWorkingShift(WorkingShift workingShift) throws SQLException {
		try (Connection connection = getDataSource().getConnection();
				PreparedStatement statement = connection.prepareStatement(
						INSERT_WORKING_SHIFT, Statement.RETURN_GENERATED_KEYS);) {
			statement.setLong(1, workingShift.getCreateTimestamp());
			statement.setInt(2, workingShift.getCreatedByEmployeeId());
			statement.setString(3, workingShift.getName());
			statement.setString(4, workingShift.getCode());
			statement.setInt(5, workingShift.getStartTimeMinutes());
			statement.setString(6, workingShift.getStartTime());
			statement.setInt(7, workingShift.getEndTimeMinutes());
			statement.setString(8, workingShift.getEndTime());
			statement.setInt(9, workingShift.getDurationMinutes());
			statement.setString(10, workingShift.getDuration());

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException(
						"Creating the discount campaign " + workingShift.getCreateTimestamp() + " failed, no rows affected.");
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1);
				} else {
					throw new SQLException(
							"Creating the discount campaign " + workingShift.getCreateTimestamp() + " failed, no rows affected.");
				}
			}
		}
	}

	@Override
	public List<WorkingShift> selectAllWorkingShifts() {
		return getJdbcTemplate().query(SELECT_WORKING_SHIFTS, getWorkingShiftRowMapper());
	}

	@Override
	public void updateWorkingShift(WorkingShift workingShift) {
		getJdbcTemplate().update(UPDATE_WORKING_SHIFT, workingShift.getName(), workingShift.getUpdatedByEmployeeId(),
				workingShift.getLastUpdateTimestamp(), workingShift.getStartTimeMinutes(), workingShift.getStartTime(),
				workingShift.getEndTimeMinutes(), workingShift.getEndTime(), workingShift.getDurationMinutes(),
				workingShift.getDuration(), workingShift.getId());
	}

}
