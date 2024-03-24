package com.kalafche.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.AuthRoleDao;
import com.kalafche.model.employee.AuthRole;

@Service
public class AuthRoleDaoImpl extends JdbcDaoSupport implements AuthRoleDao {

	private static final String GET_AUTH_ROLES_BY_EMPLOYEE_ID = "select name from auth_role ar " +
			"join employee_role er " +
			"on ar.id = er.auth_role_id " +
			"where er.employee_id = ? ";
	private static final String GET_ALL_AUTH_ROLES = "select * from auth_role";

	private BeanPropertyRowMapper<AuthRole> authRoleRowMapper;
	
	@Autowired
	public AuthRoleDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<AuthRole> getAuthRoleRowMapper() {
		if (authRoleRowMapper == null) {
			authRoleRowMapper = new BeanPropertyRowMapper<AuthRole>(AuthRole.class);
			authRoleRowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return authRoleRowMapper;
	}
	
	@Override
	public List<String> getAllRolesByEmployee(int employeeId) {
		return getJdbcTemplate().queryForList(GET_AUTH_ROLES_BY_EMPLOYEE_ID, String.class, employeeId);
	}
	
	@Override
	public List<AuthRole> getAllAuthRoles() {
		List<AuthRole> authRoles = getJdbcTemplate().query(GET_ALL_AUTH_ROLES, getAuthRoleRowMapper());

		return authRoles;
	}


}
