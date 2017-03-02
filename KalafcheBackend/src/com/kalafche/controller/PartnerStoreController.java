package com.kalafche.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.dao.PartnerStoreDao;
import com.kalafche.model.partner.PartnerStore;

@CrossOrigin
@RestController
@RequestMapping({ "/service/partnerStore" })
public class PartnerStoreController {

	@Autowired
	private PartnerStoreDao partnerStoreDao;

	@RequestMapping(value = { "/getAllPartnerStores" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public List<PartnerStore> getAllPartnerStores() {
		List<PartnerStore> partnerStores = this.partnerStoreDao.getAllPartnerStores();

		return partnerStores;
	}
	
	@RequestMapping(value = { "/insertPartnerStore" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public void insertPartnerStore(@RequestBody PartnerStore partnerStore) {
		this.partnerStoreDao.insertPartnerStore(partnerStore);
	}
}
