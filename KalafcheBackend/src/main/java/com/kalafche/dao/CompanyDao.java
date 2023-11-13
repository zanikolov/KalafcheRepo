package com.kalafche.dao;

import java.util.List;

import com.kalafche.model.CompanyDto;

public interface CompanyDao {

	public abstract List<CompanyDto> getAllCompanies();

	public abstract void insertCompany(CompanyDto company);

	public abstract CompanyDto selectCompany(String companyId);

	public abstract void updateCompany(CompanyDto company);

}
