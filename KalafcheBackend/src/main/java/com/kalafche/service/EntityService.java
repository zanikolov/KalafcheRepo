package com.kalafche.service;

import java.util.List;

import com.kalafche.model.StoreDto;

public interface EntityService {

	List<StoreDto> getStores(boolean includingWarehouse);

	String getConcatenatedStoreIdsForFiltering(String storeId);

	void createEntity(StoreDto store);

	void updateEntity(StoreDto store);

	List<StoreDto> getManagedStoresByEmployee();

	StoreDto getStoreById(Integer storeId);
	
	List<Integer> getStoreIdsByCompanyId(Integer companyId);

	StoreDto getStoreByCode(String storeCode);

}
