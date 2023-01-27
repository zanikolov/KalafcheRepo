package com.kalafche.dao;

import java.math.BigDecimal;
import java.util.List;

import com.kalafche.model.Item;

public interface ItemDao {

	public abstract Item getItem(Integer productId, Integer deviceModelId);
	
	public abstract Item getItem(String barcode);
	
	public abstract List<Item> getAllItems();

	public abstract void insertItem(Integer productId, Integer deviceModelId, String barcode);

	public abstract void updateItemBarcode(Integer id, String barcode);

	public abstract Item getItem(Integer id);

	public abstract BigDecimal getItemPriceByStoreId(Integer itemId, Integer storeId);

	public abstract void updateItemBarcode(Integer productId, Integer deviceModelId, String barcode);
	
}
