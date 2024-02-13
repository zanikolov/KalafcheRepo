package com.kalafche.service;

import com.kalafche.model.CalculationResponse;
import com.kalafche.model.Formula;

public interface FormulaService {

	CalculationResponse calculate(Formula formula);

}
