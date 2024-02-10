package com.kalafche.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthlySchedulePrintRequest {

	int month;
	int year;
	int storeId;
}
