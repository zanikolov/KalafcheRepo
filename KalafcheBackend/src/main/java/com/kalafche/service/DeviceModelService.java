package com.kalafche.service;

import java.util.List;

import com.kalafche.model.device.DeviceModel;

public interface DeviceModelService {

	List<DeviceModel> getDeviceModelsByBrand(Integer brandId);

	List<DeviceModel> getAllDeviceModels();

	void submitModel(DeviceModel model);

	void updateModel(DeviceModel model);

	List<Integer> getDeviceModelIdsForDailyRevision(Integer lastRevisedDevieModelId, Integer count);

	List<Integer> getAllDeviceModelIds();

	List<DeviceModel> getDeviceModelsByIds(List<Integer> deviceModelIds);

}
