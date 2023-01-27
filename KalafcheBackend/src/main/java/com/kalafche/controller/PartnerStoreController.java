package com.kalafche.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.model.PartnerStore;
import com.kalafche.service.PartnerStoreService;

@CrossOrigin
@RestController
@RequestMapping({"/partnerStore"})
public class PartnerStoreController {

	@Autowired
	private PartnerStoreService partnerStoreService;

	@GetMapping
	public List<PartnerStore> getAllPartnerStores() {
		return partnerStoreService.getAllPartnerStores();
	}
	
	@PutMapping
	public void createPartnerStore(@RequestBody PartnerStore partnerStore) {
		partnerStoreService.createPartnerStore(partnerStore);
	}
	
	@PostMapping
	public void updateStore(@RequestBody PartnerStore partnerStore) {
		partnerStoreService.updatePartnerStore(partnerStore);
	}
	
}
