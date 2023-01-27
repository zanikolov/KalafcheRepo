package com.kalafche.dao;

import java.util.List;

import com.kalafche.model.Partner;

public abstract interface PartnerDao {
	public abstract List<Partner> getAllPartners();

	public abstract Partner getPartnerByCode(String partnerCode);

	public abstract void insertPartner(Partner partner);

	public abstract void updatePartner(Partner partner);

	public abstract boolean checkIfPartnerNameExists(Partner partner);

	public abstract boolean checkIfPartnerDiscountCodeExists(Partner partner);
}
