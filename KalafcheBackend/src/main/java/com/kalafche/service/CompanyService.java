package com.kalafche.service;

import java.util.List;

import com.kalafche.model.CompanyDto;

public interface CompanyService {

	List<CompanyDto> getCompanies();

	void createCompany(CompanyDto company);

	void updateCompany(CompanyDto company);

	CompanyDto getCompanyById(Integer companyId);

}
