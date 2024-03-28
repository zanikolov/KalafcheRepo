package com.kalafche.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.exceptions.CommonException;
import com.kalafche.model.schedule.DailyShift;
import com.kalafche.model.schedule.EmployeeHours;
import com.kalafche.model.schedule.MonthlySchedule;
import com.kalafche.model.schedule.MonthlySchedulePrintRequest;
import com.kalafche.service.DailyShiftService;
import com.kalafche.service.MonthlyScheduleService;

@CrossOrigin
@RestController
@RequestMapping({ "/monthlySchedule" })
public class MonthlyScheduleController {

	@Autowired
	private MonthlyScheduleService monthlyScheduleService;

	@Autowired
	private DailyShiftService dailyShiftService;

	@PutMapping
	public MonthlySchedule generateMonthlySchedule(@RequestBody MonthlySchedule monthlySchedule)
			throws SQLException {
		return monthlyScheduleService.generateMonthlySchedule(monthlySchedule);
	}
	
	@PostMapping
	public void finalizeMonthlySchedule(@RequestBody MonthlySchedule monthlySchedule) throws SQLException {
		monthlyScheduleService.finalizeMonthlySchedule(monthlySchedule, false);
	}

	@PostMapping("/presentForm/{presentFormId}/employee/{employeeId}")
	public void addEmployeeToPresentForm(@PathVariable(value = "presentFormId") Integer presentFormId,
			@PathVariable(value = "employeeId") Integer employeeId) throws SQLException, CommonException {
		monthlyScheduleService.addEmployeeToPresentForm(presentFormId, employeeId);
	}
	
	@PostMapping("/presentForm")
	public void finalizePresentForm(@RequestBody MonthlySchedule monthlySchedule) throws SQLException {
		monthlyScheduleService.finalizeMonthlySchedule(monthlySchedule, true);
	}
	
	@GetMapping
	public MonthlySchedule getMonthlySchedule(@RequestParam(value = "storeId") Integer storeId,
			@RequestParam(value = "month") Integer month, @RequestParam(value = "year") Integer year) {
		return monthlyScheduleService.getMonthlySchedule(storeId, month, year, false);
	}
	
	@GetMapping("/presentForm")
	public MonthlySchedule getPresentForm(@RequestParam(value = "storeId") Integer storeId,
			@RequestParam(value = "month") Integer month, @RequestParam(value = "year") Integer year) {
		return monthlyScheduleService.getMonthlySchedule(storeId, month, year, true);
	}
	
	@PostMapping("/dailyShift")
	public List<EmployeeHours> updateDailyShift(@RequestBody DailyShift dailyShift) {
		return dailyShiftService.updateDailyShift(dailyShift);
	}

	//Only for test purposes till the schedules functionality is fully implemented and approved.
	@GetMapping("/presentFormReport/{month}/{year}")
	public ResponseEntity<byte[]> printPresentFormTest(@PathVariable(value = "month") Integer month, @PathVariable(value = "year") Integer year) throws CommonException {
		
		byte[] excelBytes = monthlyScheduleService.getMonthlyScheduleReport(month, year, 10, 1);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
		String filename = "present-form-report_" + month + "-" + year + ".xlsx";
		headers.setContentDispositionFormData(filename, filename);
		headers.set("Content-Transfer-Encoding", "binary");
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		
		ResponseEntity<byte[]> response = new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);	    
		
		return response;
	}	
	
	@PostMapping("/presentFormReport")
	public ResponseEntity<byte[]> printPresentForm(@RequestBody MonthlySchedulePrintRequest presentFormPrintRequest) throws CommonException {
		
		byte[] excelBytes = monthlyScheduleService.getMonthlyScheduleReport(presentFormPrintRequest.getMonth(), presentFormPrintRequest.getYear(), null, presentFormPrintRequest.getCompanyId());
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
		String filename = "present-form-report_" + presentFormPrintRequest.getMonth() + "-" + presentFormPrintRequest.getYear() + ".xlsx";
		headers.setContentDispositionFormData(filename, filename);
		headers.set("Content-Transfer-Encoding", "binary");
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		
		ResponseEntity<byte[]> response = new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);	    
		
		return response;
	}
	
	@PostMapping("/print")
	public ResponseEntity<byte[]> printMonthlySchedule(@RequestBody MonthlySchedulePrintRequest presentFormPrintRequest) throws CommonException {
		
		byte[] excelBytes = monthlyScheduleService.getMonthlyScheduleReport(presentFormPrintRequest.getMonth(), presentFormPrintRequest.getYear(), presentFormPrintRequest.getStoreId(), null);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
		String filename = "monthly_schedule" + presentFormPrintRequest.getMonth() + "-" + presentFormPrintRequest.getYear() + ".xlsx";
		headers.setContentDispositionFormData(filename, filename);
		headers.set("Content-Transfer-Encoding", "binary");
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		
		ResponseEntity<byte[]> response = new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);	    
		
		return response;
	}
}