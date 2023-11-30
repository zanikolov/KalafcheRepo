package com.kalafche.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.CalendarDao;
import com.kalafche.model.DayDto;

@Service
public class CalendarDaoImpl extends JdbcDaoSupport implements CalendarDao {

	private static final String INSERT_DAY = "insert into calendar (day, month, year, day_of_the_week, date, is_holiday) values (?, ?, ?, ?, ?, ?)";

	private BeanPropertyRowMapper<DayDto> rowMapper;
	
	@Autowired
	public CalendarDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<DayDto> getRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<DayDto>(DayDto.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return rowMapper;
	}

	@Override
	public void insertDays(List<DayDto> days) {
		try (Connection connection = getDataSource().getConnection();
				PreparedStatement statement = connection.prepareStatement(INSERT_DAY)) {
			connection.setAutoCommit(false);
			
			for (DayDto day : days) {
				statement.setInt(1, day.getDay());
				statement.setInt(2, day.getMonth());
				statement.setInt(3, day.getYear());
				statement.setInt(4, day.getDayOfWeek());
				statement.setDate(5, day.getDate());
				statement.setBoolean(6, day.getIsHoliday());
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
