package com.kalafche.exceptions;

public class NegativeItemQuantityException extends RuntimeException {

	private String field;
	private String message;
	
	public NegativeItemQuantityException(Integer storeItemId, Integer storeItemQuantity, Integer addition) {
		this.message = String.format("To store item with id %d could not be add %d. Current store item quantity is %d.",
				storeItemId, addition, storeItemQuantity);
	}

	public NegativeItemQuantityException(String field, String message) {
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
