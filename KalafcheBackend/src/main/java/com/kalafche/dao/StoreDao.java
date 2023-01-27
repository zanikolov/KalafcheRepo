package com.kalafche.dao;

import java.util.List;

import com.kalafche.model.StoreDto;

public interface StoreDao {

	public abstract List<StoreDto> getAllEntities();

	public abstract void insertStore(StoreDto store);

	public abstract List<StoreDto> selectStores();

	public abstract String selectStoreIdsByManager(String userName);

	public abstract StoreDto selectStore(String storeId);

	public abstract Boolean checkIfStoreNameExists(StoreDto store);

	public abstract Boolean checkIfStoreCodeExists(StoreDto store);

	public abstract void updateStore(StoreDto store);

	public abstract List<StoreDto> selectManagedStoresByEmployee(String loggedInEmployeeUsername);

}
