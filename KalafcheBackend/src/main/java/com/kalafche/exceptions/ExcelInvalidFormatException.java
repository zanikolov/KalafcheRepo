package com.kalafche.exceptions;

public class ExcelInvalidFormatException extends RuntimeException {

	private String field;
	private String message;
	
	public ExcelInvalidFormatException(String field, String message) {
		this.field = field;
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
	
	public String getField() {
		return field;
	}
	
}