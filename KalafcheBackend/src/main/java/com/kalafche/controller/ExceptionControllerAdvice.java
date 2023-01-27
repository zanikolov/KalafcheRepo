package com.kalafche.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.kalafche.exceptions.DomainObjectNotFoundException;
import com.kalafche.exceptions.DuplicationException;
import com.kalafche.exceptions.ErrorResponse;
import com.kalafche.exceptions.ExcelInvalidFormatException;
import com.kalafche.exceptions.ImageUploadException;

/**
 * Exception handler controller advice
 */
@RestControllerAdvice
public class ExceptionControllerAdvice extends  ResponseEntityExceptionHandler {
	
	/**
	 * Handles exceptions related to domain object non-existence.
	 * 
	 * @param exception domain object not found exception
	 * @return response with status 404 NOT FOUND
	 * @throws JsonProcessingException 
	 */
	@ExceptionHandler(DomainObjectNotFoundException.class)
	public ResponseEntity<String> handleDomainObjectNotFoundException(DomainObjectNotFoundException exception) throws JsonProcessingException {
		Map<String, String> errors = Maps.newHashMap();
		errors.put(exception.getField(), exception.getMessage());
		ErrorResponse errorResponse = new ErrorResponse(errors);		
		return new ResponseEntity<>(new ObjectMapper().writeValueAsString(errorResponse), HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * Handles exceptions related to entity duplication.
	 * 
	 * @param exception vote duplication exception
	 * @return response with status 409 CONFLICT
	 * @throws JsonProcessingException 
	 */
	@ExceptionHandler(DuplicationException.class)
	public ResponseEntity<String> handleEntityDuplicationdException(DuplicationException exception) throws JsonProcessingException {
		Map<String, String> errors = Maps.newHashMap();
		errors.put(exception.getField(), exception.getMessage());
		ErrorResponse errorResponse = new ErrorResponse(errors);
		return new ResponseEntity<>(new ObjectMapper().writeValueAsString(errorResponse), HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(ExcelInvalidFormatException.class)
	public ResponseEntity<String> handleExcelInvalidFormatException(ExcelInvalidFormatException exception) throws JsonProcessingException {
		Map<String, String> errors = Maps.newHashMap();
		errors.put(exception.getField(), exception.getMessage());
		ErrorResponse errorResponse = new ErrorResponse(errors);
		return new ResponseEntity<>(new ObjectMapper().writeValueAsString(errorResponse), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ImageUploadException.class)
	public ResponseEntity<String> handleExcelInvalidFormatException(ImageUploadException exception) throws JsonProcessingException {
		Map<String, String> errors = Maps.newHashMap();
		errors.put(exception.getField(), exception.getMessage());
		ErrorResponse errorResponse = new ErrorResponse(errors);
		return new ResponseEntity<>(new ObjectMapper().writeValueAsString(errorResponse), HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		Map<String, String> errors = Maps.newHashMap();
		exception.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), String.format("%s: %s", error.getField(), error.getDefaultMessage())));
		exception.getBindingResult().getGlobalErrors()
				.forEach(error -> errors.put(error.getObjectName(), String.format("%s: %s", error.getObjectName(), error.getDefaultMessage())));

		ErrorResponse apiError = new ErrorResponse(errors);
		return handleExceptionInternal(exception, apiError, headers, HttpStatus.BAD_REQUEST, request);
	}
	
}
