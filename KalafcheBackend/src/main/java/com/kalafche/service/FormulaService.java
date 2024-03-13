package com.kalafche.service;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.exceptions.CommonException;
import com.kalafche.model.CalculationResponse;
import com.kalafche.model.formula.Attribute;
import com.kalafche.model.formula.AttributeContext;
import com.kalafche.model.formula.AttributeType;
import com.kalafche.model.formula.Formula;

public interface FormulaService {

	List<CalculationResponse> calculate(Formula formula, Integer storeId) throws CommonException;

	Formula createFormula(Formula formula) throws SQLException;

	void createAttribute(Attribute attribute);

	void updateFormula(Formula formula);

	void updateAttribute(Attribute attribute);

	List<Formula> getAllFormulas();

	List<Attribute> getAllAttributes();

	Formula getFormula(Integer formulaId);

	List<AttributeType> getAttributeTypes();

	List<AttributeContext> getAttributeContexts();

}
