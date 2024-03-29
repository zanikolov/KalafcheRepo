package com.kalafche.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.dao.EmployeeDao;
import com.kalafche.model.employee.Employee;
import com.kalafche.model.employee.LoginHistory;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.LoginHistoryService;

@CrossOrigin
@RestController
@RequestMapping({ "/employee" })
public class EmployeeController {

	@Autowired
	EmployeeService employeeService;
	
	@Autowired
	LoginHistoryService loginHistoryService;
	
	@Autowired
	EmployeeDao employeeDao;
	
	@GetMapping("/login")
	public Employee loginEmployee() {
		Employee employee = employeeService.getEmployeeInfo();
		
		return employee;
	}
	
	@GetMapping
	public List<Employee> getAllEmployees() {
		return employeeService.getAllEmployees();
	}
		
	@GetMapping("/store")
	public List<Employee> getEmployeesByStoreId(@RequestParam(value = "storeId") Integer storeId) {
		return employeeService.getEmployeesByStoreId(storeId);
	}
	
	
	@GetMapping("/groupedByStore")
	public HashMap<String, List<Employee>> getAllActiveEmployeesGroupedByStore() {
		return employeeService.getAllActiveEmployeesGroupedByStore();
	}
	
	@GetMapping("/enabled")
	public List<Employee> getAllActiveEmployees() {	
		return employeeService.getAllActiveEmployees();
	}
	
	@GetMapping("/firstLoginForDate")
	public List<LoginHistory> getFirstLoginForDate(@RequestParam(value = "dateMillis") Long dateMillis) {
		return loginHistoryService.getLoginHistoryRecords(dateMillis);
	}
	
	@PutMapping
	public void createEmployee(@RequestBody Employee employee) throws SQLException {
		employeeService.createEmployee(employee);
	}
	
	@PostMapping
	public void updateEmployee(@RequestBody Employee employee) {
		employeeDao.updateEmployee(employee);		
	}	

}
