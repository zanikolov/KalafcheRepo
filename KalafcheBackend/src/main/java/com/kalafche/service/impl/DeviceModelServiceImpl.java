package com.kalafche.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kalafche.dao.DeviceModelDao;
import com.kalafche.exceptions.DuplicationException;
import com.kalafche.model.DeviceModel;
import com.kalafche.service.DeviceModelService;

@Service
public class DeviceModelServiceImpl implements DeviceModelService {

	@Autowired
	DeviceModelDao deviceModelDao;
	
	@Override
	public List<DeviceModel> getDeviceModelsByBrand(Integer brandId) {
		return deviceModelDao.getDeviceModelsByBrand(brandId);
	}

	@Override
	public List<DeviceModel> getAllDeviceModels() {
		return deviceModelDao.getAllDeviceModels();
	}

	@Override
	public void submitModel(DeviceModel model) {
		validateDeviceModel(model);
		deviceModelDao.insertModel(model);
	}

	@Override
	public void updateModel(DeviceModel model) {
		validateDeviceModel(model);
		deviceModelDao.updateModel(model);
	}

	private void validateDeviceModel(DeviceModel model) {
		if (deviceModelDao.checkIfDeviceModelExists(model)) {
			throw new DuplicationException("name", "Name duplication.");
		}
	}

	@Override
	public List<Integer> getDeviceModelIdsForDailyRevision(Integer lastRevisedDevieModelId, Integer count) {
		return deviceModelDao.getDeviceModelIdsForDailyRevision(lastRevisedDevieModelId, count);
	}

	@Override
	public List<Integer> getDeviceModelIdsForFullRevision() {
		return deviceModelDao.getDeviceModelIdsForFullRevision();
	}

	@Override
	public List<DeviceModel> getDeviceModelsByIds(List<Integer> deviceModelIds) {
		return deviceModelDao.getDeviceModelsByIds(deviceModelIds);
	}
	
}
