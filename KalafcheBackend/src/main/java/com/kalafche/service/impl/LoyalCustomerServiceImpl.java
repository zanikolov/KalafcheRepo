package com.kalafche.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kalafche.dao.LoyalCustomerDao;
import com.kalafche.exceptions.DomainObjectNotFoundException;
import com.kalafche.exceptions.DuplicationException;
import com.kalafche.model.LoyalCustomer;
import com.kalafche.service.DateService;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.LoyalCustomerService;

@Service
public class LoyalCustomerServiceImpl implements LoyalCustomerService {

	@Autowired
	LoyalCustomerDao loyalCustomerDao;
	
	@Autowired
	DateService dateService;
	
	@Autowired
	EmployeeService employeeService;
	
	@Override
	public LoyalCustomer getLoyalCustomer(String code) {
		LoyalCustomer loyalCustomer = loyalCustomerDao.getLoyalCustomerByCode(code);
		
		if (loyalCustomer == null) {
			throw new DomainObjectNotFoundException("loyalCustomerCode", "Unexisting Loyal Customer.");
		} else {
			return loyalCustomer;
		}
	}

	@Override
	public List<LoyalCustomer> getAllLoyalCustomers() {
		return loyalCustomerDao.getAllLoyalCustomers();
	}

	@Override
	public void createLoyalCustomer(LoyalCustomer loyalCustomer) {
		validateDiscountCode(loyalCustomer);
		loyalCustomer.setCreatedTimestamp(dateService.getCurrentMillisBGTimezone());
		loyalCustomer.setCreatedById(employeeService.getLoggedInEmployee().getId());
		loyalCustomerDao.insertLoyalCustomer(loyalCustomer);
	}

	@Override
	public void updateLoyalCustomer(LoyalCustomer loyalCustomer) {
		loyalCustomerDao.updateLoyalCustomer(loyalCustomer);
	}
	
	private void validateDiscountCode(LoyalCustomer loyalCustomer) {
		if (loyalCustomerDao.checkIfLoyalCustomerDiscountCodeExists(loyalCustomer)) {
			throw new DuplicationException("code", "Code duplication.");
		}
	}
	
}
