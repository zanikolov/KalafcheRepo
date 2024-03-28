package com.kalafche.model.schedule;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthlySchedulePrintRequest {

	private int month;
	private int year;
	private int storeId;
	private int companyId;
}
