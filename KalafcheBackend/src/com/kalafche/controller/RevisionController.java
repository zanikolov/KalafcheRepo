package com.kalafche.controller;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.dao.EmployeeDao;
import com.kalafche.exceptions.CommonException;
import com.kalafche.model.Employee;
import com.kalafche.service.EmployeeService;

@CrossOrigin
@RestController
@RequestMapping({ "/service/revision" })
public class RevisionController {

	@Autowired
	EmployeeService employeeService;
	
	@Autowired
	EmployeeDao employeeDao;
	
	@RequestMapping(value = { "/getEmployee" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public Employee getEmployee() {
		User loggedUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = loggedUser.getUsername(); //get logged in username
		Employee employee = employeeService.getEmployeeInfo(username);
		
		return employee;
	}
	
	@RequestMapping(value = { "/admin/getEmployeeByName" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET }, params = { "username" })
	public Employee getEmployeeByName(@RequestParam(value = "username") String username) {
		Employee employee = employeeService.getEmployeeInfo(username);
		
		return employee;
	}
	
	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public void createEmployee(@RequestBody Employee employee, HttpServletResponse response) {
		
		try {
			employee.setJobResponsibilityId(1);
			employeeService.createEmployee(employee);
		} catch (CommonException e) {
			e.printStackTrace();
			response.setStatus(HttpStatus.BAD_REQUEST.value(), "Error during creation of new employee");
		}
	}
	
	@RequestMapping(value = { "/all" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public List<Employee> getAllEmployee() {
		List<Employee> employeeList = employeeDao.getAllEmployees();
		
		return employeeList;
	}
	
	
	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.PUT })
	public void updateEmployee(@RequestBody Employee employee) {
		employeeDao.updateEmployee(employee);		
	}
	
	@RequestMapping(value = { "/disable" }, method = {org.springframework.web.bind.annotation.RequestMethod.PUT })
	public void deactivateAccount(@RequestBody int userId) {
		employeeService.deactivateAccount(userId);		
	}
	
}
