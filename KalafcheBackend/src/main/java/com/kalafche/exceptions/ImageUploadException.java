package com.kalafche.exceptions;

public class ImageUploadException extends RuntimeException{

	private String field;
	private String message;

	public ImageUploadException(String domainName, String field, String value) {
		this.field = field;
		this.message = String.format("%s with %s '%s' already exists.",
				domainName, field, value);
	}
	
	public ImageUploadException(String field, String message) {
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
