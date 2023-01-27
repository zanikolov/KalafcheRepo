package com.kalafche.dao;

import java.util.List;

import com.kalafche.model.Waste;

public interface WasteDao {

	List<Waste> searchWastes(Long startDateMilliseconds,
			Long endDateMilliseconds, String storeIds, String productCode, Integer deviceBrandId, Integer deviceModelId);

	void insertWaste(Waste waste);

}
