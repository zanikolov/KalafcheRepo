package com.kalafche.exceptions;

public class IllegalStateTransferException extends RuntimeException {

	private String field;
	private String message;

	public IllegalStateTransferException(String domainName, String oldState, String newState) {
		this.message = String.format("%s state can not be transfer from '%s' to '%s'.", domainName, oldState, newState);
	}

	public IllegalStateTransferException(String field, String message) {
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
