package com.kalafche.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.exceptions.CommonException;
import com.kalafche.model.CalculationResponse;
import com.kalafche.model.Formula;
import com.kalafche.service.FormulaService;

@CrossOrigin
@RestController
@RequestMapping({ "/formula" })
public class FormulaController {
	
	@Autowired
	private FormulaService formulaService;
	
	@PostMapping
	public CalculationResponse calculate(@RequestBody Formula formula) throws CommonException {
		return formulaService.calculate(formula);
	}
	
}