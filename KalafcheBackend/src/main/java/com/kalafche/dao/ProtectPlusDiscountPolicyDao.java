package com.kalafche.dao;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.model.protectplus.ProtectPlusDiscountPolicy;
import com.kalafche.model.protectplus.ProtectPlusDiscountPolicyProductRule;

public interface ProtectPlusDiscountPolicyDao {

	ProtectPlusDiscountPolicy getActivePolicy(Long timestamp);

	List<ProtectPlusDiscountPolicy> getAllPolicies();

	ProtectPlusDiscountPolicy getPolicy(Integer policyId);

	Integer insertPolicy(ProtectPlusDiscountPolicy policy) throws SQLException;

	void updatePolicy(ProtectPlusDiscountPolicy policy);

	void deleteProductRules(Integer policyId);

	void insertProductRule(Integer policyId, ProtectPlusDiscountPolicyProductRule productRule);

	List<ProtectPlusDiscountPolicyProductRule> getProductRules(Integer policyId);

	Integer countOverlappingActiveNonDefaultPolicies(ProtectPlusDiscountPolicy policy);

	Integer countOtherActiveDefaultPolicies(ProtectPlusDiscountPolicy policy);

	Integer countProductRulesWithMissingProducts(ProtectPlusDiscountPolicy policy);

	int deactivateExpiredNonDefaultPolicies(Long currentTimestamp);
}
