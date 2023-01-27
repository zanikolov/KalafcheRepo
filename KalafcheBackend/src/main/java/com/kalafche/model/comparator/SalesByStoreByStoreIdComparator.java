package com.kalafche.model.comparator;

import java.util.Comparator;

import com.kalafche.model.SalesByStore;

public class SalesByStoreByStoreIdComparator implements Comparator<SalesByStore> {

	@Override
	public int compare(SalesByStore salesByStore1, SalesByStore salesByStore2) {
		return salesByStore1.getStoreId().compareTo(salesByStore2.getStoreId());
	}

}
