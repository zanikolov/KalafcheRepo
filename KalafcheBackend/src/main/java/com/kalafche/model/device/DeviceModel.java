package com.kalafche.model.device;

public class DeviceModel {
	private Integer id;
	private Integer deviceBrandId;
	private String name;
	private Boolean unknownModel;

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

	public Boolean getUnknownModel() {
		return unknownModel;
	}

	public void setUnknownModel(Boolean unknownModel) {
		this.unknownModel = unknownModel;
	}

}
