package com.kalafche.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.EmployeeDao;
import com.kalafche.model.AuthRole;
import com.kalafche.model.Employee;

@Service
public class EmployeeDaoImpl extends JdbcDaoSupport implements EmployeeDao {

	private static final String GET_EMPLOYEE_BY_USERNAME = "select e.id, e.username, e.name, e.store_id, e.job_responsibility_id from employee e where username = ?";
	private static final String GET_ALL_EMPLOYEE = "select "
			+ "e.id, "
			+ "username, "
			+ "password, "
			+ "e.name, "
			+ "store_id, "
			+ "concat(ks.city, ', ',ks.name) as store_name, "
			+ "job_responsibility_id, "
			+ "jr.name as job_responsibility_name,"
			+ "enabled "
			+ "from employee e "
			+ "join store ks on e.store_id = ks.id "
			+ "join job_responsibility jr on e.job_responsibility_id = jr.id ";
	private static final String ENABLED_CLAUSE = "where e.enabled is true ";
	private static final String ID_CLAUSE = "where e.id = ? ";
	private static final String STORE_ID_CLAUSE = "and e.store_id = ? ";
	private static final String IN_CLAUSE = "and e.id in (%s) ";
	private static final String ORDER_BY_ID_CLAUSE = "order by e.id ";
	private static final String ORDER_BY_ENABLED_CLAUSE = "order by enabled desc ";
	private static final String INSERT_EMPLOYEE = "insert into employee (name, username, password, store_id, job_responsibility_id, enabled) values (?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_EMPLOYEE = "update employee set name = ?, store_id = ?, enabled = ? where id = ?";
	private static final String IS_EMPLOYEE_ADMIN = "select " +
			"count(*) " +
			"from employee_role " +
			"where auth_role_id in " +
			"( " +
			"   select " +
			"   id " +
			"   from auth_role " +
			"   where name in ('ROLE_ADMIN','ROLE_SUPERADMIN') " +
			") " +
			"and employee_id=(select id from employee where username=?) ";
	
	private static final String INSERT_EMPLOYEE_ROLE = "insert into employee_role(employee_id, auth_role_id) "
			+ "values (?, (select id from auth_role where name = ?))";	
	private static final String GET_ALL_ROLES_FOR_EMPLOYEE = "select * from auth_role r join employee_role er on r.id = er.auth_role_id where er.employee_id = ?";
	private static final String CHECK_IF_EMPLOYEE_USERNAME_EXISTS = "select count(*) from employee where username = ? ";
	private static final String ID_NOT_CLAUSE = " and id <> ?";
	private static final String IS_EMPLOYEE_MANAGER = "select " +
			"count(*) " +
			"from employee_role " +
			"where auth_role_id in " +
			"( " +
			"   select " +
			"   id " +
			"   from auth_role " +
			"   where name in ('ROLE_MANAGER') " +
			") " +
			"and employee_id=(select id from employee where username=?) ";
	
	private BeanPropertyRowMapper<Employee> employeeRowMapper;
	
	private BeanPropertyRowMapper<AuthRole> authRoleRowMapper;
	
	@Autowired
	public EmployeeDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<Employee> getEmployeeRowMapper() {
		if (employeeRowMapper == null) {
			employeeRowMapper = new BeanPropertyRowMapper<Employee>(Employee.class);
			employeeRowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return employeeRowMapper;
	}
	
	private BeanPropertyRowMapper<AuthRole> getAuthRoleRowMapper() {
		if (authRoleRowMapper == null) {
			authRoleRowMapper = new BeanPropertyRowMapper<AuthRole>(AuthRole.class);
			authRoleRowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return authRoleRowMapper;
	}

	@Override
	public List<Employee> getAllEmployees() {
		List<Employee> employeeList = getJdbcTemplate().query(GET_ALL_EMPLOYEE, getEmployeeRowMapper());
		
		return employeeList;
	}

	@Override
	public Employee getEmployee(String username) {
		List<Employee> employees = getJdbcTemplate().query(GET_EMPLOYEE_BY_USERNAME, getEmployeeRowMapper(), username);

		
		return employees.isEmpty() ? null : employees.get(0);
	}

	@Override
	public Integer insertEmployee(Employee employee) throws SQLException {
		try (Connection connection = getDataSource().getConnection();
				PreparedStatement statement = connection.prepareStatement(
						INSERT_EMPLOYEE, Statement.RETURN_GENERATED_KEYS);) {
			statement.setString(1, employee.getName());
			statement.setString(2, employee.getUsername());
			statement.setString(3, employee.getPassword());
			statement.setInt(4, employee.getStoreId());
			statement.setInt(5, employee.getJobResponsibilityId());
			statement.setBoolean(6, employee.isEnabled());

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
	public void updateEmployee(Employee employee) {
		getJdbcTemplate().update(UPDATE_EMPLOYEE, employee.getName(), employee.getStoreId(), employee.isEnabled(), employee.getId());
		
	}

	@Override
	public List<Employee> getAllActiveEmployees() {
		return getJdbcTemplate().query(GET_ALL_EMPLOYEE + ENABLED_CLAUSE + ORDER_BY_ENABLED_CLAUSE, getEmployeeRowMapper());
	}
	
	@Override
	public List<Employee> getEmployeesByIds(List<Integer> employeeIds) {
		String commaSeparatedEmployeeIds = employeeIds.stream().map(id -> id.toString())
				.collect(Collectors.joining(","));
		return getJdbcTemplate().query(GET_ALL_EMPLOYEE + ENABLED_CLAUSE + String.format(IN_CLAUSE, commaSeparatedEmployeeIds) + ORDER_BY_ID_CLAUSE, getEmployeeRowMapper());
	}
	
	@Override
	public Boolean getIsEmployeeAdmin(String username) {
	    Integer result = getJdbcTemplate().queryForObject(IS_EMPLOYEE_ADMIN, Integer.class, username);

	    return result != null && result > 0;
	}
	
	@Override
	public void insertEmployeeRole(Integer employeeId, String authRoleName) {
		getJdbcTemplate().update(INSERT_EMPLOYEE_ROLE, employeeId, authRoleName);
	}
	
	@Override
	public List<AuthRole> getAllRolesForEmployee(Integer employeeId) {
		return getJdbcTemplate().query(GET_ALL_ROLES_FOR_EMPLOYEE, getAuthRoleRowMapper(), employeeId);
	}
	
	@Override
	public boolean checkIfEmployeeUsernameExists(Employee employee) {
		Integer exists = null;
		if (employee.getId() == null) {
			exists = getJdbcTemplate().queryForObject(CHECK_IF_EMPLOYEE_USERNAME_EXISTS, Integer.class, employee.getUsername());
		} else {
			exists = getJdbcTemplate().queryForObject(CHECK_IF_EMPLOYEE_USERNAME_EXISTS + ID_NOT_CLAUSE, Integer.class, employee.getUsername(), employee.getId());
		}
			
		return exists != null && exists > 0 ;
	}

	@Override
	public Boolean getIsEmployeeManager(String username) {
	    Integer result = getJdbcTemplate().queryForObject(IS_EMPLOYEE_MANAGER, Integer.class, username);

	    return result != null && result > 0;
	}

	@Override
	public List<Employee> getAllActiveEmployeesByStore(Integer storeId) {
		return getJdbcTemplate().query(GET_ALL_EMPLOYEE + ENABLED_CLAUSE + STORE_ID_CLAUSE + ORDER_BY_ENABLED_CLAUSE, getEmployeeRowMapper(), storeId);
	}

	@Override
	public Employee getEmployeesById(Integer employeeId) {
		List<Employee> result = getJdbcTemplate().query(GET_ALL_EMPLOYEE + ID_CLAUSE, getEmployeeRowMapper(), employeeId);
		
		return result.isEmpty() ? null : result.get(0);
	}

}
