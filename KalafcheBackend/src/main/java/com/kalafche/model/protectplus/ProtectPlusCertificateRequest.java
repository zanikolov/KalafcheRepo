package com.kalafche.model.protectplus;

import com.kalafche.model.LoyalCustomer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProtectPlusCertificateRequest {

	private Integer certificateNumber;
	private LoyalCustomer loyalCustomer;
	private Integer loyalCustomerId;
	private Integer deviceModelId;
	private Integer soldStoreId;
	private Integer soldSaleId;
}
