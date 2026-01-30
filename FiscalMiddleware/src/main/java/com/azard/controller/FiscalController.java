package com.azard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azard.model.Receipt;
import com.azard.service.FiscalService;

@CrossOrigin
@RestController
@RequestMapping({ "/fiscal" })
public class FiscalController {
	
	@Autowired
	FiscalService fiscalService;
	
	@PostMapping("/print")
	public void printReceipt(@RequestBody Receipt receipt) {
		fiscalService.print(receipt);
	}
	
}