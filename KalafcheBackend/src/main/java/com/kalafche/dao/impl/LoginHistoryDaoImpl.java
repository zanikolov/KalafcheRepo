package com.kalafche.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.LoginHistoryDao;
import com.kalafche.model.LoginHistory;

@Service
public class LoginHistoryDaoImpl extends JdbcDaoSupport implements LoginHistoryDao {

	private static final String INSERT_LOGIN_HISTORY_RECORD = "insert into login_history (employee_id, login_timestamp) values (?,?)";
	
	private static final String SELECT_FIRST_LOGIN_FOR_DATE = "select " +
			"lh.id, e.id as employee_id,e.name as employee_name,concat(ks.city,' ,',ks.name) as employee_store_name,min(lh.login_timestamp) as login_timestamp " +
			"from login_history lh " +
			"join employee e on lh.employee_id=e.id " +
			"join store ks on ks.id=e.store_id " +
			"where lh.login_timestamp between ? and ? + 24*60*60*1000 " +
			"group by e.id,e.name " + 
			"order by lh.login_timestamp ";

	private BeanPropertyRowMapper<LoginHistory> rowMapper;
	
	@Autowired
	public LoginHistoryDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<LoginHistory> getRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<LoginHistory>(LoginHistory.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return rowMapper;
	}

	@Override
	public void insertLoginHistoryRecord(int employeeId, long loginTimestamp) {
		getJdbcTemplate().update(INSERT_LOGIN_HISTORY_RECORD, employeeId, loginTimestamp);	
	}

	@Override
	public List<LoginHistory> selectFirstLoginForDate(long dateMillis) {
		return getJdbcTemplate().query(
				SELECT_FIRST_LOGIN_FOR_DATE, new Object[]{dateMillis, dateMillis}, getRowMapper());
	}


}
