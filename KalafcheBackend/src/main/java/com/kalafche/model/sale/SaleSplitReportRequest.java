package com.kalafche.model.sale;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaleSplitReportRequest {

	private Integer storeId;
	private Long startDate;
	private Long endDate;

}
