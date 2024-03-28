package com.kalafche.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kalafche.dao.CompanyDao;
import com.kalafche.model.Company;
import com.kalafche.service.CompanyService;
import com.kalafche.service.EmployeeService;

@Service
public class CompanyServiceImpl implements CompanyService {

	@Autowired
	CompanyDao companyDao;	
	
	@Autowired
	EmployeeService employeeService;
	
	@Override
	public List<Company> getCompanies() {
		return companyDao.getAllCompanies();
	}

	@Override
	public void createCompany(Company company) {
		companyDao.insertCompany(company);
	}
	
	@Override
	public void updateCompany(Company company) {
		companyDao.updateCompany(company);
	}

	@Override
	public Company getCompanyById(Integer companyId) {
		return companyDao.selectCompanyById(companyId.toString());
	}

	@Override
	public Company getCompanyByCode(String companyCode) {
		return companyDao.selectCompanyByCode(companyCode);
	}

}
