package com.kalafche.dao;

import java.util.List;

import com.kalafche.model.partner.PartnerStore;

public interface PartnerStoreDao {

	public abstract List<PartnerStore> getAllPartnerStores();

	public abstract void insertPartnerStore(PartnerStore partnerStore);

}
