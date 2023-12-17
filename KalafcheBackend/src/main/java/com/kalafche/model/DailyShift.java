package com.kalafche.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyShift implements Comparable<DailyShift> {

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
	private String calendarDate;
	private Integer monthlyScheduleId;
	private Integer workingShiftId;
	private String workingShiftName;
	private Integer workingShiftDurationMinutes;
	private String workingShiftStartTime;
	private String workingShiftEndTime;
	private String workingShiftDuration;
	
	@Override
	public int compareTo(DailyShift o) {
		return this.employeeId.compareTo(o.getEmployeeId());
	}
	
}
