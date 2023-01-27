package com.kalafche.dao;

import com.kalafche.model.DeviceType;

import java.util.List;

public abstract interface DeviceTypeDao {
	public abstract List<DeviceType> getAllDeviceTypes();

	void insertDeviceType(DeviceType type);
}
