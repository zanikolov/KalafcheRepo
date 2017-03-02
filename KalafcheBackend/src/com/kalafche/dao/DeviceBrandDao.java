package com.kalafche.dao;

import java.util.List;

import com.kalafche.model.device.DeviceBrand;

public interface DeviceBrandDao {

	public abstract List<DeviceBrand> getAllBrands();

	public abstract void insertBrand(DeviceBrand brand);
}
