package com.kalafche.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeHours implements Comparable<EmployeeHours> {
	
	private Employee employee;
	private String hours;
	private Integer minutes;
	private String overtime;
	private Integer overtimeInMinutes;
	
	@Override
	public int compareTo(EmployeeHours o) {
		return this.employee.compareTo(o.getEmployee());
	}


}
