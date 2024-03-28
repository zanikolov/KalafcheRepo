package com.kalafche.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kalafche.dao.StoreDao;
import com.kalafche.exceptions.DuplicationException;
import com.kalafche.model.StoreDto;
import com.kalafche.model.employee.Employee;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.EntityService;

@Service
public class EntityServiceImpl implements EntityService {

	@Autowired
	StoreDao storeDao;	
	
	@Autowired
	EmployeeService employeeService;
	
	@Override
	public List<StoreDto> getStores( boolean includingWarehouse) {
		List<StoreDto> stores = storeDao.selectStores();
		if (includingWarehouse) {
			stores.add(getStoreByCode("RU_WH"));
		}
		return stores;
	}

	@Override
	@Transactional
	public void createEntity(StoreDto store) {
		validateStoreCode(store);
		validateStoreName(store);
		storeDao.insertStore(store);
	}
	
	@Override
	@Transactional
	public void updateEntity(StoreDto store) {
		validateStoreCode(store);
		validateStoreName(store);
		storeDao.updateStore(store);
	}
	
	private void validateStoreName(StoreDto store) {
		if (storeDao.checkIfStoreNameExists(store)) {
			throw new DuplicationException("name", "Name duplication.");
		}
	}
	
	private void validateStoreCode(StoreDto store) {
		if (storeDao.checkIfStoreCodeExists(store)) {
			throw new DuplicationException("code", "Code duplication.");
		}
	}

	@Override
	public String getConcatenatedStoreIdsForFiltering(String storeId) {
		if (storeId.equals("0")) {
			if (employeeService.isLoggedInEmployeeAdmin()) {
				return storeDao.selectStoreIdsByManager(null);
			} else if (employeeService.isLoggedInEmployeeManager()) {
				return storeDao.selectStoreIdsByManager(employeeService.getLoggedInEmployeeUsername());
			}
		}
		
		return storeId;
	}

	@Override
	public List<StoreDto> getManagedStoresByEmployee() {
		Employee loggedInEmployee = employeeService.getLoggedInEmployee();
		List<StoreDto> stores = new ArrayList<>();
		if (loggedInEmployee.getRoles().contains("ROLE_ADMIN") || loggedInEmployee.getRoles().contains("ROLE_SUPERADMIN")) {
			stores = storeDao.selectStores();
		}
		if (loggedInEmployee.getRoles().contains("ROLE_MANAGER")) {
			stores = storeDao.selectManagedStoresByEmployee(loggedInEmployee.getUsername()); 
		}
		if (loggedInEmployee.getRoles().contains("ROLE_USER")) {
			stores.add(storeDao.selectStore(loggedInEmployee.getStoreId().toString()));
		}
		
		return stores;
	}

	@Override
	public StoreDto getStoreById(Integer storeId) {
		return storeDao.selectStore(storeId.toString());
	}
	
	@Override
	public StoreDto getStoreByCode(String storeCode) {
		return storeDao.selectStoreByCode(storeCode);
	}

	@Override
	public List<Integer> getStoreIdsByCompanyId(Integer companyId) {
		return storeDao.getStoreIdsByCompanyId(companyId);
	}

}
