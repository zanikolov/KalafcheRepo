package com.kalafche.model;

public class DeviceModel {
	private Integer id;
	private Integer deviceBrandId;
	private String name;

	public Integer getId() {
		return this.id;
	}
	
	public Integer setId(Integer id) {
		return this.id = id;
	}
	
	public Integer getDeviceBrandId() {
		return this.deviceBrandId;
	}
	
	public void setDeviceBrandId(Integer deviceBrandId) {
		this.deviceBrandId = deviceBrandId;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
