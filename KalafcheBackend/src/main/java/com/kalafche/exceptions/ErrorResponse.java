package com.kalafche.exceptions;

import java.util.HashMap;
import java.util.Map;

/**
 * List of errors, used for response body in case of unsuccessful requests.
 */
public class ErrorResponse {

	private Map<String, String> errors = new HashMap<String, String>();
	
	public ErrorResponse() {
	}

	public ErrorResponse(Map<String, String> errors) {
		this.errors = errors;
	}

	public Map<String, String> getErrors() {
		return errors;
	}

	public void setErrors(Map<String, String> errors) {
		this.errors = errors;
	}
}
