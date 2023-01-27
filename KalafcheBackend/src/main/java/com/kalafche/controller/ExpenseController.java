package com.kalafche.controller;

import java.math.BigDecimal;
import java.sql.SQLException;
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
import org.springframework.web.multipart.MultipartFile;

import com.kalafche.model.ExpenseReport;
import com.kalafche.model.ExpenseType;
import com.kalafche.service.ExpenseService;

@CrossOrigin
@RestController
@RequestMapping({ "/expense" })
public class ExpenseController {

	@Autowired
	private ExpenseService expenseService;

	@GetMapping
	public ExpenseReport searchExpenses(@RequestParam(value = "startDateMilliseconds") Long startDateMilliseconds, 
			@RequestParam(value = "endDateMilliseconds") Long endDateMilliseconds, @RequestParam(value = "storeIds") String storeIds) {
		return expenseService.searchExpenses(startDateMilliseconds, endDateMilliseconds, storeIds);
	}

	@PostMapping
	public void submitExpense(@RequestParam(value = "expenseImage", required = false) MultipartFile expenseImage,
			@RequestParam("typeCode") String typeCode, @RequestParam("description") String description,
			@RequestParam("price") BigDecimal price,
			@RequestParam(value = "storeId", required = false) Integer storeId) {
		expenseService.createExpense(typeCode, description, price, storeId, expenseImage);
	}

	@GetMapping("/type")
	public List<ExpenseType> getExpenseTypes() {
		return expenseService.getExpenseTypes();
	}
	
	@PutMapping("/type")
	public ExpenseType createExpenseType(@RequestBody ExpenseType expenseType) throws SQLException {
		return expenseService.createExpenseType(expenseType);
	}
	
	@PostMapping("/type")
	public void updateExpenseType(@RequestBody ExpenseType expenseType) throws SQLException {
		expenseService.updateExpenseType(expenseType);
	}

}
