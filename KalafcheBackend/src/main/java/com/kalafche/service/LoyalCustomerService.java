package com.kalafche.service;

import java.util.List;

import com.kalafche.model.LoyalCustomer;

public interface LoyalCustomerService {

	List<LoyalCustomer> getAllLoyalCustomers();

	LoyalCustomer getLoyalCustomer(String code);

	void createLoyalCustomer(LoyalCustomer loyalCustomer);

	void updateLoyalCustomer(LoyalCustomer loyalCustomer);

}
