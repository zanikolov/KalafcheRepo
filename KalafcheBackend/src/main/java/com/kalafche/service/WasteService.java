package com.kalafche.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;

import org.springframework.web.multipart.MultipartFile;

import com.kalafche.model.Waste;
import com.kalafche.model.WasteReport;

public interface WasteService {

	WasteReport searchWastes(Long startDateMilliseconds, Long endDateMilliseconds, String storeIds, String productCode,
			Integer deviceBrandId, Integer deviceModelId);

	void submitWaste(Waste waste, MultipartFile wasteImage) throws SQLException, IllegalStateException, IOException, GeneralSecurityException;

}
