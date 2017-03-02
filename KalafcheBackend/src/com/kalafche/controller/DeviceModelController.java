package com.kalafche.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.dao.DeviceModelDao;
import com.kalafche.model.device.DeviceModel;

@CrossOrigin
@RestController
@RequestMapping({ "/service/deviceModel" })
public class DeviceModelController {
	@Autowired
	private DeviceModelDao deviceModelDao;

	@RequestMapping(value = { "/getModelsByBrand" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET }, params = { "brandId" })
	public List<DeviceModel> getModelsByBrand(@RequestParam(value = "brandId") int brandId) {
		List<DeviceModel> deviceModelsbyBrand = this.deviceModelDao.getModelsByBrand(brandId);

		return deviceModelsbyBrand;
	}
	
	@RequestMapping(value = { "/getAllDeviceModels" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public List<DeviceModel> getAllDeviceModels() {
		List<DeviceModel> deviceModels= this.deviceModelDao.getAllDeviceModels();
		
		return deviceModels;
	}
	
	@RequestMapping(value = { "/insertModel" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public void insertModel(@RequestBody DeviceModel model) {
		
		this.deviceModelDao.insertModel(model);
	}
}
