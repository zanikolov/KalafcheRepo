package com.kalafche.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kalafche.dao.PartnerStoreDao;
import com.kalafche.exceptions.DuplicationException;
import com.kalafche.model.PartnerStore;
import com.kalafche.service.PartnerStoreService;

@Service
public class PartnerStoreServiceImpl implements PartnerStoreService {

	@Autowired
	PartnerStoreDao partnerStoreDao;
	
	@Override
	public List<PartnerStore> getAllPartnerStores() {
		return partnerStoreDao.getAllPartnerStores();
	}

	@Override
	public void createPartnerStore(PartnerStore partnerStore) {
		validatePartnerStoreName(partnerStore);
		partnerStoreDao.insertPartnerStore(partnerStore);	
	}

	@Override
	public void updatePartnerStore(PartnerStore partnerStore) {
		validatePartnerStoreName(partnerStore);
		partnerStoreDao.updatePartnerStore(partnerStore);
	}
	
	private void validatePartnerStoreName(PartnerStore partnerStore) {
		if (partnerStoreDao.checkIfPartnerStoreNameExists(partnerStore)) {
			throw new DuplicationException("name", "Name duplication.");
		}
	}

}
