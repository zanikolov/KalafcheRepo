package com.kalafche.service;

import java.util.List;

import com.kalafche.model.partner.PartnerStore;

public interface PartnerStoreService {

	List<PartnerStore> getAllPartnerStores();

	void createPartnerStore(PartnerStore partnerStore);

	void updatePartnerStore(PartnerStore partnerStore);

}
