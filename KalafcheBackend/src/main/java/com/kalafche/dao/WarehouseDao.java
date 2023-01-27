package com.kalafche.dao;

public interface WarehouseDao {

	public abstract void upsertStock(Integer itemId, Integer quantity);
	
}
