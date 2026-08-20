package com.kalafche.model.protectplus;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProtectPlusActivationEmailResendReport {

	private boolean dryRun;
	private int candidateCount;
	private int sentCount;
	private int skippedCount;
	private int failedCount;
	private List<ProtectPlusActivationEmailResendResult> results;
}
