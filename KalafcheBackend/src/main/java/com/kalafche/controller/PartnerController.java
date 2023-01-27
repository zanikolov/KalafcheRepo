package com.kalafche.controller;

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

import com.kalafche.model.Partner;
import com.kalafche.service.PartnerService;

@CrossOrigin
@RestController
@RequestMapping({ "/partner" })
public class PartnerController {
	
	@Autowired
	private PartnerService partnerService;

	@GetMapping
	public List<Partner> getAllPartners() {
		return partnerService.getAllPartners();
	}
	
	@GetMapping("/{code}")
	public Partner getPartnerByCode(@PathVariable (value = "code") String code) {
		return partnerService.getPartner(code);
	}
	
	@PutMapping
	public void insertPartner(@RequestBody Partner partner) {
		partnerService.createPartner(partner);
	}
	
	@PostMapping
	public void updatePartner(@RequestBody Partner partner) {
		partnerService.updatePartner(partner);
	}
	
}
