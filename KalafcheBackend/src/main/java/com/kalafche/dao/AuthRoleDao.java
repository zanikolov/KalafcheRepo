package com.kalafche.dao;

import java.util.List;

import com.kalafche.model.employee.AuthRole;

public abstract interface AuthRoleDao {
	
	public abstract List<AuthRole> getAllAuthRoles();

	public abstract List<String> getAllRolesByEmployee(int employeeId);
}
