package com.kalafche.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.ProtectPlusDiscountPolicyDao;
import com.kalafche.model.protectplus.ProtectPlusDiscountPolicy;
import com.kalafche.model.protectplus.ProtectPlusDiscountPolicyProductRule;

@Service
public class ProtectPlusDiscountPolicyDaoImpl extends JdbcDaoSupport implements ProtectPlusDiscountPolicyDao {

	private static final String SELECT_POLICY = "select * from protect_plus_discount_policy ";

	private static final String SELECT_ACTIVE_POLICY = SELECT_POLICY +
			"where active = 1 " +
			"and valid_from_timestamp <= ? " +
			"and (valid_until_timestamp is null or valid_until_timestamp >= ?) " +
			"order by default_policy asc, valid_from_timestamp desc, id desc " +
			"limit 1";

	private static final String SELECT_POLICY_BY_ID = SELECT_POLICY + "where id = ?";

	private static final String SELECT_ALL_POLICIES = SELECT_POLICY +
			"where default_policy = 0 " +
			"order by active desc, valid_from_timestamp desc";

	private static final String INSERT_POLICY = "insert into protect_plus_discount_policy " +
			"(name, valid_from_timestamp, valid_until_timestamp, same_model_protector_discount_percent, " +
			"other_products_discount_percent, active, default_policy, created_by, created_timestamp) " +
			"values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String UPDATE_POLICY = "update protect_plus_discount_policy set " +
			"name = ?, valid_from_timestamp = ?, valid_until_timestamp = ?, " +
			"same_model_protector_discount_percent = ?, other_products_discount_percent = ?, active = ?, " +
			"default_policy = ?, updated_by = ?, last_update_timestamp = ? where id = ?";

	private static final String DEACTIVATE_EXPIRED_NON_DEFAULT_POLICIES = "update protect_plus_discount_policy " +
			"set active = 0 " +
			"where active = 1 " +
			"and default_policy = 0 " +
			"and valid_until_timestamp is not null " +
			"and valid_until_timestamp < ?";

	private static final String DELETE_PRODUCT_RULES = "delete from protect_plus_discount_policy_product_rule " +
			"where protect_plus_discount_policy_id = ?";

	private static final String INSERT_PRODUCT_RULE = "insert into protect_plus_discount_policy_product_rule " +
			"(protect_plus_discount_policy_id, product_id, discount_percent) values (?, ?, ?)";

	private static final String SELECT_PRODUCT_RULES = "select pr.*, p.code as product_code, p.name as product_name " +
			"from protect_plus_discount_policy_product_rule pr " +
			"join product p on p.id = pr.product_id " +
			"where pr.protect_plus_discount_policy_id = ? " +
			"order by p.code";

	private static final String COUNT_OVERLAPPING_ACTIVE_NON_DEFAULT_POLICIES = "select count(*) from protect_plus_discount_policy " +
			"where active = 1 " +
			"and default_policy = 0 " +
			"and (? is null or id <> ?) " +
			"and valid_from_timestamp <= coalesce(?, 9223372036854775807) " +
			"and coalesce(valid_until_timestamp, 9223372036854775807) >= ?";

	private static final String COUNT_OTHER_ACTIVE_DEFAULT_POLICIES = "select count(*) from protect_plus_discount_policy " +
			"where active = 1 " +
			"and default_policy = 1 " +
			"and (? is null or id <> ?)";

	private static final String COUNT_PRODUCTS_BY_ID = "select count(*) from product where id = ?";

	private BeanPropertyRowMapper<ProtectPlusDiscountPolicy> policyRowMapper;

	private BeanPropertyRowMapper<ProtectPlusDiscountPolicyProductRule> productRuleRowMapper;

	@Autowired
	public ProtectPlusDiscountPolicyDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}

	private BeanPropertyRowMapper<ProtectPlusDiscountPolicy> getPolicyRowMapper() {
		if (policyRowMapper == null) {
			policyRowMapper = new BeanPropertyRowMapper<>(ProtectPlusDiscountPolicy.class);
			policyRowMapper.setPrimitivesDefaultedForNullValue(true);
		}

		return policyRowMapper;
	}

	private BeanPropertyRowMapper<ProtectPlusDiscountPolicyProductRule> getProductRuleRowMapper() {
		if (productRuleRowMapper == null) {
			productRuleRowMapper = new BeanPropertyRowMapper<>(ProtectPlusDiscountPolicyProductRule.class);
			productRuleRowMapper.setPrimitivesDefaultedForNullValue(true);
		}

		return productRuleRowMapper;
	}

	@Override
	public ProtectPlusDiscountPolicy getActivePolicy(Long timestamp) {
		List<ProtectPlusDiscountPolicy> policies = getJdbcTemplate().query(SELECT_ACTIVE_POLICY, getPolicyRowMapper(),
				timestamp, timestamp);
		ProtectPlusDiscountPolicy policy = policies.isEmpty() ? null : policies.get(0);
		if (policy != null) {
			policy.setProductRules(getProductRules(policy.getId()));
		}

		return policy;
	}

	@Override
	public List<ProtectPlusDiscountPolicy> getAllPolicies() {
		List<ProtectPlusDiscountPolicy> policies = getJdbcTemplate().query(SELECT_ALL_POLICIES, getPolicyRowMapper());
		for (ProtectPlusDiscountPolicy policy : policies) {
			policy.setProductRules(getProductRules(policy.getId()));
		}

		return policies;
	}

	@Override
	public ProtectPlusDiscountPolicy getPolicy(Integer policyId) {
		List<ProtectPlusDiscountPolicy> policies = getJdbcTemplate().query(SELECT_POLICY_BY_ID, getPolicyRowMapper(), policyId);
		ProtectPlusDiscountPolicy policy = policies.isEmpty() ? null : policies.get(0);
		if (policy != null) {
			policy.setProductRules(getProductRules(policy.getId()));
		}

		return policy;
	}

	@Override
	public Integer insertPolicy(ProtectPlusDiscountPolicy policy) throws SQLException {
		try (Connection connection = getDataSource().getConnection();
				PreparedStatement statement = connection.prepareStatement(INSERT_POLICY, Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, policy.getName());
			statement.setLong(2, policy.getValidFromTimestamp());
			if (policy.getValidUntilTimestamp() == null) {
				statement.setNull(3, java.sql.Types.BIGINT);
			} else {
				statement.setLong(3, policy.getValidUntilTimestamp());
			}
			statement.setBigDecimal(4, policy.getSameModelProtectorDiscountPercent());
			statement.setBigDecimal(5, policy.getOtherProductsDiscountPercent());
			statement.setBoolean(6, Boolean.TRUE.equals(policy.getActive()));
			statement.setBoolean(7, Boolean.TRUE.equals(policy.getDefaultPolicy()));
			statement.setInt(8, policy.getCreatedById());
			statement.setLong(9, policy.getCreatedTimestamp());

			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0) {
				throw new SQLException("Creating Protect+ discount policy failed, no rows affected.");
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1);
				}

				throw new SQLException("Creating Protect+ discount policy failed, no generated key.");
			}
		}
	}

	@Override
	public void updatePolicy(ProtectPlusDiscountPolicy policy) {
		getJdbcTemplate().update(UPDATE_POLICY, policy.getName(), policy.getValidFromTimestamp(),
				policy.getValidUntilTimestamp(), policy.getSameModelProtectorDiscountPercent(),
				policy.getOtherProductsDiscountPercent(), Boolean.TRUE.equals(policy.getActive()),
				Boolean.TRUE.equals(policy.getDefaultPolicy()), policy.getUpdatedById(),
				policy.getLastUpdateTimestamp(), policy.getId());
	}

	@Override
	public void deleteProductRules(Integer policyId) {
		getJdbcTemplate().update(DELETE_PRODUCT_RULES, policyId);
	}

	@Override
	public void insertProductRule(Integer policyId, ProtectPlusDiscountPolicyProductRule productRule) {
		getJdbcTemplate().update(INSERT_PRODUCT_RULE, policyId, productRule.getProductId(),
				productRule.getDiscountPercent());
	}

	@Override
	public List<ProtectPlusDiscountPolicyProductRule> getProductRules(Integer policyId) {
		return getJdbcTemplate().query(SELECT_PRODUCT_RULES, getProductRuleRowMapper(), policyId);
	}

	@Override
	public Integer countOverlappingActiveNonDefaultPolicies(ProtectPlusDiscountPolicy policy) {
		Long validUntilTimestamp = policy.getValidUntilTimestamp();
		return getJdbcTemplate().queryForObject(COUNT_OVERLAPPING_ACTIVE_NON_DEFAULT_POLICIES, Integer.class,
				policy.getId(), policy.getId(), validUntilTimestamp, policy.getValidFromTimestamp());
	}

	@Override
	public Integer countOtherActiveDefaultPolicies(ProtectPlusDiscountPolicy policy) {
		return getJdbcTemplate().queryForObject(COUNT_OTHER_ACTIVE_DEFAULT_POLICIES, Integer.class,
				policy.getId(), policy.getId());
	}

	@Override
	public Integer countProductRulesWithMissingProducts(ProtectPlusDiscountPolicy policy) {
		int missingProducts = 0;
		if (policy.getProductRules() == null) {
			return missingProducts;
		}

		for (ProtectPlusDiscountPolicyProductRule productRule : policy.getProductRules()) {
			Integer productCount = getJdbcTemplate().queryForObject(COUNT_PRODUCTS_BY_ID,
					Integer.class, productRule.getProductId());
			if (productCount == null || productCount == 0) {
				missingProducts++;
			}
		}

		return missingProducts;
	}

	@Override
	public int deactivateExpiredNonDefaultPolicies(Long currentTimestamp) {
		return getJdbcTemplate().update(DEACTIVATE_EXPIRED_NON_DEFAULT_POLICIES, currentTimestamp);
	}
}
