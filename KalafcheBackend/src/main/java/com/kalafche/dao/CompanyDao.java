package com.kalafche.dao;

import java.util.List;

import com.kalafche.model.Company;

public interface CompanyDao {

	public abstract List<Company> getAllCompanies();

	public abstract void insertCompany(Company company);

	public abstract Company selectCompanyById(String companyId);

	public abstract void updateCompany(Company company);

	public abstract Company selectCompanyByCode(String companyCode);

}
