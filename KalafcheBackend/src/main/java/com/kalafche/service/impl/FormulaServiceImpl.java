package com.kalafche.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.fathzer.soft.javaluator.StaticVariableSet;
import com.kalafche.dao.FormulaDao;
import com.kalafche.exceptions.CommonException;
import com.kalafche.exceptions.DuplicationException;
import com.kalafche.model.CalculationResponse;
import com.kalafche.model.DataReport;
import com.kalafche.model.PeriodInMillis;
import com.kalafche.model.StoreDto;
import com.kalafche.model.formula.Attribute;
import com.kalafche.model.formula.AttributeContext;
import com.kalafche.model.formula.AttributeType;
import com.kalafche.model.formula.Formula;
import com.kalafche.model.sale.SaleReport;
import com.kalafche.service.DateService;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.EntityService;
import com.kalafche.service.ExpenseService;
import com.kalafche.service.FormulaService;
import com.kalafche.service.SaleService;
import com.kalafche.service.WasteService;

@Service
public class FormulaServiceImpl implements FormulaService {
	
	@Autowired
	SaleService saleService;
	
	@Autowired
	WasteService wasteService;
	
	@Autowired
	ExpenseService expenseService;
	
	@Autowired
	EntityService storeService;
	
	@Autowired
	DateService dateService;
	
	@Autowired
	EmployeeService employeeService;
	
	@Autowired
	FormulaDao formulaDao;
	
	@Override
	public List<CalculationResponse> calculate(Formula formula, Integer storeId) throws CommonException {
		formula = formulaDao.getFormula(formula.getId());
		List<Attribute> attributes = getAttributes(formula);
		
		DoubleEvaluator eval = new DoubleEvaluator();
		List<StoreDto> stores = storeService.getStores();
		List<CalculationResponse> resultList = new ArrayList<CalculationResponse>();
		
		for (StoreDto store : stores) {
			CalculationResponse result = new CalculationResponse();
			result.setStoreId(store.getId());
			result.setStoreName(store.getCity() + ", " + store.getName());
			StaticVariableSet<Double> variablesSet = new StaticVariableSet<Double>();
			List<Attribute> storeAttributes = new ArrayList<Attribute>();
			for (Attribute attribute : attributes) {
				Attribute storeAttribute = processStoreAttribute(store, variablesSet, attribute);
				storeAttributes.add(storeAttribute);
			}
			
			Double storeResult;
			try {
				storeResult = eval.evaluate(formula.getExpression(), variablesSet);
				BigDecimal roundedResult = new BigDecimal(storeResult);
				roundedResult = roundedResult.setScale(2, RoundingMode.HALF_UP);
				storeResult = roundedResult.doubleValue();
			} catch(Exception e) {
				storeResult = 0d;
				e.printStackTrace();
			}
			result.setResult(storeResult);
			resultList.add(result);
		}	
		
		return resultList;
	}

	private Attribute processStoreAttribute(StoreDto store, StaticVariableSet<Double> variablesSet, Attribute attribute)
			throws CommonException {
		Attribute storeAttribute = new Attribute();
		storeAttribute.setFromTimestamp(attribute.getFromTimestamp());
		storeAttribute.setToTimestamp(attribute.getToTimestamp());
		storeAttribute.setName(attribute.getName());
		
		PeriodInMillis monthInMillis = getPeriodInMillis(attribute);
		Double attributeValue = calculateAttributeValue(store, variablesSet, attribute, monthInMillis);		
		variablesSet.set(attribute.getName(), attributeValue);
		storeAttribute.setValue(attributeValue);
		
		return storeAttribute;
	}

	private Double calculateAttributeValue(StoreDto store, StaticVariableSet<Double> variablesSet,
			Attribute attribute, PeriodInMillis monthInMillis) throws CommonException {
		Double value;
		SaleReport saleReport;
		DataReport dataReport;
		switch (attribute.getContextCode()) {
		case "SALE_AMOUNT":
			saleReport = saleService.searchSaleItems(monthInMillis.getStartDateTime(),
					monthInMillis.getEndDateTime(), store.getId().toString(), null, null, null, null, null, null, null, null);					
			value = saleReport.getTotalAmount() != null ? saleReport.getTotalAmount().doubleValue() : Double.valueOf(0);
			break;
		case "SALE_TRANSACTION_COUNT":
			saleReport = saleService.searchSales(monthInMillis.getStartDateTime(),
					monthInMillis.getEndDateTime(), store.getId().toString());		
			value = saleReport.getTransactionCount() != null ? saleReport.getTransactionCount().doubleValue() : Double.valueOf(0);
			break;
		case "SALE_ITEM_COUNT":
			saleReport = saleService.searchSaleItems(monthInMillis.getStartDateTime(),
					monthInMillis.getEndDateTime(), store.getId().toString(), null, null, null, null, null, null, null, null);		
			value = saleReport.getItemCount() != null ? saleReport.getItemCount().doubleValue() : Double.valueOf(0);
			break;
		case "WASTE_AMOUNT":
			dataReport = wasteService.searchWastes(monthInMillis.getStartDateTime(),
					monthInMillis.getEndDateTime(), store.getId().toString(), null, null, null);
			value = dataReport.getTotalAmount() != null ? dataReport.getTotalAmount().doubleValue() : Double.valueOf(0);
			break;
		case "WASTE_COUNT":
			dataReport = wasteService.searchWastes(monthInMillis.getStartDateTime(),
					monthInMillis.getEndDateTime(), store.getId().toString(), null, null, null);
			value = dataReport.getCount() != null ? dataReport.getCount().doubleValue() : Double.valueOf(0);
			break;
		case "EXPENSE_AMOUNT":
			dataReport = expenseService.getExpenseReport(monthInMillis.getStartDateTime(),
					monthInMillis.getEndDateTime(), store.getId().toString(), 0, List.of("COLLECTION"));
			value = dataReport.getTotalAmount() != null ? dataReport.getTotalAmount().doubleValue() : Double.valueOf(0);
			break;
		case "EXPENSE_COUNT":
			dataReport = expenseService.getExpenseReport(monthInMillis.getStartDateTime(),
					monthInMillis.getEndDateTime(), store.getId().toString(), 0, List.of("COLLECTION"));
			value = dataReport.getCount() != null ? dataReport.getCount().doubleValue() : Double.valueOf(0);
			break;
		default:
			throw new CommonException(String.format("Incorrect attribute context [%s]", attribute.getContextCode()));
		}
		
		return value;
	}

	private PeriodInMillis getPeriodInMillis(Attribute attribute) throws CommonException {
		PeriodInMillis monthInMillis;
		switch (attribute.getTypeCode()) {
		case "ABSOLUTE":
			monthInMillis = new PeriodInMillis(attribute.getFromTimestamp(), attribute.getToTimestamp());
			break;
		case "RELATIVE":
			monthInMillis = dateService.getPeriodInMillis(-1 * attribute.getOffset(), attribute.getOffsetStartDay(),
					attribute.getOffsetEndDay());
			break;
		default:
			throw new CommonException(String.format("Incorrect attribute type [%s]", attribute.getTypeCode()));
		}

		return monthInMillis;
	}

	private List<Attribute> getAttributes(Formula formula) {
		List<String> attributeNames = extractAttributes(formula.getExpression());
		String commaSeparatedAttributeNames = attributeNames.stream().map(name -> String.format("'%s'", name))
				.collect(Collectors.joining(","));
		return formulaDao.getAtributesByNames(commaSeparatedAttributeNames);
	}
	
    public static List<String> extractAttributes(String expression) {
        List<String> attributeNames = new ArrayList<>();

        // Define a regular expression to match variable names with numbers and underscores
        Pattern pattern = Pattern.compile("[a-zA-Z0-9_]+");

        Matcher matcher = pattern.matcher(expression);

        while (matcher.find()) {
            String attributeName = matcher.group();
            if (!attributeNames.contains(attributeName)) {
            	attributeNames.add(attributeName);
            }
        }

        return attributeNames;
    }

	@Override
	public Formula createFormula(Formula formula) throws SQLException {
		validateFormulaName(formula);
		
		formula.setCreateTimestamp(dateService.getCurrentMillisBGTimezone());
		formula.setCreatedByEmployeeId(employeeService.getLoggedInEmployee().getId());
		
		Integer formulaId = formulaDao.insertFormula(formula);
		formula.setId(formulaId);
		
		return formula;
	}

	@Override
	public void createAttribute(Attribute attribute) {
		validateAttributeName(attribute);
		
		attribute.setCreateTimestamp(dateService.getCurrentMillisBGTimezone());
		attribute.setCreatedByEmployeeId(employeeService.getLoggedInEmployee().getId());
		
		if ("ABSOLUTE".equals(attribute.getTypeCode())) {
			formulaDao.insertAbsoluteAttribute(attribute);
		} else if ("RELATIVE".equals(attribute.getTypeCode())) {
			formulaDao.insertRelativeAttribute(attribute);
		}
	}

	@Override
	public void updateFormula(Formula formula) {
		validateFormulaName(formula);
		
		formula.setLastUpdateTimestamp(dateService.getCurrentMillisBGTimezone());
		formula.setUpdatedByEmployeeId(employeeService.getLoggedInEmployee().getId());
		
		formulaDao.updateFormula(formula);
	}

	@Override
	public void updateAttribute(Attribute attribute) throws CommonException {
		validateAttributeName(attribute);
		validatOffseteAttribute(attribute);
		
		attribute.setLastUpdateTimestamp(dateService.getCurrentMillisBGTimezone());
		attribute.setUpdatedByEmployeeId(employeeService.getLoggedInEmployee().getId());
		
		formulaDao.updateAttribute(attribute);
	}

	private void validatOffseteAttribute(Attribute attribute) throws CommonException {
		if (attribute.getTypeCode() == "RELATIVE" && attribute.getOffsetStartDay() != null
				&& attribute.getOffsetEndDay() != null && attribute.getOffsetStartDay() > attribute.getOffsetEndDay()) {
			throw new CommonException("Offset start day could not be greater then the offset end day.");
		}
	}

	@Override
	public List<Formula> getAllFormulas() {
		return formulaDao.getFormulas();
	}

	@Override
	public List<Attribute> getAllAttributes() {
		return formulaDao.getAtributes();
	}

	@Override
	public Formula getFormula(Integer formulaId) {
		return formulaDao.getFormula(formulaId);
	}

	@Override
	public List<AttributeType> getAttributeTypes() {
		return formulaDao.getAttributeTypes();
	}

	@Override
	public List<AttributeContext> getAttributeContexts() {
		return formulaDao.getAttributeContexts();
	}
	
	private void validateAttributeName(Attribute attribute) {
		if (formulaDao.checkIfAttributeNameExists(attribute)) {
			throw new DuplicationException("name", "Name duplication.");
		}
	}
	
	private void validateFormulaName(Formula formula) {
		if (formulaDao.checkIfFormulaNameExists(formula)) {
			throw new DuplicationException("name", "Name duplication.");
		}
	}

	@Override
	public void deleteFormula(Integer formulaId) {
		formulaDao.deleteFormula(formulaId);	
	}
	
	@Override
	public void deleteAttribute(Integer attributeId) {
		formulaDao.deleteAttribute(attributeId);	
	}

}
