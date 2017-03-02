package com.kalafche.model.device;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "device_model")
public class DeviceModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_brand_id", nullable = false)
	private DeviceBrand deviceBrand;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_type_id", nullable = false)
	private DeviceType deviceType;

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

	public DeviceBrand getDeviceBrand() {
		return deviceBrand;
	}

	public void setDeviceBrand(DeviceBrand deviceBrand) {
		this.deviceBrand = deviceBrand;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

}
