package com.kalafche.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.dao.DeviceBrandDao;
import com.kalafche.model.device.DeviceBrand;

@CrossOrigin
@RestController
@RequestMapping({ "/service/deviceBrand" })
public class DeviceBrandController {
	@Autowired
	private DeviceBrandDao deviceBrandDao;

	@RequestMapping(value = { "/getAllDeviceBrands" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public List<DeviceBrand> getAllDeviceBrands() {
		List<DeviceBrand> deviceBrands = this.deviceBrandDao.getAllBrands();

		return deviceBrands;
	}

	@RequestMapping(value = { "/insertBrand" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public void insertBrand(@RequestBody DeviceBrand brand) {

		this.deviceBrandDao.insertBrand(brand);
	}
}
