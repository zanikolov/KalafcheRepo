package com.kalafche.service;

import java.util.List;

import com.kalafche.model.Company;

public interface CompanyService {

	List<Company> getCompanies();

	void createCompany(Company company);

	void updateCompany(Company company);

	Company getCompanyById(Integer companyId);

	Company getCompanyByCode(String string);

}
