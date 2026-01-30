package com.kalafche.exceptions;

/**
 * Exception related to entity duplication.
 */
public class NoRefundedItemException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private String field;
	private String message;
	
	public NoRefundedItemException(String field, String message) {
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
