package com.kalafche.dao;

import java.util.List;

import com.kalafche.model.Tax;

public interface TaxDao {

	public List<Tax> selectTaxes(Boolean applicableOnExpenses);

	public Tax selectTaxByCode(String taxCode);
	
}
