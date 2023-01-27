package com.kalafche.exceptions;

public class CommonException extends Exception {

	private static final long serialVersionUID = 3279260925005163694L;
	
	private String message;
	
	public CommonException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
