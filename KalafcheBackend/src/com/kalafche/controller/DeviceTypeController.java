package com.kalafche.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.dao.DeviceTypeDao;
import com.kalafche.model.device.DeviceType;

@CrossOrigin
@RestController
@RequestMapping({ "/service/deviceType" })
public class DeviceTypeController {
	@Autowired
	private DeviceTypeDao deviceTypeDao;

	@RequestMapping(value = { "/getAllDeviceTypes" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public List<DeviceType> getAllDeviceTypes() {
		List<DeviceType> deviceTypes = this.deviceTypeDao.getAllDeviceTypes();

		return deviceTypes;
	}

	@RequestMapping(value = { "/insertDeviceType" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public void insertDeviceType(@RequestBody DeviceType deviceType) {

		this.deviceTypeDao.insertDeviceType(deviceType);
	}
}
