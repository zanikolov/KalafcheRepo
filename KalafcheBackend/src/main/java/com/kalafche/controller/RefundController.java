package com.kalafche.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.model.Refund;
import com.kalafche.service.RefundService;

@CrossOrigin
@RestController
@RequestMapping({ "/refund" })
public class RefundController {
	
	@Autowired
	private RefundService refundService;
	
	@GetMapping
	public List<Refund> searchRefunds(@RequestParam(value = "startDateMilliseconds") Long startDateMilliseconds, 
			@RequestParam(value = "endDateMilliseconds") Long endDateMilliseconds, @RequestParam(value = "storeIds") String storeIds,
			@RequestParam(value = "productCode", required = false) String productCode, @RequestParam(value = "deviceBrandId", required = false) Integer deviceBrandId,
			@RequestParam(value = "deviceModelId", required = false) Integer deviceModelId) {
		return refundService.searchRefunds(startDateMilliseconds, endDateMilliseconds, storeIds, productCode, deviceBrandId, deviceModelId);
	}
	
	@PutMapping
	public void submitRefund(@RequestBody Refund refund) {
		refundService.submitRefund(refund);
	}
	
}