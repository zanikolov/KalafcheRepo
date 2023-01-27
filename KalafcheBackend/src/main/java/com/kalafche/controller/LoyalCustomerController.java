package com.kalafche.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.model.LoyalCustomer;
import com.kalafche.service.LoyalCustomerService;

@CrossOrigin
@RestController
@RequestMapping({ "/loyalCustomer" })
public class LoyalCustomerController {
	
	@Autowired
	private LoyalCustomerService loyalCustomerService;

	@GetMapping
	public List<LoyalCustomer> getAllLoyalCustomers() {
		return loyalCustomerService.getAllLoyalCustomers();
	}
	
	@GetMapping("/{code}")
	public LoyalCustomer getLoyalCustomerByCode(@PathVariable (value = "code") String code) {
		return loyalCustomerService.getLoyalCustomer(code);
	}
	
	@PutMapping
	public void createLoyalCustomer(@RequestBody LoyalCustomer loyalCustomer) {
		loyalCustomerService.createLoyalCustomer(loyalCustomer);
	}
	
	@PostMapping
	public void updateLoyalCustomer(@RequestBody LoyalCustomer loyalCustomer) {
		loyalCustomerService.updateLoyalCustomer(loyalCustomer);
	}
	
}
