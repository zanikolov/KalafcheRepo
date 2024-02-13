package com.kalafche.service;

import java.util.List;

import com.kalafche.model.CalculationResponse;
import com.kalafche.model.Formula;

public interface FormulaService {

	List<CalculationResponse> calculate(Formula formula);

}
