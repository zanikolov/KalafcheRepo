package com.kalafche.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.model.DiscountCampaign;
import com.kalafche.model.DiscountCode;
import com.kalafche.model.DiscountType;
import com.kalafche.service.DiscountService;

@CrossOrigin
@RestController
@RequestMapping({ "/discount" })
public class DiscountController {
	
	@Autowired
	private DiscountService discountService;
	
	@PutMapping("/campaign")
	public DiscountCampaign createDiscountCampaign(@RequestBody DiscountCampaign discountCampaign) throws SQLException {
		return discountService.createDiscountCampaign(discountCampaign);
	}
	
	@PostMapping("/campaign")
	public void updateDiscountCampaign(@RequestBody DiscountCampaign discountCampaign) throws SQLException {
		discountService.updateDiscountCampaign(discountCampaign);
	}
	
	@GetMapping("/campaign")
	public List<DiscountCampaign> getDiscountCampaigns() {
		return discountService.getAllDiscountCampaigns();
	}
	
	@GetMapping("/{discountCampaignId}")
	public DiscountCampaign getDiscountCampaign(@PathVariable(value = "discountCampaignId") Integer discountCampaignId) {
		return discountService.getDiscountCampaign(discountCampaignId);
	}
	
	@GetMapping("/type")
	public List<DiscountType> getDiscountTypes() {
		return discountService.getDiscountTypes();
	}
	
	@GetMapping("/code")
	public List<DiscountCode> getDiscountCodes() {
		return discountService.getDiscountCodes();
	}
	
	@GetMapping("/code/partner")
	public List<DiscountCode> getAvailablePartnerDiscountCodes() {
		return discountService.getAvailableDiscountCodesForPartnerCampaign();
	}	
	
	@GetMapping("/code/loyal")
	public List<DiscountCode> getAvailableLoyalDiscountCodes() {
		return discountService.getAvailableDiscountCodesForLoyalCampaign();
	}
	
	@GetMapping("/code/{code}")
	public DiscountCode getDiscountCode(@PathVariable(value = "code") Integer code) {
		return discountService.getDiscountCode(code);
	}
	
	@PutMapping("/code")
	public DiscountCode createDiscountCode(@RequestBody DiscountCode discountCode) throws SQLException {
		return discountService.createDiscountCode(discountCode);
	}
	
	@PostMapping("/code")
	public void updateDiscountCode(@RequestBody DiscountCode discountCode) throws SQLException {
		discountService.updateDiscountCode(discountCode);
	}
	
}
