package com.kalafche.model;

import javax.validation.constraints.NotNull;

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
	@NotNull
	private Integer endTimeMinutes;
	@NotNull
	private Integer durationMinutes;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getCreateTimestamp() {
		return createTimestamp;
	}

	public void setCreateTimestamp(Long createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public Integer getCreatedByEmployeeId() {
		return createdByEmployeeId;
	}

	public void setCreatedByEmployeeId(Integer createdByEmployeeId) {
		this.createdByEmployeeId = createdByEmployeeId;
	}

	public String getCreatedByEmployeeName() {
		return createdByEmployeeName;
	}

	public void setCreatedByEmployeeName(String createdByEmployeeName) {
		this.createdByEmployeeName = createdByEmployeeName;
	}

	public Long getLastUpdateTimestamp() {
		return lastUpdateTimestamp;
	}

	public void setLastUpdateTimestamp(Long lastUpdateTimestamp) {
		this.lastUpdateTimestamp = lastUpdateTimestamp;
	}

	public Integer getUpdatedByEmployeeId() {
		return updatedByEmployeeId;
	}

	public void setUpdatedByEmployeeId(Integer updatedByEmployeeId) {
		this.updatedByEmployeeId = updatedByEmployeeId;
	}

	public String getUpdatedByEmployeeName() {
		return updatedByEmployeeName;
	}

	public void setUpdatedByEmployeeName(String updatedByEmployeeName) {
		this.updatedByEmployeeName = updatedByEmployeeName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getStartTimeMinutes() {
		return startTimeMinutes;
	}

	public void setStartTimeMinutes(Integer startTimeMinutes) {
		this.startTimeMinutes = startTimeMinutes;
	}

	public Integer getEndTimeMinutes() {
		return endTimeMinutes;
	}

	public void setEndTimeMinutes(Integer endTimeMinutes) {
		this.endTimeMinutes = endTimeMinutes;
	}

	public Integer getDurationMinutes() {
		return durationMinutes;
	}

	public void setDurationMinutes(Integer durationMinutes) {
		this.durationMinutes = durationMinutes;
	}

}
