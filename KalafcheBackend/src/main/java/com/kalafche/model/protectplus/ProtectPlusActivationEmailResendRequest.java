package com.kalafche.model.protectplus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProtectPlusActivationEmailResendRequest {

	private Integer certificateNumberFrom;
	private Integer certificateNumberTo;
	private Integer delayBetweenEmailsMillis;
	private Boolean dryRun;
}
