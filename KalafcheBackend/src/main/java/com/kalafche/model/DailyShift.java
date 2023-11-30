package com.kalafche.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyShift {

	private Integer id;
	private Long createTimestamp;
	private Long lastUpdateTimestamp;
	private Integer createdByEmployeeId;
	private String createdByEmployeeName;
	private Integer updatedByEmployeeId;
	private String updatedByEmployeeName;
	private Integer employeeId;
	private String employeeName;
	private Integer calendarId;
	private LocalDate date;
	private Integer workingShiftId;
	private String workingShiftName;
	private Integer workingShiftDurationMinutes;
	private Integer workingShiftStartTimeMinutes;
	private Integer workingShiftEndTimeMinutes;
	
}
