package com.kalafche.model.protectplus;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProtectPlusDiscountPolicyProductRule {

	private Integer id;
	private Integer protectPlusDiscountPolicyId;
	private Integer productId;
	private String productCode;
	private String productName;
	private BigDecimal discountPercent;
}
