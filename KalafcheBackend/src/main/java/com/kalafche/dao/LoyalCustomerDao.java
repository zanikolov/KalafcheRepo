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

	void updateLoyalCustomerName(Integer loyalCustomerId, String name, Integer updatedById, Long lastUpdateTimestamp);

	void updateLoyalCustomerEmail(Integer loyalCustomerId, String email, Integer updatedById, Long lastUpdateTimestamp);

	void updateLoyalCustomerPhoneNumber(Integer loyalCustomerId, String phoneNumber, Integer updatedById,
			Long lastUpdateTimestamp);

	boolean checkIfLoyalCustomerDiscountCodeExists(LoyalCustomer loyalCustomer);

}
