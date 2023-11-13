package com.kalafche.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.CompanyDao;
import com.kalafche.model.CompanyDto;

@Service
public class CompanyDaoImpl extends JdbcDaoSupport implements CompanyDao {

	private static final String SELECT_COMPANY_BY_ID = "select * from company where id = ?";
	private static final String GET_ALL_COMPANIES = "select * from company";
	private static final String INSERT_COMPANY = "insert into company (name, code) values (?, ?)";
	private static final String UPDATE_COMPANY = "update company set name = ? where id = ?";

	private BeanPropertyRowMapper<CompanyDto> rowMapper;
	
	@Autowired
	public CompanyDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<CompanyDto> getRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<CompanyDto>(CompanyDto.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return rowMapper;
	}
	
	@Override
	public List<CompanyDto> getAllCompanies() {
		return getJdbcTemplate().query(GET_ALL_COMPANIES, getRowMapper());
	}

	@Override
	public void insertCompany(CompanyDto company) {
		getJdbcTemplate().update(INSERT_COMPANY, company.getName(), company.getCode());
	}

	@Override
	public CompanyDto selectCompany(String companyId) {
		List<CompanyDto> company = getJdbcTemplate().query(SELECT_COMPANY_BY_ID, getRowMapper(), companyId);
		
		return company.isEmpty() ? null : company.get(0);
	}

	@Override
	public void updateCompany(CompanyDto company) {
		getJdbcTemplate().update(UPDATE_COMPANY, company.getName(), company.getId());
	}

}
