package com.kalafche.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.dao.PartnerDao;
import com.kalafche.model.partner.Partner;

@CrossOrigin
@RestController
@RequestMapping({ "/service/partner" })
public class PartnerController {
	@Autowired
	private PartnerDao partnerDao;

	@RequestMapping(value = { "/getAllPartners" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public List<Partner> getAllPartners() {
		List<Partner> partners = this.partnerDao.getAllPartners();

		return partners;
	}
	
	@RequestMapping(value = { "/getPartnerByCode" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET}, params = {"partnerCode"})
	public Partner getPartnerByCode(@RequestParam (value = "partnerCode") String partnerCode) {
		Partner partner = this.partnerDao.getPartnerByCode(partnerCode);

		return partner;
	}
	
	@RequestMapping(value = { "/insertPartner" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public void insertPartner(@RequestBody Partner partner) {
		this.partnerDao.insertPartner(partner);
	}
	
}
