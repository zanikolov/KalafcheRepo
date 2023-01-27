package com.kalafche.model;

public class LoginHistory {

	private int id;
	private int employeeId;
	private String employeeName;
	private String employeeStoreName;
	private long loginTimestamp;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmployeeStoreName() {
		return employeeStoreName;
	}

	public void setEmployeeStoreName(String employeeStoreName) {
		this.employeeStoreName = employeeStoreName;
	}

	public long getLoginTimestamp() {
		return loginTimestamp;
	}

	public void setLoginTimestamp(long loginTimestamp) {
		this.loginTimestamp = loginTimestamp;
	}

}
