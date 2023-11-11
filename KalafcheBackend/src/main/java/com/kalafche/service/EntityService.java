package com.kalafche.service;

import java.util.List;

import com.kalafche.model.StoreDto;

public interface EntityService {

	List<StoreDto> getStores();

	String getConcatenatedStoreIdsForFiltering(String storeId);

	void createEntity(StoreDto store);

	void updateEntity(StoreDto store);

	List<StoreDto> getManagedStoresByEmployee();

	StoreDto getStoreById(Integer storeId);

}
