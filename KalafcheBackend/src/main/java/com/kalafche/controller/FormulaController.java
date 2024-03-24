package com.kalafche.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.exceptions.CommonException;
import com.kalafche.model.CalculationResponse;
import com.kalafche.model.formula.Attribute;
import com.kalafche.model.formula.AttributeContext;
import com.kalafche.model.formula.AttributeType;
import com.kalafche.model.formula.Formula;
import com.kalafche.service.FormulaService;

@CrossOrigin
@RestController
@RequestMapping({ "/formula" })
public class FormulaController {
	
	@Autowired
	private FormulaService formulaService;
	
	@PostMapping("/calculate")
	public List<CalculationResponse> calculate(@RequestBody Formula formula, Integer storeId) throws CommonException {
		return formulaService.calculate(formula, storeId);
	}
	
	@PutMapping
	public Formula createFormula(@RequestBody Formula formula) throws SQLException {
		return formulaService.createFormula(formula);
	}
	
	@PutMapping("/attribute")
	public void createAttribute(@RequestBody Attribute attribute) throws SQLException {
		formulaService.createAttribute(attribute);
	}
	
	@PostMapping
	public void updateFormula(@RequestBody Formula formula) throws SQLException {
		formulaService.updateFormula(formula);
	}
	
	@PostMapping("/attribute")
	public void updateAttribute(@RequestBody Attribute attribute) throws SQLException, CommonException {
		formulaService.updateAttribute(attribute);
	}
	
	@DeleteMapping
	public void deleteFormula(@RequestParam(value = "formulaId") Integer formulaId) {		
		formulaService.deleteFormula(formulaId);
	}
	
	@DeleteMapping("/attribute")
	public void deleteAttribute(@RequestParam(value = "attributeId") Integer attributeId) {		
		formulaService.deleteAttribute(attributeId);
	}
	
	@GetMapping()
	public List<Formula> getFormulas() {
		return formulaService.getAllFormulas();
	}
	
	@GetMapping("/attribute")
	public List<Attribute> getAttributess() {
		return formulaService.getAllAttributes();
	}
	
	@GetMapping("/{formulaId}")
	public Formula getFormula(@PathVariable(value = "formulaId") Integer formulaId) {
		return formulaService.getFormula(formulaId);
	}
	
	@GetMapping("/attribute/type")
	public List<AttributeType> getAttributeTypes() {
		return formulaService.getAttributeTypes();
	}
	
	@GetMapping("/attribute/context")
	public List<AttributeContext> getAttributeContexts() {
		return formulaService.getAttributeContexts();
	}
	
}