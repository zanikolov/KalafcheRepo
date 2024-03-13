package com.kalafche.dao;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.model.formula.Attribute;
import com.kalafche.model.formula.AttributeContext;
import com.kalafche.model.formula.AttributeType;
import com.kalafche.model.formula.Formula;

public interface FormulaDao {

	Integer insertFormula(Formula formula) throws SQLException;

	void insertAbsoluteAttribute(Attribute attribute);

	void updateFormula(Formula formula);

	void updateAttribute(Attribute attribute);

	List<Formula> getFormulas();

	List<Attribute> getAtributes();

	Formula getFormula(Integer formulaId);

	List<AttributeType> getAttributeTypes();
	
	List<AttributeContext> getAttributeContexts();

	void insertRelativeAttribute(Attribute attribute);

	boolean checkIfAttributeNameExists(Attribute attribute);

	boolean checkIfFormulaNameExists(Formula formula);

	List<Attribute> getAtributesByNames(String commaSeparatedAttributeNames);

}
