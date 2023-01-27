package com.kalafche.model;

import java.util.List;

public class Employee {

	private Integer id;
	private String name;
	private Integer storeId;
	private String storeName;
	private Integer jobResponsibilityId;
	private String jobResponsibilityName;
	private String username;
	private String password;
	private List<String> roles;
	private List<StoreDto> managedStores;
	private boolean enabled;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public Integer getJobResponsibilityId() {
		return jobResponsibilityId;
	}

	public void setJobResponsibilityId(Integer jobResponsibilityId) {
		this.jobResponsibilityId = jobResponsibilityId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getJobResponsibilityName() {
		return jobResponsibilityName;
	}

	public void setJobResponsibilityName(String jobResponsibilityName) {
		this.jobResponsibilityName = jobResponsibilityName;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public List<StoreDto> getManagedStores() {
		return managedStores;
	}

	public void setManagedStores(List<StoreDto> managedStores) {
		this.managedStores = managedStores;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
