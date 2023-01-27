package com.kalafche.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.dao.StoreDao;
import com.kalafche.model.StoreDto;
import com.kalafche.service.EntityService;

@CrossOrigin
@RestController
@RequestMapping({ "/store" })
public class StoreController {
	
	@Autowired
	private StoreDao storeDao;
	
	@Autowired
	EntityService entityService;

	@GetMapping("/entities")
	public List<StoreDto> getAllEntities() {
		return storeDao.getAllEntities();
	}

	@PutMapping
	public void createStore(@RequestBody StoreDto store) {
		entityService.createEntity(store);
	}
	
	@PostMapping
	public void updateStore(@RequestBody StoreDto store) {
		entityService.updateEntity(store);
	}

	@GetMapping
	public List<StoreDto> getStores() {
		return entityService.getStores();
	}
	
	@GetMapping("/report")
	public List<StoreDto> getStoresForSaleReport() {
		return entityService.getManagedStoresByEmployee();
	}
	
}
