package com.kalafche.dao;

import java.util.List;

import com.kalafche.model.device.DeviceType;

public abstract interface DeviceTypeDao {
	public abstract List<DeviceType> getAllDeviceTypes();

	void insertDeviceType(DeviceType type);
}
