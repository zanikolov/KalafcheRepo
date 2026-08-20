package com.kalafche.model.protectplus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProtectPlusDiscountPolicy {

	private Integer id;
	private String name;
	private Long validFromTimestamp;
	private Long validUntilTimestamp;
	private BigDecimal sameModelProtectorDiscountPercent;
	private BigDecimal otherProductsDiscountPercent;
	private Boolean active;
	private Boolean defaultPolicy;
	private Integer createdById;
	private String createdByName;
	private Long createdTimestamp;
	private Integer updatedById;
	private String updatedByName;
	private Long lastUpdateTimestamp;
	private List<ProtectPlusDiscountPolicyProductRule> productRules = new ArrayList<>();

	public BigDecimal getProductDiscountPercent(Integer productId) {
		if (productId == null || productRules == null) {
			return null;
		}

		for (ProtectPlusDiscountPolicyProductRule productRule : productRules) {
			if (productId.equals(productRule.getProductId())) {
				return productRule.getDiscountPercent();
			}
		}

		return null;
	}
}
