package com.kalafche.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.EmployeeDao;
import com.kalafche.model.Employee;
import com.kalafche.model.device.DeviceBrand;

@Service
public class EmployeeDaoImpl extends JdbcDaoSupport implements EmployeeDao {

	private static final String GET_EMPLOYEE_BY_USERNAME = "select u.id as user_id, e.id as id, u.username, e.name, e.kalafche_store_id, e.job_responsibility_id from user u "
			+ "join employee e on u.id = e.user_id where username = ?";
	private static final String GET_ALL_EMPLOYEE = "select "
			+ "u.id as user_id, "
			+ "e.id as id, "
			+ "u.username, "
			+ "u.password, "
			+ "e.name, "
			+ "e.kalafche_store_id, "
			+ "concat(ks.city, ', ',ks.name) as kalafche_store_name, "
			+ "e.job_responsibility_id, "
			+ "jr.name as job_responsibility_name,"
			+ "u.enabled "
			+ "from user u "
			+ "join employee e on u.id = e.user_id "
			+ "join kalafche_store ks on e.kalafche_store_id = ks.id "
			+ "join job_responsibility jr on e.job_responsibility_id = jr.id ";
	private static final String INSERT_EMPLOYEE = "insert into employee (name, kalafche_store_id, job_responsibility_id, user_id) values (?, ?, ?, ?)";
	
	private static final String UPDATE_EMPLOYEE = "update employee set name = ?, kalafche_store_id = ? where id = ?";

	private BeanPropertyRowMapper<Employee> rowMapper;
	
	@Autowired
	public EmployeeDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<Employee> getRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<Employee>(Employee.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return rowMapper;
	}

	@Override
	public List<Employee> getAllEmployees() {
		List<Employee> employeeList = getJdbcTemplate().query(GET_ALL_EMPLOYEE, getRowMapper());
		
		return employeeList;
	}

	@Override
	public Employee getEmployee(String username) {
		Employee employee = null;
		try {
			employee = getJdbcTemplate().queryForObject(GET_EMPLOYEE_BY_USERNAME, getRowMapper(), username);
		} catch(EmptyResultDataAccessException e) {
			e.printStackTrace();
		}
		
		return employee;
	}

	@Override
	public void insertEmployee(Employee newEmployee) {
		getJdbcTemplate().update(INSERT_EMPLOYEE, newEmployee.getName(), newEmployee.getKalafcheStoreId(), newEmployee.getJobResponsibilityId(), newEmployee.getUserId());
	}

	@Override
	public void updateEmployee(Employee employee) {
		getJdbcTemplate().update(UPDATE_EMPLOYEE, employee.getName(), employee.getKalafcheStoreId(), employee.getId());
		
	}

}
