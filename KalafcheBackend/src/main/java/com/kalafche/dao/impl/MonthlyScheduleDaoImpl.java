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
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.MonthlyScheduleDao;
import com.kalafche.model.employee.Employee;
import com.kalafche.model.schedule.EmployeeHours;
import com.kalafche.model.schedule.MonthlySchedule;

@Service
public class MonthlyScheduleDaoImpl extends JdbcDaoSupport implements MonthlyScheduleDao {

	private static final String INSERT_MONTHLY_SCHEDULE = "insert into monthly_schedule " +
			"(create_timestamp,created_by,store_id,is_finalized,is_present_form,month,year,working_hours_in_minutes,WORKING_HOURS) VALUES " +
			"(?               ,?         ,?       ,?           ,?              ,?    ,?   ,?                       ,?) ";
	private static final String SELECT_MONTHLY_SCHEDULE = "select ms.*, CONCAT(s.city, ', ', s.name) as store_name, s.company_id as company_id, c.name as company_name from monthly_schedule ms join store s on ms.store_id = s.id join company c on s.company_id = c.id ";
	
	private static final String IS_PRESENT_CLAUSE = "where is_present_form = ? "; 
	
	private static final String IS_FINALIZED_CLAUSE = "and is_finalized = ? ";
	
	private static final String STORE_CLAUSE = "and store_id = ? ";
	
	private static final String MONTH_YEAR_CLAUSE = "and month = ? and year = ? ";
	
	private static final String COMPANY_ID_CLAUSE = "and s.company_id = ? ";
	
	private static final String ID_CLAUSE = "where ms.id = ? ";
	
	private static final String ORDER_BY_COMPANY_ID = "order by s.company_id ";
	
	private static final String SELECT_EMPLOYEE_HOURS = "select ds.employee_id, " +
			"e.name, " +
			"sum(ws.duration_minutes), " +
			"sum(if(ws.code = 'PL', 1, 0)) as paid_leaves, " +
			"sum(if(ws.code = 'UL', 1, 0)) as unpaid_leaves, " +
			"sum(if(ws.code = 'SL', 1, 0)) as sick_leaves, " +
			"sum(if(cal.is_holiday = true and ws.code not in ('', 'PL', 'UL', 'SL'), ws.duration_minutes, 0)) as work_during_holidays " +
			"from daily_shift ds " +
			"join calendar cal on ds.calendar_id = cal.id " +
			"join employee e on ds.EMPLOYEE_ID = e.id " +
			"left join working_shift ws on ds.WORKING_SHIFT_ID = ws.id " +
			"where MONTHLY_SCHEDULE_ID = ? " +
			"group by ds.employee_id, e.name " +
			"order by ds.employee_id ";

	private static final String UPDATE_MONTHLY_SCHEDULE = "update monthly_schedule set last_update_timestamp = ?, updated_by = ?, is_finalized = ? where id = ?";
	
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
			statement.setInt(3, monthlySchedule.getStoreId());
			statement.setBoolean(4, monthlySchedule.getIsFinalized());
			statement.setBoolean(5, monthlySchedule.getIsPresentForm());
			statement.setInt(6, monthlySchedule.getMonth());
			statement.setInt(7, monthlySchedule.getYear());
			statement.setInt(8, monthlySchedule.getWorkingHoursInMinutes());
			statement.setString(9, monthlySchedule.getWorkingHours());

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
	public MonthlySchedule getMonthlySchedule(Integer storeId, Integer month, Integer year, Boolean isPresentForm) {
		List<MonthlySchedule> monthlySchedule = getJdbcTemplate().query(
				SELECT_MONTHLY_SCHEDULE + IS_PRESENT_CLAUSE + STORE_CLAUSE + MONTH_YEAR_CLAUSE, new Object[] { isPresentForm, storeId, month, year },
				getRowMapper());
		return monthlySchedule != null && !monthlySchedule.isEmpty() ? monthlySchedule.get(0) : null;
	}

	@Override
	public MonthlySchedule getMonthlyScheduleById(Integer monthlyScheduleId) {
		List<MonthlySchedule> monthlySchedule = getJdbcTemplate().query(SELECT_MONTHLY_SCHEDULE + ID_CLAUSE, getRowMapper(), monthlyScheduleId);
		return monthlySchedule != null && !monthlySchedule.isEmpty() ? monthlySchedule.get(0) : null;
	}
	
	@Override
	public void updateMonthlySchedule(MonthlySchedule monthlySchedule) {
		getJdbcTemplate().update(UPDATE_MONTHLY_SCHEDULE, monthlySchedule.getLastUpdateTimestamp(),
				monthlySchedule.getUpdatedByEmployeeId(), monthlySchedule.getIsFinalized(), monthlySchedule.getId());
	}	
	
	@Override
	public List<EmployeeHours> getEmployeeHoursByMonthlyScheduleId(Integer monthlyScheduleId) {
		return getJdbcTemplate().query(SELECT_EMPLOYEE_HOURS, new RowMapper<EmployeeHours>() {
		    @Override
		    public EmployeeHours mapRow(ResultSet rs, int rowNum) throws SQLException {
		    	Employee employee = new Employee();
		    	employee.setId(rs.getInt(1));
		    	employee.setName(rs.getString(2));
		    	EmployeeHours employeeHours = new EmployeeHours();
		    	employeeHours.setEmployee(employee);
		    	employeeHours.setMinutes(rs.getInt(3));
		    	employeeHours.setPaidLeaves(rs.getInt(4));
		    	employeeHours.setUnpaidLeaves(rs.getInt(5));
		    	employeeHours.setSickLeaves(rs.getInt(6));
		    	employeeHours.setWorkDuringHolidaysInMinutes(rs.getInt(7));
		        return employeeHours;
		    }
		}, monthlyScheduleId);
	}

	@Override
	public List<MonthlySchedule> getMonthlySchedules(Integer month, Integer year, boolean isPresentForm,
			boolean isFinalized) {
		return getJdbcTemplate().query(
				SELECT_MONTHLY_SCHEDULE + IS_PRESENT_CLAUSE + IS_FINALIZED_CLAUSE + MONTH_YEAR_CLAUSE + ORDER_BY_COMPANY_ID, new Object[] { isPresentForm, isFinalized, month, year },
				getRowMapper());
	}

	@Override
	public List<MonthlySchedule> getMonthlySchedulesByCompanyId(Integer month, Integer year, Integer companyId,
			boolean isPresentForm, boolean isFinalized) {
		return getJdbcTemplate().query(
				SELECT_MONTHLY_SCHEDULE + IS_PRESENT_CLAUSE + IS_FINALIZED_CLAUSE + MONTH_YEAR_CLAUSE
						+ COMPANY_ID_CLAUSE + ORDER_BY_COMPANY_ID,
				new Object[] { isPresentForm, isFinalized, month, year, companyId }, getRowMapper());
	}

}
