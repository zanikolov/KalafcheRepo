package com.kalafche.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.fathzer.soft.javaluator.StaticVariableSet;
import com.kalafche.model.CalculationResponse;
import com.kalafche.model.Formula;
import com.kalafche.model.FormulaVariable;
import com.kalafche.model.SaleReport;
import com.kalafche.model.StoreDto;
import com.kalafche.service.EntityService;
import com.kalafche.service.FormulaService;
import com.kalafche.service.SaleService;

@Service
public class FormulaServiceImpl implements FormulaService {
	
	@Autowired
	SaleService saleService;
	
	@Autowired
	EntityService storeService;
	
	private static final DecimalFormat decfor = new DecimalFormat("0.00"); 

	@Override
	public List<CalculationResponse> calculate(Formula formula) {
		System.out.println(">> expression: " + formula.getExpression());
		System.out.println(">> storeId: " + formula.getStoreId());
		
		DoubleEvaluator eval = new DoubleEvaluator();
		List<StoreDto> stores = storeService.getStores();
		List<CalculationResponse> outcomeList = new ArrayList<CalculationResponse>();
		
		for (StoreDto store : stores) {
			CalculationResponse outcome = new CalculationResponse();
			outcome.setStoreId(store.getId());
			outcome.setStoreName(store.getCity() + ", " + store.getName());
			StaticVariableSet<Double> variablesSet = new StaticVariableSet<Double>();
			List<FormulaVariable> storeVariables = new ArrayList<FormulaVariable>();
			for (FormulaVariable variable : formula.getVariables()) {
				FormulaVariable storeVariable = new FormulaVariable(variable);
				SaleReport saleReport = saleService.searchSales(variable.getStartDateMilliseconds(), variable.getEndDateMilliseconds(), store.getId().toString());
				
				if (saleReport != null && saleReport.getTotalAmount() != null) {
					variablesSet.set(variable.getName(), saleReport.getTotalAmount().doubleValue());
				} else {
					variablesSet.set(variable.getName(), Double.valueOf(0));
				}
				
				storeVariable.setVariableValue(saleReport.getTotalAmount().setScale(2, RoundingMode.HALF_UP).doubleValue());
				storeVariables.add(storeVariable);
			}
			
			Double result;
			try {
				result = eval.evaluate(formula.getExpression(), variablesSet);
				BigDecimal roundedResult = new BigDecimal(result);
				roundedResult = roundedResult.setScale(2, RoundingMode.HALF_UP);
				result = roundedResult.doubleValue();
			} catch(Exception e) {
				result = 0d;
				e.printStackTrace();
			}
			outcome.setVariables(storeVariables);
			outcome.setFormulaOutcome(result);
			outcomeList.add(outcome);
		}
		
		
		return outcomeList;
	}

}
