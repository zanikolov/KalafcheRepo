package com.kalafche.dao;

import java.util.List;

import com.kalafche.model.LoyalCustomer;

public interface LoyalCustomerDao {

	LoyalCustomer getLoyalCustomerByCode(String code);

	List<LoyalCustomer> getAllLoyalCustomers();

	void insertLoyalCustomer(LoyalCustomer loyalCustomer);

	void updateLoyalCustomer(LoyalCustomer loyalCustomer);

	boolean checkIfLoyalCustomerDiscountCodeExists(LoyalCustomer loyalCustomer);

}
