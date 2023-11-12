package com.kalafche.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kalafche.model.Waste;
import com.kalafche.model.WasteReport;
import com.kalafche.service.WasteService;

@CrossOrigin
@RestController
@RequestMapping({ "/waste" })
public class WasteController {
	
	@Autowired
	private WasteService wasteService;
	
	@GetMapping
	public WasteReport searchWastes(@RequestParam(value = "startDateMilliseconds") Long startDateMilliseconds, 
			@RequestParam(value = "endDateMilliseconds") Long endDateMilliseconds, @RequestParam(value = "storeIds") String storeIds,
			@RequestParam(value = "productCode", required = false) String productCode, @RequestParam(value = "deviceBrandId", required = false) Integer deviceBrandId,
			@RequestParam(value = "deviceModelId", required = false) Integer deviceModelId) {
		return wasteService.searchWastes(startDateMilliseconds, endDateMilliseconds, storeIds, productCode, deviceBrandId, deviceModelId);
	}
	
	@PostMapping
	public void submitWaste(@RequestParam("wasteImage") MultipartFile wasteImage,
			@RequestParam("itemId") Integer itemId, @RequestParam("description") String description)
			throws SQLException, IllegalStateException, IOException, GeneralSecurityException {
		wasteService.submitWaste(new Waste(itemId, description), wasteImage);
	}
	
}

