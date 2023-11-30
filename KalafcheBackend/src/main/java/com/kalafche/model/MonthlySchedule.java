package com.kalafche.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthlySchedule {

	private Integer id;
	private Integer storeId;
	private String storeName;
	private Integer companyId;
	private String companyName;
	private Long createTimestamp;
	private Long lastUpdateTimestamp;
	private Integer createdByEmployeeId;
	private String createdByEmployeeName;
	private Integer updatedByEmployeeId;
	private String updatedByEmployeeName;
	private Integer month;
	private Integer year;
	private Boolean isFinalized;
	List<DailyShift> dailyShifts;
	List<Employee> employees;
	
}
