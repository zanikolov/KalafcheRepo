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

import com.kalafche.dao.CompanyDao;
import com.kalafche.model.Company;
import com.kalafche.service.CompanyService;

@CrossOrigin
@RestController
@RequestMapping({ "/company" })
public class CompanyController {
	
	@Autowired
	private CompanyDao companyDao;
	
	@Autowired
	CompanyService companyService;

	@GetMapping
	public List<Company> getAllCompanies() {
		return companyDao.getAllCompanies();
	}

	@PutMapping
	public void createCompany(@RequestBody Company company) {
		companyService.createCompany(company);
	}
	
	@PostMapping
	public void updateCompany(@RequestBody Company company) {
		companyService.updateCompany(company);
	}

	@GetMapping("/{companyId}")
	public Company getCompanyById(@PathVariable(value = "companyId") Integer companyId) {
		return companyService.getCompanyById(companyId);
	}
	
}
