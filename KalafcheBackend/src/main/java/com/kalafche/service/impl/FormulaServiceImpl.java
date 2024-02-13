package com.kalafche.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.fathzer.soft.javaluator.StaticVariableSet;
import com.kalafche.model.CalculationResponse;
import com.kalafche.model.Formula;
import com.kalafche.model.FormulaVariable;
import com.kalafche.model.SaleReport;
import com.kalafche.service.FormulaService;
import com.kalafche.service.SaleService;

@Service
public class FormulaServiceImpl implements FormulaService {
	
	@Autowired
	SaleService saleService;

	@Override
	public CalculationResponse calculate(Formula formula) {
		System.out.println(">> expression: " + formula.getExpression());
		System.out.println(">> storeId: " + formula.getStoreId());
		
//		Map<String, BigDecimal> variablesMap = new HashMap<String, BigDecimal>();
//		
//		for (FormulaVariable variable : formula.getVariables()) {
//			SaleReport saleReport = saleService.searchSales(variable.getStartDateMilliseconds(), variable.getEndDateMilliseconds(), formula.getStoreId());
//			
//			if (saleReport != null && saleReport.getTotalAmount() != null) {
//				variablesMap.put(variable.getName(), saleReport.getTotalAmount());
//			} else {
//				variablesMap.put(variable.getName(), BigDecimal.ZERO);
//			}
//		}
		
		DoubleEvaluator eval = new DoubleEvaluator();
		StaticVariableSet<Double> variablesSet = new StaticVariableSet<Double>();
		for (FormulaVariable variable : formula.getVariables()) {
			SaleReport saleReport = saleService.searchSales(variable.getStartDateMilliseconds(), variable.getEndDateMilliseconds(), formula.getStoreId());
			
			if (saleReport != null && saleReport.getTotalAmount() != null) {
				variablesSet.set(variable.getName(), saleReport.getTotalAmount().doubleValue());
			} else {
				variablesSet.set(variable.getName(), Double.valueOf(0));
			}
		}
		System.out.println(variablesSet);
		Double result = eval.evaluate(formula.getExpression(), variablesSet);
		
	System.out.println(">>>>> result: " + result);
		
		return null;
	}

}
