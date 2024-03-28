package com.kalafche.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.CompanyDao;
import com.kalafche.model.Company;

@Service
public class CompanyDaoImpl extends JdbcDaoSupport implements CompanyDao {

	private static final String GET_ALL_COMPANIES = "select * from company ";
	private static final String ID_CLAUSE = " where id = ?";
	private static final String CODE_CLAUSE = " where code = ?";
	private static final String INSERT_COMPANY = "insert into company (name, code) values (?, ?)";
	private static final String UPDATE_COMPANY = "update company set name = ? where id = ?";

	private BeanPropertyRowMapper<Company> rowMapper;
	
	@Autowired
	public CompanyDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<Company> getRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<Company>(Company.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return rowMapper;
	}
	
	@Override
	public List<Company> getAllCompanies() {
		return getJdbcTemplate().query(GET_ALL_COMPANIES, getRowMapper());
	}

	@Override
	public void insertCompany(Company company) {
		getJdbcTemplate().update(INSERT_COMPANY, company.getName(), company.getCode());
	}

	@Override
	public Company selectCompanyById(String companyId) {
		List<Company> company = getJdbcTemplate().query(GET_ALL_COMPANIES + ID_CLAUSE, getRowMapper(), companyId);
		
		return company.isEmpty() ? null : company.get(0);
	}

	@Override
	public void updateCompany(Company company) {
		getJdbcTemplate().update(UPDATE_COMPANY, company.getName(), company.getId());
	}

	@Override
	public Company selectCompanyByCode(String companyCode) {
		List<Company> company = getJdbcTemplate().query(GET_ALL_COMPANIES + CODE_CLAUSE, getRowMapper(), companyCode);
		
		return company.isEmpty() ? null : company.get(0);
	}

}
