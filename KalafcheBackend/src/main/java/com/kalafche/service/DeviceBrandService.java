package com.kalafche.service;

import java.util.List;

import com.kalafche.model.DeviceBrand;

public interface DeviceBrandService {

	List<DeviceBrand> getAllBrands();

	void insertBrand(DeviceBrand brand);

}
