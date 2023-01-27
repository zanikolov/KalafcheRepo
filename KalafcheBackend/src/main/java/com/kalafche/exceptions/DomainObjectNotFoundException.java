package com.kalafche.exceptions;

/**
 * Exception related to domain object non-existence.
 */
public class DomainObjectNotFoundException extends RuntimeException {

	private String field;
	private String message;
	
	public DomainObjectNotFoundException(String entityName, String field, String value) {
		this.field = field;
		this.message = String.format("%s with %s %s could not be found", entityName, field, value);
	}

	public DomainObjectNotFoundException(String field, String message) {
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
