package com.kalafche.model.email;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailSendResult {

	private boolean sent;
	private String errorMessage;

	public static EmailSendResult sent() {
		EmailSendResult result = new EmailSendResult();
		result.setSent(true);
		return result;
	}

	public static EmailSendResult failed(String errorMessage) {
		EmailSendResult result = new EmailSendResult();
		result.setSent(false);
		result.setErrorMessage(errorMessage);
		return result;
	}
}
