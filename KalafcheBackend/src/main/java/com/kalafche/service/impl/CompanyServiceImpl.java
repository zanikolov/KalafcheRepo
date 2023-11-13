package com.kalafche.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kalafche.dao.CompanyDao;
import com.kalafche.model.CompanyDto;
import com.kalafche.service.CompanyService;
import com.kalafche.service.EmployeeService;

@Service
public class CompanyServiceImpl implements CompanyService {

	@Autowired
	CompanyDao companyDao;	
	
	@Autowired
	EmployeeService employeeService;
	
	@Override
	public List<CompanyDto> getCompanies() {
		return companyDao.getAllCompanies();
	}

	@Override
	public void createCompany(CompanyDto company) {
		companyDao.insertCompany(company);
	}
	
	@Override
	public void updateCompany(CompanyDto company) {
		companyDao.updateCompany(company);
	}

	@Override
	public CompanyDto getCompanyById(Integer companyId) {
		return companyDao.selectCompany(companyId.toString());
	}

}
