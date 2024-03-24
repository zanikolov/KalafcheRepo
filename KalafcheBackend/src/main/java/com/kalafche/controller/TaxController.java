package com.kalafche.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.model.Tax;
import com.kalafche.service.TaxService;

@CrossOrigin
@RestController
@RequestMapping({ "/tax" })
public class TaxController {

	@Autowired
	private TaxService taxService;

	@GetMapping("/expense")
	public List<Tax> getTaxesApplicableOnExpenses() {
		return taxService.getTaxes(true);
	}

}
