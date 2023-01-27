package com.kalafche.dao;

import java.util.List;

import com.kalafche.model.PartnerStore;

public interface PartnerStoreDao {

	public List<PartnerStore> getAllPartnerStores();

	public void insertPartnerStore(PartnerStore partnerStore);

	public void updatePartnerStore(PartnerStore partnerStore);

	public boolean checkIfPartnerStoreNameExists(PartnerStore partnerStore);

}
