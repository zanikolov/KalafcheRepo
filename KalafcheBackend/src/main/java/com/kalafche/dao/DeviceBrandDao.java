package com.kalafche.dao;

import com.kalafche.model.DeviceBrand;

import java.util.List;

public interface DeviceBrandDao {

	public abstract List<DeviceBrand> getAllBrands();

	public abstract void insertBrand(DeviceBrand brand);

	public abstract boolean checkIfDeviceBrandExists(DeviceBrand brand);
	
	public abstract DeviceBrand selectDeviceBrand(Integer deviceBrandId);
	
}
