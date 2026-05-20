package com.kalafche.dao;

import java.util.List;

import com.kalafche.model.LoyalCustomer;

public interface LoyalCustomerDao {

	LoyalCustomer getLoyalCustomerByCode(String code);

	LoyalCustomer getLoyalCustomerById(Integer id);

	List<LoyalCustomer> getLoyalCustomersByPhoneNumberOrEmail(String phoneNumber, String email);

	List<LoyalCustomer> getAllLoyalCustomers();

	Integer insertLoyalCustomer(LoyalCustomer loyalCustomer);

	void updateLoyalCustomer(LoyalCustomer loyalCustomer);

	boolean checkIfLoyalCustomerDiscountCodeExists(LoyalCustomer loyalCustomer);

}
