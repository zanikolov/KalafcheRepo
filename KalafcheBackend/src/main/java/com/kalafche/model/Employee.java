package com.kalafche.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Employee implements Comparable<Employee>{

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
	
	@Override
	public int compareTo(Employee o) {
		// TODO Auto-generated method stub
		return this.id.compareTo(o.getId());
	}

}
