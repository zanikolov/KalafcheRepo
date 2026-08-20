package com.kalafche.service;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.model.protectplus.ProtectPlusDiscountPolicy;

public interface ProtectPlusDiscountPolicyService {

	ProtectPlusDiscountPolicy getActivePolicy(Long timestamp);

	List<ProtectPlusDiscountPolicy> getAllPolicies();

	ProtectPlusDiscountPolicy savePolicy(ProtectPlusDiscountPolicy policy) throws SQLException;

	int deactivateExpiredNonDefaultPolicies();
}
