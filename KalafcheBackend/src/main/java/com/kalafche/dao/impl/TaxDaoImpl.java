package com.kalafche.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.TaxDao;
import com.kalafche.model.Tax;

@Service
public class TaxDaoImpl extends JdbcDaoSupport implements TaxDao {

	private static final String SELECT_TAXES = "select id, code, name, rate, is_applicable_on_expense from tax t ";
	private static final String IS_APPLICABLE_ON_EXPENSE_CLAUSE = " where t.is_applicable_on_expense = ? ";
	private static final String CODE_CRITERIA = " where t.code = ? ";

	private BeanPropertyRowMapper<Tax> rowMapper;

	@Autowired
	public TaxDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}

	private BeanPropertyRowMapper<Tax> getRowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<Tax>(Tax.class);
			rowMapper.setPrimitivesDefaultedForNullValue(true);
		}

		return rowMapper;
	}

	@Override
	public List<Tax> selectTaxes(Boolean applicableOnExpenses) {
		if (applicableOnExpenses == null) {
			return getJdbcTemplate().query(SELECT_TAXES, getRowMapper());
		}
		return getJdbcTemplate().query(SELECT_TAXES + IS_APPLICABLE_ON_EXPENSE_CLAUSE, getRowMapper(),
				applicableOnExpenses);
	}
	
	@Override
	public Tax selectTaxByCode(String taxCode) {
		List<Tax> taxes = getJdbcTemplate().query(SELECT_TAXES + CODE_CRITERIA, getRowMapper(), taxCode);
		
		return taxes.isEmpty() ? null : taxes.get(0);
	}

}
