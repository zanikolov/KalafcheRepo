package com.kalafche.model;

public class ProductType {
	
	private Integer id;
	private String name;
	private Integer masterTypeId;
	private String masterTypeName;

	public Integer getId() {
		return this.id;
	}
	
	public Integer setId(Integer id) {
		return this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getMasterTypeId() {
		return masterTypeId;
	}

	public void setMasterTypeId(Integer masterTypeId) {
		this.masterTypeId = masterTypeId;
	}

	public String getMasterTypeName() {
		return masterTypeName;
	}

	public void setMasterTypeName(String masterTypeName) {
		this.masterTypeName = masterTypeName;
	}
	
}
