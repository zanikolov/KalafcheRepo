package com.kalafche.dao;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.model.employee.AuthRole;
import com.kalafche.model.employee.Employee;

public interface EmployeeDao {

	public abstract List<Employee> getAllEmployees();
	
	public abstract Employee getEmployee(String username);

	public abstract Integer insertEmployee(Employee newEmployee) throws SQLException;

	public abstract void updateEmployee(Employee employee);

	public abstract List<Employee> getAllActiveEmployees();

	public abstract Boolean getIsEmployeeAdmin(String username);

	public abstract List<Employee> getEmployeesByIds(List<Integer> employeeIds);

	public abstract void insertEmployeeRole(Integer employeeId, String authRoleName);

	public abstract List<AuthRole> getAllRolesForEmployee(Integer employeeId);

	public abstract boolean checkIfEmployeeUsernameExists(Employee employee);

	public abstract Boolean getIsEmployeeManager(String username);

	public abstract List<Employee> getAllActiveEmployeesByStore(Integer storeId);

	public abstract Employee getEmployeesById(Integer employeeId);

}
