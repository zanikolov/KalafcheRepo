package com.kalafche.service.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.kalafche.dao.ProtectPlusDiscountPolicyDao;
import com.kalafche.exceptions.DomainObjectNotFoundException;
import com.kalafche.model.employee.Employee;
import com.kalafche.model.protectplus.ProtectPlusDiscountPolicy;
import com.kalafche.model.protectplus.ProtectPlusDiscountPolicyProductRule;
import com.kalafche.service.DateService;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.ProtectPlusDiscountPolicyService;

@Service
public class ProtectPlusDiscountPolicyServiceImpl implements ProtectPlusDiscountPolicyService {

	private static final BigDecimal ZERO = BigDecimal.ZERO;
	private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

	@Autowired
	private ProtectPlusDiscountPolicyDao protectPlusDiscountPolicyDao;

	@Autowired
	private DateService dateService;

	@Autowired
	private EmployeeService employeeService;

	@Override
	public ProtectPlusDiscountPolicy getActivePolicy(Long timestamp) {
		ProtectPlusDiscountPolicy policy = protectPlusDiscountPolicyDao.getActivePolicy(timestamp);
		if (policy == null) {
			throw new DomainObjectNotFoundException("protectPlusDiscountPolicy",
					"No active Protect+ discount policy found.");
		}

		return policy;
	}

	@Override
	public List<ProtectPlusDiscountPolicy> getAllPolicies() {
		return protectPlusDiscountPolicyDao.getAllPolicies();
	}

	@Override
	public int deactivateExpiredNonDefaultPolicies() {
		return protectPlusDiscountPolicyDao.deactivateExpiredNonDefaultPolicies(dateService.getCurrentMillisBGTimezone());
	}

	@Override
	@Transactional
	public ProtectPlusDiscountPolicy savePolicy(ProtectPlusDiscountPolicy policy) throws SQLException {
		ProtectPlusDiscountPolicy existingPolicy = getExistingPolicy(policy);
		validatePolicy(policy, existingPolicy);
		Employee employee = employeeService.getLoggedInEmployee();
		Long currentTimestamp = dateService.getCurrentMillisBGTimezone();

		if (policy.getId() == null) {
			policy.setDefaultPolicy(false);
			policy.setCreatedById(employee.getId());
			policy.setCreatedTimestamp(currentTimestamp);
			Integer policyId = protectPlusDiscountPolicyDao.insertPolicy(policy);
			policy.setId(policyId);
		} else {
			policy.setDefaultPolicy(false);
			policy.setUpdatedById(employee.getId());
			policy.setLastUpdateTimestamp(currentTimestamp);
			protectPlusDiscountPolicyDao.updatePolicy(policy);
			protectPlusDiscountPolicyDao.deleteProductRules(policy.getId());
		}

		if (policy.getProductRules() != null) {
			for (ProtectPlusDiscountPolicyProductRule productRule : policy.getProductRules()) {
				protectPlusDiscountPolicyDao.insertProductRule(policy.getId(), productRule);
			}
		}

		return protectPlusDiscountPolicyDao.getPolicy(policy.getId());
	}

	private ProtectPlusDiscountPolicy getExistingPolicy(ProtectPlusDiscountPolicy policy) {
		if (policy == null || policy.getId() == null) {
			return null;
		}

		ProtectPlusDiscountPolicy existingPolicy = protectPlusDiscountPolicyDao.getPolicy(policy.getId());
		if (existingPolicy == null) {
			throw new DomainObjectNotFoundException("id", "Non-existing Protect+ discount policy.");
		}

		return existingPolicy;
	}

	private void validatePolicy(ProtectPlusDiscountPolicy policy, ProtectPlusDiscountPolicy existingPolicy) {
		if (policy == null) {
			throw new IllegalArgumentException("Protect+ discount policy is required.");
		}
		if (Boolean.TRUE.equals(policy.getDefaultPolicy())
				|| existingPolicy != null && Boolean.TRUE.equals(existingPolicy.getDefaultPolicy())) {
			throw new IllegalArgumentException("protectPlusDiscountPolicyDefaultCanNotBeManaged");
		}
		if (StringUtils.isEmpty(policy.getName())) {
			throw new IllegalArgumentException("Protect+ discount policy name is required.");
		}
		if (policy.getValidFromTimestamp() == null) {
			throw new IllegalArgumentException("Protect+ discount policy valid from timestamp is required.");
		}
		if (policy.getValidUntilTimestamp() != null
				&& policy.getValidUntilTimestamp().compareTo(policy.getValidFromTimestamp()) < 0) {
			throw new IllegalArgumentException("protectPlusDiscountPolicyInvalidPeriod");
		}

		validatePercent(policy.getSameModelProtectorDiscountPercent(), "sameModelProtectorDiscountPercent");
		validatePercent(policy.getOtherProductsDiscountPercent(), "otherProductsDiscountPercent");
		validateProductRules(policy);
		validatePolicyPeriodOverlap(policy);
	}

	private void validatePolicyPeriodOverlap(ProtectPlusDiscountPolicy policy) {
		if (!Boolean.TRUE.equals(policy.getActive())) {
			return;
		}
		if (Boolean.TRUE.equals(policy.getDefaultPolicy())) {
			if (protectPlusDiscountPolicyDao.countOtherActiveDefaultPolicies(policy) > 0) {
				throw new IllegalArgumentException("protectPlusDiscountPolicyMultipleActiveDefaults");
			}
			return;
		}

		if (protectPlusDiscountPolicyDao.countOverlappingActiveNonDefaultPolicies(policy) > 0) {
			throw new IllegalArgumentException("protectPlusDiscountPolicyOverlapsActiveCampaign");
		}
	}

	private void validateProductRules(ProtectPlusDiscountPolicy policy) {
		Set<Integer> productIds = new HashSet<>();
		if (policy.getProductRules() == null || policy.getProductRules().isEmpty()) {
			return;
		}

		for (ProtectPlusDiscountPolicyProductRule productRule : policy.getProductRules()) {
			if (productRule.getProductId() == null) {
				throw new IllegalArgumentException("Protect+ product rule product is required.");
			}
			if (!productIds.add(productRule.getProductId())) {
				throw new IllegalArgumentException("Protect+ product rule duplicates product.");
			}
			validatePercent(productRule.getDiscountPercent(), "productRuleDiscountPercent");
		}

		if (protectPlusDiscountPolicyDao.countProductRulesWithMissingProducts(policy) > 0) {
			throw new DomainObjectNotFoundException("productId", "Non-existing product in Protect+ product rule.");
		}
	}

	private void validatePercent(BigDecimal value, String fieldName) {
		if (value == null) {
			throw new IllegalArgumentException("Protect+ discount policy " + fieldName + " is required.");
		}
		if (value.compareTo(ZERO) < 0 || value.compareTo(ONE_HUNDRED) > 0) {
			throw new IllegalArgumentException("Protect+ discount policy " + fieldName + " should be between 0 and 100.");
		}
	}
}
