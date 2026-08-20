package com.kalafche.model.protectplus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProtectPlusActivationEmailResendResult {

	private Integer certificateId;
	private Integer certificateNumber;
	private String customerEmail;
	private boolean sent;
	private boolean skipped;
	private String message;
}
