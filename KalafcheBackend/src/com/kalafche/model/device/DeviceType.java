package com.kalafche.model.device;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="device_type")
public class DeviceType {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)	
	private int id;
	
	@Column(name = "name", nullable = false)	
	private String name;

	public int getId() {
		return this.id;
	}
	
	public int setId(int id) {
		return this.id = id;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
