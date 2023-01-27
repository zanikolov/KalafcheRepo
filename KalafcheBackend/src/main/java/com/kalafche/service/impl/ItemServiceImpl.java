package com.kalafche.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.kalafche.dao.ItemDao;
import com.kalafche.exceptions.DuplicationException;
import com.kalafche.model.Item;
import com.kalafche.service.ItemService;

@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	ItemDao itemDao;
	
	@Override
	public List<Item> getAllItems() {
		return itemDao.getAllItems();
	}

	@Override
	public Item getItemByBarcode(String barcode) {
		return itemDao.getItem(barcode);
	}
	
	@Override
	public Item getItemByProductIdAndDeviceModelId(Integer productId, Integer deviceModelId) {
		return itemDao.getItem(productId, deviceModelId);
	}

	@Override
	public void submitItem(Item item) {
		if (getItemByProductIdAndDeviceModelId(item.getProductId(), item.getDeviceModelId()) != null) {
			throw new DuplicationException("productCode", "Item duplicate.");
		}
		if (getItemByBarcode(item.getBarcode()) != null) {
			throw new DuplicationException("barcode", "Barcode duplicate.");
		}
		itemDao.insertItem(item.getProductId(), item.getDeviceModelId(), item.getBarcode());
	}

	@Override
	public void updateItem(Item item) {
		if (getItemByBarcode(item.getBarcode()) != null) {
			throw new DuplicationException("barcode", "Barcode duplicate.");
		}
		itemDao.updateItemBarcode(item.getId(), item.getBarcode());
	}
	
	@Override
	public void updateItemOnlyForIntegrationPurposes(Integer productId, Integer deviceModelId, String barcode) {
		Item item = getItemByBarcode(barcode);
		if (item != null) {
			itemDao.updateItemBarcode(item.getId(), "");
		}
		itemDao.updateItemBarcode(productId, deviceModelId, barcode);
	}

	@Override
	public void insertItemOrUpdateBarcode(Item item) {
		Item existingItem = getItemByProductIdAndDeviceModelId(item.getProductId(), item.getDeviceModelId());
		if (existingItem != null) {
			if (StringUtils.isEmpty(existingItem.getBarcode())) {
				updateItem(item);
				return;
			} else {
				throw new DuplicationException("productCode", "Item duplicate.");
			}
		} else if (getItemByBarcode(item.getBarcode()) != null) {
			throw new DuplicationException("barcode", "Barcode duplicate.");
		} else {
			itemDao.insertItem(item.getProductId(), item.getDeviceModelId(), item.getBarcode());
		}
	}
}
