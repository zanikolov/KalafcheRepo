package com.kalafche.model.sale;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProtectPlusKpiReport {

	private Integer selectedMonthMonth;
	private Integer selectedMonthYear;
	private List<ProtectPlusKpiRow> selectedMonthRows;
	private List<ProtectPlusKpiRow> trendRows;
}
