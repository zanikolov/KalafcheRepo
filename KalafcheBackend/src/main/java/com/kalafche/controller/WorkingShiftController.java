package com.kalafche.controller;

import java.sql.SQLException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.model.schedule.WorkingShift;
import com.kalafche.service.WorkingShiftService;

@CrossOrigin
@RestController
@RequestMapping({ "/workingShift" })
@Validated
public class WorkingShiftController {

	@Autowired
	private WorkingShiftService workingShiftService;

	@PutMapping
	public WorkingShift createWorkingShift(@Valid @RequestBody WorkingShift workingShift) throws SQLException {
		return workingShiftService.createWorkingShift(workingShift);
	}

	@PostMapping
	public void updateWorkingShift(@Valid @RequestBody WorkingShift workingShift) throws SQLException {
		workingShiftService.updateWorkingShift(workingShift);
	}

	@GetMapping
	public List<WorkingShift> getWorkingShifts() {
		return workingShiftService.getWorkingShifts();
	}

}
