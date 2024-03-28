package com.kalafche.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kalafche.dao.AuthRoleDao;
import com.kalafche.dao.EmployeeDao;
import com.kalafche.exceptions.DuplicationException;
import com.kalafche.model.StoreDto;
import com.kalafche.model.employee.Employee;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.EntityService;
import com.kalafche.service.LoginHistoryService;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	EmployeeDao employeeDao;
	
	@Autowired
	AuthRoleDao authRoleDao;
	
	@Autowired
	EntityService entityService;
	
	@Autowired
	LoginHistoryService loginHistoryService;
	
	@Override
	@Transactional
	public Employee getEmployeeInfo() {		
		Employee employee = getLoggedInEmployee();
		if (employee == null) {
			return null;
		}
		
		if(employee.getStoreId() == null) {
			employee.setStoreId(0);
		}
		loginHistoryService.trackLoginHistory(employee.getId());
		
		employee.setRoles(authRoleDao.getAllRolesByEmployee(employee.getId()));
		
		return employee;
	}
	
	@Override
	public List<Employee> getAllEmployees() {
		return employeeDao.getAllEmployees();
	}

	public List<Employee> getAllActiveEmployees() {
		return employeeDao.getAllActiveEmployees();
	}
	
	@Override
	public void updateEmployee(Employee employee) {
		employeeDao.updateEmployee(employee);
	}
	
	@Override
	@Transactional
	public void createEmployee(Employee employee) throws SQLException {
		validateUsername(employee);
		employee.setJobResponsibilityId(1);
		Integer employeeId = employeeDao.insertEmployee(employee);
		employeeDao.insertEmployeeRole(employeeId, "ROLE_USER");
	}
	
	@Override
	public String getLoggedInEmployeeUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();		
	}
	
	@Override
	public Employee getLoggedInEmployee() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();		
		Employee employee = employeeDao.getEmployee(username);
		employee.setRoles(authRoleDao.getAllRolesByEmployee(employee.getId()));
		
		return employee;
	}
	
	@Override
	public Boolean isLoggedInEmployeeAdmin() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();	
		return employeeDao.getIsEmployeeAdmin(username);
	}
	
	@Override
	public Boolean isLoggedInEmployeeManager() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();	
		return employeeDao.getIsEmployeeManager(username);
	}
	
	@Override
	public List<Employee> getEmployeesByIds(List<Integer> employeeIds) {
		return employeeDao.getEmployeesByIds(employeeIds);
	}
	
	@Override
	public Employee getEmployeesById(Integer employeeId) {
		return employeeDao.getEmployeesById(employeeId);
	}
	
	@Override
	public Employee getEmployeeByUsername(String username) {
		return employeeDao.getEmployee(username);
	}
	
	private void validateUsername(Employee employee) {
		if (employeeDao.checkIfEmployeeUsernameExists(employee)) {
			throw new DuplicationException("username", "Username duplication.");
		}
	}

	@Override
	public List<Employee> getEmployeesByStoreId(Integer storeId) {
		return employeeDao.getAllActiveEmployeesByStore(storeId);
	}

	@Override
	public HashMap<String, List<Employee>> getAllActiveEmployeesGroupedByStore() {
		HashMap<String, List<Employee>> employeesGroupedByStore = new HashMap<String, List<Employee>>();
		List<StoreDto> stores = entityService.getStores(false);
		for (StoreDto store : stores) {
			 employeesGroupedByStore.put(store.getCity() + ", " + store.getName(), getEmployeesByStoreId(store.getId()));
		}
		
		return employeesGroupedByStore;
	}
	
}
