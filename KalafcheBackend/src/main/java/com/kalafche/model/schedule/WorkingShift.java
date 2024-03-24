package com.kalafche.model.schedule;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkingShift {

	private Integer id;
	private Long createTimestamp;
	private Integer createdByEmployeeId;
	private String createdByEmployeeName;
	private Long lastUpdateTimestamp;
	private Integer updatedByEmployeeId;
	private String updatedByEmployeeName;
	@NotNull
	private String code;
	@NotNull
	private String name;
	private String description;
	@NotNull
	private Integer startTimeMinutes;
	private String startTime;
	@NotNull
	private Integer endTimeMinutes;
	private String endTime;
	@NotNull
	private Integer durationMinutes;
	private String duration;

}
